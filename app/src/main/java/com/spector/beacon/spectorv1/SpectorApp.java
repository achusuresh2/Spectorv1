package com.spector.beacon.spectorv1;

import android.app.Application;

/**
 * Created by Hrishikesh on 5/23/2015.
 */
public class SpectorApp extends Application {

    private SessionDetails sessionDetails = new SessionDetails();

    public SessionDetails getSessionDetails() {
        return sessionDetails;
    }

    public void setSessionDetails(SessionDetails _sessionDetails) {
        sessionDetails = _sessionDetails;
    }
}


