package com.spector.beacon.spectorv1;

import com.estimote.sdk.repackaged.retrofit_v1_9_0.retrofit.http.Body;
import com.google.gson.Gson;

import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by tai on 23/05/15.
 */
public interface BeaconHTTPInterface {
    /**
     * gets the user of the mac address corresponding to the mac address
     * @param macAddr mac address
     * @return
     */
    @GET("/api/users/{user}")
    Gson getUser(@Path("user") String macAddr);

    /**
     * Returns list of all macaddress
     * @return Gson
     */
    @GET("/api/users/")
    List<Gson> listMacAddr();

    /**
     * @param macAddr the macAddress of the beacon
     * @return the beacon if it is successful
     */
    @DELETE("/api/users/{user}")
    Gson deleteMacAddr(@Path("user") String macAddr);

    /**
     *
     * @param macAddr
     * @param update
     * @return
     */
    @POST("/api/user/{user}")
    Gson updateMacAddrProp(@Path("user") String macAddr, @Body Gson update);

    @POST("/api/users/")
    Gson createUser(@Body Gson user);
}