package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.MyReservationsListActivity;
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.ReviewFormActivity;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

public class ReservationListDialog extends ReservationContentDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.actions_dialog, null);
        Button detailsButton = root.findViewById(R.id.details_button);
        Button removeButton = root.findViewById(R.id.remove_button);

        Button reviewButton = root.findViewById(R.id.review_button);
        final Button spamButton = root.findViewById(R.id.spam_button);
        ImageButton closeButton = root.findViewById(R.id.close_button);
        TextView title = root.findViewById(R.id.actions_title);

        title.setText(R.string.reservation);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if ((isMyReservations && !reservation.isPicked()) || reservation.getIsSpam())
            DialogUtils.makeButtonGrey(spamButton, getResources());
        else
            spamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spamButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            if (isMyReservations)
                                alertDialog.setMessage(getResources().getString(R.string.myResSpamInfo));
                            else
                                alertDialog.setMessage(getResources().getString(R.string.myPicksSpamInfo));

                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    spamClick(spamButton);
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alertDialog.show();
                        }
                    });

                }
            });


        if (!isMyReservations)
            removeButton.setVisibility(View.GONE);
        else {
            if (reservation.isPicked())
                DialogUtils.makeButtonGrey(removeButton, getResources());
            else
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.removeClick(dialog, "reservations", key, getActivity());
                    }
                });
        }

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReservationContentDialog newFragment = new ReservationDetailsDialog();
                ReservationContentDialog.initInstance(newFragment, key, reservation, isMyReservations, isPickable);
                newFragment.show(getFragmentManager(), "ReservationDetailsDialog");
                dialog.dismiss();
            }
        });


        if (isMyReservations)
            reviewButton.setVisibility(View.GONE);
        else {
            if (reservation.getIsReviewed())
                DialogUtils.makeButtonGrey(reviewButton, getResources());
            else {
                reviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewClick();
                    }
                });
            }
        }

        builder.setView(root);

        dialog = builder.create();
        return dialog;
    }

    private void reviewClick() {
        Intent intent = new Intent(getActivity(), ReviewFormActivity.class);
        intent.putExtra("reservation", reservation);
        intent.putExtra("reservationKey", key);
        startActivityForResult(intent, 0);
        dialog.dismiss();
    }


    private void spamClick(final Button spamButton) {

        String spammer;
        reservation.setIsSpam(true);
        if (isMyReservations) {
            // my reservation picker is spammer
            spammer = "picker";
            DBUtils.updateSpamToUser(reservation.getPickedByUid(), reservation.getUid(), "reservations", key);
        } else {
            // I picked reservation owner is spammer
            spammer = "reservation owner";
            DBUtils.updateSpamToUser(reservation.getUid(), reservation.getPickedByUid(), "pickedReservations", key);
        }

        DialogUtils.makeButtonGrey(spamButton, getResources());
        Toast.makeText(getActivity(), spammer + " is reported", Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }


}
