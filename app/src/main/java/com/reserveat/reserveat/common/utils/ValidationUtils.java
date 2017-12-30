package com.reserveat.reserveat.common.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.reserveat.reserveat.R;

public class ValidationUtils {

    private static final int OK = 0;

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

    public static boolean isValidValues(String[] mandatoryFieldsValues, TextView[] mandatoryFields, String TAG) {

        View focusView = null;
        int numOfMandatoryFields = mandatoryFieldsValues.length;
        int [] mandatoryFieldsError = new int[mandatoryFieldsValues.length];
        for(int i = 0; i < numOfMandatoryFields; i++){
            mandatoryFieldsError[i] = ValidationUtils.isEmptyTextField(mandatoryFieldsValues[i]);
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
}
