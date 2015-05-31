package com.spector.beacon.spectorv1;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

/**
 * Created by Hrishikesh on 5/23/2015.
 */
public class SpectorApp extends Application {

    private SessionDetails sessionDetails = new SessionDetails();
    private String BaseStationID = null;

    public SessionDetails getSessionDetails() {
        return sessionDetails;
    }

    public String getBaseStationID() {
        return BaseStationID;
    }

    public void setSessionDetails(SessionDetails _sessionDetails) {
        sessionDetails = _sessionDetails;
        EstimoteSDK.initialize(this, sessionDetails.getAppID(), sessionDetails.getAppToken());
        EstimoteSDK.enableDebugLogging(true);
    }

    public void setBaseStationID(String baseStationID) {
        BaseStationID = baseStationID;
    }
}


