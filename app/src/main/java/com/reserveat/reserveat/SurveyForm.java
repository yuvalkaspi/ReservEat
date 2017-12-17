package com.reserveat.reserveat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reserveat.reserveat.common.DialogUtils;
import com.reserveat.reserveat.common.Reservation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.reserveat.reserveat.common.DialogUtils.showRadioButtonDialog;
import static com.reserveat.reserveat.common.DialogUtils.showRatingDialog;

public class SurveyForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final HashMap<Integer, Object> surveyAnswers = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form);

        //temporary. TODO: should come from the data base
        setReservationDetails();

        Button q1Button = findViewById(R.id.q1);
        q1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRadioButtonDialog(R.string.q1, R.array.SurveyBusyRestaurantOptions, SurveyForm.this);
            }
        });

        Button q2Button = findViewById(R.id.q2);
        q2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRadioButtonDialog(R.string.q2, R.array.SurveyBusyRestaurantOptions, SurveyForm.this);
            }
        });




        Button q3Button = findViewById(R.id.q3);
        q3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog(getString(R.string.q3), SurveyForm.this, R.layout.rating);
            }
        });


    }

    private void setReservationDetails() {

        Reservation reservation = new Reservation("114" , "Benedict", "Ben Yehoda", "17/12/2017 17:00", 6 , "neta");
        TextView restaurantName = findViewById(R.id.restaurant);
        TextView branch = findViewById(R.id.branch);
        TextView dateAndHour = findViewById(R.id.date);

        restaurantName.setText("Restaurant: " + reservation.getRestaurant());
        branch.setText("Branch: " + reservation.getBranch());
        dateAndHour.setText("Reservation's Date And Hour: " + reservation.getDate());

    }




}
