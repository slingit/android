package me.boopit.boopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jamie on 14/11/14.
 */
public class SetupArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] optionsValues;
    private final String[] optionsDescriptions;

    public SetupArrayAdapter(Context context, String[] optionsValues, String[] optionsDescriptions) {
        super(context, R.layout.setup_choices_list, optionsValues);
        this.context = context;
        this.optionsValues = optionsValues;
        this.optionsDescriptions = optionsDescriptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Define the layout inflater
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.setup_choices_list, parent, false);

        // Get all the different parts of the layout
        TextView optionsTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView descriptionsTextView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView accompImageView = (ImageView) rowView.findViewById(R.id.icon);

        // set the text of the options/descriptions TextViews
        optionsTextView.setText(optionsValues[position]);
        descriptionsTextView.setText(optionsDescriptions[position]);
        // and now set the values
        String fOOS = optionsValues[position];
        if(fOOS.startsWith("This")) {
            // set image view to first device icon
            accompImageView.setImageResource(R.drawable.setuplist_first_device);
        } else {
            // set image view to existing group icon
            accompImageView.setImageResource(R.drawable.setuplist_existing_group);
        }

        return rowView;
    }
}
