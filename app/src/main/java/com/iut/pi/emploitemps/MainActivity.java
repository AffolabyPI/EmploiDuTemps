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

public class MainActivity extends Activity {
    private Spinner formation;
    private Spinner spinGroupe;
    private Button button;
    private String groupe, json;
    private ImageView img;
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
        setContentView(R.layout.activity_main);

        formation = (Spinner) findViewById(R.id.spin);
        spinGroupe = (Spinner) findViewById(R.id.spinGroupe);
        button = (Button) findViewById(R.id.button);
        img = (ImageView) findViewById(R.id.imageViewMain);

        ArrayAdapter choixFormation = ArrayAdapter.createFromResource(this, R.array.formation, android.R.layout.simple_spinner_item);
        choixFormation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinGroupe.setAdapter(choixFormation);

        Intent init = getIntent();
        groupeInfo = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, init.getStringArrayExtra("tab"));
        formation.setAdapter(groupeInfo);

        try {
            url = new URL("http://agile.pierrebourgeois.fr:8080/v1/groupe");
            new JSONParser().execute(url);
            boolean exit = false;
            while (!exit) {
                if (json != null) {
                    exit = true;
                }
            }

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }


        spinGroupe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinGroupe.getSelectedItem().toString().trim().equals("Info")) {
                    populateTab();
                    groupeInfo = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, tab);
                    groupeInfo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    formation.setAdapter(groupeInfo);
                } else {
                    populateTab();
                    groupeGeii = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, tab);
                    groupeGeii.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    formation.setAdapter(groupeGeii);
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
        groupe = formation.getSelectedItem().toString().trim();
        bundle.putString("Groupe", groupe);
        bundle.putString("JSON", json);
        bundle.putString("Code", getCode(formation.getSelectedItem().toString()));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public String getCode(String nom){
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<ArrayList<Groupe>>() {
        }.getType();
        List<Groupe> groupes = gson.fromJson(json, type);

        for (Groupe groupe : groupes){
            if(groupe.getIdentifiant().equals(nom) || groupe.getNom().equals(nom)){
                return groupe.getCodeGroupe();
            }
        }
        return null;
    }

    public void populateTab(){
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
            if ((groupe.getIdentifiant().startsWith("BIO") || groupe.getNom().contains("BIO")) && spinGroupe.getSelectedItem().equals("Bio")) {
                contact.put(ALIAS, groupe.getAlias());
                contact.put(CODE_GROUPE, groupe.getCodeGroupe());
                contact.put(COULEUR_FOND, groupe.getCouleur());
                contact.put(NOM, groupe.getNom());
                contact.put(IDENTIFIANT, groupe.getIdentifiant());
                // adding contact to contact list
                contactList.add(contact);
                list.add(groupe.getIdentifiant());
            } else if((groupe.getIdentifiant().startsWith("N") || groupe.getNom().contains("I-N")) && spinGroupe.getSelectedItem().equals("Info")){
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
        for (int i = 0; i < list.size(); i++){
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
