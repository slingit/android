package me.boopit.boopapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import android.provider.Settings.Secure;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class MainActivity extends ListActivity {

    private String TAG = "BOOP";
    private GoogleCloudMessaging gcm;
    private String GCMToken;
    private Context context = this;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    // get this from the API Console
    private String SENDER_ID = "816614913265";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get SharedPreferences
        settings = getSharedPreferences("Boop", MODE_PRIVATE);

        // Configure the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // TODO: enable translation and change all these values
        toolbar.setTitle("Set up");
        toolbar.setTitleTextAppearance(this, R.style.Theme_BoopSetUpTheme_Title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
            }
        });
        // And lists!
        String[] optionsValues = new String[]{"This is my first device", "Add to an existing group"};
        String[] optionsDescriptions = new String[]{"We'll create a new Boop group for you.", "Be sure to have your other device handy"};
        // finally, put our custom Adapter into action. (see SetupArrayAdapter)
        SetupArrayAdapter adapter = new SetupArrayAdapter(this, optionsValues, optionsDescriptions);
        setListAdapter(adapter);

        firstLaunch();

    }

    /*@Override
    protected void onResume() {
        // apparently we need to check play services on resume as well
        checkPlayServices();
        Log.i(TAG, "Resuming for some reason");
        firstLaunch();
        super.onResume();
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //TODO: handle this click
        return super.onOptionsItemSelected(item);
    }

    // Handle people clicking on options
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        switch (position) {
            case 0:
                // Intent for first device
                Intent firstDeviceIntent = new Intent(this, SetupFirstDevice.class);
                firstDeviceIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                firstDeviceIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(firstDeviceIntent);
                break;
            case 1:
                // Intent for existing group
                Intent existingGroupIntent = new Intent(this, SetupExistingGroup.class);
                existingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                existingGroupIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(existingGroupIntent);
                break;
        }
    }

    /*
     * Check to see if Google Play Services is installed.
     * If not, then prompt the user to install it - or, if GPS isn't available for
     * the user's device, quit gracefully.
     */
    private Boolean checkPlayServices() {
        Log.i(TAG, "Checking GPS");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
                Log.i(TAG, "GPS not successful");
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // prompt to solve the error
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                // this device just doesn't work - build an error
                AlertDialog.Builder playServicesAlert = new AlertDialog.Builder(this);
                playServicesAlert.setTitle("Not supported");
                playServicesAlert.setMessage("This app requires Google Play Services in order to get data to your device. Unfortunately, your device does not support it.");
                playServicesAlert.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                playServicesAlert.setIcon(android.R.drawable.ic_dialog_alert);
                playServicesAlert.show();
                return false;
            }
            return false;
        }
        return true;
    }

    /*
     * get the current registration ID for GCM. If result is empty, the app needs to register.
     * @return registration ID
     */
    private String getRegistrationId(Context context) {
        String registrationID = settings.getString(PROPERTY_REG_ID, "");
        if (registrationID.isEmpty()) {
            Log.i(TAG, "Registration not found");
            return "";
        }
        // Check if app was updated; if so, clear the registration ID
        int registeredVersion = settings.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationID;
    }

    /*
     * Get the current version of the app, for comparing later.
     * @return app version
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Couldn't get package name " + e);
        }
    }

    /*
     * Check to see if this is the first launch, and if so register the app with GCM.
     * TODO: if not first launch, show BoopMainApp.class
     */

    private void firstLaunch() {
        // Check if it's the first launch
        //boolean firstRun = settings.getBoolean("firstRun", true);
        boolean firstRun = true;
        if (firstRun) {
            // get device ID
            String androidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            settings.edit().putString("androidID", androidID).apply();
            // check if Google Play Services is installed, the app won't work otherwise
            if (checkPlayServices()) {
                // check if ths device has already registered for GCM
                gcm = GoogleCloudMessaging.getInstance(this);
                GCMToken = getRegistrationId(context);
                if (GCMToken.isEmpty()) {
                    // register the GCM token in the background
                    registerInBackground();
                    Log.i(TAG, "Registering in background");
                } else {
                    settings.edit().putString("GCMToken", GCMToken).apply();
                }
            }

            // set flag to false to avoid running this in future
            settings.edit().putBoolean("firstRun", false).apply();
        }
    }

    /*
     * Register the application with GCM servers asynchronously.
     * Stores the registration ID and VersionCode in shared preferences.
     */
    private void registerInBackground() {
        Log.i(TAG, "REGISTERING");
        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "Doing in background");
                String msg = "";
                try {
                    // if GCM is null, initialise it (shouldn't happen)
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    GCMToken = gcm.register(SENDER_ID);
                    msg = "Devide registered, registration ID is " + GCMToken;
                    // store these so they persist
                    storeRegistrationID(context, GCMToken);
                } catch(IOException ex) {
                    msg = "Error: " + ex.getLocalizedMessage();
                    // don't try and register again. TODO: present to the user
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationID(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId for app version" + appVersion);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}