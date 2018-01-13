package com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.reserveat.reserveat.R;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceDialog extends BaseChoiceDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Integer> mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titleStringId)
                .setMultiChoiceItems(contentId, null ,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                int selectedItem = which + 1;
                                if (isChecked) {
                                    mSelectedItems.add(selectedItem);
                                } else if (mSelectedItems.contains(selectedItem)) {
                                    mSelectedItems.remove(Integer.valueOf(selectedItem));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClickMultipleChoice(MultipleChoiceDialog.this, index, mSelectedItems);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });

        return builder.create();
    }
}
