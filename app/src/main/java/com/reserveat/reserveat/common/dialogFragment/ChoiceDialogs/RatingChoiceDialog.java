package com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.reserveat.reserveat.R;


public class RatingChoiceDialog extends BaseChoiceDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(contentId, null);
        final RatingBar ratingBar = (RatingBar)root.findViewById(R.id.ratingbar);

        builder.setTitle(titleStringId)
                .setView(root)
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(RatingChoiceDialog.this ,index ,ratingBar.getRating() );
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
