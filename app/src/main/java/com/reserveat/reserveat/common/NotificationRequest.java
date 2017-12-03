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
    private String date;
    private String hour;
    private int numOfPeople;
    private boolean isFlexible;

    public NotificationRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public NotificationRequest(String uid, String restaurant, String date, String hour, int numOfPeople, boolean isFlexible ) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.date = date;
        this.hour = hour;
        this.numOfPeople = numOfPeople;
        this.isFlexible = isFlexible;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("restaurant", restaurant);
        result.put("date", date);
        result.put("hour", hour);
        result.put("numOfPeople", numOfPeople);
        result.put("isFlexible", isFlexible);

        return result;
    }

    public String getRestaurant(){
        return this.restaurant;
    }

    public String getDate(){
        return this.date;
    }

    public String getHour(){
        return this.hour;
    }

    public int getNumOfPeople(){
        return this.numOfPeople;
    }



}


