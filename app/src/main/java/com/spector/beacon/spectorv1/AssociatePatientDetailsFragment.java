package com.spector.beacon.spectorv1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.cloud.model.BroadcastingPower;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class AssociatePatientDetailsFragment extends Fragment {

    private String LOG_TAG;
    private  Beacon workingBeacon;
    BeaconConnection connection;
    private boolean broadcastWriteSuccess = false;
    private boolean serverUpdateSuccess = false;
    private FragmentActivity parentActivity;

    public AssociatePatientDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_associate_patient_details, container, false);
        parentActivity = getActivity();
        Button submitButton = (Button)v.findViewById(R.id.submit_button);
        Button cancelButton = (Button)v.findViewById(R.id.cancel_button);
        final RadioGroup mobilityGroup = (RadioGroup)v.findViewById(R.id.mobility_group);
        final EditText nameText = (EditText)v.findViewById(R.id.name_text);
        final EditText locationText = (EditText)v.findViewById(R.id.location_text);
        LOG_TAG = getClass().toString();
        workingBeacon = (Beacon)parentActivity.getIntent().getExtras().get("BEACON");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject patientInfoJSON = new JSONObject();
                try {
                    patientInfoJSON.put("NAME", nameText.getText().toString());
                    patientInfoJSON.put("LOCATION", locationText.getText().toString());
                    int radioButtonID = mobilityGroup.getCheckedRadioButtonId();
                    switch (radioButtonID) {
                        case R.id.low_button:
                            patientInfoJSON.put("MOBILITY", "LOW");
                            adjustBroadcastingPower(BroadcastingPower.LEVEL_1);
                            break;
                        case R.id.medium_button:
                            patientInfoJSON.put("MOBILITY","MEDIUM");
                            adjustBroadcastingPower(BroadcastingPower.LEVEL_4);
                            break;
                        case R.id.high_button:
                            patientInfoJSON.put("MOBILITY","HIGH");
                            adjustBroadcastingPower(BroadcastingPower.LEVEL_8);
                            break;
                    }
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, "Could not create patient data object. " + ex.getMessage());
                }

                //HTTP Class Code here
                String patientInfoJSONString = patientInfoJSON.toString();
                makeToast(patientInfoJSONString);
                sendUpdateToServer(patientInfoJSONString);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.setResult(-1);
                parentActivity.finish();
            }
        });

        return v;
    }

    private void makeToast(String message) {
        Toast.makeText(parentActivity, message, Toast.LENGTH_LONG).show();
    }

    private void adjustBroadcastingPower(final BroadcastingPower power) {
        connection = new BeaconConnection(parentActivity, workingBeacon, new BeaconConnection.ConnectionCallback() {
            @Override
            public void onAuthenticated(BeaconInfo beaconInfo) { parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connection.writeBroadcastingPower(power, new BeaconConnection.WriteCallback() {
                        @Override
                        public void onSuccess() {
                            parentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    broadcastWriteSuccess = true;
                                    connection.close();
                                }
                            });

                        }

                        @Override
                        public void onError(final EstimoteDeviceException e) {
                            parentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOG_TAG, "There was an error adjusting patient mobility. " + e.getMessage());
                                    broadcastWriteSuccess = false;
                                    connection.close();
                                }
                            });

                        }
                    });
                }
            });

            }

            @Override
            public void onAuthenticationError(final EstimoteDeviceException e) { parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(LOG_TAG, "Could not connection to beacon. " + e.getMessage());
                    makeToast("Could not connect to beacon. " + e.getMessage());
                }
            });

            }

            @Override
            public void onDisconnected() {parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (broadcastWriteSuccess) {
                        makeToast("Patient mobility was updated!");
                    }
                }
            });

            }
        });

        connection.authenticate();
    }

    private void sendUpdateToServer(String update) {
        serverUpdateSuccess= true; //Until we have the real method

        if (serverUpdateSuccess && broadcastWriteSuccess) {
            parentActivity.setResult(1);
            parentActivity.finish();
        }
    }
}
