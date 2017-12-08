package com.reserveat.reserveat.common;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Reservation implements Serializable {

    private String uid;
    private String restaurant;
    private String branch;
    private String fullDate;
    private int numOfPeople;
    private String reservationName;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String restaurant, String branch, String fullDate, int numOfPeople, String reservationName) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.branch = branch;
        this.fullDate = fullDate;
        this.numOfPeople = numOfPeople;
        this.reservationName = reservationName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("restaurant", restaurant);
        result.put("branch", branch);
        result.put("date", fullDate);
        result.put("numOfPeople", numOfPeople);
        result.put("reservationName", reservationName);

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
        return fullDate;
    }

    public void setDate(String date) {
        this.fullDate = date;
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

}