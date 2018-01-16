package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.ReviewFormActivity;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

public class ReservationListDialog extends ContentBaseDialog {

    private String resToGet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        resToGet =  isMyReservations? "reservations" : "pickedReservations";

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

        if((isMyReservations && !isPicked) || isSpam)
            makeButtonGrey(spamButton);
        else
            spamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spamClick(spamButton);
                }
            });


        if(!isMyReservations)
            removeButton.setVisibility(View.GONE);
        else{
            if (isPicked)
                makeButtonGrey(removeButton);
            else
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeClick("reservations");
                    }
                });
        }

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentBaseDialog newFragment = new ReservationDetailsDialog();
                DialogUtils.initContentDialog(newFragment, key, isMyReservations, isSpam, isPicked, isReviewed, isPickable);
                newFragment.show(getFragmentManager(), "ReservationDetailsDialog");
                dialog.dismiss();
            }
        });


        if(isMyReservations)
            reviewButton.setVisibility(View.GONE);
        else{
            if(isReviewed)
                makeButtonGrey(reviewButton);
            else{
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

        final DatabaseReference reservationRef = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child(resToGet).child(key);

        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reservation reservation = dataSnapshot.getValue(Reservation.class);
                Intent intent = new Intent(getActivity(), ReviewFormActivity.class );
                intent.putExtra("reservation", reservation);
                intent.putExtra("reservationKey", key);
                startActivityForResult(intent , 0);
                dialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void spamClick(final Button spamButton) {

        final DatabaseReference reservationRef = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child(resToGet).child(key);

        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reservation reservation = dataSnapshot.getValue(Reservation.class);
                String spammer;
                reservation.setIsSpam(true);
                if(isMyReservations) {
                    // my reservation picker is spammer
                    spammer = "picker";
                    DBUtils.updateSpamToUser(reservation.getPickedByUid(), reservation.getUid(),"reservations", key);
                }else{
                    // I picked reservation owner is spammer
                    spammer = "reservation owner";
                    DBUtils.updateSpamToUser(reservation.getUid(), reservation.getPickedByUid(),"pickedReservations", key);
                }

                makeButtonGrey(spamButton);
                Toast.makeText(getActivity() ,spammer + " is reported", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}
