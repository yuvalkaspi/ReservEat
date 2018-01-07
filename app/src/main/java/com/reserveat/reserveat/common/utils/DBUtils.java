package com.reserveat.reserveat.common.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.common.dbObjects.Restaurant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Golan on 13/12/2017.
 */

public class DBUtils {

    private final static String RESTAURANT = "restaurant";
    private static final String TAG = "DBUtils";

    public static int maxNumOfStars = 3;


    private final static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getDatabaseRef() {
        return mDatabase;
    }

    public static String getCurrentUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /*
    Add stars to user and update starRemoveDate field
     */
    public static void updateStarsToUser(final int numOfStars) {

        final String userId = getCurrentUserID();
        final DatabaseReference userRef = mDatabase.child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currNumOfStars = dataSnapshot.child("stars").getValue(Integer.class);

                Map<String, Object> childUpdates = new HashMap<>();

                int finalNumOfStars = Math.min(maxNumOfStars, currNumOfStars + numOfStars);
                childUpdates.put("/users/" + userId + "/stars", finalNumOfStars);

                DateFormat dateFormat = new SimpleDateFormat(DateUtils.fullDateFormatDB, Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                Date date;
                if (currNumOfStars > 0) {//starRemoveDate exists
                    try {
                        date = dateFormat.parse(dataSnapshot.child("starRemoveDate").getValue(String.class));
                        cal.setTime(date);
                    } catch (ParseException e) {
                        Log.w(TAG, "Parse error- field starRemoveDate", e);
                    }
                }
                cal.add(Calendar.MONTH, numOfStars);
                childUpdates.put("/users/" + userId + "/starRemoveDate", dateFormat.format(cal.getTime()));

                mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Update stars to user: success");
                        } else {
                            Log.w(TAG, "Update stars to user: failure", task.getException());
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /*
    Add spamReport to user , spam logic is in backend
     */
    public static void updateSpamToUser(final String spammerUserId, final String reporterUserId,final String listToUpdate, final String key ) {

        final DatabaseReference userRef = mDatabase.child("users").child(spammerUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int currNumOfSpam = dataSnapshot.child("spamReports").getValue(Integer.class);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + spammerUserId + "/spamReports", currNumOfSpam+1);
                childUpdates.put("/users/" + reporterUserId + "/" + listToUpdate + "/" + key + "/isSpam", true);

                mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Update spam to user: success");
                        } else {
                            Log.w(TAG, "Update spam to user: failure", task.getException());
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static void addingPlaceToDB(Place place, final String TAG) {

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


    public static void initUser(String refreshedToken, String displayName, final Context context) {

        final String userId = getCurrentUserID();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + userId + "/instanceId", refreshedToken);
        childUpdates.put("/users/" + userId + "/stars", 0);
        childUpdates.put("/users/" + userId + "/spamReports", 0);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "initUser- database: success");
                } else {
                    Log.w(TAG, "initUser- database: failure", task.getException());
                }
            }
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "updateProfile:success");
                        } else {
                            Log.w(TAG, "updateProfile:failure", task.getException());
                            Toast.makeText(context, "Update failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(context, MainActivity.class );
                        context.startActivity(intent);
                    }
                });
        //todo: nullpointerexception?
    }
}