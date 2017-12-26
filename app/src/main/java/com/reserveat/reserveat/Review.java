package com.reserveat.reserveat;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Golan on 18/12/2017.
 */

class Review {

    private int busyWhenArrive;
    private int busyWhenLeft;
    private float rate;

    private static final int BUSY_WHEN_ARRIVE = 1;
    private static final int BUSY_WHEN_LEFT = 2;
    private static final int RATE = 3;


    public Review(HashMap<Integer, Object> userAnswers) {
        this.busyWhenArrive = (Integer)userAnswers.get(BUSY_WHEN_ARRIVE);
        this.busyWhenLeft = (Integer)userAnswers.get(BUSY_WHEN_LEFT);
        this.rate = (Float)userAnswers.get(RATE);
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
