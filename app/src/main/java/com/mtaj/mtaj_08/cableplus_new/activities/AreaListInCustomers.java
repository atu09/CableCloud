package com.mtaj.mtaj_08.cableplus_new.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mtaj.mtaj_08.cableplus_new.AddCustomer_1;
import com.mtaj.mtaj_08.cableplus_new.CustomerListActivity;
import com.mtaj.mtaj_08.cableplus_new.InfiniteScrollListener;
import com.mtaj.mtaj_08.cableplus_new.R;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

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
import java.util.ArrayList;
import java.util.HashMap;

public class AreaListInCustomers extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    ArrayList<HashMap<String, String>> areaList = new ArrayList<>();
    String siteURL, userId, contractorId, entities, URL;
    ListView listView;
    TextView tvAddCustomer;
    SimpleAdapter adapter;

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list_in_customers);

        listView = (ListView) findViewById(R.id.listView);
        tvAddCustomer = (TextView) findViewById(R.id.textView30);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Area List");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new SimpleAdapter(AreaListInCustomers.this, areaList, R.layout.arealist_customers, new String[]{"AreaName", "TotalCustomer"}, new int[]{R.id.textView2, R.id.textView24});
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                editor.remove("AreaId");
                editor.remove("from");

                editor.putString("AreaStatus", "true");
                editor.putString("AreaName", areaList.get(position).get("AreaName"));
                editor.putString("AreaId", areaList.get(position).get("AreaId"));
                editor.putString("from", "Customer");
                editor.apply();

                Intent i = new Intent(getApplicationContext(), CustomerListActivity.class);
                startActivity(i);

                finish();
            }
        });

        tvAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddCustomer_1.class);
                startActivity(i);
            }
        });

        listView.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                AreaListInCustomers.this.page = AreaListInCustomers.this.page + 1;

                URL = siteURL + "/GetCustomersByAreaForCollectionApp?contractorId=" + contractorId + "&userId=" + userId + "&entityId=" + entities
                        + "&startindex=" + String.valueOf(AreaListInCustomers.this.page) + "&noofrecords=20";

                new JSONAsync().execute(new String[]{URL});

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = pref.getString("Userid", "");
        siteURL = pref.getString("SiteURL", "");
        contractorId = pref.getString("Contracotrid", "");
        entities = pref.getString("Entityids", "");
        String URL = siteURL + "/GetCustomersByAreaForCollectionApp?contractorId=" + contractorId + "&userId=" + userId + "&entityId=" + entities + "&startindex=" + String.valueOf(page) + "&noofrecords=20";
        new JSONAsync().execute(new String[]{URL});
    }

    @Override
    public void onBackPressed() {

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

    private class JSONAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(AreaListInCustomers.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {

                if (json.getString("status").equalsIgnoreCase("True")) {

                    final JSONArray entityArray = json.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityArray.length(); i++) {
                        JSONObject e = (JSONObject) entityArray.get(i);

                        String areaId = e.getString("AreaId");
                        String areaName = TextUtils.htmlEncode(e.getString("AreaName"));
                        String totalCustomer = e.getString("TotalCustomer");

                        HashMap<String, String> map = new HashMap<>();
                        map.put("AreaId", areaId);
                        map.put("AreaName", areaName);
                        map.put("TotalCustomer", totalCustomer);

                        areaList.add(map);

                    }

                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(AreaListInCustomers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
