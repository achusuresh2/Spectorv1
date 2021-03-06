package com.spector.beacon.spectorv1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

import java.util.ArrayList;
import java.util.List;


public class EnrollBeacon extends ActionBarActivity {

    private static String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static Region ALL_ESTIMOTE_BEACONS = new Region("regionid", ESTIMOTE_PROXIMITY_UUID, null, null);

    private BeaconManager beaconManager = null;
    private String LOG_TAG = "";

    private BeaconConnection connection = null;
    private SessionDetails sessionDetails = null;

    private BeaconAdapter beaconAdapter;
    private ArrayList<Beacon> visibleBeacons = null;

    private boolean enrollDone = false;

    ListView listBeacon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_beacon);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_enroll_beacon, menu);
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

    @Override
    protected void onStart() {
        super.onStart();

        beaconManager = new BeaconManager(this);
        LOG_TAG = getClass().toString();

        final Context context = this;
        listBeacon = (ListView) findViewById(R.id.list_beacon_enroll);

        //This will enroll the selected beacon and overwrite its UUID
        listBeacon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Beacon selectedBeacon = beaconAdapter.getItem(position);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                visibleBeacons = new ArrayList<Beacon>();
                                visibleBeacons.add(selectedBeacon);
                                enrollBeacons(visibleBeacons);
                            }
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to enroll the beacon, " + selectedBeacon.getMacAddress() + "?").setPositiveButton("Yes", onClickListener)
                        .setNegativeButton("No", onClickListener).show();
            }
        });

        Button enrollAllButton = (Button)findViewById(R.id.enroll_all_button);
        enrollAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollBeacons(visibleBeacons);
            }
        });

        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

                //Show a list of beacons
                ArrayList<Beacon> beaconArrayList = new ArrayList<>(beacons);

                //Store beacons for enroll all button to work
                visibleBeacons = beaconArrayList;

                //Show a list of all ranged beacons
                if (beaconAdapter == null) {
                    beaconAdapter = new BeaconAdapter(context, R.layout.list_item_beacon, beaconArrayList);
                    listBeacon.setAdapter(beaconAdapter);
                } else {
                    beaconAdapter.clear();
                    beaconAdapter.addAll(beaconArrayList);
                }
            }
        });
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

    }

    private boolean enrollBeacons(ArrayList<Beacon> beacons) {
        enrollDone = false;
        sessionDetails = ((SpectorApp)getApplicationContext()).getSessionDetails();

        for (Beacon beacon: beacons) {
            connection = new BeaconConnection(this, beacon, createConnectionCallBack());
            showToast("Connecting to beacon...");
        }
        if (!connection.isConnected()) {
            connection.authenticate();
        }


        if (enrollDone) {
            return true;
        } else {
            return false;
        }

    }

    private BeaconConnection.ConnectionCallback createConnectionCallBack() {
        return new BeaconConnection.ConnectionCallback() {
            @Override
            public void onAuthenticated(final BeaconInfo beaconInfo) { runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("Connected to beacon: " + beaconInfo.macAddress);
                    connection.writeProximityUuid(sessionDetails.getProxUUID(), new BeaconConnection.WriteCallback() {
                        @Override
                        public void onSuccess() { runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("UUID Successfully updated! Beacon might no longer be visible here");
                                connection.close();
                            }
                        });
                        }

                        @Override
                        public void onError(final EstimoteDeviceException e) { runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("UUID update failed. " + e.getMessage());
                                connection.close();
                            }
                        });
                        }
                    });
                }
            });
            }

            @Override
            public void onAuthenticationError(final EstimoteDeviceException e) { runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("Error connecting to Beacon: " + e.getMessage());
                }
            });
            }

            @Override
            public void onDisconnected() { runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("Disconnected from Beacon");
                }
            });
            }
        };
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

