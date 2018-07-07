package com.cable.cloud.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;


import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.CustomerDetail_Offline;
import com.cable.cloud.CustomerDetails;
import com.cable.cloud.activities.CustomerListActivity;
import com.cable.cloud.DBHelper;
import com.cable.cloud.InfiniteScrollListener;
import com.cable.cloud.R;
import com.cable.cloud.customs.MovableFloatingActionButton;
import com.cable.cloud.helpers.Utils;
//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class PaymentFragment extends Fragment {

    private static final String PREF_NAME = "LoginPref";

    ListView lvArea;
    String url;
    String str = "\u20B9";


    ArrayList<HashMap<String, String>> areaDetails = new ArrayList<HashMap<String, String>>();

    TextView tvtotalcol, tvtotaloa;

    MovableFloatingActionButton fabSearch;

    String siteUrl, uid, cid, eid, URL;

    RequestQueue requestQueue;

    SwipeRefreshLayout srLayout;

    SimpleAdapter adapter;

    SharedPreferences pref;

    int mPage = 0;

    DBHelper myDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = getActivity();

        View view = inflater.inflate(R.layout.no_access_layout, null);

        final SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.getBoolean("IsBilling", true)) {
            url = getArguments().getString("url");
            view = inflater.inflate(R.layout.payment, null);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (pref.getBoolean("IsBilling", true)) {

            lvArea = (ListView) view.findViewById(R.id.listView);
            fabSearch = (MovableFloatingActionButton) view.findViewById(R.id.fab);

            tvtotalcol = (TextView) view.findViewById(R.id.btnCancel);
            tvtotaloa = (TextView) view.findViewById(R.id.btnNext);

            srLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            if (url.equalsIgnoreCase("-")) {

                myDB = new DBHelper(getContext());

                double totalOs = 0.0, totalCollection = 0.0;

                Cursor cursor = myDB.getAreas();

                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);

                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {

                            String aid = cursor.getString(cursor.getColumnIndex("AREA_ID"));
                            String aname = cursor.getString(cursor.getColumnIndex("AREA_NAME"));
                            String acode = cursor.getString(cursor.getColumnIndex("AREA_CODE"));
                            String Aoa = cursor.getString(cursor.getColumnIndex("AREA_OUTSTANDING"));
                            String Acol = cursor.getString(cursor.getColumnIndex("AREA_COLLECTION"));

                            totalOs = totalOs + Double.parseDouble(Aoa);
                            totalCollection = totalCollection + Double.parseDouble(Acol);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("AreaId", aid);
                            map.put("AreaName", aname);
                            map.put("AreaCode", acode);
                            /*map.put("Outstanding", str+Aoa);
                            map.put("Collection", str+Acol);*/
                            map.put("Outstanding", str + format.format(Double.parseDouble(Aoa)));
                            map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                            areaDetails.add(map);

                        } while (cursor.moveToNext());
                    }

                    adapter = new SimpleAdapter(getContext(), areaDetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                    lvArea.setAdapter(adapter);

                    myDB.close();

                    String tc = String.valueOf(totalCollection);
                    String toa = String.valueOf(totalOs);

                    tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                    tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                    srLayout.setEnabled(false);

                }

            } else {
                adapter = new SimpleAdapter(getContext(), areaDetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                lvArea.setAdapter(adapter);

                srLayout.setRefreshing(true);
                mPage = 0;

                CallVolleys(url);
            }


            srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (!url.equals("-")) {

                        if (srLayout.isEnabled()) {
                            srLayout.setRefreshing(true);
                            areaDetails.clear();
                            mPage = 0;
                            CallVolleys(url);

                        } else {
                            srLayout.setRefreshing(false);
                        }
                    } else {
                        areaDetails.clear();
                        double totalos = 0.0, totalcol = 0.0;

                        Cursor c = myDB.getAreas();

                        DecimalFormat format = new DecimalFormat();
                        format.setDecimalSeparatorAlwaysShown(false);

                        if (c.getCount() > 0) {
                            if (c.moveToFirst()) {
                                do {

                                    String aid = c.getString(c.getColumnIndex("AREA_ID"));
                                    String aname = c.getString(c.getColumnIndex("AREA_NAME"));
                                    String acode = c.getString(c.getColumnIndex("AREA_CODE"));
                                    String Aoa = c.getString(c.getColumnIndex("AREA_OUTSTANDING"));
                                    String Acol = c.getString(c.getColumnIndex("AREA_COLLECTION"));

                                    totalos = totalos + Double.parseDouble(Aoa);
                                    totalcol = totalcol + Double.parseDouble(Acol);

                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("AreaId", aid);
                                    map.put("AreaName", aname);
                                    map.put("AreaCode", acode);
                                    map.put("Outstanding", str + format.format(Double.parseDouble(Aoa)));
                                    map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                                    areaDetails.add(map);

                                } while (c.moveToNext());
                            }

                            adapter.notifyDataSetChanged();

                            String tc = String.valueOf(totalcol);
                            String toa = String.valueOf(totalos);

                            tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                            tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                            srLayout.setRefreshing(false);

                        }
                    }
                }
            });


            lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Context con = getActivity();

                    SharedPreferences pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("from");
                    editor.putString("AreaStatus", "true");
                    editor.putString("AreaId", areaDetails.get(position).get("AreaId"));
                    editor.putString("AreaName", areaDetails.get(position).get("AreaName"));
                    editor.putString("Collection", areaDetails.get(position).get("Collection"));
                    editor.putString("from", "Payment");
                    editor.apply();

                    Intent i = new Intent(getContext(), CustomerListActivity.class);
                    startActivity(i);
                }
            });

            lvArea.setOnScrollListener(new InfiniteScrollListener(1) {
                @Override
                public void loadMore(int page, int totalItemsCount) {

                    // mPage = page;

                    if (!url.equals("-")) {

                        mPage = mPage + 1;

                        CallVolleys(url);
                    }
                }

                @Override
                public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (lvArea.getChildAt(0) != null) {
                        srLayout.setEnabled(lvArea.getFirstVisiblePosition() == 0 && lvArea.getChildAt(0).getTop() == 0);
                    }

                    super.onScrollStateChanged(view, scrollState);
                }
            });

            fabSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater li = LayoutInflater.from(getContext());

                    View vs = li.inflate(R.layout.layput_dialog_search_payment, null);

                    EditText etsearch = (EditText) vs.findViewById(R.id.editText20);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                    dialogBuilder.setView(vs);

                    etsearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                                if (!url.equals("-")) {

                                    URL = siteUrl + "/SearchCustomerForCollectionApp";

                                    CallVolley(URL, v.getText().toString());
                                } else {
                                    try {

                                        Cursor c = myDB.SearchCustomer(v.getText().toString());

                                        if (c != null && c.getCount() > 0) {
                                            if (c.moveToFirst()) {

                                                String name = c.getString(c.getColumnIndex(myDB.NAME));
                                                String acno = c.getString(c.getColumnIndex(myDB.ACCOUNTNO));
                                                String cid = c.getString(c.getColumnIndex(myDB.PK_CUSTOMER_IDD));
                                                String bid = c.getString(c.getColumnIndex(myDB.BILL_ID));

                                                // Toast.makeText(getContext(), name + acno + contractorId + bid, Toast.LENGTH_SHORT).show();

                                                Intent i = new Intent(getContext(), CustomerDetail_Offline.class);
                                                i.putExtra("cname", name);
                                                i.putExtra("A/cNo", acno);
                                                i.putExtra("CustomerId", cid);
                                                i.putExtra("billId", bid);
                                                //i.putExtra("TotalOutStandingAmount",ss.get("TotalOutStandingAmount"));
                                                startActivity(i);
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "No Customer found..!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("Error:", e.toString());
                                    }

                                }

                                return true;
                            }
                            return false;
                        }
                    });

                    AlertDialog ad = dialogBuilder.create();
                    ad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ad.show();
                }
            });

        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());

        // paymentAreaListAdapter=new PaymentAreaListAdapter(getContext(),areaDetails);

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

    }


    public void CallVolley(String a, String text) {


        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        if (!srLayout.isRefreshing()){
            srLayout.setRefreshing(true);
        }

        try {

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
                            try {

                                if (srLayout.isRefreshing()){
                                    srLayout.setRefreshing(false);
                                }

                                if (loader.isShowing()) {
                                    loader.dismiss();
                                }

                                Utils.checkLog("payment", response, null);

                                if (response.getString("status").equalsIgnoreCase("True")) {
                                    String title = null, acno = null, custid = null;
                                    final JSONArray entityArray = response.getJSONArray("CustomerInfoList");

                                    for (int i = 0; i < entityArray.length(); i++) {
                                        JSONObject e = (JSONObject) entityArray.get(i);

                                        title = e.getString("Name");
                                        acno = e.getString("AccountNo");
                                        custid = e.getString("CustomerId");
                                    }

                                    Intent i = new Intent(getContext(), CustomerDetails.class);
                                    i.putExtra("cname", title);
                                    i.putExtra("A/cNo", acno);
                                    i.putExtra("CustomerId", custid);
                                    // i.putExtra("from","Payment");
                                    // editor.putString("from", "Payment");
                                    startActivity(i);
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

                            if (srLayout.isRefreshing()){
                                srLayout.setRefreshing(false);
                            }

                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(getContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a) {

        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();

        if (!srLayout.isRefreshing()){
            srLayout.setRefreshing(true);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("userId", uid);
        map.put("entityId", eid);
        map.put("startindex", String.valueOf(mPage));
        map.put("noofrecords", "10");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (loader.isShowing()) {
                                loader.dismiss();
                            }

                            if (srLayout.isRefreshing()){
                                srLayout.setRefreshing(false);
                            }

                            Utils.checkLog("payment", response, null);

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                DecimalFormat format = new DecimalFormat();
                                format.setDecimalSeparatorAlwaysShown(false);

                                final JSONArray entityArray = response.getJSONArray("AreaInfoList");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    String aid = e.getString("AreaId");
                                    String aname = e.getString("AreaName");
                                    String acode = e.getString("AreaCode");
                                    String Aoa = e.getString("Outstanding");
                                    String Acol = e.getString("Collection");

                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("AreaId", aid);
                                    map.put("AreaName", aname);
                                    map.put("AreaCode", acode);
                                    map.put("Outstanding", str + format.format(Double.parseDouble(Aoa)));
                                    map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                                    areaDetails.add(map);

                                }

                                adapter.notifyDataSetChanged();

                                String tc = response.getString("TotalCollection");
                                String toa = response.getString("TotalOutstanding");

                                tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                                tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                                srLayout.setRefreshing(false);

                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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

                        if (srLayout.isRefreshing()){
                            srLayout.setRefreshing(false);
                        }

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    public void loadOfflineData() {
        Cursor c = myDB.getAreas();

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    String aid = c.getString(c.getColumnIndex("AREA_ID"));
                    String aname = c.getString(c.getColumnIndex("AREA_NAME"));
                    String acode = c.getString(c.getColumnIndex("AREA_CODE"));
                    String Aoa = c.getString(c.getColumnIndex("AREA_OUTSTANDING"));
                    String Acol = c.getString(c.getColumnIndex("AREA_COLLECTION"));

                    HashMap<String, String> map = new HashMap<>();

                    map.put("AreaId", aid);
                    map.put("AreaName", aname);
                    map.put("AreaCode", acode);
                    map.put("Outstanding", str + Aoa);
                    map.put("Collection", str + Acol);

                    areaDetails.add(map);

                } while (c.moveToNext());
            }
        }

    }


}