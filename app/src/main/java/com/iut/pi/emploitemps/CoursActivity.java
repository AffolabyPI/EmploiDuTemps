package com.iut.pi.emploitemps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursActivity extends AppCompatActivity {
    // JSON Node names
    private static final String ALIAS = "alias";
    private static final String CODE_GROUPE = "codeGroupe";
    private static final String CODE_SEANCE = "codeSeance";
    private static final String DATE_SEANCE = "dateSeance";
    private static final String DUREE_SEANCE = "dureeSeance";
    private static final String HEURE_SEANCE = "heureSeance";
    private static final String MODULE_NOM = "moduleNom";
    private static final String NOM_GROUPE = "nomGroupe";
    private static final String NOM_PROF = "nomProf";
    private String jsonStr;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cours);

        listView = (ListView) findViewById(R.id.listCours);

        Intent bundle = getIntent();
        jsonStr = bundle.getStringExtra("JSON2");
        Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();
        new GetContacts().execute();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<ArrayList<Cours>>() {
                }.getType();
                List<Cours> cours = gson.fromJson(jsonStr, type);

                for (Cours c : cours) {
                    Log.d("Test", c.getCodeGroupe());
                    HashMap<String, String> contact = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    contact.put(ALIAS, c.getAlias());
                    contact.put(CODE_GROUPE, c.getCodeGroupe());
                    contact.put(CODE_SEANCE, c.getCodeSeance());
                    contact.put(DATE_SEANCE, c.getDateSeance());
                    contact.put(DUREE_SEANCE, c.getDureeSeance());
                    if (c.getHeureSeance().length() > 3) {
                        String lel = "";
                        lel = c.getHeureSeance().substring(0, 2) + "h" + c.getHeureSeance().substring(2, c.getHeureSeance().length());
                        contact.put(HEURE_SEANCE, lel);
                    } else {
                        String lel = "";
                        lel = c.getHeureSeance().substring(0, 1) + "h" + c.getHeureSeance().substring(1, c.getHeureSeance().length());
                        contact.put(HEURE_SEANCE, lel);
                    }
                    contact.put(MODULE_NOM, c.getModuleNom());
                    contact.put(NOM_GROUPE, c.getNomGroupe());
                    contact.put(NOM_PROF, c.getNomProf());

                    // adding contact to contact list
                    contactList.add(contact);
                }
            } catch (Exception e) {
                Log.e("ServiceHandler", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    CoursActivity.this, contactList,
                    R.layout.list_item, new String[]{ALIAS, NOM_PROF,
                    HEURE_SEANCE}, new int[]{R.id.id,
                    R.id.name, R.id.name2});

            listView.setAdapter(adapter);
        }

    }
}
