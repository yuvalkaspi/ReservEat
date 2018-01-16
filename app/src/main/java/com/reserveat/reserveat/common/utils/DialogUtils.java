package com.reserveat.reserveat.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.R;

import java.util.HashMap;
import java.util.Map;

public class DialogUtils {

    private static final String TAG = "DialogUtils";

    public static void removeClick(final Dialog dialog, String location, String key, final Context context) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + DBUtils.getCurrentUser() + "/" + location + "/" + key, null);
        childUpdates.put( "/" + location + "/" + key, null);
        DatabaseReference popUpDatabase = FirebaseDatabase.getInstance().getReference();

        popUpDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "removeClick:success", task.getException());
                    dialog.dismiss();

                } else {
                    Log.w(TAG, "removeClick:failure", task.getException());
                    Toast.makeText(context , "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void makeButtonGrey(Button button, Resources resources) {
        button.setTextColor(resources.getColor(R.color.lightGray));
    }
}
