package com.spector.beacon.spectorv1;

/**
 * Created by Hrishikesh on 5/30/2015.
 */
public class SpectorBeacon {

    private String patientName;
    private String patientID;
    private String location;
    private String privilege;
    private String baseStationID;
    private double distance;
    private String macAddress;

    //Default constructor
    public SpectorBeacon() {
    }

    public String getPatientName() {
        return patientName;
    }

    public double getDistance() {
        return distance;
    }

    public String getBaseStationID() {
        return baseStationID;
    }

    public String getLocation() {
        return location;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setBaseStationID(String baseStationID) {
        this.baseStationID = baseStationID;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
