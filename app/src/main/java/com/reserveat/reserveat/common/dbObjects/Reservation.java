package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.reserveat.reserveat.common.utils.DateUtils.*;


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
    private String day;
    private String timeOfDay;
    private boolean isReviewed;
    private boolean isSpam;
    private String SeattingArea;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String restaurant, String branch, String placeId, String fullDate, int numOfPeople, String reservationName, int hotness, Day day, TimeOfDay timeOfDay, String SeattingArea, boolean isSpam) {
        this.uid = uid;
        this.pickedByUid = "none"; // new reservation is not picked yet
        this.restaurant = restaurant;
        this.branch = branch;
        this.fullDate = fullDate;
        this.numOfPeople = numOfPeople;
        this.reservationName = reservationName;
        this.hotness = hotness;
        this.placeId = placeId;
        this.day = day.name();
        this.timeOfDay = timeOfDay.name();
        this.SeattingArea = SeattingArea;
        this.isSpam = isSpam;
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
        result.put("day", day);
        result.put("timeOfDay", timeOfDay);
        result.put("SeattingArea", SeattingArea);
        result.put("isSpam", isSpam);

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

    public void setPickedByUid(String pickedByUid) { this.pickedByUid = pickedByUid; }

    public String getPickedByUid() { return pickedByUid; }

    public String getDay() {
        return day;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

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

    public int getHotness() {
        return hotness;
    }

    public void setHotness(int hotness) {
        this.hotness = hotness;
    }

    public String getReservationName() {
        return reservationName;
    }

    public boolean isPicked() {
        return (!"none".equals(pickedByUid));
    }

    public boolean getIsReviewed() { return isReviewed; }

    public String getSeattingArea() {
        return SeattingArea;
    }

    public boolean getIsSpam() { return isSpam; }

    public void setIsSpam(boolean spam) { isSpam = spam; }

}