package com.reserveat.reserveat;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.utils.DBUtils;


import static com.reserveat.reserveat.common.utils.DBUtils.getCurrentUserID;

public class ProfileActivity extends BaseActivity {

    private Integer currNumOfStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ImageView imageView0Star = findViewById(R.id.star0);
        final ImageView imageView1Star = findViewById(R.id.star1);
        final ImageView imageView2Star = findViewById(R.id.star2);
        final ImageView imageView3Star = findViewById(R.id.star3);

        final String userId = getCurrentUserID();
        final DatabaseReference dbRef = DBUtils.getDatabaseRef();
        final DatabaseReference userRef = dbRef.child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currNumOfStars = dataSnapshot.child("stars").getValue(Integer.class);
                switch (currNumOfStars){
                    case 0:
                        imageView0Star.setVisibility(View.VISIBLE);
                        return;
                    case 1:
                        imageView1Star.setVisibility(View.VISIBLE);
                        return;
                    case 2:
                        imageView2Star.setVisibility(View.VISIBLE);
                        return;
                    case 3:
                        imageView3Star.setVisibility(View.VISIBLE);
                        return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




}
