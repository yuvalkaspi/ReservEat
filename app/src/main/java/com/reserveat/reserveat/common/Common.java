package com.reserveat.reserveat.common;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.R;
import com.reserveat.reserveat.ReservationHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class Common {

    public static final String hourFormat = "HH:mm";
    public static final String dateFormatUser = "dd/MM/yyyy";
    public static final String dateFormatDB = "yyyy/MM/dd";

    private static final int OK = 0;

    public static void updateUI(FirebaseUser currentUser , Context packageContext){
        if (currentUser != null){
            Intent intent = new Intent(packageContext, MainActivity.class );
            packageContext.startActivity(intent);
        }
        //todo
    }

    //returns errCode if email is invalid. else- returns 0
    public static int isEmailValid(String email) {
        int res = isEmptyTextField(email);
        if (res == OK && !email.contains("@")){
            res = R.string.error_invalid_email;
        }
        return res;
    }

    //returns errCode if password is invalid. else- returns 0
    public static int isPasswordValid(String password) {
        int res = isEmptyTextField(password);
        if (res == OK && password.length() < 6){
            res = R.string.error_invalid_password;
        }
        return res;
    }

    //returns errCode if field is empty. else- returns 0
    public static int isEmptyTextField(String field){
        if (TextUtils.isEmpty(field)){
            return R.string.error_field_required;
        }
        return OK;
    }

    public static String switchDateFormat(String date, String oldFormat, String newFormat) throws ParseException{
        DateFormat dateFormat = new SimpleDateFormat(oldFormat, Locale.getDefault());
        Date d = dateFormat.parse(date);
        dateFormat = new SimpleDateFormat(newFormat, Locale.getDefault());
        return dateFormat.format(d);
    }

    /* Receives a string contains date and hour
       Returns array which contains that date and the hour */
    public static List<String> getDate(String dateAndHour){

        int indexOfSpace = dateAndHour.indexOf(" ");
        String date = dateAndHour.substring(0, indexOfSpace);
        String hour = dateAndHour.substring(indexOfSpace + 1);

        List<String> dateAndHourList = new ArrayList<>();
        dateAndHourList.add(date);
        dateAndHourList.add(hour);

        return dateAndHourList;
    }


    public static void myPopulateViewHolder(ReservationHolder viewHolder, Reservation model) throws ParseException {
        String date = model.getDate();
        int indexOfSpace = date.indexOf(" ");
        String dateOldFormat = date.substring(0, indexOfSpace);
        String hour = date.substring(indexOfSpace + 1);
        String dateNewFormat = Common.switchDateFormat(dateOldFormat, Common.dateFormatDB, Common.dateFormatUser);
        viewHolder.setRestaurant(model.getRestaurant());
        viewHolder.setBranch(model.getBranch());
        viewHolder.setDate(dateNewFormat);
        viewHolder.setHour(hour);
        viewHolder.setNumOfPeople(model.getNumOfPeople());
    }

    public static void popUpWindowCreate(final PopupWindow mPopupWindow, View customView, Reservation reservation) {
        mPopupWindow.setElevation(5.0f);

        TextView restaurantTextView = (TextView) customView.findViewById(R.id.popup_resturant_name);
        restaurantTextView.setText(reservation.getRestaurant());

        TextView branchTextView = (TextView) customView.findViewById(R.id.popup_branch);
        branchTextView.setText(reservation.getBranch());

        String date = reservation.getDate();
        int indexOfSpace = date.indexOf(" ");
        String dateOldFormat = date.substring(0, indexOfSpace);
        String hour = date.substring(indexOfSpace + 1);

        try {
            TextView dateTextView = (TextView) customView.findViewById(R.id.popup_date);
            String dateNewFormat = Common.switchDateFormat(dateOldFormat, Common.dateFormatDB, Common.dateFormatUser);
            dateTextView.setText(dateNewFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        TextView hourTextView = (TextView) customView.findViewById(R.id.popup_hour);
        hourTextView.setText(hour);

        TextView numOfPeopleTextView = (TextView) customView.findViewById(R.id.popup_num_of_people);
        numOfPeopleTextView.setText("" + reservation.getNumOfPeople());

        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
    }
}
