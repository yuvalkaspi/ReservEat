package com.reserveat.reserveat.common;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Reservation implements Serializable {

    private String uid;
    private String pickedByUid;
    private String restaurant;
    private String branch;
    private String fullDate;
    private int numOfPeople;
    private String reservationName;
    private String placeId;
    private int hotness;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String restaurant, String branch, String placeId, String fullDate, int numOfPeople, String reservationName, int hotness) {
        this.uid = uid;
        this.pickedByUid = "none"; // new reservation is not picked yet
        this.restaurant = restaurant;
        this.branch = branch;
        this.fullDate = fullDate;
        this.numOfPeople = numOfPeople;
        this.reservationName = reservationName;
        this.hotness = hotness;
        this.placeId = placeId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("pickedByUid", pickedByUid);
        result.put("restaurant", restaurant);
        result.put("branch", branch);
        result.put("date", fullDate);
        result.put("numOfPeople", numOfPeople);
        result.put("reservationName", reservationName);
        result.put("placeId", placeId);
        result.put("hotness", hotness);

        return result;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getUid() {
        return uid;
    }

    public void setPicker(String pickedByUid) { this.pickedByUid = pickedByUid; }

    public void setPlaceId(String placeId) { this.placeId = placeId; }

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