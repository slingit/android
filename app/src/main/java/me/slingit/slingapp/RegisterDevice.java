package me.slingit.slingapp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.util.UUID;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jamie on 03/01/15.
 */
public class RegisterDevice {
    
    public static Boolean registrationResult;

    /**
     * The initial device registration. Called when a user taps either of the two menu options.
     * @param context       The context from the calling activity
     */
    public static void initialRegistration(Context context) {
        final String TAG = "SLING";
        final String versionNo = context.getResources().getString(R.string.api_version);
        
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);
        
        // generate secret as UUID, save it - needs to remain the same for the life of the device
        String deviceSecret = UUID.randomUUID().toString();
        
        // put these into storage for later use
        settings.edit().putString("deviceSecret", deviceSecret).apply();

        // Get the UUID we generated earlier, use as deviceID
        String deviceID = settings.getString("deviceID", "DEFAULT");

        // check this is actually loaded
        while(deviceID == "DEFAULT") {
            deviceID = settings.getString("deviceID", "DEFAULT");
        }
        
        // create a POST request to /devices/create
        AsyncHttpClient client = new AsyncHttpClient();
        
        // add the API version header
        client.addHeader("X-API-Version", versionNo);
        
        // Create the JSON object for the request
        JSONObject jo = new JSONObject();
        try {
           jo = new JSONObject().put("devices", new JSONObject().put("id", deviceID).put("secret", deviceSecret));
        } catch(JSONException e) {
            Log.i(TAG, "JSON ERROR: " + e);
        }
        
        Log.i(TAG, jo.toString());
        
        // create the ByteArrayEntity to send as the request
        ByteArrayEntity requestEntity = new ByteArrayEntity(jo.toString().getBytes());

        String requestURL = context.getResources().getString(R.string.api_url) + "/devices";
        client.post(context, requestURL, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                if(statusCode == 201) {
                    registrationResult = true;
                }
                Log.i(TAG, "RESPONSE[" + statusCode + "]: " + new String(response));
                // TODO: pass this back to the user
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  byte[] response, Throwable error) {
                registrationResult = false;
                Log.i(TAG, "RESPONSE[" + statusCode + "]");
                error.printStackTrace();
                // TODO: try again
            }
        });
     }

    /**
     * The group registration, either after scanning a QR or creating one.
     * @param context       The context from the calling activity
     * @param groupUUID     The group UUID the device shall register with
     */
    public static void groupRegistration(Context context, String groupUUID) {
        final String TAG = "SLING";
        final String versionNo = context.getResources().getString(R.string.api_version);
        
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);

        // get the the device UUID
        String deviceID = settings.getString("deviceID", "DEFAULT");
        
        // get the device secret
        String deviceSecret = settings.getString("deviceSecret", "DEFAULT");

        // check this is actually loaded
        while(deviceID == "DEFAULT") {
            deviceID = settings.getString("deviceID", "DEFAULT");
        }
        
        while(deviceSecret == "DEFAULT") {
            deviceSecret = settings.getString("deviceSecret", "DEFAULT");
        }
        
        // create a POST request to /devices/:id (update)
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // add the API version header
        client.addHeader("X-API-Version", versionNo);

        // add params
        params.add("id", deviceID);
        params.add("secret", deviceSecret);
        params.add("group", groupUUID);

        // as we're updating, we pass the ID as part of the string
        String requestURL = context.getResources().getString(R.string.api_url) + "/devices/" + deviceID;
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

    /**
     * Allows the device to register to receive push notifications.
     * @param context       The context from the calling activity
     */
    public static void pushRegistration(Context context) {
        final String TAG = "SLING";
        final String versionNo = context.getResources().getString(R.string.api_version);
        
        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);

        // Get the UUID we generated earlier, use as deviceID
        String deviceID = settings.getString("deviceID", "DEFAULT");

        // check this is actually loaded
        while(deviceID == "DEFAULT") {
            deviceID = settings.getString("deviceID", "DEFAULT");
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
        params.add("id", deviceID);
        params.add("pushToken", pushToken);
        params.add("type", "AndroidDevice");

        // add the API version header
        client.addHeader("X-API-Version", versionNo);
        
        //TODO: generate request
    }

    /**
     * Checks the registration status of a device. Used to check if server registration completes succesfully.
     * @param context       The context from the calling activity
     */
    public static void showDeviceRegistration(Context context) {
        final String TAG = "Sling";
        final String versionNo = context.getResources().getString(R.string.api_version);

        SharedPreferences settings = context.getSharedPreferences("Boop", context.MODE_PRIVATE);

        // Get the UUID we generated earlier, use as deviceID
        String deviceID = settings.getString("deviceID", "DEFAULT");

        // check this is actually loaded
        while(deviceID == "DEFAULT") {
            deviceID = settings.getString("deviceID", "DEFAULT");
        }
        
        String deviceSecret = settings.getString("deviceSecret", "DEFAULT");
        while(deviceSecret == "DEFAULT") {
            deviceSecret = settings.getString("deviceSecret", "DEFAULT");
        }
        
        AsyncHttpClient client = new AsyncHttpClient();

        // use basic auth based on the locally stored values
        client.setBasicAuth(deviceID, deviceSecret);
        
        String requestURL = context.getResources().getString(R.string.api_url) + "/devices";
        
        // actually build the request
        // Create the JSON object for the request
        JSONObject jo = new JSONObject();
        try {
            jo = new JSONObject().put("show", new JSONObject().put("id", deviceID).put("secret", deviceSecret));
        } catch(JSONException e) {
            Log.i(TAG, "JSON ERROR: " + e);
        }

        Log.i(TAG, jo.toString());

        // create the ByteArrayEntity to send as the request
        ByteArrayEntity requestEntity = new ByteArrayEntity(jo.toString().getBytes());
        
        // make the request
        client.post(context, requestURL, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "success");
                //TODO: return the details of registration back to client.
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
