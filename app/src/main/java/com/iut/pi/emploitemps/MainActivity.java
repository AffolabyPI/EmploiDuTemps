package com.iut.pi.emploitemps;

import android.app.Activity;
import android.content.Intent;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    private Spinner spinGroupe;
    private Spinner formation;
    private Button button;
    private String groupe;
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
                if(formation.getSelectedItem().toString().trim().equals("Info")){
                    spinGroupe.setAdapter(groupeInfo);
                }else{
                    spinGroupe.setAdapter(groupeGeii);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //String test = new JSONParser().getJSONFromUrl("http://www.w3schools.com/website/customers_mysql.php");
        //Toast.makeText(getApplicationContext(), test, Toast.LENGTH_LONG).show();
    }

    public void doOk(View view) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        Bundle bundle = new Bundle();
        groupe = spinGroupe.getSelectedItem().toString().trim();
        bundle.putString("Groupe", groupe);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;

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
