package com.cable.cloud;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;

import com.cable.cloud.customs.MovableFloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cable.cloud.activities.DashBoardActivity;
import com.cable.cloud.helpers.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.cable.cloud.customs.MDDialog;

public class Comments_List_Activity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    ListView listView;

    ArrayList<HashMap<String, String>> commentsDetails = new ArrayList<>();

    MovableFloatingActionButton fabadd;

    String siteUrl, URL, URL1, uid;

    SimpleAdapter adapter;
    String tag, cmpid, custid, title, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments__list_);

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");

        listView = (ListView) findViewById(R.id.listView5);
        fabadd = (MovableFloatingActionButton) findViewById(R.id.fab);

        Intent j = getIntent();
        cmpid = j.getExtras().getString("Complainid");
        from = j.getExtras().getString("From");
        title = j.getExtras().getString("title");
        custid = j.getExtras().getString("CustomerId");


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        if (pref.getString("RoleId", "").equals("2")) {
            tag = "Web";
        } else {
            tag = "AssignUser";
        }


        URL = siteUrl + "/withtag?complainId=" + cmpid + "&commentTag=" + tag;

        new JSONAsynk().execute(new String[]{URL});


        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = getLayoutInflater();

                View vs = li.inflate(R.layout.comments_dialog, null);

                final EditText edtcomment = (EditText) vs.findViewById(R.id.editText20);

                MDDialog.Builder mdalert = new MDDialog.Builder(Comments_List_Activity.this);
                mdalert.setContentView(vs);
                mdalert.setPositiveButton("POST", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (edtcomment.getText().toString().length() > 0) {

                                String text = edtcomment.getText().toString();

                                URL1 = siteUrl + "/AddComplainCommentForCollectionApp?loginuserId=" + uid + "&complainId=" + cmpid + "&comment=" + URLEncoder.encode(text, "UTF-8");

                                new JSONAsynks().execute(new String[]{URL1});

                            } else {
                                Snackbar.make(v, "Please Enter Comment", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mdalert.setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mdalert.setTitle("Add Comment");
                mdalert.setWidthMaxDp(600);
                mdalert.setShowTitle(true);
                mdalert.setShowButtons(true);
                mdalert.setBackgroundCornerRadius(5);


                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (from.equals("Details")) {
            Intent i = new Intent(getApplicationContext(), ComplainDetails.class);
            i.putExtra("title", title);
            i.putExtra("customerId", custid);
            i.putExtra("complainId", cmpid);
            startActivity(i);
        } else if (from.equals("Notification")) {
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
        }

        finish();

    }

    public JSONObject makeHttpRequest(String url) {

        JSONObject jsonObject = null;
        try {

            HttpParams httpParameters = new BasicHttpParams();

            int timeoutConnection = 500000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            int timeoutSocket = 500000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpresponse = httpclient.execute(httpGet);
            HttpEntity httpEntity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            jsonObject = new JSONObject(sb.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private class JSONAsynk extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(Comments_List_Activity.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);


        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {


            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {


                if (jsonObject.getString("status").equalsIgnoreCase("True")) {

                    final JSONArray entityArray = jsonObject.getJSONArray("lstcomp");

                    for (int i = 0; i < entityArray.length(); i++) {
                        JSONObject s = (JSONObject) entityArray.get(i);

                        String uname = s.getString("username");
                        String comment = s.getString("comment");
                        String date = s.getString("date");

                        HashMap<String, String> map = new HashMap<>();

                        map.put("username", uname);
                        map.put("comment", comment);
                        map.put("date", date);

                        commentsDetails.add(map);

                    }

                    adapter = new SimpleAdapter(Comments_List_Activity.this, commentsDetails, R.layout.layout_comments, new String[]{"username", "comment", "date"}, new int[]{R.id.textView95, R.id.textView96, R.id.textView97});
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(Comments_List_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private class JSONAsynks extends AsyncTask<String, String, JSONObject> {

        Dialog loader;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(Comments_List_Activity.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {

                if (jsonObject.getString("status").equalsIgnoreCase("True")) {
                    Intent i = new Intent(getApplicationContext(), Comments_List_Activity.class);
                    i.putExtra("Complainid", cmpid);
                    i.putExtra("title", title);
                    i.putExtra("CustomerId", custid);
                    i.putExtra("From", from);
                    startActivity(i);

                    finish();
                } else {
                    Toast.makeText(Comments_List_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
