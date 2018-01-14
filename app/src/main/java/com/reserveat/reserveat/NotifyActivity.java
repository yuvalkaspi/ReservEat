package com.reserveat.reserveat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DateUtils;
import com.reserveat.reserveat.common.dbObjects.NotificationRequest;
import com.reserveat.reserveat.common.utils.ValidationUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.location.places.Place.TYPE_RESTAURANT;

public class NotifyActivity extends BaseActivity {

    private static final String TAG = "NotifyActivity";
    private EditText restaurantEditText;
    private EditText dateEditText;
    private EditText hourEditText;
    private EditText numOfPeopleEditText;
    private EditText branchEditText;
    private EditText descriptionEditText;
    private Switch isFlexibleSwitch;
    private Calendar current = Calendar.getInstance();
    private FirebaseUser currentUser;
    private String restaurant = "";
    private String placeID;
    private ImageButton infoMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        infoMsg = findViewById(R.id.infoMsgButton);
        infoMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(NotifyActivity.this).create();
                alertDialog.setMessage(getResources().getString(R.string.notificationInfo));

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();
            }
        });


        //Calendar myCalendar = Calendar.getInstance();
        branchEditText = findViewById(R.id.branch);
        branchEditText.setKeyListener(null); // make branch uneditable

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_RESTAURANT).setCountry("IL")
                .build();

        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint("Enter Restaurant");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if(place.getPlaceTypes().contains(TYPE_RESTAURANT)){
                    Log.i(TAG, "Place: " + place.getName());
                    restaurant = place.getName().toString();
                    placeID = place.getId();
                    branchEditText.setText(place.getAddress());
                    branchEditText.setVisibility(View.VISIBLE);
                    DBUtils.addingPlaceToDB(place, TAG);
                    autocompleteFragment.setMenuVisibility(false);
                }else{
                    autocompleteFragment.setText("");
                    Toast.makeText(NotifyActivity.this, "place is not a restaurant\nplease enter again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // click on autocompleteFragment clear button
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        // ((EditText) autocompleteFragment.getView()
                        // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                        autocompleteFragment.setText("");
                        view.setVisibility(View.GONE);
                        branchEditText.setText("");
                        branchEditText.setVisibility(View.GONE);
                    }
                });

        dateEditText = findViewById(R.id.date);
        hourEditText = findViewById(R.id.hour);
        numOfPeopleEditText = findViewById(R.id.numOfPeople);
        descriptionEditText = findViewById(R.id.description);
        Button saveButton = findViewById(R.id.save);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = current.get(Calendar.YEAR);
                int month = current.get(Calendar.MONTH);
                int day = current.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(NotifyActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        DateFormat dateFormat = new SimpleDateFormat(DateUtils.dateFormatUser, Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        Date dateObj = calendar.getTime();
                        dateEditText.setText(dateFormat.format(dateObj));
                    }
                },year,month,day);
                dpd.show();
            }
        });

        hourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = current.get(Calendar.HOUR_OF_DAY);
                int minutes = current.get(Calendar.MINUTE);
                TimePickerDialog tpd = new TimePickerDialog(NotifyActivity.this , R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        DateFormat dateFormat = new SimpleDateFormat(DateUtils.hourFormat, Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minutes);
                        Date dateObj = calendar.getTime();
                        hourEditText.setText(dateFormat.format(dateObj));
                    }
                },hour,minutes,false);
                tpd.show();
            }
        });

        isFlexibleSwitch = findViewById(R.id.isFlexible);
        isFlexibleSwitch.setText("Is time flexible?");
        isFlexibleSwitch.setChecked(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyOnCancel();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(NotifyActivity.this, LoginActivity.class );
            startActivity(intent);
        }
    }



    private void notifyOnCancel() {

        try {
            String branch = branchEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String hour = hourEditText.getText().toString().trim();
            String numOfPeople = numOfPeopleEditText.getText().toString().trim();
            boolean isFlexible = isFlexibleSwitch.isChecked();
            String description = descriptionEditText.getText().toString().trim();

            if (!isValidValues(description, numOfPeople, date, hour, descriptionEditText, numOfPeopleEditText, dateEditText, hourEditText)){
                return;
            }

            String newFullDateString = "";
            if(ValidationUtils.isEmptyTextField(date) == 0) {//date exists
                newFullDateString = DateUtils.switchDateFormat(date, DateUtils.dateFormatUser, DateUtils.dateFormatDB) + " " + hour;
            }
            //check if a reservation is already exist
            NotificationRequest notificationRequest = new NotificationRequest(currentUser.getUid(), restaurant, branch, placeID, newFullDateString, Integer.valueOf(numOfPeople), isFlexible, description);
            addNotificationRequestToDB(notificationRequest);

        } catch (ParseException e) {
            Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }
    }

    /*  Receives all the data that a user inserted to the feilds in the notification form
    *  returns true if at least one of them is not empty
    *  otherwise returns false
    * */
    private boolean isValidValues(String description, String numOfPeople, String date, String hour, EditText descriptionEditText, EditText numOfPeopleEditText, EditText dateEditText, EditText hourEditText ) {
        int resNumOfPeople = ValidationUtils.isEmptyTextField(numOfPeople);
        int resDate = ValidationUtils.isEmptyTextField(date);
        int resHour = ValidationUtils.isEmptyTextField(hour);
        int resDescription = ValidationUtils.isEmptyTextField(description);

        View focusView = null;
        numOfPeopleEditText.setError(null);
        hourEditText.setError(null);
        dateEditText.setError(null);
        descriptionEditText.setError(null);

        if(resDescription != 0){
            descriptionEditText.setError(getString(resDescription));
            focusView = descriptionEditText;
        }
        if(resNumOfPeople != 0){
            numOfPeopleEditText.setError(getString(resNumOfPeople));
            focusView = numOfPeopleEditText;
        }
        if((resDate != 0 && resHour == 0)){
            dateEditText.setError(getString(resDate));
            focusView = dateEditText;
        }
        if((resDate == 0 && resHour != 0)){
            hourEditText.setError(getString(resHour));
            focusView = hourEditText;
        }
        if (focusView != null) {
            Log.w(TAG, "fields verification error: field was entered incorrect");
            focusView.requestFocus();
            return false;
        }
        return true;
    }


    private void addNotificationRequestToDB(NotificationRequest notificationRequest){
        Log.i(TAG, "adding a new notification request to DB");

        String key = DBUtils.getDatabaseRef().child("notificationRequests").push().getKey();
        Map<String, Object> notificationRequestValues = notificationRequest.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/notificationRequests/" + key, notificationRequestValues);
        childUpdates.put("/users/" + currentUser.getUid() + "/notificationRequests/" + key, notificationRequestValues);

        DBUtils.getDatabaseRef().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "add new notification request: success", task.getException());
                    Toast.makeText(NotifyActivity.this, "Your notification request was saved!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NotifyActivity.this, MainActivity.class );
                    startActivity(intent);
                }else{
                    Log.w(TAG, "add new notification request: failure", task.getException());
                    Toast.makeText(NotifyActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
