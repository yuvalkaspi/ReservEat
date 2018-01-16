package com.reserveat.reserveat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DateUtils;


import java.text.ParseException;

import static com.reserveat.reserveat.common.utils.DBUtils.getCurrentUserID;
import static com.reserveat.reserveat.common.utils.DateUtils.dateFormatDB;
import static com.reserveat.reserveat.common.utils.DateUtils.dateFormatUser;

public class ProfileActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ImageView imageView0Star = findViewById(R.id.star0);
        final ImageView imageView1Star = findViewById(R.id.star1);
        final ImageView imageView2Star = findViewById(R.id.star2);
        final ImageView imageView3Star = findViewById(R.id.star3);
        final TextView spamCounterTextView = findViewById(R.id.spamCounter);
        final TextView profileHeader = findViewById(R.id.ProfileHeader);
        final TextView resetStarDateTextView = findViewById(R.id.ResetStarDate);

        final String userId = getCurrentUserID();
        final DatabaseReference dbRef = DBUtils.getDatabaseRef();
        final DatabaseReference userRef = dbRef.child("users").child(userId);

        String usernameDisplay = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        profileHeader.setText(usernameDisplay);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int numOfSpamReports = dataSnapshot.child("spamReports").getValue(Integer.class);
                spamCounterTextView.setText("You were reported as spammer " + numOfSpamReports + " times");

                int currNumOfStars = dataSnapshot.child("stars").getValue(Integer.class);

                if(currNumOfStars != 0){
                    String resetStarnextDate = dataSnapshot.child("starRemoveDate").getValue(String.class).split(" ")[0];
                    try {
                        resetStarnextDate = DateUtils.switchDateFormat(resetStarnextDate, dateFormatDB, dateFormatUser);
                    } catch(ParseException e){
                        resetStarnextDate = "";
                    }
                    resetStarDateTextView.setText("Losing star date: " + resetStarnextDate);
                }

                switch (currNumOfStars){
                    case 0:
                        imageView0Star.setVisibility(View.VISIBLE);
                        return;
                    case 1:
                        imageView1Star.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        imageView2Star.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        imageView3Star.setVisibility(View.VISIBLE);
                        break;
                }

                resetStarDateTextView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button myResButton = findViewById(R.id.MyReservations);
        myResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(ProfileActivity.this, MyReservationsListActivity.class );
                    startActivity(intent);
            }
        });

        Button myPicksButton = findViewById(R.id.MyPickedReservations);
        myPicksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyReservationsListActivity.class );
                intent.putExtra("isMyReservations", false);
                startActivity(intent);
            }
        });

        Button myNotificationButton = findViewById(R.id.MyNotificationReq);
        myNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyNotificationsListActivity.class );
                startActivity(intent);
            }
        });


    }




}
