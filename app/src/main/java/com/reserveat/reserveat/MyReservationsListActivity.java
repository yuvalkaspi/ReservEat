package com.reserveat.reserveat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.ReservationContentDialog;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.ReservationListDialog;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;

import java.text.ParseException;

public class MyReservationsListActivity extends BaseActivity {

    private static final String TAG = "MyReservationsActivity";
    private FirebaseUser currentUser;
    private PopupWindow mPopupWindow;
    private boolean isMyReservations;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations_list);
        isMyReservations = getIntent().getBooleanExtra("isMyReservations" , true);
        TextView listTitleTV = (TextView) findViewById(R.id.reservation_list_title);
        if(!isMyReservations)
            listTitleTV.setText(getResources().getString(R.string.myPickedReservations));

        String currentUserID = DBUtils.getCurrentUserID();


        String resToGet =  isMyReservations? "reservations" : "pickedReservations";
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child(resToGet);

        emptyView = (TextView) findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter = new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try{
                    ReservationUtils.myPopulateViewHolder(viewHolder, model);
                }catch(ParseException e){
                    Toast.makeText(MyReservationsListActivity.this, "error!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "populateViewHolder: failure");
                }

                final Reservation reservation = model;
                viewHolder.setOnClickListener(new ReservationHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final String key = getRef(position).getKey();
                        ReservationContentDialog newFragment = new ReservationListDialog();
                        ReservationContentDialog.initInstance(newFragment, key, reservation, isMyReservations, false);
                        newFragment.show(getFragmentManager(), "ReservationListDialog");
                    }
                });

                Log.i(TAG, "populateViewHolder: success");
            }
        };

        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference reservationsRef;
        if(isMyReservations){
            reservationsRef = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child("reservations");
        }else{
            reservationsRef = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child("pickedReservations");
        }
        reservationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(MyReservationsListActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }



}
