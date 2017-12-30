package com.reserveat.reserveat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MyReservationsListActivity extends AppCompatActivity {

    private static final String TAG = "MyReservationsActivity";
    FirebaseUser currentUser;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations_list);

        String currentUserID = DBUtils.getCurrentUserID();


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("reservations");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
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

                        Button spamButton = (Button) customView.findViewById(R.id.popup_spam_Button);
                        spamButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popUpSpamClick(reservation, customView, key);
                            }
                        });

                        Button removeButton = (Button) customView.findViewById(R.id.popup_remove_Button);
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popUpRemoveClick(reservation, customView, key);
                            }
                        });

                        Button reviewButton = (Button) customView.findViewById(R.id.popup_review_Button);
                        reviewButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MyReservationsListActivity.this, ReviewFormActivity.class );
                                intent.putExtra("reservation", reservation);
                                startActivity(intent);
                            }
                        });

                        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_popup_close);
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                mPopupWindow.dismiss();
                            }
                        });

                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.my_reservations_list), Gravity.CENTER,0,0);
                    }
                });
                Log.i(TAG, "populateViewHolder: success");
            }
        };

        recyclerView.setAdapter(adapter);

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
        //Todo
        Toast.makeText(getApplicationContext() , "handle spam", Toast.LENGTH_LONG).show();
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
