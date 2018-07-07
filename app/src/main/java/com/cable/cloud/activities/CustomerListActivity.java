package com.cable.cloud.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.cable.cloud.helpers.ConnectivityReceiver;
import com.cable.cloud.CustomerListAdapter;
import com.cable.cloud.helpers.DBHelper;
import com.cable.cloud.InfiniteScrollListener;
import com.cable.cloud.R;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerListActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    ArrayList<HashMap<String, String>> customerDetailsMaps = new ArrayList<>();
    CustomerListAdapter adapter;
    ListView listView;
    TextView tvEmpty;
    TextView tvTotalCollection, tvTotalOs;
    LinearLayout footerLayout;

    String siteurl, uid, cid, aid, eid, URL;
    String from;
    String inrSymbol = "\u20B9";

    int mPage = 0;

    SharedPreferences pref;
    DBHelper myDB;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        listView = (ListView) findViewById(R.id.listView);
        tvTotalCollection = (TextView) findViewById(R.id.textView28);
        tvTotalOs = (TextView) findViewById(R.id.textView30);
        tvEmpty = (TextView) findViewById(R.id.textView99);
        footerLayout = (LinearLayout) findViewById(R.id.footer);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        myDB = new DBHelper(this);

        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        cid = pref.getString("Contracotrid", "");
        aid = pref.getString("AreaId", "");
        eid = pref.getString("Entityids", "");
        from = pref.getString("from", "");

        tvTotalCollection.setText(pref.getString("Collection", ""));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pref.getString("AreaName", ""));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (from.equals("Customer") || from.equals("Search")) {
            footerLayout.setVisibility(View.GONE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new CustomerListAdapter(getApplicationContext(), customerDetailsMaps);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                if (isConnected) {
                    mPage = mPage + 1;
                    if (from.equals("Customer")) {
                        URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";
                    } else if (from.equals("Payment")) {
                        URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";
                    }
                    new JSONAsync().execute();
                }

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> ss = (HashMap<String, String>) adapter.getItem(position);

                if (isConnected) {

                    if (from.equals("Customer")) {
                        Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("MQno", ss.get("MQNo"));
                        startActivity(i);

                        finish();

                    }
                    if (from.equals("Payment")) {

                        Intent i = new Intent(getApplicationContext(), CustomerOnlineDetailsActivity.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("CustomerId", ss.get("CustomerId"));
                        i.putExtra("from", "Payment");
                        startActivity(i);

                        finish();
                    }
                    if (from.equals("Search")) {

                        Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("MQno", ss.get("MQNo"));
                        startActivity(i);

                        finish();
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(), CustomerOfflineDetailsActivity.class);
                    i.putExtra("cname", ss.get("Name"));
                    i.putExtra("A/cNo", ss.get("AccountNo"));
                    i.putExtra("CustomerId", ss.get("CustomerId"));
                    i.putExtra("billId", ss.get("billId"));
                    startActivity(i);

                    finish();
                }

            }
        });

        if (from.equals("Customer")) {
            URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";
        } else if (from.equals("Payment")) {
            URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";
        } else if (from.equals("Search")) {
            URL = pref.getString("URL", "");
        }

        isConnected = ConnectivityReceiver.isConnected();

        if (isConnected) {
            new JSONAsync().execute();
        } else {
            Cursor cursor = myDB.getCustomers(aid);

            DecimalFormat format = new DecimalFormat("#");
            format.setDecimalSeparatorAlwaysShown(false);

            if (cursor.moveToFirst()) {
                do {

                    String cid = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                    String cname = cursor.getString(cursor.getColumnIndex("NAME"));
                    String caddress = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    String carea = cursor.getString(cursor.getColumnIndex("AREA"));
                    String cacno = cursor.getString(cursor.getColumnIndex("ACCOUNTNO"));
                    String cphone = cursor.getString(cursor.getColumnIndex("PHONE"));
                    String cemail = cursor.getString(cursor.getColumnIndex("EMAIL"));
                    String castatus = cursor.getString(cursor.getColumnIndex("ACCOUNTSTATUS"));
                    String cmq = cursor.getString(cursor.getColumnIndex("MQNO"));
                    String ctotaloa = cursor.getString(cursor.getColumnIndex("TOTAL_OUTSTANDING"));
                    String commentCount = cursor.getString(cursor.getColumnIndex("COMMENT_COUNT"));
                    String billid = cursor.getString(cursor.getColumnIndex("BILL_ID"));


                    HashMap<String, String> map = new HashMap<>();
                    map.put("CustomerId", cid);
                    map.put("Name", cname);
                    map.put("Address", caddress);
                    map.put("Area", carea);
                    map.put("AccountNo", cacno);
                    map.put("Phone", cphone);
                    map.put("Email", cemail);
                    map.put("AccountStatus", castatus);
                    map.put("MQNo", cmq);
                    map.put("TotalOutStandingAmount", format.format(Double.parseDouble(ctotaloa)));
                    map.put("CustCommentCount", commentCount);
                    map.put("billId", billid);

                    customerDetailsMaps.add(map);

                } while (cursor.moveToNext());
            }

            long totalOs = 0;

            if (customerDetailsMaps.size() > 0) {
                for (int k = 0; k < customerDetailsMaps.size(); k++) {
                    totalOs = totalOs + Long.parseLong(customerDetailsMaps.get(k).get("TotalOutStandingAmount"));
                }
            }

            String toa = String.valueOf(totalOs);
            tvTotalOs.setText(inrSymbol + format.format(Double.parseDouble(toa)));

            adapter.addItems(customerDetailsMaps);

            if (adapter.getCount() > 0) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences.Editor editor = pref.edit();
        editor.remove("AreaStatus");
        editor.remove("AreaId");
        editor.remove("AreaName");
        editor.apply();

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);

        searchView.setQueryHint("Search Customers");
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusableInTouchMode(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnected) {

                    try {

                        mPage = 0;
                        customerDetailsMaps.clear();

                        if (from.equals("Customer")) {
                            URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + URLEncoder.encode(query, "UTF-8");
                        } else if (from.equals("Payment")) {
                            URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + URLEncoder.encode(query, "UTF-8");
                        }

                        new JSONAsync().execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    try {

                        customerDetailsMaps.clear();
                        Cursor cursor = myDB.SearchCustomer(query);

                        if (cursor != null && cursor.getCount() > 0) {
                            if (cursor.moveToFirst()) {

                                DecimalFormat format = new DecimalFormat("#");
                                format.setDecimalSeparatorAlwaysShown(false);

                                String cid = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                                String cname = cursor.getString(cursor.getColumnIndex("NAME"));
                                String caddress = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                                String carea = cursor.getString(cursor.getColumnIndex("AREA"));
                                String cacno = cursor.getString(cursor.getColumnIndex("ACCOUNTNO"));
                                String cphone = cursor.getString(cursor.getColumnIndex("PHONE"));
                                String cemail = cursor.getString(cursor.getColumnIndex("EMAIL"));
                                String castatus = cursor.getString(cursor.getColumnIndex("ACCOUNTSTATUS"));
                                String cmq = cursor.getString(cursor.getColumnIndex("MQNO"));
                                String ctotaloa = cursor.getString(cursor.getColumnIndex("TOTAL_OUTSTANDING"));
                                String commentCount = cursor.getString(cursor.getColumnIndex("COMMENT_COUNT"));
                                String billid = cursor.getString(cursor.getColumnIndex("BILL_ID"));


                                HashMap<String, String> map = new HashMap<>();
                                map.put("CustomerId", cid);
                                map.put("Name", cname);
                                map.put("Address", caddress);
                                map.put("Area", carea);
                                map.put("AccountNo", cacno);
                                map.put("Phone", cphone);
                                map.put("Email", cemail);
                                map.put("AccountStatus", castatus);
                                map.put("MQNo", cmq);
                                map.put("TotalOutStandingAmount", format.format(Double.parseDouble(ctotaloa)));
                                map.put("CustCommentCount", commentCount);
                                map.put("billId", billid);

                                customerDetailsMaps.add(map);
                            }

                            adapter.addItems(customerDetailsMaps);

                            if (adapter.getCount() > 0) {
                                tvEmpty.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "No Customer found..!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Error:", e.toString());
                    }
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
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

    class JSONAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(CustomerListActivity.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(URL);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {
                ArrayList<HashMap<String, String>> customerDetailsMaps = new ArrayList<>();

                if (json.getString("status").equalsIgnoreCase("True")) {

                    String str = "\u20B9", commentCount = "0";

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityArray = json.getJSONArray("CustomerInfoList");


                    for (int i = 0; i < entityArray.length(); i++) {
                        JSONObject e = (JSONObject) entityArray.get(i);


                        String cid = e.getString("CustomerId");
                        String cname = e.getString("Name");
                        String caddress = e.getString("Address");
                        String carea = e.getString("Area");
                        String cacno = e.getString("AccountNo");
                        String cphone = e.getString("Phone");
                        String cemail = e.getString("Email");
                        String castatus = e.getString("AccountStatus");
                        String cmq = e.getString("DeviceNo");
                        String ctotaloa = e.getString("TotalOutStandingAmount");
                        String csmartcard = e.getString("SmartCardNo");
                        String PackageName = (!TextUtils.isEmpty(e.getString("PackageName")) && !e.getString("PackageName").equals("null")) ? e.getString("PackageName") : " ";

                        if (!from.equals("Search")) {
                            commentCount = e.getString("CustCommentCount");
                        }

                        HashMap<String, String> map = new HashMap<>();
                        map.put("CustomerId", cid);
                        map.put("Name", cname);
                        map.put("Address", caddress);
                        map.put("Area", carea);
                        map.put("AccountNo", cacno);
                        map.put("Phone", cphone);
                        map.put("Email", cemail);
                        map.put("AccountStatus", castatus);
                        map.put("MQNo", cmq);
                        map.put("SmartCardNo", csmartcard);
                        map.put("TotalOutStandingAmount", format.format(Double.parseDouble(ctotaloa)));
                        map.put("CustCommentCount", commentCount);
                        map.put("PackageName", PackageName);

                        customerDetailsMaps.add(map);
                    }

                    String toa = json.getString("TotalOutstanding");
                    tvTotalOs.setText(str + format.format(Double.parseDouble(toa)));

                } else {
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                }

                adapter.addItems(customerDetailsMaps);

                if (adapter.getCount() > 0) {
                    tvEmpty.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }

            } catch (Exception ex) {
                Toast.makeText(CustomerListActivity.this, "Error:++" + ex, Toast.LENGTH_SHORT).show();
            }

        }

    }
}
