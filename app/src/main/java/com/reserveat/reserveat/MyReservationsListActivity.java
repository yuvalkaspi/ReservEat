package com.reserveat.reserveat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.Reservation;

public class MyReservationsListActivity extends AppCompatActivity {

    private static final String TAG = "MyReservationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations_list);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("reservations");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter = new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ReservationHolder viewHolder, Reservation model, int position) {
                viewHolder.setRestaurant(model.getRestaurant());
                viewHolder.setBranch(model.getBranch());
                viewHolder.setDate(model.getDate());
                viewHolder.setHour(model.getHour());
                viewHolder.setNumOfPeople(model.getNumOfPeople());
                final Reservation reservation = model;
                viewHolder.setOnClickListener(new ReservationHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), reservation.getRestaurant() + " Selected", Toast.LENGTH_LONG).show();
                    }
                });
                Log.i(TAG, "success:populateViewHolder");
            }
        };

        recyclerView.setAdapter(adapter);


    }
}
