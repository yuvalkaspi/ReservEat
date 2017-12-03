package com.reserveat.reserveat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.reserveat.reserveat.common.Common;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        firstNameEditText= findViewById(R.id.firstName);
        lastNameEditText= findViewById(R.id.lastName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button joinButton = findViewById(R.id.join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Common.updateUI(currentUser,SignUpActivity.this);
    }

    /**
     * Attempts to sign up.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign up attempt is made.
     */
    private void attemptSignUp() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final String firstName = firstNameEditText.getText().toString().trim();
        final String lastName = lastNameEditText.getText().toString().trim();


        TextView[] formTextViewArr = {passwordEditText, emailEditText, lastNameEditText, firstNameEditText};//order desc
        int[] formTextViewErrCodeArr = new int[formTextViewArr.length];

        View focusView = null;

        formTextViewErrCodeArr[0] = Common.isPasswordValid(password);
        formTextViewErrCodeArr[1] = Common.isEmailValid(email);
        formTextViewErrCodeArr[2] = Common.isEmptyTextField(lastName);
        formTextViewErrCodeArr[3] = Common.isEmptyTextField(firstName);

        //todo: check if first name and last name contain only letters

        for (int i = 0; i < formTextViewArr.length; i ++){
            int res = formTextViewErrCodeArr[i];
            TextView textView = formTextViewArr[i];
            if(res != 0){//error
                textView.setError(getString(res));
                focusView = textView;
            }else{
                textView.setError(null);// Reset error.
            }
        }

        if (focusView != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.w(TAG, "fields verification error: field was entered incorrect");
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.i(TAG, "fields verification: success");
            addUserToDB(email, password, firstName, lastName);

        }
    }


    private void addUserToDB(String email, String password, final String firstName, final String lastName) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName + " " + lastName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(TAG, "updateProfile:success");
                                            }else{
                                                Log.w(TAG, "updateProfile:failure", task.getException());
                                                Toast.makeText(SignUpActivity.this, "Update failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            Common.updateUI(mAuth.getCurrentUser(),SignUpActivity.this);
                                        }
                                    });
                            //todo: nullpointerexception?

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                            Common.updateUI(null,SignUpActivity.this);
                        }

                    }
                });
    }





}
