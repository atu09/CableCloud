package com.cable.cloud.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cable.cloud.AreaParent;
import com.cable.cloud.ComplainDataAdapter;
import com.cable.cloud.ComplainDetails;
import com.cable.cloud.CustomerChild;
import com.cable.cloud.MyExpandableListAdapter;
import com.cable.cloud.R;
import com.cable.cloud.SearchWithCustomerListInComplains;
import com.cable.cloud.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class ComplainFragment extends Fragment {

    private static final String PREF_NAME = "LoginPref";

    BottomNavigationBar bottomNavigationBar;
    ListView lvComplain;
    String url;

    ArrayList<HashMap<String, String>> areaList = new ArrayList<>();
    MyExpandableListAdapter expandableListAdapter;

    AreaParent parent;
    CustomerChild child;

    TextView tvTotalComplaint;

    List<AreaParent> expList = new ArrayList<>();

    SwipeRefreshLayout srLayout;

    String siteUrl, uid, cid, aid, eid, URL;
    RequestQueue requestQueue;

    ComplainDataAdapter adapter;

    ExpandableListView expListComplain;

    SharedPreferences pref;

    ComplainDataAdapter complainDataAdapter;

    TextView tvEmpty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_offline, null);

        url = getArguments().getString("url");
        if (!url.equalsIgnoreCase("-")) {
            if (pref.getString("RoleId", "").equalsIgnoreCase("2")) {
                view = inflater.inflate(R.layout.complain, null);
                URL = siteUrl + "/GetComplainListByAreaForAdminCollectionApp";
                CallVolley(URL);

            } else {
                if (pref.getBoolean("IsComplain", true)) {
                    url = siteUrl + "/GetComplainListByAreaForCollectionApp";
                    CallVolleyss(url);
                    view = inflater.inflate(R.layout.complaint_2, null);
                } else {
                    view = inflater.inflate(R.layout.no_access_layout, null);
                }
            }
        }

        return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context con = getActivity();
        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.getString("RoleId", "").equalsIgnoreCase("2") && !url.equals("-")) {

            srLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            lvComplain = (ListView) view.findViewById(R.id.listView5);
            tvEmpty = (TextView) view.findViewById(R.id.textView100);

            bottomNavigationBar = (BottomNavigationBar) view.findViewById(R.id.bottom_navigation_bar);

            bottomNavigationBar
                    .addItem(new BottomNavigationItem(R.drawable.areaicon, "By Area").setActiveColorResource(R.color.ToolbarColor))
                    .addItem(new BottomNavigationItem(R.drawable.customericon, "By Operator").setActiveColorResource(R.color.ToolbarColor))
                    .addItem(new BottomNavigationItem(R.drawable.ic_search_white, "Search").setActiveColorResource(R.color.ToolbarColor)).initialise();

            Utils.closeKeyboard(getContext());

            srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    srLayout.setRefreshing(true);

                    if (bottomNavigationBar.getCurrentSelectedPosition() == 0) {
                        URL = siteUrl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);

                    } else if (bottomNavigationBar.getCurrentSelectedPosition() == 1) {
                        URL = siteUrl + "/GetComplainListByUserForAdminCollectionApp";
                        CallVolleys(URL);
                    }
                }
            });

            lvComplain.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lvComplain == null || lvComplain.getChildCount() == 0) ?
                            0 : lvComplain.getChildAt(0).getTop();
                    srLayout.setEnabled((topRowVerticalPosition >= 0));

                }
            });

            bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                @Override
                public void onTabSelected(int position) {

                    if (position == 0) {
                        URL = siteUrl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);
                    }
                    if (position == 1) {

                        URL = siteUrl + "/GetComplainListByUserForAdminCollectionApp";
                        CallVolleys(URL);
                    }

                    if (position == 2) {
                        Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                        i.putExtra("Mode", "Search");
                        i.putExtra("title", "Search Customer");
                        i.putExtra("status", "ACTIVE");
                        i.putExtra("Id", "");
                        startActivity(i);
                    }


                }

                @Override
                public void onTabUnselected(int position) {

                }

                @Override
                public void onTabReselected(int position) {

                    if (position == 0) {
                        URL = siteUrl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);
                    }
                    if (position == 1) {

                        URL = siteUrl + "/GetComplainListByUserForAdminCollectionApp";
                        CallVolleys(URL);
                    }

                    if (position == 2) {
                        Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                        i.putExtra("Mode", "Search");
                        i.putExtra("title", "Search Customer");
                        i.putExtra("status", "ACTIVE");
                        i.putExtra("Id", "");
                        startActivity(i);
                    }


                }
            });

            lvComplain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    TextView tvareaname = (TextView) view.findViewById(R.id.textView31);
                    LinearLayout tvnew = (LinearLayout) view.findViewById(R.id.llnew);
                    LinearLayout tvhigh = (LinearLayout) view.findViewById(R.id.llhigh);
                    LinearLayout tvactive = (LinearLayout) view.findViewById(R.id.llactive);
                    LinearLayout tvresolve = (LinearLayout) view.findViewById(R.id.llresolved);

                    tvnew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (areaList.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "NEW");
                                i.putExtra("Id", areaList.get(position).get("userId"));
                                i.putExtra("title", areaList.get(position).get("userName"));
                                startActivity(i);

                            }


                            if (areaList.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "NEW");
                                i.putExtra("Id", areaList.get(position).get("areaId"));
                                i.putExtra("title", areaList.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvhigh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (areaList.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "HIGH");
                                i.putExtra("Id", areaList.get(position).get("userId"));
                                i.putExtra("title", areaList.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (areaList.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "HIGH");
                                i.putExtra("Id", areaList.get(position).get("areaId"));
                                i.putExtra("title", areaList.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvactive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (areaList.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "ACTIVE");
                                i.putExtra("Id", areaList.get(position).get("userId"));
                                i.putExtra("title", areaList.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (areaList.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "ACTIVE");
                                i.putExtra("Id", areaList.get(position).get("areaId"));
                                i.putExtra("title", areaList.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvresolve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (areaList.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "RESOLVED");
                                i.putExtra("Id", areaList.get(position).get("userId"));
                                i.putExtra("title", areaList.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (areaList.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "RESOLVED");
                                i.putExtra("Id", areaList.get(position).get("areaId"));
                                i.putExtra("title", areaList.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                }
            });


        } else {

            if (pref.getBoolean("IsComplain", true) && !url.equals("-")) {

                expListComplain = (ExpandableListView) view.findViewById(R.id.expandableListView1);

                tvTotalComplaint = (TextView) view.findViewById(R.id.btnNext);

                srLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

                tvEmpty = (TextView) view.findViewById(R.id.textView100);

                srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        srLayout.setRefreshing(true);
                        CallVolleyss(url);

                    }
                });

                expListComplain.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        int topRowVerticalPosition = (expListComplain == null || expListComplain.getChildCount() == 0) ?
                                0 : expListComplain.getChildAt(0).getTop();
                        srLayout.setEnabled((topRowVerticalPosition >= 0));

                    }
                });

                expListComplain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                        String cid = expList.get(groupPosition).getChildren().get(childPosition).getCmpid();
                        String cmid = expList.get(groupPosition).getChildren().get(childPosition).getCustid();

                        //Toast.makeText(getContext(), contractorId, Toast.LENGTH_SHORT).show();
                        // Toast.makeText(getContext(), cmid, Toast.LENGTH_SHORT).show();


                        Intent i = new Intent(getContext(), ComplainDetails.class);
                        i.putExtra("title", expList.get(groupPosition).getChildren().get(childPosition).getName());
                        i.putExtra("customerId", expList.get(groupPosition).getChildren().get(childPosition).getCustid());
                        i.putExtra("complainId", expList.get(groupPosition).getChildren().get(childPosition).getCmpid());
                        startActivity(i);

                        return false;
                    }
                });

            }
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());

        complainDataAdapter = new ComplainDataAdapter(getContext(), areaList);

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        cid = pref.getString("Contracotrid", "");
        eid = pref.getString("Entityids", "");

    }

    public void CallVolley(String a) {


        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("loginuserId", uid);
        map.put("entityIds", eid);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            areaList.clear();
                            Utils.checkLog("complain", response, null);
                            if (response.getString("status").equalsIgnoreCase("True")) {

                                final JSONArray entityArray = response.getJSONArray("UserComplainInfoList");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("type", "Area");
                                    map.put("areaId", e.getString("areaId"));
                                    map.put("areaname", e.getString("areaname"));
                                    map.put("newcomplaincount", e.getString("newcomplaincount"));
                                    map.put("highcomplaincount", e.getString("highcomplaincount"));
                                    map.put("activecomplaincount", e.getString("activecomplaincount"));
                                    map.put("resolvecomplaincount", e.getString("resolvecomplaincount"));
                                    map.put("newcomplaincommentcount", e.getString("newcomplaincommentcount"));
                                    map.put("highcomplaincommentcount", e.getString("highcomplaincommentcount"));
                                    map.put("activecomplaincommentcount", e.getString("activecomplaincommentcount"));
                                    map.put("resolvecomplaincommentcount", e.getString("resolvecomplaincommentcount"));

                                    areaList.add(map);

                                }

                                if (areaList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }

                                adapter = new ComplainDataAdapter(getContext(), areaList);
                                lvComplain.setAdapter(adapter);

                                srLayout.setRefreshing(false);
                            } else {
                                if (areaList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
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


                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    public void CallVolleys(String a) {


        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("loginuserId", uid);
        map.put("entityIds", eid);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            areaList.clear();
                            Utils.checkLog("complain", response, null);
                            if (response.getString("status").equalsIgnoreCase("True")) {
                                final JSONArray entityarray = response.getJSONArray("UserComplainInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("type", "User");
                                    map.put("userId", e.getString("userId"));
                                    map.put("userName", e.getString("userName"));
                                    map.put("newcomplaincount", e.getString("newcomplaincount"));
                                    map.put("highcomplaincount", e.getString("highcomplaincount"));
                                    map.put("activecomplaincount", e.getString("activecomplaincount"));
                                    map.put("resolvecomplaincount", e.getString("resolvecomplaincount"));
                                    map.put("newcomplaincommentcount", e.getString("newcomplaincommentcount"));
                                    map.put("highcomplaincommentcount", e.getString("highcomplaincommentcount"));
                                    map.put("activecomplaincommentcount", e.getString("activecomplaincommentcount"));
                                    map.put("resolvecomplaincommentcount", e.getString("resolvecomplaincommentcount"));

                                    areaList.add(map);

                                }

                                if (areaList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }

                                adapter = new ComplainDataAdapter(getContext(), areaList);
                                lvComplain.setAdapter(adapter);

                                srLayout.setRefreshing(false);
                            } else {
                                if (areaList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
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

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(request);


    }

    public void CallVolleyss(String a) {

        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("loginuserId", uid);
        map.put("entityIds", eid);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            expList.clear();
                            Utils.checkLog("complain", response, null);
                            if (response.getString("status").equalsIgnoreCase("True")) {

                                tvTotalComplaint.setText("TOTAL COMPLAINTS:  " + response.getString("totalComplain"));

                                final JSONArray entityArray = response.getJSONArray("UserComplainInfoList");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = entityArray.getJSONObject(i);

                                    parent = new AreaParent();

                                    String aid = e.getString("areaId");
                                    String aname = e.getString("areaname");
                                    String acomplaincount = e.getString("areacomplaincount");
                                    String commentcount = e.getString("areacommentcount");


                                    parent.setName(aname);
                                    parent.setCount(acomplaincount);
                                    parent.setCommentcount(commentcount);
                                    parent.setChildren(new ArrayList<CustomerChild>());


                                    JSONArray jsarr = e.getJSONArray("lstCustInfo");


                                    for (int j = 0; j < jsarr.length(); j++) {
                                        child = new CustomerChild();

                                        JSONObject s = jsarr.getJSONObject(j);

                                        String cid = s.getString("customerId");
                                        String cname = s.getString("Name");
                                        String cacno = s.getString("AccNo");
                                        String cmqno = s.getString("MqNo");
                                        String cadd = s.getString("Address");
                                        String cmpid = s.getString("complainId");
                                        String commentcounts = s.getString("commentcount");

                                        child.setCustid(cid);
                                        child.setCmpid(cmpid);
                                        child.setName(cname);
                                        child.setAcno(cacno);
                                        child.setMqno(cmqno);
                                        child.setAddress(cadd);
                                        child.setCommentcount(commentcounts);

                                        parent.getChildren().add(child);

                                    }

                                    expList.add(parent);

                                }

                                expandableListAdapter = new MyExpandableListAdapter(getContext(), expList);
                                expListComplain.setAdapter(expandableListAdapter);


                                srLayout.setRefreshing(false);


                            } else {
                                if (expList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);

                                    srLayout.setRefreshing(false);
                                }

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

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


}