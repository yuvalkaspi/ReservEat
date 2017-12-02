package com.reserveat.reserveat;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.NotificationRequest;
import com.reserveat.reserveat.common.Reservation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotifyActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
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

        restaurantEditText= (EditText) findViewById(R.id.restaurant);
        dateEditText= (EditText) findViewById(R.id.date);
        hourEditText = (EditText) findViewById(R.id.hour);
        numOfPeopleEditText = (EditText) findViewById(R.id.numOfPeople);
        Button saveButton = (Button) findViewById(R.id.save);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = current.get(Calendar.YEAR);
                int month  = current.get(Calendar.MONTH);
                int day  = current.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(NotifyActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateEditText.setText(day + "/" + (month+1) + "/" + year);
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
                TimePickerDialog tpd = new TimePickerDialog(NotifyActivity.this , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        hourEditText.setText(hour + ":" + minutes);
                    }
                },hour,minutes,false);
                tpd.show();
            }
        });

        isFlexibleSwitch = (Switch) findViewById(R.id.isFlexible);
        isFlexibleSwitch.setText("is flexible?");

        mDatabase = FirebaseDatabase.getInstance().getReference();
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

        String restaurant = restaurantEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String hour = hourEditText.getText().toString().trim();
        String numOfPeople = numOfPeopleEditText.getText().toString();
        boolean isFlexible = isFlexibleSwitch.isChecked();

        //check if a reservation is already exist
       // if(isReservationExists(restaurant, date, hour, numOfPeople, isFlexible)){
         //   User user;
           // Reservation reservation;
            //notifyUserOnCancellation(user, reservation);
        //}
        //else{
            //save request in notification table
            addNotificationRequestToDB(restaurant, date, hour, Integer.valueOf(numOfPeople), isFlexible);
        //}

    }

    private void addNotificationRequestToDB(String restaurant, String date, String hour, int numOfPeople, boolean isFlexible){
        Log.i(TAG, "adding a new notification request to DB");

        String key = mDatabase.child("notificationRequests").push().getKey();
        NotificationRequest notficationRequest = new NotificationRequest(currentUser.getUid(), restaurant, date, hour, numOfPeople, isFlexible);
        Map<String, Object> notficationRequestValues = notficationRequest.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/notificationRequests/" + key, notficationRequestValues);
        childUpdates.put("/users/" + currentUser.getUid() + "/notificationRequests/" + key, notficationRequestValues);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new notification request: success", task.getException());
                    Toast.makeText(NotifyActivity.this, "Your notification request was saved!", Toast.LENGTH_LONG).show();
                }else{
                    Log.w(TAG, "add new notification request: failure", task.getException());
                    Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


}
