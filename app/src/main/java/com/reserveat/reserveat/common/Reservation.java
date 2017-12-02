package com.reserveat.reserveat.common;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 12/1/2017.
 */

@IgnoreExtraProperties
public class Reservation implements Serializable {

    private String uid;
    private String restaurant;
    private String branch;
    private String date;
    private String hour;
    private int numOfPeople;
    private String reservationName;
    private String OtherInfo;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String restaurant, String branch, String date, String hour, int numOfPeople, String reservationName, String OtherInfo) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.branch = branch;
        this.date = date;
        this.hour = hour;
        this.numOfPeople = numOfPeople;
        this.reservationName = reservationName;
        this.OtherInfo = OtherInfo;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("restaurant", restaurant);
        result.put("branch", branch);
        result.put("date", date);
        result.put("hour", hour);
        result.put("numOfPeople", numOfPeople);
        result.put("reservationName", reservationName);
        result.put("other info", OtherInfo);

        return result;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }

    public String getOtherInfo() {
        return OtherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        OtherInfo = otherInfo;
    }
}