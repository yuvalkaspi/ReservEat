package com.reserveat.reserveat.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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


}
