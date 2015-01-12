package me.slingit.slingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class ShareActivity extends ActionBarActivity {
    
    String TAG = "SLING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        
        // get all the information we require from the incoming request
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        
        Log.i(TAG, type);
        
        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if(type.startsWith("audio")) {
                // handle audio
            } else if(type.startsWith("image")) {
                Log.i(TAG, intent.getDataString());
                // handle any images coming through
                ImageView demoImageView = (ImageView) findViewById(R.id.share_intent_demo);
                demoImageView.setImageURI(intent.getData());
            } else if(type.startsWith("message")) {
                // ?!?
            } else if(type.startsWith("multipart")) {
                // handle files
            } else if(type.startsWith("text")) {
                // handle text intent
            } else if(type.startsWith("video")) {
                // it's a video!
            } else {
                // it's something we don't want to deal with, not called from a Share intent
                System.exit(0);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
