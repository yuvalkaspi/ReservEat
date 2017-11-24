package com.reserveat.reserveat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.HashMap;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class AddActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    EditText restaurantEditText;
    EditText dateEditText;
    EditText hourEditText;
    EditText numOfPeopleEditText;
    private static final String TAG = "AddActivity";

    Calendar current = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        //Calendar myCalendar = Calendar.getInstance();

        restaurantEditText= (EditText) findViewById(R.id.restaurant);
        dateEditText= (EditText) findViewById(R.id.date);
        hourEditText = (EditText) findViewById(R.id.hour);
        numOfPeopleEditText = (EditText) findViewById(R.id.numOfPeople);
        Button addButton = (Button) findViewById(R.id.add);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = current.get(Calendar.YEAR);
                int month  = current.get(Calendar.MONTH);
                int day  = current.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        dateEditText.setText(i2+"/"+(i1+1)+"/"+i);
                        //Toast.makeText(MainActivity.this, i2+"/"+(i1+1)+"/"+i, Toast.LENGTH_SHORT).show();
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
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hourEditText.setText(i+":"+i1);
                        //Toast.makeText(MainActivity.this, i+":"+i1, Toast.LENGTH_SHORT).show();
                    }
                },hour,minutes,true);
                tpd.show();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReservationToDB();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
//            String name = currentUser.getDisplayName();
//            Toast.makeText(getApplicationContext(),"Hello " + name, Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(AddActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }

    private void addReservationToDB() {

        Log.i(TAG, "adding new reservation to DB");
        String restaurant = restaurantEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String hour = hourEditText.getText().toString().trim();
        int numOfPeople = Integer.valueOf(numOfPeopleEditText.getText().toString());

        Reservation reservation = new Reservation("Yuval",restaurant, date, hour, numOfPeople);
        Map<String, Object> reservationValues = reservation.toMap();

        mDatabase.child("reservations").push().setValue(reservationValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new reservation:success", task.getException());
                    Toast.makeText(AddActivity.this, "Your reservation was saved!", Toast.LENGTH_LONG).show();
                }else{
                    Log.w(TAG, "add new reservation:failure", task.getException());
                    Toast.makeText(AddActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

@IgnoreExtraProperties
class Reservation {

    public String uid;
    public String restaurant;
    public String date;
    public String hour;
    public int numOfPeople;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String restaurant, String date, String hour, int numOfPeople) {
        this.uid = uid;
        this.restaurant = restaurant;
        this.date = date;
        this.hour = hour;
        this.numOfPeople = numOfPeople;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("restaurant", restaurant);
        result.put("date", date);
        result.put("hour", hour);
        result.put("numOfPeople", numOfPeople);

        return result;
    }

}