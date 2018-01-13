package com.reserveat.reserveat.common.dialogFragment.contentDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.dbObjects.NotificationRequest;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.Review;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DateUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class NotificationRequestDetailsDialog extends ContentBaseDialog {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(contentId, null);

        fillNotificationRequestDetails(root);

        builder.setTitle(titleStringId)
                .setView(root);

        dialog = builder.create();
        return dialog;
    }




    private void fillNotificationRequestDetails(final View view){

        final DatabaseReference notificationRef = DBUtils.getDatabaseRef().child("notificationRequests").child(key);

        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                NotificationRequest notificationRequest = dataSnapshot.getValue(NotificationRequest.class);

                setDetail(notificationRequest.getRestaurant(), R.id.dialog_resturant_name, view);
                setDetail(notificationRequest.getBranch(), R.id.dialog_branch, view);

                String date = notificationRequest.getDate();
                String dateNewFormat = null;
                String hour = null;

                if( date != null && !date.equals("")) {
                    int indexOfSpace = date.indexOf(" ");
                    String dateOldFormat = date.substring(0, indexOfSpace);
                    hour = date.substring(indexOfSpace + 1);
                    try {
                        dateNewFormat = DateUtils.switchDateFormat(dateOldFormat, DateUtils.dateFormatDB, DateUtils.dateFormatUser);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    setDetail(dateNewFormat, R.id.dialog_date, view);
                    setDetail(hour, R.id.dialog_hour, view);
                }

                setDetail(dateNewFormat, R.id.dialog_date, view);
                setDetail(hour, R.id.dialog_hour, view);
                setDetail(notificationRequest.getIsFlexible()? "Yes":"No", R.id.timeFlexible, view);
                setDetail(Integer.toString(notificationRequest.getNumOfPeople()), R.id.dialog_num_of_people, view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setDetail(String detail, int textViewId, View detailsView){
        String newDetail = (detail == null || detail.equals("")) ? "Flexible" : detail;
        TextView textView = detailsView.findViewById(textViewId);
        textView.setText(newDetail);
    }
}