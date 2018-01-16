package com.reserveat.reserveat.common.dialogFragment.contentDialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.reserveat.reserveat.common.dbObjects.NotificationRequest;


public abstract class NotificationContentDialog extends DialogFragment {

    public static String dialogKey = "dialogKey";

    protected String key;
    protected NotificationRequest notificationRequest;
    protected Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getArguments().getString(NotificationContentDialog.dialogKey);
    }

    public void setNotificationRequest(NotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    public static void initInstance(NotificationContentDialog dialog, String key, NotificationRequest notificationRequest) {
        Bundle args = new Bundle();
        args.putString(NotificationContentDialog.dialogKey, key);
        dialog.setArguments(args);
        dialog.setNotificationRequest(notificationRequest);
    }
}
