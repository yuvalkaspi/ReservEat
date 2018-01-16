package com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;

import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.List;

public abstract class BaseChoiceDialog extends DialogFragment{

    public static String dialogTitle = "dialogTitle";
    public static String dialogContent = "dialogContent";
    public static String dialogIndex = "dialogIndex";

    protected int titleStringId;
    protected int contentId;
    protected int index = -1;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result);
        void onDialogPositiveClickMultipleChoice(DialogFragment dialog, int dialogIndex, List<Integer> mSelectedItems);
    }

    protected BaseChoiceDialog.NoticeDialogListener mListener;


    public static void initInstance(BaseChoiceDialog dialog, int titleStringId, int contentId, int index){
        Bundle args = new Bundle();
        args.putInt(BaseChoiceDialog.dialogTitle, titleStringId);
        args.putInt(BaseChoiceDialog.dialogContent, contentId);
        args.putInt(BaseChoiceDialog.dialogIndex, index);
        dialog.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleStringId = getArguments().getInt(BaseChoiceDialog.dialogTitle);
        contentId = getArguments().getInt(BaseChoiceDialog.dialogContent);
        index = getArguments().getInt(BaseChoiceDialog.dialogIndex);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BaseChoiceDialog.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
