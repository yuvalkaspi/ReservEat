package com.reserveat.reserveat;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.DBUtils;
import com.reserveat.reserveat.common.NotificationRequest;
import com.reserveat.reserveat.common.Reservation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotifyActivity extends AppCompatActivity {

    private static final String TAG = "NotifyActivity";
    private EditText restaurantEditText;
    private EditText dateEditText;
    private EditText hourEditText;
    private EditText numOfPeopleEditText;
    private Switch isFlexibleSwitch;
    private Calendar current = Calendar.getInstance();
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        //Calendar myCalendar = Calendar.getInstance();

        restaurantEditText = findViewById(R.id.restaurant);
        dateEditText = findViewById(R.id.date);
        hourEditText = findViewById(R.id.hour);
        numOfPeopleEditText = findViewById(R.id.numOfPeople);
        Button saveButton = findViewById(R.id.save);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = current.get(Calendar.YEAR);
                int month = current.get(Calendar.MONTH);
                int day = current.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(NotifyActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog tpd = new TimePickerDialog(NotifyActivity.this , R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        DateFormat dateFormat = new SimpleDateFormat(Common.hourFormat, Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minutes);
                        Date dateObj = calendar.getTime();
                        hourEditText.setText(dateFormat.format(dateObj));
                    }
                },hour,minutes,false);
                tpd.show();
            }
        });

        isFlexibleSwitch = (Switch) findViewById(R.id.isFlexible);
        isFlexibleSwitch.setText("is flexible?");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyOnCancel();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(NotifyActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }

    private void notifyOnCancel() {

        try {
            String restaurant = restaurantEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String hour = hourEditText.getText().toString().trim();
            String numOfPeople = numOfPeopleEditText.getText().toString().trim();
            boolean isFlexible = isFlexibleSwitch.isChecked();
            String[] mandatoryFeildsValues = {restaurant, date, hour, numOfPeople};
            if (!isValidValues(mandatoryFeildsValues)) {
                Toast.makeText(NotifyActivity.this, "PLEASE FILL AT LEAST ONE FIELD", Toast.LENGTH_LONG).show();
                return;
            }
            String dateNewFormat = "";
            if(date != null && !date.isEmpty())
                dateNewFormat = Common.switchDateFormat(date, Common.dateFormatUser, Common.dateFormatDB);

            String newFullDateString = dateNewFormat + " " + hour;
            //check if a reservation is already exist
            NotificationRequest notificationRequest = new NotificationRequest(currentUser.getUid(), restaurant, newFullDateString, numOfPeople, isFlexible);
            addNotificationRequestToDB(notificationRequest);

        } catch (ParseException e) {
            Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }
    }

    /*  Receives all the data that a user inserted to the feilds in the notification form
    *  returns true if at least one of them is not empty
    *  otherwise returns false
    * */
    private boolean isValidValues(String[] feildsValues) {
        boolean foundFilledFeild = false;
        for(String val : feildsValues){
            if(val != null && !val.isEmpty()){
                foundFilledFeild = true;
            }
        }
        return foundFilledFeild;
    }

    private void addNotificationRequestToDB(NotificationRequest notificationRequest){
        Log.i(TAG, "adding a new notification request to DB");

        String key = DBUtils.getDatabaseRef().child("notificationRequests").push().getKey();
        Map<String, Object> notificationRequestValues = notificationRequest.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/notificationRequests/" + key, notificationRequestValues);
        childUpdates.put("/users/" + currentUser.getUid() + "/notificationRequests/" + key, notificationRequestValues);

        DBUtils.getDatabaseRef().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new notification request: success", task.getException());
                    Toast.makeText(NotifyActivity.this, "Your notification request was saved!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NotifyActivity.this, MainActivity.class );
                    startActivity(intent);
                }else{
                    Log.w(TAG, "add new notification request: failure", task.getException());
                    Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
