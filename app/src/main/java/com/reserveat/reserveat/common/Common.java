package com.reserveat.reserveat.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.LoginActivity;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.R;

/**
 * Created by user on 11/23/2017.
 */

public class Common {

    public static void updateUI(FirebaseUser currentUser , Context packageContext){
        if (currentUser != null){
            Intent intent = new Intent(packageContext, MainActivity.class );
            packageContext.startActivity(intent);
        }
        //todo
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }



}
