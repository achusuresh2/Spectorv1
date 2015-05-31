package com.spector.beacon.spectorv1;

import com.google.gson.Gson;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by tai on 23/05/15.
 */
public class BeaconHTTP {
    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("http://192.168.0.101:3000")
            .build();
    BeaconHTTPInterface service = restAdapter.create(BeaconHTTPInterface.class);

    public String getUser (String macAddr){
        return service.getUser(macAddr);
    }
    public List<String> listMacAddr(){
        return service.listMacAddr();
    }
    public String updateUser (String macAddr, String update){
        return service.updateMacAddrProp(macAddr, update);
    }
    public String deleteMacAddr (String macAddr) {
        return service.deleteMacAddr(macAddr);
    }
    public String createUser (String user){
        return service.createUser(user);
    }
}
