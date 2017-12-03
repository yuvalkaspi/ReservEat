package com.reserveat.reserveat.common;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.R;

/**
 * Created by user on 11/23/2017.
 */

public class Common {

    static final int OK = 0;

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




}
