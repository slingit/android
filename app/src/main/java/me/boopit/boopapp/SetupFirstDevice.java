package me.boopit.boopapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.UUID;

import com.loopj.android.http.*;

import org.apache.http.Header;


public class SetupFirstDevice extends Activity {

    private int transitionTime = 450;
    private PopupWindow noCameraWindow;
    private String groupUUID;
    private String androidID;
    private String pushToken;
    private String createUrl;
    private SharedPreferences settings;
    private String TAG = "BOOP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_first_device);

        settings = getSharedPreferences("Boop", MODE_PRIVATE);
        // Set the title
        // initial toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitle("First device");
        toolbar.setTitleTextAppearance(this, R.style.Theme_BoopSetUpTheme_Title);

        // Pretty colour transition
        TransitionDrawable transition = (TransitionDrawable) findViewById(R.id.toolbar).getBackground();
        transition.startTransition(transitionTime);

        // Blue status bar on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkBlue));
        }

        // Fade in the rest of the content
        RelativeLayout lay = (RelativeLayout)findViewById(R.id.relativeLayoutContent);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lay, "alpha", 0f, 1f);
        fadeIn.setDuration(transitionTime);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.start();

        // Create a UUID for the group
        groupUUID = UUID.randomUUID().toString();

        // And get the 64-bit ANDROID_ID
        androidID = settings.getString("androidID", "DEFAULT");

        // Get the Push Token from First device setup
        pushToken = settings.getString("GCMToken", "DEFAULT");

        Log.i(TAG, "ID: " + androidID + ", TOKEN: " + pushToken);

        // if pushToken hasn't initialied properly, we're gonna wait until it has
        while(pushToken == "DEFAULT") {
            pushToken = settings.getString("GCMToken", "DEFAULT");
        }

        // and the same with androidID
        while(androidID == "DEFAULT") {
            androidID = settings.getString("androidID", "DEFAULT");
        }

        // Once we have these values, generate a QR code
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(
                    // cut down the image size to stop UI lag
                    groupUUID, BarcodeFormat.QR_CODE, 512, 512
            );
            // then encode image as matrix and display
            ImageView QRImageView = (ImageView)findViewById(R.id.QRImageView);
            QRImageView.setImageBitmap(MatrixToBitMap(matrix));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Once we show the QR code, _then_ create the group on the Boop servers
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("device[group_id]", groupUUID);
        params.put("device[id]", androidID);
        params.put("device[token]", pushToken);
        params.put("device[type]", "GoogleDevice");
        // get URL from strings.xml, set it
        createUrl = getResources().getString(R.string.api_url) + "/v1/devices";
        Log.i(TAG,  "URL: " + createUrl);
        // now send the post request
        client.post(this, createUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.i(TAG, "GROUP CREATED: " + response);
                if(statusCode == 201) {
                    // created succesfully
                    Log.i(TAG, "GROUP CREATED: " + response);
                    //TODO: create a GCM listener to respond when another device joins the group.
                } else {
                    // reporting success, but probably not _actually_ success
                    Log.i(TAG, "SUCFAIL (" +  statusCode + ") : "+ response);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Don't show the whole response, clogs up logcat
                Log.i(TAG, "HTTPERR: " + statusCode);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup_first_device, menu);
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

    public void noCamera(View v) {
        LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.activity_setup_first_device_nocamera, null);
        // launch our XKCD-tastic backup solution
        noCameraWindow = new PopupWindow(popupView, 800, 1000, true);
        // set the animation
        noCameraWindow.setAnimationStyle(R.style.Theme_BoopSetUpTheme_PopUpAnimation);
        noCameraWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void removePopup(View v) {
        noCameraWindow.dismiss();
    }

    /*
     * Write Matrix to a new Bitmap.
     * @param matrix: the matrix to write.
     * @return the new Bitmap object.
     */
    public static Bitmap MatrixToBitMap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        // use RGB_565 for lower memory footprint
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // and set the background color
        ColorDrawable cd = new ColorDrawable(0xfff3f3f3);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : cd.getColor());
            }
        }
        return bmp;
    }
}
