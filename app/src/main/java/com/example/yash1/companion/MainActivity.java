package com.example.yash1.companion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    public void reDirectActivity()
    {
        if(ParseUser.getCurrentUser().get( "riderordriver" ) == "driver")
        {

            Log.i( "redirect", "redirecting" );
            ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>( "request" );
            parseQuery.whereEqualTo( "username" , ParseUser.getCurrentUser().getUsername() );
            parseQuery.findInBackground( new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e==null)
                    {
                        if(objects.size()>0)
                        {
                            ParseObject parseObject = objects.get( 0 );
                            Intent intent = new Intent(MainActivity.this, request.class);
                            intent.putExtra( "type", (String) parseObject.get( "type" ) );
                            startActivity( intent );
                        }
                        else
                        {
                            Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                            startActivity( intent );
                        }
                    }
                }
            } );

        }
        else
        {
            Intent intent = new Intent(MainActivity.this, accept.class);
            startActivity( intent );
        }
    }

    public void getStarted(View view)
    {
            Switch userTypeSwitch = (Switch) findViewById( R.id.switch2 );
            String userType = "rider";
            if (userTypeSwitch.isChecked()) {
                userType = "driver";
            }
            ParseUser.getCurrentUser().put( "riderordriver", userType );
            ParseUser.getCurrentUser().saveInBackground( new SaveCallback() {
                @Override
                public void done(ParseException e) {
                   reDirectActivity();
                }
            } );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("a47884ff7f7f72608d04d6c780309384b06111e8")
                .clientKey( "f04a8cc47c874eb8c9e4c6c7dc2ba7b6fe0e0663")
                .server("http://18.218.119.174:80/parse/")
                .build()
        );


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        if(ParseUser.getCurrentUser()==null)
        {
            ParseAnonymousUtils.logIn( new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.i( "result", "sucessfull" );
                    } else {
                        Log.i( "result", "unsucessfull" );
                    }
                }
            });
        }
    }
}
