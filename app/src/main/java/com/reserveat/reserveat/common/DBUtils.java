package com.reserveat.reserveat.common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Golan on 13/12/2017.
 */

public class DBUtils {

    private final static String RESTAURANT = "restaurant";


    private final static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference getDatabaseRef(){
        return mDatabase;
    }





}
