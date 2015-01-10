package me.slingit.slingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class DeviceRegistered extends Activity {

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_registered);
        // initialise toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.registered_device_headline));
        toolbar.setTitleTextAppearance(this, R.style.Theme_BoopSetUpTheme_Title);
        
        // set the settings
        settings = this.getSharedPreferences("Boop", MODE_PRIVATE);

        // once device registered, we never want to show the start screen again - set flag
        settings.edit().putBoolean("firstRun", false).apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_registered, menu);
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

    public void goToNavView(View v) {
        Intent navViewIntent = new Intent(this, SlingAuthenticatedMainView.class);
        navViewIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(navViewIntent);
    }
}
