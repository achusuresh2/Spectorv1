package com.spector.beacon.spectorv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hrishikesh on 5/22/2015.
 */

public class BeaconAdapter extends ArrayAdapter<Beacon> {
    public BeaconAdapter(Context context, int resource, ArrayList<Beacon> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v==null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_beacon, null);
        }

        Beacon beacon = getItem(position);

        if (beacon != null) {
            TextView uuidText = (TextView)v.findViewById(R.id.uuid_text);
            TextView majorText = (TextView)v.findViewById(R.id.major_text);
            TextView minorText = (TextView)v.findViewById(R.id.minor_text);
            TextView distText = (TextView)v.findViewById(R.id.distance_text);

            if (uuidText != null) {
                uuidText.setText("Beacon: " + beacon.getProximityUUID());
            }

            if (majorText != null) {
                majorText.setText("Major: " + beacon.getMajor());
            }

            if (minorText != null) {
                minorText.setText("Minor: " + beacon.getMinor());
            }

            if (distText != null) {
                double roundDistance = (double) Math.round(Utils.computeAccuracy(beacon) * 100) / 100;
                distText.setText("Distance: " + roundDistance + "m");
            }
        }

        return v;
    }

    @Override
    public void clear() {
        super.clear();
    }
}