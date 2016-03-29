package com.iut.pi.emploitemps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursActivity extends AppCompatActivity {
    // JSON Node names
    private static final String ALIAS = "alias";
    private static final String CODE_PROF = "codeProf";
    private static final String COULEUR_FOND = "couleurFond";
    private static final String NOM = "nom";
    private static final String PRENOM = "^prenom";
    private String jsonStr;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cours);

        listView = (ListView) findViewById(R.id.listCours);

        Intent bundle = getIntent();
        jsonStr = bundle.getStringExtra("JSON");
        new GetContacts().execute();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            /*pDialog = new ProgressDialog(CoursActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();*/

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<ArrayList<Prof>>(){

                }.getType();
                List<Prof> profs = gson.fromJson(jsonStr, type);

                for(Prof prof: profs){
                    Log.d("Test", prof.getId());
                }


                /*JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray contacts = jsonObj.getJSONArray("");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String alias = c.getString(ALIAS);
                    String id = c.getString(CODE_PROF);
                    String couleur = c.getString(COULEUR_FOND);
                    String nom = c.getString(NOM);
                    String prenom = c.getString(PRENOM);

                    // tmp hashmap for single contact
                    HashMap<String, String> contact = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    contact.put(ALIAS, alias);
                    contact.put(CODE_PROF, id);
                    contact.put(COULEUR_FOND, couleur);
                    contact.put(NOM, nom);
                    contact.put(PRENOM, prenom);

                    // adding contact to contact list
                    contactList.add(contact);

                }*/
            } catch (Exception e) {
                Log.e("ServiceHandler", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    CoursActivity.this, contactList,
                    R.layout.list_item, new String[]{CODE_PROF, NOM,
                    PRENOM}, new int[]{R.id.id,
                    R.id.name, R.id.name2});

            listView.setAdapter(adapter);
        }

    }
}
