package com.reserveat.reserveat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reserveat.reserveat.common.dbObjects.NotificationRequest;
import com.reserveat.reserveat.common.dbObjects.NotificationRequestHolder;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.NotificationContentDialog;
import com.reserveat.reserveat.common.dialogFragment.contentDialogs.NotificationRequestListDialog;
import com.reserveat.reserveat.common.utils.DBUtils;


public class MyNotificationsListActivity extends BaseActivity  {

    private static final String TAG = "MyNotificationsActivity";
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FirebaseRecyclerAdapter<NotificationRequest, NotificationRequestHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications_list);
        String currentUserID = DBUtils.getCurrentUserID();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("notificationRequests");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.empty_view);

        adapter = new FirebaseRecyclerAdapter<NotificationRequest, NotificationRequestHolder>(NotificationRequest.class, R.layout.notification_request,NotificationRequestHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(NotificationRequestHolder viewHolder, NotificationRequest model, int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setIsActive(model.getIsActive());

                final NotificationRequest notificationRequest = model;
                viewHolder.setOnClickListener(new NotificationRequestHolder.ClickListener() {
                      @Override
                    public void onItemClick(View view, int position) {
                          final String key = getRef(position).getKey();

                          NotificationContentDialog newFragment = new NotificationRequestListDialog();
                          NotificationContentDialog.initInstance(newFragment ,key, notificationRequest);
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

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference notificationsRef = DBUtils.getDatabaseRef().child("users").child(DBUtils.getCurrentUserID()).child("notificationRequests");
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

}
