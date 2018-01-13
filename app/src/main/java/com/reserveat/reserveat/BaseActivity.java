package com.reserveat.reserveat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity{
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Homepage:
                Intent intent_home = new Intent(getApplicationContext(), MainActivity.class );
                startActivity(intent_home);
                return true;
            case R.id.Profile:
                Intent intent_profile = new Intent(getApplicationContext(), ProfileActivity.class );
                startActivity(intent_profile);
                return true;
            case R.id.MyReservations:
                Intent intent_res_list = new Intent(getApplicationContext(), MyReservationsListActivity.class );
                startActivity(intent_res_list);
                return true;
            case R.id.pickedReservationsList:
                Intent intent_picked_res_list = new Intent(getApplicationContext(), MyReservationsListActivity.class );
                intent_picked_res_list.putExtra("isMyReservations" , false);
                startActivity(intent_picked_res_list);
                return true;
            case R.id.myNotifications:
                Intent intent_my_notifications_list = new Intent(getApplicationContext(), MyNotificationsListActivity.class );
                startActivity(intent_my_notifications_list);
                return true;
            case R.id.statistics:
                Intent intent_statistics = new Intent(getApplicationContext(), StatisticsActivity.class );
                startActivity(intent_statistics);
                return true;
            case R.id.contactUs:
                Intent intent_contact = new Intent(getApplicationContext(), ContactActivity.class );
                startActivity(intent_contact);
                return true;
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class );
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
