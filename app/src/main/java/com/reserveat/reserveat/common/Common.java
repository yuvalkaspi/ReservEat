package com.reserveat.reserveat.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.reserveat.reserveat.common.Common.Day.FRIDAY;
import static com.reserveat.reserveat.common.Common.Day.MONDAY;
import static com.reserveat.reserveat.common.Common.Day.SATURDAY;
import static com.reserveat.reserveat.common.Common.Day.SUNDAY;
import static com.reserveat.reserveat.common.Common.Day.THURSDAY;
import static com.reserveat.reserveat.common.Common.Day.TUESDAY;
import static com.reserveat.reserveat.common.Common.Day.WEDNESDAY;
import static com.reserveat.reserveat.common.Common.TimeOfDay.AFTERNOON;
import static com.reserveat.reserveat.common.Common.TimeOfDay.EVENING;
import static com.reserveat.reserveat.common.Common.TimeOfDay.MORNING;
import static com.reserveat.reserveat.common.Common.TimeOfDay.NIGHT;
import static com.reserveat.reserveat.common.Common.TimeOfDay.NOON;


public class Common {

    public static final String hourFormat = "HH:mm";
    public static final String dateFormatUser = "dd/MM/yyyy";
    public static final String dateFormatDB = "yyyy/MM/dd";
    public static final String fullDateFormatDB = dateFormatDB + " " + hourFormat;
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

    /* Receives string represents date in format- oldFormat
       Returns  string represents the same date in format- newFormat */
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
        numOfPeopleTextView.setText(Integer.toString(reservation.getNumOfPeople()));

        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });
    }


    public static void myPopulateViewHolderSurvey(ReservationHolder viewHolder, Reservation model) {
        String fullDate = model.getDate();
        viewHolder.setRestaurant(model.getRestaurant());
        viewHolder.setBranch(model.getBranch());
        viewHolder.setDate(fullDate);
    }

    public static boolean isValidValues(String[] mandatoryFieldsValues, TextView[] mandatoryFields, String TAG) {

        View focusView = null;
        int numOfMandatoryFields = mandatoryFieldsValues.length;
        int [] mandatoryFieldsError = new int[mandatoryFieldsValues.length];
        for(int i = 0; i < numOfMandatoryFields; i++){
            mandatoryFieldsError[i] = Common.isEmptyTextField(mandatoryFieldsValues[i]);
        }

        for (int i = 0; i < numOfMandatoryFields; i ++){
            int result = mandatoryFieldsError[i];
            TextView textView = mandatoryFields[i];
            if (result != 0){ //error
                //textView.setError(getString(result));
                focusView = textView;
            } else {
                textView.setError(null); //Reset error
            }
        }
        if (focusView != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.w(TAG, "fields verification error: field was entered incorrect");
            focusView.requestFocus();
            return false;
        } else {
            Log.i(TAG, "fields verification: success");
            return true;
        }

    }


    public static Day getDaybyDate(Calendar calendar) {

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return SUNDAY;

            case Calendar.MONDAY:
                return MONDAY;

            case Calendar.TUESDAY:
                return TUESDAY;

            case Calendar.WEDNESDAY:
                return WEDNESDAY;

            case Calendar.THURSDAY:
                return THURSDAY;

            case Calendar.FRIDAY:
                return FRIDAY;
        }
        return SATURDAY;
    }

    public static TimeOfDay getTimeOfDay(String time){

        int hour = Integer.valueOf(time.split(":")[0]);
        if(hour <= 12){
            return MORNING;
        } else if(hour <= 15){
            return NOON;
        } else if(hour <= 18){
            return AFTERNOON;
        } else if(hour <= 21){
            return EVENING;
        }
        return NIGHT;
    }


    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY
    }

    public enum TimeOfDay {
        MORNING, NOON, AFTERNOON, EVENING,
        NIGHT
    }


}

