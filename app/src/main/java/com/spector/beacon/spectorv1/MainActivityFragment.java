package com.spector.beacon.spectorv1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button rangingButton = (Button)rootView.findViewById(R.id.ranging_button);
        Button doctorButton = (Button)rootView.findViewById(R.id.doctor_button);

        rangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rangingActivityIntent = new Intent(getActivity(), RangingActivity.class);
                startActivity(rangingActivityIntent);
            }
        });

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"This feature is not yet available!", Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}
