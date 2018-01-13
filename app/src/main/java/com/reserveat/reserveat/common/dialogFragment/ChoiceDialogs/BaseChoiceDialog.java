package com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;

import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.List;

public abstract class BaseChoiceDialog extends DialogFragment{

    protected int titleStringId;
    protected int contentId;
    protected int index = -1;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result);
        void onDialogPositiveClickMultipleChoice(DialogFragment dialog, int dialogIndex, List<Integer> mSelectedItems);
    }

    protected BaseChoiceDialog.NoticeDialogListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleStringId = getArguments().getInt(DialogUtils.dialogTitle);
        contentId = getArguments().getInt(DialogUtils.dialogContent);
        index = getArguments().getInt(DialogUtils.dialogIndex);
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
