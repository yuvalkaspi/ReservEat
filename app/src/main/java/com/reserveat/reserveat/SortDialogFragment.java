package com.reserveat.reserveat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class SortDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int index);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int[] selectedItem = new int[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_by)
                .setSingleChoiceItems(R.array.SortByOptions, 0,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedItem[0] = which;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //todo
                        mListener.onDialogPositiveClick(SortDialogFragment.this, selectedItem[0]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(SortDialogFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


}
