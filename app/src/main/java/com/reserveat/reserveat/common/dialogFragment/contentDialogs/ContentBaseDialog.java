package com.reserveat.reserveat.common.dialogFragment.contentDialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;


public class ContentBaseDialog extends DialogFragment {

    protected String key;
    protected boolean isMyReservations;
    protected boolean isSpam;
    protected boolean isPicked;
    protected boolean isReviewed;
    protected boolean isPickable;
    protected Dialog dialog;
    private static final String TAG = "ContentBaseDialog";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getArguments().getString(DialogUtils.dialogKey);
        isMyReservations = getArguments().getBoolean(DialogUtils.isMyReservation);
        isSpam = getArguments().getBoolean(DialogUtils.isSpam);
        isPicked = getArguments().getBoolean(DialogUtils.isPicked);
        isReviewed = getArguments().getBoolean(DialogUtils.isReviewed);
        isPickable = getArguments().getBoolean(DialogUtils.isPickable);
    }


    protected void removeClick(String location) {
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
                    Toast.makeText(getActivity() , "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void makeButtonGrey(Button button) {
        button.setTextColor(getResources().getColor(R.color.lightGray));
    }
}
