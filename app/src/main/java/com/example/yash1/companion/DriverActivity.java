package com.example.yash1.companion;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(DriverActivity.this, request.class);
        if(view.getId()==R.id.imageButton2)
        {
            intent.putExtra("Type" , "bike");
            Log.i( "type", "bike" );
        }
        else
        if(view.getId()==R.id.imageButton7)
        {
            intent.putExtra("Type" , "micro");
            Log.i( "type", "micro" );
        }
        else
        if(view.getId()==R.id.imageButton6)
        {
            intent.putExtra("Type" , "mini");
            Log.i( "type", "mini" );
        }
        else
        if(view.getId()==R.id.imageButton8)
        {
            intent.putExtra("Type" , "sedan");
            Log.i( "type", "sedan" );
        }
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_driver );

        ImageButton bike = findViewById( R.id.imageButton2 );
        ImageButton micro = findViewById( R.id.imageButton6 );
        ImageButton mini = findViewById( R.id.imageButton7 );
        ImageButton sedan = findViewById( R.id.imageButton8 );
        bike.setOnClickListener( this );
        micro.setOnClickListener( this );
        mini.setOnClickListener( this );
        sedan.setOnClickListener( this );

    }
}
