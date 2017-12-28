package com.reserveat.reserveat.common;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Golan on 24/12/2017.
 */

class Restaurant {

    private String restaurantName;
    private String branch;
    private String phoneNumber;
    private Float rating;
    private Integer priceLevel;

    public Restaurant(String restaurantName, String branch, String phoneNumber, Float rating, Integer priceLevel) {
        this.restaurantName = restaurantName;
        this.branch = branch;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.priceLevel = priceLevel;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getBranch() {
        return branch;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Float getRating() {
        return rating;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("restaurantName", restaurantName);
        result.put("branch", branch);
        result.put("phoneNumber", phoneNumber);
        result.put("branch", branch);
        result.put("rating", rating);
        result.put("priceLevel", priceLevel);

        return result;
    }
}