package com.reserveat.reserveat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.Reservation;

public class AddActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    EditText restaurantEditText;
    EditText branchEditText;
    EditText dateEditText;
    EditText hourEditText;
    EditText numOfPeopleEditText;
    EditText reservationNameEditText;
    EditText OtherInfoEditText;
    FirebaseUser currentUser;
    private static final String TAG = "AddActivity";

    Calendar current = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        //Calendar myCalendar = Calendar.getInstance();

        restaurantEditText = findViewById(R.id.restaurant);
        branchEditText = findViewById(R.id.branch);
        dateEditText = findViewById(R.id.date);
        hourEditText = findViewById(R.id.hour);
        numOfPeopleEditText = findViewById(R.id.numOfPeople);
        reservationNameEditText = findViewById(R.id.reservationName);
        OtherInfoEditText = findViewById(R.id.otherInfo);
        Button addButton = findViewById(R.id.add);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = current.get(Calendar.YEAR);
                int month = current.get(Calendar.MONTH);
                int day = current.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        DateFormat dateFormat = new SimpleDateFormat(Common.dateFormatUser, Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        Date dateObj = calendar.getTime();
                        dateEditText.setText(dateFormat.format(dateObj));
                    }
                },year,month,day);
                dpd.show();
            }
        });

        hourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = current.get(Calendar.HOUR_OF_DAY);
                int minutes = current.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(AddActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        DateFormat dateFormat = new SimpleDateFormat(Common.hourFormat, Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minutes);
                        Date dateObj = calendar.getTime();
                        hourEditText.setText(dateFormat.format(dateObj));
                    }
                },hour,minutes,true);
                tpd.show();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddReservation();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(AddActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }


    private void attemptAddReservation() {

        String restaurant = restaurantEditText.getText().toString().trim();
        String branch = branchEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String hour = hourEditText.getText().toString().trim();
        String numOfPeople = numOfPeopleEditText.getText().toString();
        String reservationName = reservationNameEditText.getText().toString().trim();
        String OtherInfo = OtherInfoEditText.getText().toString().trim();
        //todo: OtherInfo - optional field?

        TextView[] formTextViewArr = {OtherInfoEditText, reservationNameEditText, numOfPeopleEditText,
                hourEditText, dateEditText, branchEditText, restaurantEditText};//order desc

        int[] formTextViewErrCodeArr = new int[formTextViewArr.length];

        View focusView = null;

        formTextViewErrCodeArr[0] = Common.isEmptyTextField(OtherInfo);
        formTextViewErrCodeArr[1] = Common.isEmptyTextField(reservationName);
        formTextViewErrCodeArr[2] = Common.isEmptyTextField(numOfPeople);
        formTextViewErrCodeArr[3] = Common.isEmptyTextField(hour);
        formTextViewErrCodeArr[4] = Common.isEmptyTextField(date);
        formTextViewErrCodeArr[5] = Common.isEmptyTextField(branch);
        formTextViewErrCodeArr[6] = Common.isEmptyTextField(restaurant);

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
            addReservationToDB(restaurant, branch, date, hour, Integer.valueOf(numOfPeople), reservationName, OtherInfo);

        }
    }

    private void addReservationToDB(String restaurant, String branch, String date, String hour, int numOfPeople, String reservationName, String OtherInfo) {

        Log.i(TAG, "adding a new reservation to DB");
        String key = mDatabase.child("reservations").push().getKey();
        try{
            String dateNewFormat = Common.switchDateFormat(date, Common.dateFormatUser, Common.dateFormatDB);
            String newFullDateString = dateNewFormat + " " + hour;
            Reservation reservation = new Reservation(currentUser.getUid(), restaurant, branch, newFullDateString, numOfPeople, reservationName, OtherInfo);
            Map<String, Object> reservationValues = reservation.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/reservations/" + key, reservationValues);
            childUpdates.put("/users/" + currentUser.getUid() + "/reservations/" + key, reservationValues);

            mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "add new reservation:success", task.getException());
                        Intent intent = new Intent(AddActivity.this, MainActivity.class );
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "add new reservation:failure", task.getException());
                        Toast.makeText(AddActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (ParseException e){
            //todo
            Toast.makeText(AddActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }





    }
}

