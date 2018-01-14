package com.reserveat.reserveat;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.MultipleChoiceDialog;
import com.reserveat.reserveat.common.dialogFragment.ChoiceDialogs.BaseChoiceDialog;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DateUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;
import com.reserveat.reserveat.common.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.places.Place.TYPE_RESTAURANT;

public class StatisticsActivity extends BaseActivity implements BaseChoiceDialog.NoticeDialogListener{

    private static final String TAG = "StatisticsActivity";
    private String placeID;
    private EditText branchEditText;
    private FirebaseUser currentUser;
    private GraphView statisticsGraph;
    private TextView timeOfDayChoiceText;
    private  DateUtils.Day[] daysArr = DateUtils.Day.values();
    private  DateUtils.TimeOfDay[] timeOfDayArr = DateUtils.TimeOfDay.values();
    private List<Integer> timeOfDayChoiceList = new ArrayList<>();
    private int[] timeOfDayColor = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK};
    private ImageButton infoMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        infoMsg = findViewById(R.id.infoMsgButton);
        infoMsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(StatisticsActivity.this).create();
                alertDialog.setMessage(getResources().getString(R.string.StatisticsInfo));

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();
            }
        });
        
        branchEditText = findViewById(R.id.branch);
        statisticsGraph = findViewById(R.id.graph);

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_RESTAURANT).setCountry("IL")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                placeID = place.getId();
                branchEditText.setText(place.getAddress());
                branchEditText.setVisibility(View.VISIBLE);
                DBUtils.addingPlaceToDB(place, TAG);
                autocompleteFragment.setMenuVisibility(false);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        timeOfDayChoiceText = findViewById(R.id.timeOfDayChoice);
        timeOfDayChoiceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseChoiceDialog newFragment = new MultipleChoiceDialog();
                DialogUtils.initChoiceDialog(newFragment, R.string.timeOfDay, R.array.timeOfDayOptions,-1);
                newFragment.show(getFragmentManager(), "SingleChoiceDialog");
            }
        });

        Button saveButton = findViewById(R.id.search);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(successFieldVerification()){
                    calculateStatistics();
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(StatisticsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean successFieldVerification() {
        //String restaurant = restaurantEditText.getText().toString().trim();
        String branch = branchEditText.getText().toString().trim();
        String timeOfDay = timeOfDayChoiceText.getText().toString().trim();

        String[] mandatoryFieldsValues = {timeOfDay, branch};
        TextView[] formTextViewArr = {timeOfDayChoiceText, branchEditText};//order desc
        int[] formTextViewErrCodeArr = new int[formTextViewArr.length];
        View focusView = null;

        for (int i = 0 ; i < formTextViewErrCodeArr.length ; i ++ ){
            formTextViewErrCodeArr[i] = ValidationUtils.isEmptyTextField(mandatoryFieldsValues[i]);
        }

        for (int i = 0; i < formTextViewArr.length; i ++){
            int res = formTextViewErrCodeArr[i];
            TextView textView = formTextViewArr[i];
            if(res != 0){//error
                textView.setError(getString(res));
                focusView = textView;
            }else{
                textView.setError(null);// Reset error.
            }
        }
        if (focusView != null) {
            Log.w(TAG, "fields verification error: field was entered incorrect");
            focusView.requestFocus();
            return false;
        } else {
            Log.i(TAG, "fields verification: success");
            return true;
        }
    }


    private void calculateStatistics() {

        statisticsGraph.setVisibility(View.GONE);
        statisticsGraph.removeAllSeries();

        final DatabaseReference countResRef = DBUtils.getDatabaseRef().child("statistics").child(placeID);
        final double[][] countReservations = new double[timeOfDayArr.length][daysArr.length];
        countResRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                    DateUtils.Day day = DateUtils.Day.valueOf(daySnapshot.getKey());
                    for (DataSnapshot timeOfDaysnapshot : daySnapshot.getChildren()) {
                        DateUtils.TimeOfDay timeOfDay = DateUtils.TimeOfDay.valueOf(timeOfDaysnapshot.getKey());
                        int count = timeOfDaysnapshot.getValue(Integer.class);
                        countReservations[timeOfDay.getIndex() - 1][day.getIndex() - 1] += count;
                    }
                }

                final DatabaseReference userRef = DBUtils.getDatabaseRef().child("statistics").child("totalNumOfDays");

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int[] totalNumOfDays = new int[daysArr.length];
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                                DateUtils.Day day = DateUtils.Day.valueOf(daySnapshot.getKey());
                                totalNumOfDays[day.getIndex() - 1] = daySnapshot.getValue(Integer.class);
                            }
                        }
                        for (int i = 0; i < timeOfDayArr.length; i++) {
                            for (int j = 0; j < daysArr.length; j++) {
                                if(totalNumOfDays[j] == 0){
                                    countReservations[i][j] = 0;
                                }else{
                                    countReservations[i][j] = countReservations[i][j] / totalNumOfDays[j];
                                }

                            }
                        }
                        createGraph(countReservations);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void createGraph(double[][] countReservation) {

        //int i = 0;
        int count = 0;

        for(int i = 0 ; i < timeOfDayArr.length; i++){
            if(timeOfDayChoiceList.contains(i + 1)){
                addSeries(countReservation[i], timeOfDayColor[count], timeOfDayArr[i].name());
                count++;
            }

        }
//        for (int timeOfDayInt : timeOfDayChoiceList){
//            addSeries(countReservation[timeOfDayInt - 1], timeOfDayColor[i], timeOfDayArr[timeOfDayInt - 1].name());
//            i++;
//        }

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(statisticsGraph);
        staticLabelsFormatter.setHorizontalLabels(getXLabels());
        statisticsGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        statisticsGraph.getGridLabelRenderer().setTextSize(50f);
        statisticsGraph.getGridLabelRenderer().setNumHorizontalLabels(DateUtils.Day.values().length);
        statisticsGraph.getLegendRenderer().setVisible(true);
        statisticsGraph.getLegendRenderer().setFixedPosition(0, 0);
        statisticsGraph.getGridLabelRenderer().reloadStyles();

        statisticsGraph.setVisibility(View.VISIBLE);
    }


    String[] getXLabels() {
        String[] labels = new String[daysArr.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = DateUtils.Day.values()[i].name().substring(0, 3);
        }
        return labels;
    }

    private void addSeries(double[] data, int color, String title) {

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();

        for (int i = 0; i < data.length; i++) {
            series1.appendData(new DataPoint(i, data[i]), true, data.length);
        }

        series1.setTitle(title);
        series1.setColor(color);
        series1.setDrawDataPoints(true);
        series1.setDataPointsRadius(8);
        series1.setThickness(5);

        statisticsGraph.addSeries(series1);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int dialogIndex, float result) {
        // Do nothing
    }

    @Override
    public void onDialogPositiveClickMultipleChoice(DialogFragment dialog, int dialogIndex, List<Integer> mSelectedItems) {
        timeOfDayChoiceList = mSelectedItems;
        StringBuilder items = new StringBuilder();
        int numOfChosenItems = timeOfDayChoiceList.size();
        int countItems = 0;

        for(int i = 0 ; i < timeOfDayArr.length; i ++){
            if(timeOfDayChoiceList.contains(i + 1)){
                countItems++;
                items.append(timeOfDayArr[i].name());
                if(countItems < numOfChosenItems){
                    items.append(", ");
                }
            }
        }

        timeOfDayChoiceText.setText(items.toString());
    }

}

