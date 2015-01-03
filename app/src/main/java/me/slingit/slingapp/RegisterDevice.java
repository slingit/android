package me.slingit.slingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.util.UUID;

import com.loopj.android.http.*;

import org.apache.http.Header;

/**
 * Created by jamie on 03/01/15.
 */
public class RegisterDevice {
    
    public static Boolean registrationResult;

    /*
        The initial device registration. Called when a user taps either of the two menu options. 
        Parameters expected: Context of the calling Activity
        Returns: success callback
     */
    public static void initialRegistration(Context context) {
        final String TAG = "SLING";
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);
        
        // generate secret as UUID, save it - needs to remain the same for the life of the device
        String deviceSecret = UUID.randomUUID().toString();
        
        // put these into storage for later use
        settings.edit().putString("deviceSecret", deviceSecret).apply();

        // Get the 64-bit ANDROID_ID, use as deviceID
        String androidID = settings.getString("androidID", "DEFAULT");

        // check this is actually loaded
        while(androidID == "DEFAULT") {
            androidID = settings.getString("androidID", "DEFAULT");
        }
        
        // create a POST request to /devices/create
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        // add params
        params.add("id", androidID);
        params.add("secret", deviceSecret);
        
        String requestURL = context.getResources().getString(R.string.api_url) + "/v1/devices";
        client.post(context, requestURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                if(statusCode == 201) {
                    registrationResult = true;
                }
                Log.i(TAG, "RESPONSE[" + statusCode + "]: ");
                // TODO: pass this back to the user
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, org.json.JSONObject response) {
                registrationResult = false;
                Log.i(TAG, "RESPONSE[" + statusCode + "]: ");
                // TODO: try again
            }
        });
     }

    /*
        The group registration, either after scanning a QR or creating one.
        Parameters expected: Context of the calling Activity, group UUID
        Returns: success callback
     */
    public static void groupRegistration(Context context, String groupUUID) {
        final String TAG = "SLING";
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);

        // get the the device UUID
        String androidID = settings.getString("androidID", "DEFAULT");
        
        // get the device secret
        String deviceSecret = settings.getString("deviceSecret", "DEFAULT");

        // check this is actually loaded
        while(androidID == "DEFAULT") {
            androidID = settings.getString("androidID", "DEFAULT");
        }
        
        while(deviceSecret == "DEFAULT") {
            deviceSecret = settings.getString("deviceSecret", "DEFAULT");
        }
        
        // create a POST request to /devices/:id (update)
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // add params
        params.add("id", androidID);
        params.add("secret", deviceSecret);
        params.add("group", groupUUID);

        // as we're updating, we pass the ID as part of the string
        String requestURL = context.getResources().getString(R.string.api_url) + "/v1/devices/" + androidID;
        client.post(context, requestURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                if(statusCode == 201) {
                    registrationResult = true;
                }
                Log.i(TAG, "RESPONSE[" + statusCode + "]: ");
                // TODO: pass this back to the user
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, org.json.JSONObject response) {
                registrationResult = false;
                Log.i(TAG, "RESPONSE[" + statusCode + "]: ");
                // TODO: try again
            }
        });
    }
    
    public static void pushRegistration(Context context) {
        final String TAG = "SLING";
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);

        // Get the 64-bit ANDROID_ID, use as deviceID
        String androidID = settings.getString("androidID", "DEFAULT");

        // check this is actually loaded
        while(androidID == "DEFAULT") {
            androidID = settings.getString("androidID", "DEFAULT");
        }
        
        //get the Push Token we generated on first launch.
        String pushToken = settings.getString("GCMToken", "DEFAULT");
        // if pushToken hasn't initialied properly, we're gonna wait until it has
        while(pushToken == "DEFAULT") {
            pushToken = settings.getString("GCMToken", "DEFAULT");
        }

        // create a POST request to /devices/create
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // add params
        params.add("id", androidID);
        params.add("pushToken", pushToken);
        params.add("type", "AndroidDevice");
        
        //TODO: generate request
        
    }
}
