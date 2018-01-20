package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.Restaurant;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DateUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class ReservationDetailsDialog extends ReservationContentDialog {

    public static int numOfStarsPerPick = 2;
    private static final String TAG = "ReservationDetailsDlg";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.reservation_details_dialog, null);

        ImageButton closeButton = (ImageButton) root.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        fillReservationDetails(root);

        builder.setView(root);

        dialog = builder.create();
        return dialog;
    }

    private void fillReservationDetails(final View view) {

        setDetail(reservation.getRestaurant(), R.id.resturant_detail, view);
        setDetail(reservation.getBranch(), R.id.branch_detail, view);

        String date = reservation.getDate();
        String dateNewFormat = null;
        String hour = null;

        if (date != null && !date.equals("")) {
            int indexOfSpace = date.indexOf(" ");
            String dateOldFormat = date.substring(0, indexOfSpace);
            hour = date.substring(indexOfSpace + 1);
            try {
                dateNewFormat = DateUtils.switchDateFormat(dateOldFormat, DateUtils.dateFormatDB, DateUtils.dateFormatUser);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setDetail(dateNewFormat, R.id.date_detail, view);
            setDetail(hour, R.id.hour_detail, view);
        }

        setDetail(Integer.toString(reservation.getNumOfPeople()), R.id.num_of_people_detail, view);
        setDetail(reservation.getSeattingArea(), R.id.seatting_area_detail, view);
        fillPhoneDetails(view, reservation);

        if (isPickable) {
            Button pickButton = (Button) view.findViewById(R.id.pick_Button);
            pickButton.setVisibility(View.VISIBLE);
            pickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    pickClick(reservation, view);
                }
            });
        } else {
            fillReservationHiddenDetails(view, reservation);
        }

    }

    private void fillReservationHiddenDetails(View view, Reservation reservation) {
        if (!isMyReservations) {
            setDetailAndVisibility("Reservation name is : ", R.id.name_form_detail, view);
            setDetailAndVisibility(reservation.getReservationName(), R.id.name_detail, view);
        }

        LinearLayout phoneLayout = view.findViewById(R.id.phoneLayout);
        TextView phoneTextView = view.findViewById(R.id.phone_detail);
        if (!phoneTextView.getText().equals("")) {
            phoneLayout.setVisibility(View.VISIBLE);
        }

        if (!phoneTextView.getText().equals(""))
            phoneLayout.setVisibility(View.VISIBLE);
    }

    private void setDetailAndVisibility(String detail, int textViewId, View detailsView) {
        TextView textView = detailsView.findViewById(textViewId);
        textView.setText(detail);
        textView.setVisibility(View.VISIBLE);
    }


    private void setDetail(String detail, int textViewId, View detailsView) {
        TextView textView = detailsView.findViewById(textViewId);
        textView.setText(detail);
    }


    public void fillPhoneDetails(final View view, Reservation reservation) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("restaurants").child(reservation.getPlaceId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "get restaurant:success");
                final Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                TextView phoneTextView = (TextView) view.findViewById(R.id.phone_detail);
                phoneTextView.setText(restaurant.getPhoneNumber());

                phoneTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //request call permission if necessary
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                            return;
                        }
                        getActivity().getApplication().getApplicationContext().startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "get restaurant:failure");
            }
        });
    }


    public void pickClick(final Reservation reservation, final View view) {

        String userId = DBUtils.getCurrentUserID();
        final Button pickButton = (Button) view.findViewById(R.id.pick_Button);

        if (reservation.getUid().equals(userId)) {// My reservation
            Toast.makeText(getActivity(), "You can't pick your own reservation", Toast.LENGTH_LONG).show();
            return;
        }

        final TextView noteTextView = (TextView) view.findViewById(R.id.note_detail);


        reservation.setPickedByUid(userId);
        Map<String, Object> reservationValues = reservation.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + userId + "/pickedReservations/" + key, reservationValues);
        childUpdates.put("/users/" + reservation.getUid() + "/reservations/" + key, reservationValues);
        childUpdates.put("/historyReservations/" + key, reservationValues);
        childUpdates.put("/reservations/" + key, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "pick reservation:success", task.getException());

                    pickButton.setVisibility(View.GONE);
                    noteTextView.setVisibility(View.VISIBLE);
                    fillReservationHiddenDetails(view, reservation);

                    DBUtils.updateStarsToUser(numOfStarsPerPick, reservation.getUid());
                    DBUtils.updateReliabilityToUser(reservation.getUid(), reservation.getHotness());
                } else {
                    Log.w(TAG, "pick reservation:failure", task.getException());
                    Toast.makeText(getActivity(), "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
