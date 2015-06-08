package com.spector.beacon.spectorv1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.exception.EstimoteException;

import java.util.List;


public class AssociatePatient extends ActionBarActivity {

    private static String ESTIMOTE_PROXIMITY_UUID = null;
    private static Region ALL_ESTIMOTE_BEACONS = null;
    private BeaconManager beaconManager;
    private String LOG_TAG = "";
    private SessionDetails sessionDetails = null;
    private Context context = null;
    private Beacon closestBeacon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_patient);

        //Set up all the default variables
        sessionDetails = ((SpectorApp)getApplicationContext()).getSessionDetails();
        ESTIMOTE_PROXIMITY_UUID = sessionDetails.getProxUUID();
        ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
        beaconManager = new BeaconManager(this);
        LOG_TAG = getClass().toString();
        context = this;
        Button associateButton = (Button)findViewById(R.id.associate_patient_button);


        //Make sure bluetooth is on
        if (beaconManager.isBluetoothEnabled() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Activity.RESULT_OK);
        }

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                Double cBeaconDist = 100.00;

                for (Beacon b: list) {
                    if (Utils.computeAccuracy(b) < cBeaconDist) {
                        closestBeacon = b;
                        cBeaconDist = Utils.computeAccuracy(b);
                    }
                }

                TextView cBeaconText = (TextView)findViewById(R.id.closest_text);
                if (closestBeacon != null) {
                    cBeaconText.setText("Mac Address: " + closestBeacon.getMacAddress()
                            + "\nMajor: " + closestBeacon.getMajor() + "\n Minor: " + closestBeacon.getMinor());
                }

            }
        });

        associateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closestBeacon != null) {
                    Intent patientDetailsIntent = new Intent(context, AssociatePatientDetails.class);
                    patientDetailsIntent.putExtra("BEACON",closestBeacon);
                    startActivityForResult(patientDetailsIntent, 1);
                } else {
                    Toast.makeText(context, "There are no beacons nearby to associate...", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    Toast.makeText(context, "Looking for beacons...", Toast.LENGTH_SHORT).show();
                } catch (RemoteException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_associate_patient, menu);
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
}
