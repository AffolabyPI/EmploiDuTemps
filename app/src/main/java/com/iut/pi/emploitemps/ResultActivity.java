package com.iut.pi.emploitemps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ResultActivity extends Activity {
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
