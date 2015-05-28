package com.spector.beacon.spectorv1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class RangingActivityFragment extends Fragment {

    private static String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static Region ALL_ESTIMOTE_BEACONS = null;

    private BeaconManager beaconManager;
    private String LOG_TAG = "";

    private BeaconAdapter beaconAdapter;

    private ArrayList<Beacon> lastSeenBeacons = null;

    public RangingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_ranging, container, false);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get the organization UUID if available, otherwise use default - In production there will always be one.
        String loadedProxUUID = ((SpectorApp)getActivity().getApplicationContext()).getSessionDetails().getProxUUID();
        if (loadedProxUUID != "PROXUUID") {
            ESTIMOTE_PROXIMITY_UUID = loadedProxUUID;
        }
        ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

        //Initialize the beacon manager and get the Log Tag
        beaconManager = new BeaconManager(getActivity());
        LOG_TAG = getClass().toString();

        //Make sure the scanner scans for 200ms every 1 second
        beaconManager.setForegroundScanPeriod(200, 1000);

        //Make sure bluetooth is on
        if (beaconManager.isBluetoothEnabled() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Activity.RESULT_OK);
        }

        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                //Beacon ranging code goes here

                //Show a list of beacons
                ArrayList<Beacon> beaconArrayList = new ArrayList<>(beacons);
                if (beaconAdapter == null) {
                    beaconAdapter = new BeaconAdapter(getActivity(), R.layout.list_item_beacon, beaconArrayList);
                    final ListView listBeacon = (ListView)getView().findViewById(R.id.list_beacon);
                    listBeacon.setAdapter(beaconAdapter);
                } else {
                    beaconAdapter.clear();
                    beaconAdapter.addAll(beaconArrayList);
                }

                //Send beacons and their distances back to the server
                JSONArray beaconsJSON = new JSONArray();
                for (Beacon beacon: beaconArrayList) {
                    JSONObject beaconJSON = new JSONObject();
                    try {
                        beaconJSON.put("MAC",beacon.getMacAddress());
                        beaconJSON.put("BASE STATION","Random Station ID");
                        beaconJSON.put("DISTANCE",Utils.computeAccuracy(beacon));
                    } catch (JSONException ex) {
                        Log.e(LOG_TAG, "JSON ERROR: " + ex.getMessage());
                    }
                    beaconsJSON.put(beaconJSON);
                }

                if (lastSeenBeacons != null) {
                    for (Beacon beacon: lastSeenBeacons) {
                        if (beaconArrayList.contains(beacon) == false){
                            //Code to add lost beacons to JSON beacon collection
                            JSONObject beaconJSON = new JSONObject();
                            try {
                                beaconJSON.put("MAC",beacon.getMacAddress());
                                beaconJSON.put("BASE STATION","Random Station ID");
                                beaconJSON.put("DISTANCE",-1);
                            } catch (JSONException ex) {
                                Log.e(LOG_TAG, "JSON ERROR: " + ex.getMessage());
                            }
                            beaconsJSON.put(beaconJSON);
                        }
                    }
                }

                //New Async Task to send beacons to server
                //would go here


                //Store just seen beacons for next scan
                lastSeenBeacons = beaconArrayList;

            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                //This section can be used when a new beacon enters the scene
            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });

        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    Toast.makeText(getActivity(), "Scanning...", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

    }
}