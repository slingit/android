package me.slingit.slingapp;

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

import me.slingit.slingapp.R;


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

        // Blue status bar on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkBlue));
        }
        
        // create device UUID
        String groupUUID = UUID.randomUUID().toString();
        
        // Store in preferences
        settings.edit().putString("groupUUID", groupUUID).apply();
        
        // Once we have these values, generate a QR code
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(
                    // cut down the image size to stop UI lag
                    groupUUID, BarcodeFormat.QR_CODE, 400, 400
            );
            // then encode image as matrix and display
            ImageView QRImageView = (ImageView)findViewById(R.id.QRImageView);
            QRImageView.setImageBitmap(MatrixToBitMap(matrix));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // send off a device registration, if true, send group UUID
        RegisterDevice.initialRegistration(this, true);
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
        // TODO: get words from service.
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
