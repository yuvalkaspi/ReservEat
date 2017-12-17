package com.reserveat.reserveat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MySurveyActivity extends AppCompatActivity {

    private List<Button> surveyToFillList;
    private List<Button> surveyFilledList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_survey);

        //TODO: adapt this code to list of buttons
        Button surveyButton = findViewById(R.id.SURVEY1);
        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MySurveyActivity.this, SurveyForm.class );
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
