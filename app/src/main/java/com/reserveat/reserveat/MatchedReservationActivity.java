package com.reserveat.reserveat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;

import java.text.ParseException;

public class MatchedReservationActivity extends AppCompatActivity {

    private static final String TAG = "MatchedReservActivity";
    FirebaseUser currentUser;
    private PopupWindow mPopupWindow;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_reservation);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_matched);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String reservationId = null;
        if(getIntent().getExtras() != null){
            reservationId = getIntent().getExtras().getString("reservationId", "");
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("reservations");

        FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter = new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase.orderByKey().equalTo(reservationId)) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try{
                    ReservationUtils.myPopulateViewHolder(viewHolder, model);
                }catch(ParseException e){
                    Toast.makeText(MatchedReservationActivity.this, "error!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "populateViewHolder: failure");
                }

                final Reservation reservation = model;
                viewHolder.setOnClickListener(new ReservationHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        key = getRef(position).getKey();
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View customView = inflater.inflate(R.layout.pop_up_reservation_layout,null);
                        mPopupWindow = new PopupWindow(
                                customView,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        mPopupWindow.setElevation(5.0f);
                        ReservationUtils.popUpWindowCreate(mPopupWindow, customView, reservation);

                        Button pickButton = (Button) customView.findViewById(R.id.pick_Button);
                        pickButton.setVisibility(View.VISIBLE);
                        pickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ReservationUtils.popUpPickClick(reservation, customView, key, currentUser.getUid(), MatchedReservationActivity.this);
                            }
                        });
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.matched_reservation), Gravity.CENTER,0,0);
                    }
                });

                Log.i(TAG, "populateViewHolder: success");
            }
        };

        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
        }else{
            Intent intent = new Intent(MatchedReservationActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }
}



