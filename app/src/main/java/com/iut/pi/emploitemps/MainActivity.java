package com.iut.pi.emploitemps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity {
    private Spinner spinGroupe;
    private Spinner formation;
    private Button button;
    private String groupe;
    //private TextView text;
    private ImageView img;
    private ArrayAdapter groupeInfo;
    private ArrayAdapter groupeGeii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinGroupe = (Spinner) findViewById(R.id.spin);
        formation = (Spinner) findViewById(R.id.spinGroupe);
        button = (Button) findViewById(R.id.button);
        //text = (TextView) findViewById(R.id.textMain);
        img = (ImageView) findViewById(R.id.imageViewMain);

        //text.setTextColor(getResources().getColor(R.color.colorAccent));

        groupeInfo = ArrayAdapter.createFromResource(this, R.array.groupeDutInfo, android.R.layout.simple_spinner_item);
        groupeInfo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        groupeGeii = ArrayAdapter.createFromResource(this, R.array.groupeDutGeii, android.R.layout.simple_spinner_item);
        groupeGeii.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter choixFormation = ArrayAdapter.createFromResource(this, R.array.formation, android.R.layout.simple_spinner_item);
        choixFormation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        formation.setAdapter(choixFormation);

        String tag = "json_obj_req";
        String url = "http://api.androidhive.info/volley/person_object.json";

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading ...");
        mDialog.show();

        JsonObjectRequest json = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Test", response.toString());
                mDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Test", error.toString());
                mDialog.hide();
            }
        });
        try {
            AppController.getInstance().addToRequestQueue(json, tag);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        formation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (formation.getSelectedItem().toString().trim().equals("Info")) {
                    spinGroupe.setAdapter(groupeInfo);
                } else {
                    spinGroupe.setAdapter(groupeGeii);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void doOk(View view) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        Bundle bundle = new Bundle();
        groupe = spinGroupe.getSelectedItem().toString().trim();
        bundle.putString("Groupe", groupe);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
