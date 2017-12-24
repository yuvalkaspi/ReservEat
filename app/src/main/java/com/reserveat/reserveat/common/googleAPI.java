package com.reserveat.reserveat.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.reserveat.reserveat.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Golan on 24/12/2017.
 */

public class googleAPI  {

    String researvEatApiKey = "AIzaSyBx0YhYvGzfBnLO1yOvS1OM04cW2vmEdmw";
    String searchRestaurant = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    String type = "&type=restaurant";
    String key = "key=";


    String getSearchRestaurantString(String restaurant, String branch){

        StringBuilder sb = new StringBuilder();
        sb.append(searchRestaurant);
        sb.append(restaurant).append("+").append(branch);
        sb.append(type);
        sb.append(key).append(researvEatApiKey);

        return sb.toString();

    }






}
