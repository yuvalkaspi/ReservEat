package com.reserveat.reserveat.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.reserveat.reserveat.R;
import com.reserveat.reserveat.SurveyForm;

/**
 * Created by Golan on 17/12/2017.
 */

public class DialogUtils {

    public static int[] showRadioButtonDialog(final int title, int options, final Activity activity) {
        final int[] selectedItem = new int[1];
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setSingleChoiceItems(options, 0,
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
                        // mListener.onDialogPositiveClick(SurveyForm.this, selectedItem[0]);
                        Toast.makeText(activity, "Saved!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener.onDialogNegativeClick(SurveyForm.this);
                    }
                });
        builder.create().show();;
        return selectedItem;
    }


    public static float[] showRatingDialog(final String title, final Activity activity, int layout){
        final float[] selectedRete = new float[1];
        final AlertDialog.Builder alertDialog =  new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        //inflating layout and finding RatingBar widget
        View root = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
        alertDialog.setView(root);
        final RatingBar ratingBar = (RatingBar)root.findViewById(R.id.ratingbar);
        alertDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                selectedRete[0] = ratingBar.getRating();
                //todo
                // mListener.onDialogPositiveClick(SurveyForm.this, selectedItem[0]);
                Toast.makeText(activity, "Saved!", Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
        return selectedRete;
    }

}
