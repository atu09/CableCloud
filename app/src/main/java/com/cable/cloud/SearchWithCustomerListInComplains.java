package com.cable.cloud;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchWithCustomerListInComplains extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView lvcustomerincomplain;

    ArrayList<HashMap<String, String>> customerdetails = new ArrayList<>();

    private static final String PREF_NAME = "LoginPref";

    Customer_Complain_Adapter da;

    String mode, title, id, status;

    MenuItem searchMenuItem;

    String siteurl, uid, cid, aid, eid, URL;
    RequestQueue requestQueue;

    SwipeRefreshLayout swrefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchwithcustomerincomplains);

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);


        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        cid = pref.getString("Contracotrid", "");
        eid = pref.getString("Entityids", "");

        Intent j = getIntent();
        mode = j.getExtras().getString("Mode");
        title = j.getExtras().getString("title");
        id = j.getExtras().getString("Id");
        status = j.getExtras().getString("status");


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);

        lvcustomerincomplain = (ListView) findViewById(R.id.listcustomerincomplains);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mode.equals("User")) {
            URL = siteurl + "/CustomercomplainlistbyuserforAdminCollectionApp";

            HashMap<String, String> map = new HashMap<>();
            map.put("startindex", "0");
            map.put("noofrecords", "10000");
            map.put("contractorid", cid);
            map.put("entityId", eid);
            map.put("userId", id);
            map.put("status", status);
            map.put("filterCustomer", "");

            CallVolley(URL, map);
        }

        if (mode.equals("Area")) {
            URL = siteurl + "/CustomercomplainlistbyareaforAdminCollectionApp";

            HashMap<String, String> map = new HashMap<>();
            map.put("startindex", "0");
            map.put("noofrecords", "10000");
            map.put("contractorid", cid);
            map.put("entityId", eid);
            map.put("areadId", id);
            map.put("status", status);
            map.put("filterCustomer", "");

            CallVolley(URL, map);
        }

        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.customerlist, null);

        CardView cv = (CardView) v.findViewById(R.id.app);
        cv.setCardBackgroundColor(Color.parseColor("#e59400"));

        swrefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);


        swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swrefresh.setRefreshing(true);

                if (mode.equals("User")) {
                    URL = siteurl + "/CustomercomplainlistbyuserforAdminCollectionApp";

                    HashMap<String, String> map = new HashMap<>();
                    map.put("startindex", "0");
                    map.put("noofrecords", "10000");
                    map.put("contractorid", cid);
                    map.put("entityId", eid);
                    map.put("userId", id);
                    map.put("status", status);
                    map.put("filterCustomer", "");

                    CallVolley(URL, map);
                }

                if (mode.equals("Area")) {
                    URL = siteurl + "/CustomercomplainlistbyareaforAdminCollectionApp";

                    HashMap<String, String> map = new HashMap<>();
                    map.put("startindex", "0");
                    map.put("noofrecords", "10000");
                    map.put("contractorid", cid);
                    map.put("entityId", eid);
                    map.put("areadId", id);
                    map.put("status", status);
                    map.put("filterCustomer", "");

                    CallVolley(URL, map);
                }


            }
        });


        lvcustomerincomplain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int topRowVerticalPosition = (lvcustomerincomplain == null || lvcustomerincomplain.getChildCount() == 0) ?
                        0 : lvcustomerincomplain.getChildAt(0).getTop();
                swrefresh.setEnabled((topRowVerticalPosition >= 0));

            }
        });

        lvcustomerincomplain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> ss = (HashMap<String, String>) da.getItem(position);

                Intent i = new Intent(getApplicationContext(), ComplainListviaCustomerActivity.class);
                i.putExtra("title", ss.get("Name"));
                i.putExtra("CustomerId", ss.get("CustomerId"));
                i.putExtra("Mode", "Complaint");
                i.putExtra("status", status);
                startActivity(i);

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_complains, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.searchcomplain);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(params);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);

        changeSearchViewTextColor(searchView);


        searchView.setQueryHint("Search Customers");
        searchView.setIconifiedByDefault(false);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        if (mode.equals("Search")) {
            searchMenuItem.expandActionView();
            searchView.requestFocus();

            Utils.closeKeyboard(this);
        }

        return true;
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        URL = siteurl + "/SearchCustomerForCollectionApp";
        CallVolleys(URL, query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    public void CallVolley(String a, HashMap<String, String> map) {


        final Dialog loader = Utils.getLoader(this);
        loader.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            customerdetails.clear();

                            String commentcount = "0";

                            if (response.getString("status").equals("True")) {

                                final JSONArray entityarray = response.getJSONArray("CustomerInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    commentcount = e.getString("CustCommentCount");

                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("CustomerId", e.getString("CustomerId"));
                                    map.put("Name", e.getString("Name"));
                                    map.put("Address", e.getString("Address"));
                                    map.put("AccountNo", e.getString("AccountNo"));
                                    map.put("MQNo", e.getString("MQNo"));
                                    map.put("CustCommentCount", commentcount);

                                    customerdetails.add(map);

                                }

                                LayoutInflater li = getLayoutInflater();
                                View vs = li.inflate(R.layout.customerlist, null);

                                vs.findViewById(R.id.textView32).setVisibility(View.GONE);

                                da = new Customer_Complain_Adapter(SearchWithCustomerListInComplains.this, customerdetails);
                                lvcustomerincomplain.setAdapter(da);
                                lvcustomerincomplain.setTextFilterEnabled(true);

                                swrefresh.setRefreshing(false);


                            } else {
                                Toast.makeText(SearchWithCustomerListInComplains.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        error.printStackTrace();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    public void CallVolleys(String a, String text) {


        final Dialog loader = Utils.getLoader(this);
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("startindex", "0");
        map.put("noofrecords", "10000");
        map.put("contractorid", cid);
        map.put("userId", uid);
        map.put("entityId", eid);
        map.put("filterCustomer", text);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {
                            customerdetails.clear();

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                String commentcount = "0";
                                final JSONArray entityArray = response.getJSONArray("CustomerInfoList");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("CustomerId", e.getString("CustomerId"));
                                    map.put("Name", e.getString("Name"));
                                    map.put("Address", e.getString("Address"));
                                    map.put("AccountNo", e.getString("AccountNo"));
                                    map.put("MQNo", e.getString("MQNo"));
                                    map.put("CustCommentCount", commentcount);

                                    customerdetails.add(map);
                                }
                                da = new Customer_Complain_Adapter(SearchWithCustomerListInComplains.this, customerdetails);
                                lvcustomerincomplain.setAdapter(da);
                                lvcustomerincomplain.setTextFilterEnabled(true);

                            } else {
                                Toast.makeText(SearchWithCustomerListInComplains.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        error.printStackTrace();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);


    }

}
