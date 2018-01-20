package com.reserveat.reserveat.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

    public static void removeClick(final Dialog dialog, final String location, final String key, final Context context) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + DBUtils.getCurrentUserID() + "/" + location + "/" + key, null);
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

    public static void makeButtonGrey(Button button, final Resources resources, final int msg) {
        button.setTextColor(resources.getColor(R.color.lightGray));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext() , resources.getString(msg), Toast.LENGTH_LONG).show();
            }
        });
    }
}
