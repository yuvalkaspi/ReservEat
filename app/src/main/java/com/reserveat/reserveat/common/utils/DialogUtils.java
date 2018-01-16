package com.reserveat.reserveat.common.utils;

import android.os.Bundle;

import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.BaseChoiceDialog;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.ContentBaseDialog;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.NotificationRequestListDialog;

public class DialogUtils {

    public static String dialogTitle = "dialogTitle";
    public static String dialogContent = "dialogContent";
    public static String dialogIndex = "dialogIndex";
    public static String dialogKey = "dialogKey";
    public static String isMyReservation = "isMyReservations";
    public static String isSpam = "isSpam";
    public static String isPicked = "isPicked";
    public static String isReviewed = "isReviewed";
    public static String isPickable = "isPickable";

    public static void initChoiceDialog(BaseChoiceDialog dialog, int titleStringId, int contentId, int index){
        Bundle args = new Bundle();
        args.putInt(DialogUtils.dialogTitle, titleStringId);
        args.putInt(DialogUtils.dialogContent, contentId);
        args.putInt(DialogUtils.dialogIndex, index);
        dialog.setArguments(args);
    }

    public static void initContentDialog(ContentBaseDialog dialog, String key, boolean isMyReservations, boolean isSpam, boolean isPicked, boolean isReviewed, boolean isPickable){
        Bundle args = new Bundle();
        args.putString(DialogUtils.dialogKey, key);
        args.putBoolean(DialogUtils.isMyReservation, isMyReservations);
        args.putBoolean(DialogUtils.isSpam, isSpam);
        args.putBoolean(DialogUtils.isPicked, isPicked);
        args.putBoolean(DialogUtils.isReviewed, isReviewed);
        args.putBoolean(DialogUtils.isPickable, isPickable);
        dialog.setArguments(args);
    }
}
