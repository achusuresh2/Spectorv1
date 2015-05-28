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

    public Gson getUser (String macAddr){
        return service.getUser(macAddr);
    }
    public List<Gson> listMacAddr(){
        return service.listMacAddr();
    }
    public Gson updateUser (String macAddr, Gson update){
        return service.updateMacAddrProp(macAddr, update);
    }
    public Gson deleteMacAddr (String macAddr) {
        return service.deleteMacAddr(macAddr);
    }
    public Gson createUser (Gson user){
        return service.createUser(user);
    }
}
