package com.example.yash1.companion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class accept extends AppCompatActivity {

    ListView requestListView;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ArrayList<Double> requestLatitude = new ArrayList<Double>();
    ArrayList<Double> requestLongitude = new ArrayList<Double>();
    ArrayList<String> requestUsername =  new ArrayList<String>();
    ArrayList<String> requestType =  new ArrayList<String>();

    public void updateListView(Location location) {
        if (location != null) {

            requestListView = findViewById( R.id.view );
            arrayList.clear();
            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>( "request" );
            final ParseGeoPoint parseGeoPoint = new ParseGeoPoint( location.getLatitude(), location.getLongitude() );
            parseQuery.whereNear( "location", parseGeoPoint );
            parseQuery.setLimit( 10 );
            arrayAdapter = new ArrayAdapter( this, android.R.layout.simple_list_item_1, arrayList );
            requestListView.setAdapter( arrayAdapter );
            parseQuery.findInBackground( new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        arrayList.clear();
                        requestLongitude.clear();
                        requestLatitude.clear();
                        if (objects.size() > 0) {
                            for (ParseObject object : objects) {
                                ParseGeoPoint location = (ParseGeoPoint) object.get( "location" );
                                Double distance = parseGeoPoint.distanceInKilometersTo( location );
                                String type = (String) object.get( "type" );

                                distance = (double) (Math.round( distance * 10 )) / 10;

                                arrayAdapter.add( distance.toString() + " Km " + "Vehicle :  " + type );
                                requestLatitude.add( location.getLatitude() );
                                requestLongitude.add( location.getLongitude() );
                                requestUsername.add( (String)object.get( "username" ) );
                                requestType.add( (String)object.get( "type" ) );


                            }
                        } else {
                            arrayAdapter.add( "No nearBy Requests!!!! " );
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            } );

        }
    }

    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateListView( location );

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
        setContentView( R.layout.activity_accept );
        setTitle( "NearBy Requests" );

        arrayList.clear();
        locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
        } else {
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
            } else {
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
                Location lastKnownLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                updateListView( lastKnownLocation );
            }

        }
        requestListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ActivityCompat.checkSelfPermission( accept.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( accept.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
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

                if(requestLatitude.size()>position && requestLongitude.size() > position && lastKnownLocation!=null )
                {

                    ParseObject parseObject = new ParseObject( "Accept" );
                    parseObject.put( "riderlatitude", lastKnownLocation.getLatitude() );
                    parseObject.put( "riderlongitude",lastKnownLocation.getLongitude() );
                    parseObject.put( "username", requestUsername.get(position) );
                    parseObject.put( "type", requestType.get( position ));

                    parseObject.saveInBackground( new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                Log.i( "check", "no error" );
                            }
                            else
                            {
                                Log.i( "check", "error" );
                            }
                        }
                    } );
                    Intent intent = new Intent(accept.this,RiderActivity.class);
                    intent.putExtra( "driverlatitude", requestLatitude.get( position ) );
                    intent.putExtra( "driverlongitude", requestLongitude.get( position ) );
                    intent.putExtra( "riderlatitude", lastKnownLocation.getLatitude() );
                    intent.putExtra( "riderlogitude",lastKnownLocation.getLatitude());
                    intent.putExtra( "driverusername", requestUsername.get( position ) );
                    //startActivity( intent );


                }
            }
        } );

    }
}
