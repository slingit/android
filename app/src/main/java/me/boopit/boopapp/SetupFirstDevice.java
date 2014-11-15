package me.boopit.boopapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


public class SetupFirstDevice extends Activity {

    private int transitionTime = 450;
    private PopupWindow noCameraWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_first_device);
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
}
