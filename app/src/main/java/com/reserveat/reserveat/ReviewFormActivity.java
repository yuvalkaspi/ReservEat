package com.reserveat.reserveat;

import android.content.Intent;
import android.app.DialogFragment;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.common.dbObjects.Review;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.SingleChoiceDialog;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.BaseChoiceDialog;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.RatingChoiceDialog;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewFormActivity extends BaseActivity implements BaseChoiceDialog.NoticeDialogListener {

    private static final int NUM_OF_QUESTIONS = 4;
    private Button[] buttons = new Button[NUM_OF_QUESTIONS];
    private final HashMap<Integer, Float> userAnswers = new HashMap<>();
    private FirebaseUser currentUser;
    private static final int NUM_OF_STARS_PER_REVIEW = 1;

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
        final Reservation reservation = (Reservation) extras.get("reservation");
        setReservationDetails(reservation);

        final String reservationKey = (String) extras.get("reservationKey");

        buttons[0] = findViewById(R.id.q1);
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new SingleChoiceDialog();
                BaseChoiceDialog.initInstance(newFragment, R.string.q1, R.array.ReviewBusyRestaurantOptions,1);
                newFragment.show(getFragmentManager(), "q1ChoiceDialogFragment");
            }
        });


        buttons[1]= findViewById(R.id.q2);
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new RatingChoiceDialog();
                BaseChoiceDialog.initInstance(newFragment, R.string.q2,R.layout.rating,2);
                newFragment.show(getFragmentManager(), "q2ChoiceDialogFragment");
            }
        });

        buttons[2]= findViewById(R.id.q3);
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new SingleChoiceDialog();
                BaseChoiceDialog.initInstance(newFragment, R.string.q3, R.array.ReviewYesNoOptions,3);
                newFragment.show(getFragmentManager(), "q3ChoiceDialogFragment");
            }
        });


        buttons[3]= findViewById(R.id.q4);
        buttons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new SingleChoiceDialog();
                BaseChoiceDialog.initInstance(newFragment, R.string.q4, R.array.ReviewYesNoOptions,4);
                newFragment.show(getFragmentManager(), "q4ChoiceDialogFragment");
            }
        });

        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAllQuestionsFilled()){
                    Review review = new Review(userAnswers, DBUtils.getCurrentUserID());
                    insertDataToDB(review, reservation, reservationKey);
                    DBUtils.updateStarsToUser(NUM_OF_STARS_PER_REVIEW, reservation.getPickedByUid());
                    Toast.makeText(ReviewFormActivity.this, "THANKS! YOU EARN 1 START", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(ReviewFormActivity.this, MyReviewActivity.class );
                    //startActivity(intent);
                    finish();
                } else{
                    Toast.makeText(ReviewFormActivity.this, "FAILED: PLEASE ANSWER ALL QUESTIONS", Toast.LENGTH_LONG).show();

                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = DBUtils.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(ReviewFormActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }


    private void insertDataToDB(final Review review, Reservation reservation, String reservationKey) {
        Log.i(TAG, "adding a new review to DB");

        String placeId = reservation.getPlaceId();
        String key = DBUtils.getDatabaseRef().child("reviews").child(placeId).child(reservation.getDay()).child(reservation.getTimeOfDay()).push().getKey();
        Map<String, Object> reviewValues = review.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        final String reviewPath = "/reviews/" + placeId + "/" + reservation.getDay() + "/" + reservation.getTimeOfDay();
        childUpdates.put(reviewPath +  "/" + key, reviewValues);
        childUpdates.put("/users/" + DBUtils.getCurrentUserID() + "/reviews/" + key, reviewValues);
        childUpdates.put("/users/" + DBUtils.getCurrentUserID() + "/pickedReservations/" + reservationKey + "/isReviewed", true);

        DBUtils.getDatabaseRef().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new review: success", task.getException());
                    DBUtils.updateReliabilityToUser(DBUtils.getCurrentUserID(), review, reviewPath);
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
        Toast.makeText(ReviewFormActivity.this, "Saved!", Toast.LENGTH_LONG).show();
        userAnswers.put(dialogIndex, result);
        buttons[dialogIndex - 1].setBackgroundResource(R.color.answeredButtonInReview);
    }

    @Override
    public void onDialogPositiveClickMultipleChoice(DialogFragment dialog, int dialogIndex, List<Integer> mSelectedItems) {
        // Do nothing
    }
}
