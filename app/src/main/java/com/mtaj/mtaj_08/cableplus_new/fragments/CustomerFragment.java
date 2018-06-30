package com.mtaj.mtaj_08.cableplus_new.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mtaj.mtaj_08.cableplus_new.AddCustomer_1;
import com.mtaj.mtaj_08.cableplus_new.activities.AreaListInCustomers;
import com.mtaj.mtaj_08.cableplus_new.CustomerListActivity;
import com.mtaj.mtaj_08.cableplus_new.R;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class CustomerFragment extends Fragment {

    SharedPreferences pref;
    String LOGIN_PREF = "LoginPref";

    CardView cvArea;
    TextView tvAddCustomer;
    EditText etSearch;

    String url;
    String siteURL, userId, contractorId, entities;

    RequestQueue requestQueue;
    SwipeRefreshLayout refreshLayout;
    ListView listView;
    SimpleAdapter adapter;

    ArrayList<HashMap<String, String>> customerDetailsMap = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.no_access_layout, container, false);

        url = getArguments().getString("url");
        if (pref.getBoolean("IsCustomer", true)) {
            if (url.equalsIgnoreCase("-")) {
                view = inflater.inflate(R.layout.layout_offline, null);
            } else {
                view = inflater.inflate(R.layout.customers, container, false);

                cvArea = (CardView) view.findViewById(R.id.card_view2);
                tvAddCustomer = (TextView) view.findViewById(R.id.textView30);
                listView = (ListView) view.findViewById(R.id.listView7);
                refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
                etSearch = (EditText) view.findViewById(R.id.editText20);

                CallVolleys(url);
            }
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (pref.getBoolean("IsCustomer", true) && !url.equals("-")) {

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CallVolleys(url);
                }
            });

            etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    try {

                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                            String URL = siteURL + "/SearchCustomerForCollectionApp?startindex=0&noofrecords=10000000&contractorid=" + contractorId + "&userId=" + userId + "&entityId=" + entities + "&filterCustomer=" + URLEncoder.encode(v.getText().toString(), "UTF-8");
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("from");
                            editor.putString("from", "Search");
                            editor.putString("URL", URL);
                            editor.apply();

                            Intent i = new Intent(getContext(), CustomerListActivity.class);
                            startActivity(i);

                            return true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getContext(), AreaListInCustomers.class);
                    startActivity(i);

                }
            });

            tvAddCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), AddCustomer_1.class);
                    startActivity(i);

                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());
        pref = getActivity().getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);

        siteURL = pref.getString("SiteURL", "");
        userId = pref.getString("Userid", "");
        contractorId = pref.getString("Contracotrid", "");
        entities = pref.getString("Entityids", "");
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

    public void CallVolleys(String a) {
        JsonObjectRequest request;

        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", contractorId);
        map.put("loginuserId", userId);
        map.put("entityIds", entities);

        request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }

                        try {
                            customerDetailsMap.clear();

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                String ta = response.getString("TotalArea");
                                String tc = response.getString("TotalCustomers");
                                String tab = response.getString("TotalAssignDevice");
                                String tuab = response.getString("TotalUnAssignDevice");


                                HashMap<String, String> map = new HashMap<>();
                                map.put("TotalArea", ta);
                                map.put("TotalCustomers", tc);
                                map.put("TotalAssignDevice", tab);
                                map.put("TotalUnAssignDevice", tuab);

                                customerDetailsMap.add(map);

                                adapter = new SimpleAdapter(getContext(), customerDetailsMap, R.layout.layout_customer, new String[]{"TotalArea", "TotalCustomers", "TotalAssignDevice"}, new int[]{R.id.textView65, R.id.textView8, R.id.textView10});
                                listView.setAdapter(adapter);

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

                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


}
