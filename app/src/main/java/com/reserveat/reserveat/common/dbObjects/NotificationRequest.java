package com.reserveat.reserveat.common.dbObjects;

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
    private String description;
    private boolean isActive;

    public NotificationRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public NotificationRequest(String uid, String restaurant, String branch, String placeID, String fullDate, int numOfPeople, boolean isFlexible, String description) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.branch = branch;
        this.placeId = placeID;
        this.fullDate = fullDate;
        this.numOfPeople = numOfPeople;
        this.isFlexible = isFlexible;
        this.description = description;
        this.isActive = true;
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
        result.put("description", description);
        result.put("isActive", isActive);

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

    public String getDate(){
        return this.fullDate;
    }

    public boolean getIsFlexible(){
        return this.isFlexible;
    }

    public boolean getIsActive(){return this.isActive;}

    public int getNumOfPeople(){
        return this.numOfPeople;
    }

    public String getDescription(){
        return this.description;
    }



}


