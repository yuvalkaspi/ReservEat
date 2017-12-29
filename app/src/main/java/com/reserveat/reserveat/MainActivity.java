package com.reserveat.reserveat;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.reserveat.reserveat.common.DBUtils;
import com.reserveat.reserveat.common.Reservation;
import com.reserveat.reserveat.common.ReservationHolder;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogFragment;
import com.reserveat.reserveat.common.dialogFragment.OurDialogFragment;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OurDialogFragment.NoticeDialogListener {

    private final String[] sortBy = {"date", "numOfPeople", "hotness"};
    private final Boolean[] sortByDescOrder = {false , false, true};
    private static final String TAG = "MainActivity";
    DatabaseReference mDatabase;
    DatabaseReference popUpDatabase;
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    LinearLayoutManager linearLayoutManager;
    String key;
    private PopupWindow mPopupWindow;
    public static int numOfStarsPerPick = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addButton = findViewById(R.id.add_cancellation);
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
                OurDialogFragment newFragment = new ChoiceDialogFragment();
                OurDialogFragment.initDialog(newFragment, R.string.sort_by, R.array.SortByOptions,-1);
                newFragment.show(getFragmentManager(), "ChoiceDialogFragment");
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("reservations");
        popUpDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        createAdapter("date",false);
    }

    private void createAdapter(String orderByOption, Boolean sortByDescOrder) {

        Log.i(TAG, "Sorting reservations by " + orderByOption);

        final FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase.orderByChild(orderByOption)) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try{
                    Common.myPopulateViewHolder(viewHolder, model);
                }catch(ParseException e){
                    Toast.makeText(MainActivity.this, "error!", Toast.LENGTH_LONG).show();
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

                        Common.popUpWindowCreate(mPopupWindow, customView, reservation);

                        Button pickButton = (Button) customView.findViewById(R.id.pick_Button);
                        pickButton.setVisibility(View.VISIBLE);
                        pickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popUpPickClick(reservation, customView);
                            }
                        });
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.activity_main_page), Gravity.CENTER,0,0);
                    }
                });
                Log.i(TAG, "populateViewHolder: success");
            }
        };
        linearLayoutManager.setStackFromEnd(sortByDescOrder);
        linearLayoutManager.setReverseLayout(sortByDescOrder);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result) {
        int index = (int)result - 1;
        createAdapter(sortBy[index], sortByDescOrder[index]);
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
                    noteTextView.setText(" it is your responsibility to validate the resrvation");
                    DBUtils.updateStarsToUser(numOfStarsPerPick);

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
                Intent mySurveyIntent = new Intent(MainActivity.this, MyReviewActivity.class );
                startActivity(mySurveyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
