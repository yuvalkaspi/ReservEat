package com.reserveat.reserveat.common.dbObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Golan on 17/12/2017.
 */

@IgnoreExtraProperties
public class ReviewData implements Serializable {

    private String restaurant;
    private String branch;
    private String fullDate;



    public ReviewData(String restaurant, String branch, String fullDate){
        this.restaurant = restaurant;
        this.branch = branch;
        this.fullDate = fullDate;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getBranch() {
        return branch;
    }


    public Map<String,String> toMap() {

        HashMap<String, String> surveyValues = new HashMap<>();
        surveyValues.put("restaurant" , restaurant);
        surveyValues.put("branch" , branch);
        surveyValues.put("fullDate" , fullDate);

        return surveyValues;
    }
}
