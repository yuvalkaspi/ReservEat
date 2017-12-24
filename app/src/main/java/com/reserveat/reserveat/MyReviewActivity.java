package com.reserveat.reserveat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.DBUtils;
import com.reserveat.reserveat.common.Reservation;

import java.text.ParseException;

public class MyReviewActivity extends AppCompatActivity {

    private static final String TAG = "MyReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_survey);

        String currentUserID = DBUtils.getCurrentUserID();

        DatabaseReference mDatabase = DBUtils.getDatabaseRef().child("users").child(currentUserID).child("pickedReservations");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter = new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.survey, ReservationHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try {
                    Common.myPopulateViewHolder(viewHolder, model);
                    final Reservation reservation = model;
                    viewHolder.setOnClickListener(new ReservationHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(MyReviewActivity.this, ReviewForm.class );
                            intent.putExtra("reservation", reservation);
                            startActivity(intent);
                        }
                    });
                    Log.i(TAG, "populateViewHolder: success");
                } catch (ParseException e) {
                    Toast.makeText(MyReviewActivity.this, "error!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "populateViewHolder: failure");
                }
            }
        };

        recyclerView.setAdapter(adapter);


    }
}
