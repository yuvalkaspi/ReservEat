package com.reserveat.reserveat;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.Common;
import com.reserveat.reserveat.common.Reservation;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements SortDialogFragment.NoticeDialogListener {

    private final String[] sortBy = {"date","numOfPeople"};
    private static final String TAG = "MainActivity";
    DatabaseReference mDatabase;
    RecyclerView recyclerView;

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

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createAdapter("date");
    }

    private void createAdapter(String orderByOption) {

        Log.i(TAG, "Sorting reservations by " + orderByOption);

        FirebaseRecyclerAdapter<Reservation, ReservationHolder> adapter =
                new FirebaseRecyclerAdapter<Reservation, ReservationHolder>(Reservation.class, R.layout.reservation,ReservationHolder.class, mDatabase.orderByChild(orderByOption)) {
            @Override
            protected void populateViewHolder(ReservationHolder viewHolder, Reservation model, int position) {
                try{
                    String date = model.getDate();
                    int indexOfSpace = date.indexOf(" ");
                    String dateOldFormat = date.substring(0, indexOfSpace);
                    String hour = date.substring(indexOfSpace + 1);
                    String dateNewFormat = Common.switchDateFormat(dateOldFormat, Common.dateFormatDB, Common.dateFormatUser);
                    viewHolder.setRestaurant(model.getRestaurant());
                    viewHolder.setBranch(model.getBranch());
                    viewHolder.setDate(dateNewFormat);
                    viewHolder.setHour(hour);
                    viewHolder.setNumOfPeople(model.getNumOfPeople());
                    Log.i(TAG, "populateViewHolder: success");
                }catch(ParseException e){
                    Toast.makeText(MainActivity.this, "error!", Toast.LENGTH_LONG).show();
                    Log.w(TAG, "populateViewHolder: failure");
                }

            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int index) {
        createAdapter(sortBy[index]);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
            case R.id.item3:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
