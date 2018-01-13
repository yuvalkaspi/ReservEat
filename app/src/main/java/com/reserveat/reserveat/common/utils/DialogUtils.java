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

    public static void initChoiceDialog(BaseChoiceDialog dialog, int titleStringId, int contentId, int index){
        Bundle args = new Bundle();
        args.putInt(DialogUtils.dialogTitle, titleStringId);
        args.putInt(DialogUtils.dialogContent, contentId);
        args.putInt(DialogUtils.dialogIndex, index);
        dialog.setArguments(args);
    }

    public static void initContentDialog(ContentBaseDialog dialog, int titleStringId, int contentId, String key){
        Bundle args = new Bundle();
        args.putInt(DialogUtils.dialogTitle, titleStringId);
        args.putInt(DialogUtils.dialogContent, contentId);
        args.putString(DialogUtils.dialogKey, key);
        dialog.setArguments(args);
    }
}
