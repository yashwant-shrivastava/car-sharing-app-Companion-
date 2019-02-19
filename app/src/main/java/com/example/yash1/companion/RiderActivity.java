package com.example.yash1.companion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Handler handler = new Handler();
    Intent intent;

    Boolean DriverActive = false;

    public void checkForUpdates() {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>( "request" );
        parseQuery.whereEqualTo( "username", intent.getStringExtra( "driverusername" ) );
        parseQuery.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        DriverActive = true;
                        Log.i( "check", "found" );
                        Toast.makeText( RiderActivity.this, "Driver is on the Way!!", Toast.LENGTH_SHORT ).show();
                        if (ActivityCompat.checkSelfPermission( RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( RiderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
                        Location location = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                        ParseGeoPoint parseGeoPoint = objects.get( 0 ).getParseGeoPoint( "location" );
                        LatLng riderLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        LatLng driverLocation = new LatLng( parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude() );

                        mMap.clear();
                        ArrayList<Marker> markers = new ArrayList<>();
                        markers.add( mMap.addMarker( new MarkerOptions().position( riderLocation).title( "Rider Location" ) ) );
                        markers.add( mMap.addMarker( new MarkerOptions().position( driverLocation).title( "Driver Location" ) ) );

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 30;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                    }
                }
                handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        checkForUpdates();
                    }
                } ,3000);
            }
        } );

    }


    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(DriverActive!=false) {
                LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude() );
                mMap.clear();
                mMap.addMarker( new MarkerOptions().position( userLocation ).title( "Your Location" ) );
                mMap.moveCamera( CameraUpdateFactory.newLatLng( userLocation ) );

            }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rider );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.RL);
        assert mapFragment != null;
        mapFragment.getMapAsync( this );


        RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.map_layout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LatLng riderLocation = new LatLng( intent.getDoubleExtra( "riderlatitude", 0 ), intent.getDoubleExtra( "riderlongitude",0 ) );
                LatLng driverLocation = new LatLng( intent.getDoubleExtra( "driverlatitude", 0 ), intent.getDoubleExtra( "driverlongitude",0 ) );

                mMap.clear();
                ArrayList<Marker> markers = new ArrayList<>();
                markers.add( mMap.addMarker( new MarkerOptions().position( riderLocation).title( "Rider Location" ) ) );
                markers.add( mMap.addMarker( new MarkerOptions().position( driverLocation).title( "Driver Location" ) ) );

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 30;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        });
         //checkForUpdates();


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
