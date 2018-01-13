package com.reserveat.reserveat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reserveat.reserveat.common.dbObjects.NotificationRequest;
import com.reserveat.reserveat.common.dbObjects.NotificationRequestHolder;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.NotificationRequestListDialog;
import com.reserveat.reserveat.common.utils.DialogUtils;
import com.reserveat.reserveat.common.utils.ReservationUtils;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.dbObjects.Reservation;


public class MyNotificationsListActivity extends BaseActivity  {

    private static final String TAG = "MyNotificationsActivity";
    FirebaseUser currentUser;
    private PopupWindow mPopupWindow;
    boolean isMyReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications_list);
        String currentUserID = DBUtils.getCurrentUserID();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("notificationRequests");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<NotificationRequest, NotificationRequestHolder> adapter = new FirebaseRecyclerAdapter<NotificationRequest, NotificationRequestHolder>(NotificationRequest.class, R.layout.notification_request,NotificationRequestHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(NotificationRequestHolder viewHolder, NotificationRequest model, int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setIsActive(model.getIsActive());

                final NotificationRequest notificationRequest = model;
                viewHolder.setOnClickListener(new NotificationRequestHolder.ClickListener() {
                      @Override
                    public void onItemClick(View view, int position) {
                          final String key = getRef(position).getKey();

                          NotificationRequestListDialog newFragment = new NotificationRequestListDialog();
                          DialogUtils.initContentDialog(newFragment, R.string.notification_request, R.layout.notification_request_dialog,key);
                          newFragment.show(getFragmentManager(), "NotificationRequestListDialog");
                    }

                    @Override
                    public void onSwitchClick(boolean isChecked, int position) {
                        final String key = getRef(position).getKey();
                        DBUtils.setNotificationRequestActive(key, isChecked);
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
        if (currentUser == null){
            Intent intent = new Intent(MyNotificationsListActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }
}
