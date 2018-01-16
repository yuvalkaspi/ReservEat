package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.reserveat.reserveat.common.dbObjects.Reservation;

public abstract class ReservationContentDialog extends DialogFragment {

    public static String dialogKey = "dialogKey";
    public static String isMyReservationKey = "isMyReservationsKey";
    public static String isPickableKey = "isPickableKey";

    protected String key;
    protected Reservation reservation;
    protected boolean isPickable;
    protected boolean isMyReservations;
    protected Dialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getArguments().getString(ReservationContentDialog.dialogKey);
        isMyReservations = getArguments().getBoolean(ReservationContentDialog.isMyReservationKey);
        isPickable = getArguments().getBoolean(ReservationContentDialog.isPickableKey);
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public static void initInstance(ReservationContentDialog dialog, String key, Reservation reservation, boolean isMyReservations, boolean isPickable) {
        Bundle args = new Bundle();
        args.putString(ReservationContentDialog.dialogKey, key);
        args.putBoolean(ReservationContentDialog.isMyReservationKey, isMyReservations);
        args.putBoolean(ReservationContentDialog.isPickableKey, isPickable);
        dialog.setArguments(args);
        dialog.setReservation(reservation);
    }
}
