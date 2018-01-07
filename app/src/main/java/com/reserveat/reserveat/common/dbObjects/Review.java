package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Review {

    private int busyRate;
    private float rate;

    private static final int BUSY_RATE = 1;
    private static final int RATE = 2;


    public Review(HashMap<Integer, Float> userAnswers) {
        this.busyRate = userAnswers.get(BUSY_RATE).intValue();
        this.rate = userAnswers.get(RATE);
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("busyRate", busyRate);
        result.put("rate", rate);

        return result;
    }
}
