package com.reserveat.reserveat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class AddActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    EditText resturantEditText;
    EditText dateEditText;
    EditText hourEditText;
    EditText numOfPeopleEditText;

    Calendar current = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        //Calendar myCalendar = Calendar.getInstance();

        resturantEditText= (EditText) findViewById(R.id.resturant);
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

    private void addReservationToDB() {

        String resturant = resturantEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String hour = hourEditText.getText().toString().trim();
        int numOfPeople = Integer.valueOf(numOfPeopleEditText.getText().toString());

        Reservation reservation = new Reservation("Yuval",resturant, date, hour, numOfPeople);
        Map<String, Object> reservationValues = reservation.toMap();

        mDatabase.child("reservations").push().setValue(reservationValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddActivity.this, "Your reservation was saved!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

@IgnoreExtraProperties
class Reservation {

    public String uid;
    public String resturant;
    public String date;
    public String hour;
    public int numOfPeople;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Reservation(String uid, String resturant, String date, String hour, int numOfPeople) {
        this.uid = uid;
        this.resturant = resturant;
        this.date = date;
        this.hour = hour;
        this.numOfPeople = numOfPeople;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("resturant", resturant);
        result.put("date", date);
        result.put("hour", hour);
        result.put("numOfPeople", numOfPeople);

        return result;
    }

}