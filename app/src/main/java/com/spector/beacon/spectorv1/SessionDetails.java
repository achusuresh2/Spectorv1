package com.spector.beacon.spectorv1;

/**
 * Created by Hrishikesh on 5/23/2015.
 */
public class SessionDetails {
    private String _appID = null;
    private String _appToken  = null;
    private String _proxUUID = null;

    public SessionDetails(String appID, String appToken, String proxUUID){
        _appID = appID;
        _appToken = appToken;
        _proxUUID = proxUUID;
    }

    public SessionDetails() {
    }

    public String getAppID() {
        return _appID;
    }

    public String getAppToken() {
        return _appToken;
    }

    public String getProxUUID() {
        return _proxUUID;
    }

}
