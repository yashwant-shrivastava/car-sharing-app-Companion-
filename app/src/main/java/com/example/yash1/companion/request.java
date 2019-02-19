package com.example.yash1.companion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.CompactDecimalFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import javax.crypto.spec.GCMParameterSpec;

public class request extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button;
    boolean request = false;
    LocationManager locationManager;
    Intent intent;
    Handler handler = new Handler();
    boolean update = false;
    public void checkForUpdate() {
        ParseQuery<ParseObject> parseQuery1 = new ParseQuery<ParseObject>( "Accept" );
        parseQuery1.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername() );
        //parseQuery1.whereEqualTo( "type", intent.getStringExtra( "type" ) ) ;
        parseQuery1.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        update= true;
                        ParseObject object = objects.get( 0 );
                        Log.i( "check for object ", "found" );
                        Double latitude = object.getDouble( "riderlatitude" );
                        Double longitude = object.getDouble( "riderlongitude" );
                        if (ActivityCompat.checkSelfPermission( request.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( request.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Location lastKnownLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + lastKnownLocation.getLatitude() + "," +
                                                lastKnownLocation.getLongitude()+ "&daddr=" +latitude + "," +longitude));
                        startActivity(intent);

                    }
                    handler.postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            if(update==false)
                            checkForUpdate();
                        }
                    } , 10000);

                }
            }
        });

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude() );
            mMap.clear();
            mMap.addMarker( new MarkerOptions().position( userLocation ).title( "Your Location" ) );
            mMap.moveCamera( CameraUpdateFactory.newLatLng( userLocation ) );
            ParseUser.getCurrentUser().put( "location", new ParseGeoPoint(location.getLatitude(),location.getLongitude()) );
            ParseUser.getCurrentUser().saveInBackground();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void logout(View view)
    {
        ParseUser.logOut();
        Intent intent1 = new Intent(request.this, MainActivity.class);
        startActivity( intent1 );
        finish();
    }

    public void onclick(View view) {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (request) {
            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>( "request" );
            parseQuery.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername() );
            parseQuery.findInBackground( new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e==null){
                        if(objects.size()>0)
                        {
                            for(ParseObject object : objects)
                            {
                                object.deleteInBackground();
                            }
                            request = false;
                            button.setText( "Request" );
                        }
                    }
                }
            } );
        } else {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
            Location location = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
            if (location != null) {
                ParseObject parseObject = new ParseObject( "request" );
                parseObject.put( "username", ParseUser.getCurrentUser().getUsername() );
                parseObject.put( "type", getIntent().getStringExtra( "Type" ) );
                ParseGeoPoint currentUserLocation = new ParseGeoPoint( location.getLatitude(), location.getLongitude() );
                parseObject.put( "location", currentUserLocation );
                parseObject.saveInBackground( new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i( "check ", "no error" );
                            button.setText( "Cancel" );
                            request = true;
                        } else {
                            Log.i( "check ", "error" );
                        }
                    }
                } );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );

        } else {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );

        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_request );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        button = findViewById( R.id.request );

        final ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>( "request" );
        parseQuery.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername() );
        parseQuery.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0)
                    {
                        request = true;
                        button.setText( "Cancel" );
                    }
                }
            }
        } );
        update = false;
        intent= getIntent();
/*

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> parseQuery1 = new ParseQuery<ParseObject>( "accept" );
                parseQuery1.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername() );
                while(true) {
                    parseQuery1.findInBackground( new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e==null)
                            {
                                if(objects.size()>0)
                                {
                                    ParseObject object = objects.get( 0 );
                                    Log.i( "check ", "found" );
                                }
                            }
                        }
                    } );
                }
            }
        };
        new Thread(runnable).start();
        */

        checkForUpdate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
        }
        else
        {
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else {
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
                mMap.clear();
                Location lastKnownLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                LatLng userLocation= new LatLng( lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude() );
                mMap.addMarker( new MarkerOptions().position( userLocation ).title( "Your Location" ) );
                mMap.moveCamera( CameraUpdateFactory.newLatLng( userLocation ) );
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 14.0f));            }

        }
    }
}
