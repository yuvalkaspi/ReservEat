package com.reserveat.reserveat.common.dialogFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;

import com.reserveat.reserveat.R;


public abstract class OurDialogFragment extends DialogFragment{

    public static String dialogTitle = "dialogTitle";
    public static String dialogContent = "dialogContent";
    public static String dialogIndex = "dialogIndex";

    protected int titleStringId;
    protected int contentId;
    protected int index = -1;

    public static void initDialog(OurDialogFragment dialog, int titleStringId, int contentId, int index){
        Bundle args = new Bundle();
        args.putInt(OurDialogFragment.dialogTitle, titleStringId);
        args.putInt(OurDialogFragment.dialogContent, contentId);
        args.putInt(OurDialogFragment.dialogIndex, index);
        dialog.setArguments(args);
    }


    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    protected OurDialogFragment.NoticeDialogListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleStringId = getArguments().getInt(dialogTitle);
        contentId = getArguments().getInt(dialogContent);
        index = getArguments().getInt(dialogIndex);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ChoiceDialogFragment.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
