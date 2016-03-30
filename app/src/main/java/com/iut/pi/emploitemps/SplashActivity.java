package com.iut.pi.emploitemps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends Activity {
    private Intent intent;
    private String groupe, json;
    private ArrayAdapter groupeInfo;
    private ArrayAdapter groupeGeii;
    private String[] tab;
    private URL url;
    private static final String ALIAS = "alias";
    private static final String CODE_GROUPE = "codeGroupe";
    private static final String COULEUR_FOND = "couleurFond";
    private static final String NOM = "nom";
    private static final String IDENTIFIANT = "identifiant";
    private ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    private ArrayList list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        intent = new Intent(getApplicationContext(), MainActivity.class);
        try {
            url = new URL("http://agile.pierrebourgeois.fr:8080/v1/groupe");
            new JSONParser().execute(url);
            boolean exit = false;
            while (!exit) {
                if (json != null) {
                    exit = true;
                }
            }
            populateTab();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    intent.putExtra("tab", tab);
                    startActivity(intent);
                    finish();
                }
            }, 3 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateTab() {
        // Filter result

        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<ArrayList<Groupe>>() {
        }.getType();
        List<Groupe> groupes = gson.fromJson(json, type);
        list = new ArrayList<String>();

        for (Groupe groupe : groupes) {
            Log.d("Test", groupe.getCodeGroupe());
            HashMap<String, String> contact = new HashMap<String, String>();

            // adding each child node to HashMap key => value
            if (groupe.getIdentifiant().startsWith("N") || groupe.getNom().contains("I-N")) {
                contact.put(CODE_GROUPE, groupe.getCodeGroupe());
                contact.put(COULEUR_FOND, groupe.getCouleur());
                contact.put(NOM, groupe.getNom());
                contact.put(IDENTIFIANT, groupe.getIdentifiant());
                // adding contact to contact list
                contactList.add(contact);
                list.add(groupe.getIdentifiant());
            }
        }
        tab = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tab[i] = list.get(i).toString();
        }
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

                is = new BufferedInputStream(connect.getInputStream());
                String response = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
                SplashActivity.this.json = response;
                return response;
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            return "";
        }

    }

}
