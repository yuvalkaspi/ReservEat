package com.reserveat.reserveat;


import android.content.Intent;
import android.app.DialogFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogFragment;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.DBUtils;
import com.reserveat.reserveat.common.dialogFragment.OurDialogFragment;
import com.reserveat.reserveat.common.dialogFragment.RatingDialogFragment;
import com.reserveat.reserveat.common.Reservation;

import java.util.HashMap;
import java.util.Map;

public class ReviewForm extends AppCompatActivity implements OurDialogFragment.NoticeDialogListener {

    static final int NUM_OF_QUESTIONS = 3;
    private Button[] buttons = new Button[NUM_OF_QUESTIONS];
    final HashMap<Integer, Float> userAnswers = new HashMap<>();
    private static final String TAG = "SurveyFormActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final HashMap<Integer, Object> surveyAnswers = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form);

        //temporary. TODO: should come from the data base
        //Reservation reservation
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            //The key argument here must match that used in the other activity
        }
        Reservation reservation = (Reservation) extras.get("reservation");
        setReservationDetails(reservation);

        buttons[0] = findViewById(R.id.q1);
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OurDialogFragment newFragment = new ChoiceDialogFragment();
                OurDialogFragment.initDialog(newFragment, R.string.q1, R.array.SurveyBusyRestaurantOptions,1);
                newFragment.show(getFragmentManager(), "q1ChoiceDialogFragment");
            }
        });

        buttons[1] = findViewById(R.id.q2);
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OurDialogFragment newFragment = new ChoiceDialogFragment();
                OurDialogFragment.initDialog(newFragment, R.string.q2, R.array.SurveyBusyRestaurantOptions,2);
                newFragment.show(getFragmentManager(), "q2ChoiceDialogFragment");
            }
        });




        buttons[2]= findViewById(R.id.q3);
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OurDialogFragment newFragment = new RatingDialogFragment();
                OurDialogFragment.initDialog(newFragment, R.string.q3,R.layout.rating,3);
                newFragment.show(getFragmentManager(), "q3ChoiceDialogFragment");
            }
        });

        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAllQuestionsFilled()){
                    Review review = new Review(userAnswers);
                    insertDataToDB(review);
                    addStarToUser();
                    Toast.makeText(ReviewForm.this, "THANKS! YOU EARN 1 START", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ReviewForm.this, MyReviewActivity.class );
                    startActivity(intent);
                } else{
                    Toast.makeText(ReviewForm.this, "FAILED: PLEASE ANSWER ALL QUESTIONS", Toast.LENGTH_LONG).show();

                }

            }
        });



    }

    private void addStarToUser() {
        Log.i(TAG, "adding a new start user");

        DatabaseReference ref = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID());

        //DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).setValue(["stars": stars+1]);

        Query query = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child("stars");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Long starsNum = (Long)dataSnapshot.getValue();
               // DBUtils.updateStarsToUser(starsNum + 1, DBUtils.getCurrentUserID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void insertDataToDB(Review review) {
        Log.i(TAG, "adding a new review to DB");

        String key = DBUtils.getDatabaseRef().child("reviews").push().getKey();
        Map<String, Object> reviewValues = review.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/reviews/" + key, reviewValues);
        childUpdates.put("/users/" + DBUtils.getCurrentUserID() + "/reviews/" + key, reviewValues);

        DBUtils.getDatabaseRef().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new review: success", task.getException());
                }else{
                    Log.w(TAG, "add new review: failure", task.getException());
                }
            }
        });

    }

    private boolean checkAllQuestionsFilled() {
        for(int i = 1; i <= NUM_OF_QUESTIONS; i++){
            if(!userAnswers.containsKey(i)){
                return false;
            }
        }
        return true;
    }

    private void setReservationDetails(Reservation reservation) {

        TextView restaurantName = findViewById(R.id.restaurant);
        TextView branch = findViewById(R.id.branch);
        TextView dateAndHour = findViewById(R.id.date);

        restaurantName.setText("Restaurant:   " + reservation.getRestaurant());
        branch.setText("Branch:   " + reservation.getBranch());
        dateAndHour.setText("Date:   " + reservation.getDate());

    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result) {
        Toast.makeText(ReviewForm.this, "Saved!", Toast.LENGTH_LONG).show();
        userAnswers.put(dialogIndex, result);
        buttons[dialogIndex - 1].setBackgroundResource(R.color.answeredButtonInReview);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

}
