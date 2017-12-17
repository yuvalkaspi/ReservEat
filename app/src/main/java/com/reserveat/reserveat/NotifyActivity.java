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
                DatePickerDialog dpd = new DatePickerDialog(NotifyActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog tpd = new TimePickerDialog(NotifyActivity.this , new TimePickerDialog.OnTimeSetListener() {
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
            String dateNewFormat = Common.switchDateFormat(date, Common.dateFormatUser, Common.dateFormatDB);
            String hour = hourEditText.getText().toString().trim();
            String newFullDateString = dateNewFormat + " " + hour;
            String numOfPeople = numOfPeopleEditText.getText().toString().trim();
            boolean isFlexible = isFlexibleSwitch.isChecked();
            String[] mandatoryFeildsValues = {restaurant, date, hour, numOfPeople};
            TextView[] mandatoryFields = {restaurantEditText, dateEditText, hourEditText, numOfPeopleEditText};
            if (!isValidValues(mandatoryFeildsValues, mandatoryFields)) {
                return;
            }

            //check if a reservation is already exist
            NotificationRequest notificationRequest = new NotificationRequest(currentUser.getUid(), restaurant, newFullDateString, Integer.valueOf(numOfPeople), isFlexible);

            String reservationID = isCancellationExist(notificationRequest);
            if (!reservationID.equals(""))
                notifyUserOnCancellation(reservationID);
            else //save request in notification table
                addNotificationRequestToDB(notificationRequest);
        } catch (ParseException e) {
            //todo
            Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }
    }

    private void notifyUserOnCancellation(String reservationID){

        //Reservation reservation = DBUtils.getReservationByID(reservationID);
       // String notificationMessage = createAlertMessageReservationFound(reservation);
        //Common.NotifyUser(notificationMessage);
    }


    private String createAlertMessageReservationFound(Reservation reservation){
        StringBuilder sb = new StringBuilder();
        sb.append("We found a reservation for you!");
        sb.append("the reservation is at ").append(reservation.getRestaurant());
        String branch = reservation.getBranch();
        if(branch != null && !branch.isEmpty()) {
            sb.append(", ");
            sb.append(branch);
        }
        sb.append("for ").append(reservation.getNumOfPeople()).append("peoples");
        List<String> dateAndHour = Common.getDate(reservation.getDate());
        sb.append("on ").append(dateAndHour.get(0)).append("at ").append(dateAndHour.get(1));

        return sb.toString();
    }



    private String isCancellationExist(final NotificationRequest notificationRequest){

        final String[] isFound = {""}; //define as array because it should be changed anonymous inner class
        Query query = DBUtils.getDatabaseRef().child("reservations").orderByChild("restaurant").equalTo(notificationRequest.getRestaurant());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Map<String, Object> newPost = (Map<String, Object>) childSnapshot.getValue();
                    if (isMatch(notificationRequest, newPost)) {
                        isFound[0] = dataSnapshot.getKey();
                        Log.i(TAG, "notification request already exist in DB");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return isFound[0];
    }

    private boolean isMatch(NotificationRequest notificationRequest, Map<String, Object> newPost){

        return newPost.get("fullDate").equals(notificationRequest.getFullDate()) &&
                newPost.get("numOfPeople").equals(Long.valueOf(notificationRequest.getNumOfPeople()));

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
                }else{
                    Log.w(TAG, "add new notification request: failure", task.getException());
                    Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


    private boolean isValidValues(String[] mandatoryFeildsValues, TextView[] mandatoryFields) {

        View focusView = null;
        int numOfMandatoryFields = mandatoryFeildsValues.length;
        int [] mandatoryFieldsError = new int[mandatoryFeildsValues.length];
        for(int i = 0; i < numOfMandatoryFields; i++){
            mandatoryFieldsError[i] = Common.isEmptyTextField(mandatoryFeildsValues[i]);
        }

        for (int i = 0; i < numOfMandatoryFields; i ++){
            int result = mandatoryFieldsError[i];
            TextView textView = mandatoryFields[i];
            if (result != 0){ //error
                textView.setError(getString(result));
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
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.i(TAG, "fields verification: success");
            return true;
        }

    }

}
