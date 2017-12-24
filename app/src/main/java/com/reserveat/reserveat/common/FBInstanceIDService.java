package com.reserveat.reserveat.common;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FBInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FBInstanceIDService";
    DatabaseReference mDatabase;
    FirebaseUser currentUser;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(currentUser.getUid()).child("instanceId").setValue(refreshedToken);
        }

    }

}
