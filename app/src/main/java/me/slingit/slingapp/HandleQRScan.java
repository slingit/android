package me.slingit.slingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by jamie on 14/11/14.
 */
public class HandleQRScan extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String TAG = "BOOP";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Init scanner view, and set it as the content view
        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // We should start the camera!
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop using the camera
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v(TAG, rawResult.getText()); // Prints scan results
        // and then register the device with the group
        RegisterDevice.groupRegistration(this, rawResult.getText());
        // Stop the camera
        mScannerView.stopCamera();
        // And, once we've done some Async things, move to the complete class which contains group information.
        Intent registeredIntent = new Intent(this, DeviceRegistered.class);
        // We don't want no history let it burn, burn
        registeredIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(registeredIntent);
        finish();
    }
}
