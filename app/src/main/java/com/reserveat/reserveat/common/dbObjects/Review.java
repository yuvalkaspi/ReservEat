package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Review {

    private static final int BUSY_RATE = 1;
    private static final int RATE = 2;
    private static final int WAS_LINE = 3;
    private static final int BOOK_INADVANCE = 4;

    private int busyRate;
    private float rate;
    private int wasLine;
    private int needToBookInAdvance;
    private String userId;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Review(HashMap<Integer, Float> userAnswers, String userId) {
        this.busyRate = userAnswers.get(BUSY_RATE).intValue();
        this.rate = userAnswers.get(RATE);
        this.wasLine = userAnswers.get(WAS_LINE).intValue();
        this.needToBookInAdvance = userAnswers.get(BOOK_INADVANCE).intValue();
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("busyRate", busyRate);
        result.put("rate", rate);
        result.put("userId", userId);
        result.put("wasLine", wasLine);
        result.put("needToBookInAdvance", needToBookInAdvance);

        return result;
    }

    public int getBusyRate() { return busyRate; }

    public float getRate() { return rate; }

    public int getWasLine() { return wasLine; }

    public int getNeedToBookInAdvance() { return needToBookInAdvance; }

}
