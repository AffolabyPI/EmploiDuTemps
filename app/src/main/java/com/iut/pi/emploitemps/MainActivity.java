package com.iut.pi.emploitemps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private Spinner spinGroupe;
    private Spinner formation;
    private Button button;
    private String groupe, json;
    private ImageView img;
    private ArrayAdapter groupeInfo;
    private ArrayAdapter groupeGeii;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinGroupe = (Spinner) findViewById(R.id.spin);
        formation = (Spinner) findViewById(R.id.spinGroupe);
        button = (Button) findViewById(R.id.button);
        img = (ImageView) findViewById(R.id.imageViewMain);


        groupeInfo = ArrayAdapter.createFromResource(this, R.array.groupeDutInfo, android.R.layout.simple_spinner_item);
        groupeInfo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        groupeGeii = ArrayAdapter.createFromResource(this, R.array.groupeDutGeii, android.R.layout.simple_spinner_item);
        groupeGeii.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter choixFormation = ArrayAdapter.createFromResource(this, R.array.formation, android.R.layout.simple_spinner_item);
        choixFormation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        formation.setAdapter(choixFormation);
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

        try {
            url = new URL("http://agile.pierrebourgeois.fr:8080/v1/prof");
            new JSONParser().execute(url);
            boolean exit = false;
            while(!exit){
                if(json != null){
                    Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
                    exit = true;
                }
            }
        } catch (Exception e) {
            Log.e("Nose", e.toString());
        }
    }

    public void doOk(View view) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        Bundle bundle = new Bundle();
        groupe = spinGroupe.getSelectedItem().toString().trim();
        bundle.putString("Groupe", groupe);
        bundle.putString("JSON", json);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class JSONParser extends AsyncTask<URL, Void, String> {
        InputStream is = null;

        @Override
        protected String doInBackground(URL... request) {
           return getJSONFromUrl(request[0]);
        }

        public String getJSONFromUrl(URL request) {
            // Making HTTP request
            try {
                HttpURLConnection connect = (HttpURLConnection) request.openConnection();
                connect.setRequestMethod("GET");

                //Toast.makeText(getApplicationContext(), "" + connect.getResponseCode(), Toast.LENGTH_LONG).show();
                is = new BufferedInputStream(connect.getInputStream());
                String response = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                MainActivity.this.json = response;
                return response;
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            return "";
        }

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
