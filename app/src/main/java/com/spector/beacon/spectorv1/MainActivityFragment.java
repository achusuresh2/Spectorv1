package com.spector.beacon.spectorv1;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
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
        Button doctorButton = (Button)rootView.findViewById(R.id.spector_button);
        Button manageButton = (Button)rootView.findViewById(R.id.manage_button);

        SessionDetails sessionDetails = ((SpectorApp)getActivity().getApplicationContext()).getSessionDetails();
        if (sessionDetails.getAppID() == null) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(loginIntent, 1);
        }

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

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"This feature is not yet available either!", Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            SessionDetails sessionDetails = ((SpectorApp)getActivity().getApplicationContext()).getSessionDetails();
            Toast.makeText(getActivity(), "Login Success. App Token: " + sessionDetails.getAppToken(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Login Failed. Application would have terminated", Toast.LENGTH_LONG).show();
        }

    }
}
