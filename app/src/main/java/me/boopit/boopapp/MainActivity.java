package me.boopit.boopapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ListActivity {

    private String TAG = "BOOP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initial toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitle("Set up");
        toolbar.setTitleTextAppearance(this, R.style.Theme_BoopSetUpTheme_Title);

        // Set navigation icon
        //toolbar.setNavigationIcon(R.drawable.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Navigation", Toast.LENGTH_SHORT).show();
            }
        });

        // And lists!
        String[] optionsValues = new String[] { "This is my first device", "Add to an existing group" };
        String[] optionsDescriptions = new String[] { "We'll create a new Boop group for you.", "Be sure to have your other device handy"};
        // use custom adapter
        SetupArrayAdapter adapter = new SetupArrayAdapter(this, optionsValues, optionsDescriptions);
        setListAdapter(adapter);

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

    // Handle people clicking on options, because that's how we roll
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        switch(position) {
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
}
