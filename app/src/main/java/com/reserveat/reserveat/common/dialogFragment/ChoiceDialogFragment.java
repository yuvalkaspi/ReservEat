package com.reserveat.reserveat.common.dialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.reserveat.reserveat.R;


public class ChoiceDialogFragment extends OurDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int[] selectedItem = new int[1];
        selectedItem[0] = 1;//initializing
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titleStringId)
                .setSingleChoiceItems(contentId, 0,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedItem[0] = which + 1;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ChoiceDialogFragment.this ,index ,selectedItem[0]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(ChoiceDialogFragment.this);
                    }
                });

        return builder.create();
    }

}