package com.iut.pi.emploitemps;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        extras = getIntent().getExtras();
        String result = extras.getString("Groupe");

        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
    }

}
