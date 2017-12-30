package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Review {

    private int busyWhenArrive;
    private int busyWhenLeft;
    private float rate;
    private String userId; //id of user who filled the review
    private String reservationId;


    private static final int BUSY_WHEN_ARRIVE = 1;
    private static final int BUSY_WHEN_LEFT = 2;
    private static final int RATE = 3;


    public Review(HashMap<Integer, Float> userAnswers) {
        this.busyWhenArrive = userAnswers.get(BUSY_WHEN_ARRIVE).intValue();
        this.busyWhenLeft = userAnswers.get(BUSY_WHEN_LEFT).intValue();
        this.rate = userAnswers.get(RATE);
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("busyWhenArrive", busyWhenArrive);
        result.put("busyWhenLeft", busyWhenLeft);
        result.put("rate", rate);

        return result;
    }
}
