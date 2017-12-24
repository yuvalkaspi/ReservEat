package com.reserveat.reserveat.common;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.AddActivity;
import com.reserveat.reserveat.MainActivity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Golan on 13/12/2017.
 */

public class DBUtils {

    private final static String RESTAURANT = "restaurant";
    private static final String TAG = "DBUtils";


    private final static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getDatabaseRef(){
        return mDatabase;
    }

    public static String getCurrentUserID(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public static void updateStarsToUser(Long starts, String userID) {

        //Map<String, Integer> reviewValues = new HashMap<>();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + userID + "/stars", starts);

        DBUtils.getDatabaseRef().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new review: success", task.getException());
                }else{
                    Log.w(TAG, "add new review: failure", task.getException());
                }
            }
        });

    }


    public static void addingPlaceToDB(Place place,final String TAG) {

        Log.i(TAG, "adding a new reservation to DB");
        String key = place.getId();
        String restaurantName = place.getName().toString();
        String branch = place.getAddress().toString();
        String phoneNumber = place.getPhoneNumber().toString();
        Float rating = place.getRating();
        Integer priceLevel = place.getPriceLevel();

        Restaurant restaurant = new Restaurant(restaurantName, branch, phoneNumber, rating, priceLevel);
        Map<String, Object> restaurantValues = restaurant.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/restaurants/" + key, restaurantValues);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "add new restaurant:success", task.getException());
                } else {
                    Log.w(TAG, "add new restaurant:failure", task.getException());
                }
            }
        });


    }
}
