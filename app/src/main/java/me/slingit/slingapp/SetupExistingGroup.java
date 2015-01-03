package me.slingit.slingapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import me.slingit.slingapp.R;


public class SetupExistingGroup extends Activity {

    private int transitionTime = 450;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_existing_group);
        // Set the title
        // initial toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitle("Welcome back");
        toolbar.setTitleTextAppearance(this, R.style.Theme_BoopSetUpTheme_Title);

        // Pretty colour transition
        TransitionDrawable transition = (TransitionDrawable) findViewById(R.id.toolbar).getBackground();
        transition.startTransition(transitionTime);

        // Blue status bar on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkGreen));
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
        getMenuInflater().inflate(R.menu.menu_setup_existing_group, menu);
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

    public void startQRScan(View v) {
        Intent scanIntent = new Intent(this, HandleQRScan.class);
        startActivity(scanIntent);
    }
}
