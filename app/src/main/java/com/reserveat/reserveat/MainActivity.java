package com.reserveat.reserveat;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.Reservation;
import com.reserveat.reserveat.common.ReviewData;
import com.reserveat.reserveat.common.ReservationHolder;
import com.reserveat.reserveat.common.SortDialogFragment;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SortDialogFragment.NoticeDialogListener {

    private final String[] sortBy = {"date","numOfPeople"};
    private static final String TAG = "MainActivity";
    DatabaseReference mDatabase;
    DatabaseReference myDatabase;
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    String key;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.add_cancellation);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class );
                startActivity(intent);
            }
        });

        Button notifyButton = findViewById(R.id.notification);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotifyActivity.class );
                startActivity(intent);
            }
        });

        Button sortButton = findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new SortDialogFragment();
                newFragment.show(getFragmentManager(), "SortDialogFragment");
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("reservations");
        myDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createAdapter("date");
    }

    private void createAdapter(String orderByOption) {

        Log.i(TAG, "Sorting reservations by " + orderByOption);

        final FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase.orderByChild(orderByOption)) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try{
                    Common.myPopulateViewHolder(viewHolder, model);
                    final Reservation reservation = model;
                    viewHolder.setOnClickListener(new ReservationHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            key = getRef(position).getKey();
                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View customView = inflater.inflate(R.layout.pop_up_reservation_layout,null);
                            mPopupWindow = new PopupWindow(
                                    customView,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            mPopupWindow.setElevation(5.0f);

                            TextView keyTextView = (TextView) customView.findViewById(R.id.tv);
                            keyTextView.setText("restaurant is " + reservation.getRestaurant());

                            Button closeButton = (Button) customView.findViewById(R.id.ib_close);
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Dismiss the popup window
                                    mPopupWindow.dismiss();
                                }
                            });

                            Button pickButton = (Button) customView.findViewById(R.id.pick_Button);
                            pickButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popUpPickClick(reservation, customView);
                                    reservation.setPicker(currentUser.getUid());
                                    Map<String, Object> reservationValues = reservation.toMap();
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    //Map<String, String> surveyValues = createNewSurveyValues(reservation);

                                    childUpdates.put("/users/" + currentUser.getUid() + "/pickedReservations/" + key, reservationValues);
                                    childUpdates.put("/historyReservations/" + key, reservationValues);
                                    childUpdates.put("/reservations/" + key, null);
                                   // childUpdates.put("/users/" + currentUser.getUid() + "/surveys/" + key, surveyValues);
                                    myDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(TAG, "pick reservation:success", task.getException());
                                                mPopupWindow.dismiss();
                                            } else {
                                                Log.w(TAG, "pick reservation:failure", task.getException());
                                                Toast.makeText(getApplicationContext() , "Error!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                                }
                            });
                            mPopupWindow.setFocusable(true);
                            mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.activity_main_page), Gravity.CENTER,0,0);

                        }
                    });
                    Log.i(TAG, "populateViewHolder: success");
                }catch(ParseException e){
                    Toast.makeText(MainActivity.this, "error!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "populateViewHolder: failure");
                }
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private Map<String, String> createNewSurveyValues(Reservation reservation) {

        ReviewData survey = new ReviewData(reservation.getRestaurant(), reservation.getBranch(), reservation.getDate());
        return survey.toMap();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int index) {
        createAdapter(sortBy[index]);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

    private void popUpPickClick(final Reservation reservation, View customView) {
        final Button pickButton = (Button) customView.findViewById(R.id.pick_Button);
        final TextView nameFormTextView = (TextView) customView.findViewById(R.id.popup_name_form);
        final TextView nameTextView = (TextView) customView.findViewById(R.id.popup_name);
        final TextView noteTextView = (TextView) customView.findViewById(R.id.popup_note);

        reservation.setPicker(currentUser.getUid());
        Map<String, Object> reservationValues = reservation.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + currentUser.getUid() + "/pickedReservations/" + key, reservationValues);
        childUpdates.put("/historyReservations/" + key, reservationValues);
        childUpdates.put("/reservations/" + key, null);

        popUpDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "pick reservation:success", task.getException());
                    pickButton.setVisibility(View.GONE);
                    nameFormTextView.setVisibility(View.VISIBLE);
                    nameFormTextView.setText("Reservation name is : ");
                    nameTextView.setVisibility(View.VISIBLE);
                    nameTextView.setText(reservation.getReservationName());
                    noteTextView.setVisibility(View.VISIBLE);
                    noteTextView.setText("*note it is your responsibility to validate the resrvation");

                } else {
                    Log.w(TAG, "pick reservation:failure", task.getException());
                    Toast.makeText(getApplicationContext() , "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
//            String name = currentUser.getDisplayName();
//            Toast.makeText(getApplicationContext(),"Hello " + name, Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class );
                startActivity(intent);
                return true;
            case R.id.MyReservations:
                Intent intent_res_list = new Intent(MainActivity.this, MyReservationsListActivity.class );
                startActivity(intent_res_list);
                return true;
            case R.id.mySurveys:
                Intent mySurveyIntent = new Intent(MainActivity.this, MySurveyActivity.class );
                startActivity(mySurveyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
