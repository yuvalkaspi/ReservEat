package com.reserveat.reserveat.common.dialogFragment.contentDialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;


public class ContentBaseDialog extends DialogFragment {

    protected int titleStringId;
    protected int contentId;
    protected String key;
    Dialog dialog;
    private static final String TAG = "ContentBaseDialog";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleStringId = getArguments().getInt(DialogUtils.dialogTitle);
        contentId = getArguments().getInt(DialogUtils.dialogContent);
        key = getArguments().getString(DialogUtils.dialogKey);
    }


    protected void removeClick(Map<String, Object> removeFromDB) {
        DatabaseReference databaseReference = DBUtils.getDatabaseRef();

        databaseReference.updateChildren(removeFromDB).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
