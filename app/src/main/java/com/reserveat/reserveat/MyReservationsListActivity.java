package com.reserveat.reserveat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.reserveat.reserveat.common.utils.DateUtils.isDatePassed;

public class MyReservationsListActivity extends BaseActivity {

    private static final String TAG = "MyReservationsActivity";
    private FirebaseUser currentUser;
    private PopupWindow mPopupWindow;
    private static final String myResSpamInfo = "myResSpamInfo";
    private static final String myPickResSpamInfo = "myPickResSpamInfo";

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
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View customView = inflater.inflate(R.layout.pop_up_my_reservations,null);
                        mPopupWindow = new PopupWindow(
                                customView,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        mPopupWindow.setElevation(5.0f);

                        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_popup_close);
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                mPopupWindow.dismiss();
                            }
                        });

                        Button detailsButton = (Button) customView.findViewById(R.id.popup_details_Button);
                        detailsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                mPopupWindow.dismiss();
                                popUpDetailsClick(reservation, key);
                            }
                        });

                        Button spamButton = (Button) customView.findViewById(R.id.popup_spam_Button);
                        if(isMyReservations && !reservation.isPicked()) //|| reservation.getIsSpam())
                            makeButtonGrey(spamButton, getResources().getString(R.string.spam_not_picked_msg));
                        else
                            if(reservation.getIsSpam())
                            makeButtonGrey(spamButton, getResources().getString(R.string.spam_reported_msg));
                            else{
                            spamButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(MyReservationsListActivity.this).create();
                                    if(isMyReservations)
                                        alertDialog.setMessage(myResSpamInfo);
                                    else
                                        alertDialog.setMessage(myPickResSpamInfo);

                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            popUpSpamClick(reservation, customView, key);
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    alertDialog.show();}
                        });
                            }


                        Button removeButton = (Button) customView.findViewById(R.id.popup_remove_Button);
                        if(!isMyReservations)
                            removeButton.setVisibility(View.GONE);
                        else{
                            if (reservation.isPicked())
                                makeButtonGrey(removeButton, getResources().getString(R.string.remove_picked_msg));
                            else
                                removeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        popUpRemoveClick(reservation, customView, key);
                                    }
                                });
                        }

                        Button reviewButton = (Button) customView.findViewById(R.id.popup_review_Button);
                        if(isMyReservations)
                            reviewButton.setVisibility(View.GONE);
                        else{
                            if(reservation.getIsReviewed())
                                makeButtonGrey(reviewButton, getResources().getString(R.string.review_filed_msg));
                            else{
                                if(!isDatePassed(reservation.getDate()))
                                    makeButtonGrey(reviewButton, getResources().getString(R.string.review_not_arrived_msg));
                                else{
                                    reviewButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(MyReservationsListActivity.this, ReviewFormActivity.class );
                                            intent.putExtra("reservation", reservation);
                                            intent.putExtra("restaurantKey", key);
                                            startActivityForResult(intent , 0);
                                            mPopupWindow.dismiss();
                                        }
                                    });
                                }
                            }
                        }

                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.my_reservations_list), Gravity.CENTER,0,0);
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

    private void makeButtonGrey(Button button , final String msg) {
        button.setTextColor(getResources().getColor(R.color.lightGray));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext() , msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void popUpRemoveClick(Reservation reservation, View customView, String key) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + currentUser.getUid() + "/reservations/" + key, null);
        childUpdates.put("/reservations/" + key, null);
        DatabaseReference popUpDatabase = FirebaseDatabase.getInstance().getReference();

        popUpDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "remove reservation:success", task.getException());
                    mPopupWindow.dismiss();

                } else {
                    Log.w(TAG, "remove reservation:failure", task.getException());
                    Toast.makeText(getApplicationContext() , "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void popUpSpamClick(Reservation reservation, View customView, String key) {

        String spammer;
        reservation.setIsSpam(true);
        if(isMyReservations) {
            // my reservation picker is spammer
            spammer = "picker";
            DBUtils.updateSpamToUser(reservation.getPickedByUid(), reservation.getUid(),"reservations", key);
        }else{
            // I picked reservation owner is spammer
            spammer = "reservation owner";
            DBUtils.updateSpamToUser(reservation.getUid(), reservation.getPickedByUid(),"pickedReservations", key);
        }

        Button spamButton = (Button) customView.findViewById(R.id.popup_spam_Button);
        makeButtonGrey(spamButton, getResources().getString(R.string.spam_reported_msg));
        Toast.makeText(getApplicationContext() ,spammer + " is reported", Toast.LENGTH_LONG).show();
        mPopupWindow.dismiss();

    }


    private void popUpDetailsClick(Reservation reservation, String key) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View detailsCustomView = inflater.inflate(R.layout.pop_up_reservation_layout,null);
        PopupWindow mPopupWindow = new PopupWindow(
                detailsCustomView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        ReservationUtils.popUpWindowCreate(mPopupWindow, detailsCustomView, reservation, MyReservationsListActivity.this);

        if(!isMyReservations){
            final TextView nameFormTextView = (TextView) detailsCustomView.findViewById(R.id.popup_name_form);
            final TextView nameTextView = (TextView) detailsCustomView.findViewById(R.id.popup_name);
            nameFormTextView.setVisibility(View.VISIBLE);
            nameFormTextView.setText("Reservation name is : ");
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(reservation.getReservationName());
        }

        LinearLayout phoneLayout = detailsCustomView.findViewById(R.id.phoneLayout);
        phoneLayout.setVisibility(View.VISIBLE);

        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.my_reservations_list), Gravity.CENTER,0,0);
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
