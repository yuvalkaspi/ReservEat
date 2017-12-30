package com.reserveat.reserveat.common;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Golan on 02/12/2017.
 */

public class NotificationRequest implements Serializable {

    private String uid;
    private String restaurant;
    private String branch;
    private String placeId;
    private String fullDate;
    private int numOfPeople;
    private boolean isFlexible;

    public NotificationRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public NotificationRequest(String uid, String restaurant, String branch, String placeID, String fullDate, int numOfPeople, boolean isFlexible) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.branch = branch;
        this.placeId = placeID;
        this.fullDate = fullDate;
        this.numOfPeople = numOfPeople;
        this.isFlexible = isFlexible;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("restaurant", restaurant);
        result.put("branch", branch);
        result.put("placeId", placeId);
        result.put("date", fullDate);
        result.put("numOfPeople", numOfPeople);
        result.put("isFlexible", isFlexible);

        return result;
    }

    public String getRestaurant(){
        return this.restaurant;
    }

    public String getBranch(){
        return this.branch;
    }

    public String getPlaceId(){
        return this.placeId;
    }

    public String getFullDate(){
        return this.fullDate;
    }

    public int getNumOfPeople(){
        return this.numOfPeople;
    }



}


