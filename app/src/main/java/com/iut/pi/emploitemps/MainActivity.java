package com.iut.pi.emploitemps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
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

    public void doOk(View view) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        Bundle bundle = new Bundle();
        groupe = spinGroupe.getSelectedItem().toString().trim();
        bundle.putString("Groupe", groupe);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static JSONObject requestWebService(String service) {
        JSONObject jsonObject = null;
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL("https://httpbin.org/get");
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(125);
            urlConnection.setReadTimeout(125);

            //Handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                System.out.println("Pull up");
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Pull out");
            }

            //Create JSON object from content
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));
        } catch (Exception e) {
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    private static void disableConnectionReuseIfNecessary() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
            System.setProperty("http.keepAlive", "false");
    }

    private static String getResponseText(InputStream inStream){
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}
