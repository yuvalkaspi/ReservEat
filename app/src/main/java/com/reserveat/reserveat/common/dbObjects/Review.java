package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Review {

    private int busyRate;
    private float rate;
    private String userId;

    private static final int BUSY_RATE = 1;
    private static final int RATE = 2;


    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Review(HashMap<Integer, Float> userAnswers, String userId) {
        this.busyRate = userAnswers.get(BUSY_RATE).intValue();
        this.rate = userAnswers.get(RATE);
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("busyRate", busyRate);
        result.put("rate", rate);
        result.put("userId", userId);

        return result;
    }

    public int getBusyRate() { return busyRate; }

    public float getRate() { return rate; }

}
