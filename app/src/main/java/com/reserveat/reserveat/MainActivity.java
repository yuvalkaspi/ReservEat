package com.reserveat.reserveat;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.utils.DialogUtils;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.SingleChoiceDialog;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.BaseChoiceDialog;
import com.reserveat.reserveat.common.utils.ValidationUtils;

import java.text.ParseException;
import java.util.List;

public class MainActivity extends BaseActivity implements BaseChoiceDialog.NoticeDialogListener {


    private final String[] sortBy = {"date", "numOfPeople", "hotness"};
    private final Boolean[] sortByDescOrder = {false , false, true};
    private static final String TAG = "MainActivity";
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    LinearLayoutManager linearLayoutManager;
    String key;


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

        LinearLayout notifyButton = findViewById(R.id.notification);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotifyActivity.class );
                startActivity(intent);
            }
        });

        LinearLayout sortButton = findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new SingleChoiceDialog();
                DialogUtils.initChoiceDialog(newFragment, R.string.sort_by, R.array.SortByOptions,-1);
                newFragment.show(getFragmentManager(), "SingleChoiceDialog");
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("reservations");

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
                    ReservationUtils.myPopulateViewHolder(viewHolder, model);
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
                        PopupWindow mPopupWindow = new PopupWindow(
                                customView,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );


                        ReservationUtils.popUpWindowCreate(mPopupWindow, customView, reservation, MainActivity.this);

                        Button pickButton = (Button) customView.findViewById(R.id.pick_Button);
                        //Todo make gery when uid is current user
                        pickButton.setVisibility(View.VISIBLE);
                        pickButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ReservationUtils.popUpPickClick(reservation, customView, key, currentUser.getUid(), MainActivity.this);
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
    public void onDialogPositiveClickMultipleChoice(DialogFragment dialog, int dialogIndex, List<Integer> mSelectedItems) {
        // Do nothing
    }


    @Override
    public void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //ValidationUtils.isUserValid(currentUser, getApplicationContext(), true);
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class );
            startActivity(intent);
        }else{
            ValidationUtils.showProgress(true, getResources(),findViewById(R.id.recycler_view),findViewById(R.id.main_progress));
            Task<GetTokenResult> x = currentUser.getIdToken(true).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class );
                    startActivity(intent);
                }
            });
            x.addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    ValidationUtils.showProgress(false, getResources(),findViewById(R.id.recycler_view),findViewById(R.id.main_progress));
                }
            });
        }
    }

}
