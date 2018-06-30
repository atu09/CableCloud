package com.mtaj.mtaj_08.cableplus_new.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.mtaj.mtaj_08.cableplus_new.Badge_Remider_Alert;
import com.mtaj.mtaj_08.cableplus_new.HomeDataAdapter;
import com.mtaj.mtaj_08.cableplus_new.Map_User_Tracking;
import com.mtaj.mtaj_08.cableplus_new.OnCountAssignment;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class HomeFragment extends Fragment {

    String url;
    OnCountAssignment mcount;
    SharedPreferences pref;
    private static final String LOGIN_PREF = "LoginPref";

    SwipeRefreshLayout refreshLayout;
    ListView listView;
    FloatingActionButton fab;

    ArrayList<HashMap<String, String>> homelist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.no_access_layout, null);
        if (pref.getBoolean("IsDashboard", true)) {
            url = getArguments().getString("url");
            if (url.equalsIgnoreCase("-")) {
                view = inflater.inflate(R.layout.layout_offline, null);
            } else {
                view = inflater.inflate(R.layout.home, null);
                refreshLayout = view.findViewById(R.id.refresh);

                new JSONAsync().execute();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        if (pref.getBoolean("IsDashboard", true) && !url.equals("-")) {

            listView = (ListView) view.findViewById(R.id.listView6);

            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            fab = (FloatingActionButton) view.findViewById(R.id.fabtrack);


            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                    refreshLayout.setEnabled((topRowVerticalPosition >= 0));

                }
            });

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new JSONAsync().execute();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), Map_User_Tracking.class));

                }
            });

        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getContext().getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);

    }

    public void setOnDetailsDefinedListener(OnCountAssignment listener) {
        mcount = listener;
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
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent(), "UTF-8"));

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

        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
            dialog = Utils.getLoader(getActivity());
            dialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utils.checkLog("entity", url, null);
            return makeHttpRequest(url);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
            dialog.dismiss();

            Utils.checkLog("entity", json, null);
            try {
                if (json.getString("status").equalsIgnoreCase("True")) {

                    homelist.clear();
                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    String s1 = json.getString("TodayCollection");
                    String s2 = json.getString("CurrentMonthBill");
                    String s3 = json.getString("Lastmonthoutstandingamount");
                    String s4 = json.getString("TotalOutstanding");
                    String s9 = json.getString("ThisMonthCollection");


                    Double total = Double.parseDouble(s2) + Double.parseDouble(s3);


                    String s5 = json.getString("TodayComplain");
                    String s6 = json.getString("CurrentPendingComplain");
                    String s7 = json.getString("LastPendingComplain");
                    String s8 = json.getString("TotalComplain");

                    HashMap<String, String> map = new HashMap<>();

                    map.put("TodayCollection", format.format(Double.parseDouble(s1)));
                    map.put("CurrentMonthBill", format.format(Double.parseDouble(s2)));
                    map.put("Lastmonthoutstandingamount", format.format(Double.parseDouble(s3)));
                    map.put("TotalOutstanding", format.format(Double.parseDouble(s4)));
                    map.put("ThisMonthCollection", format.format(Double.parseDouble(s9)));
                    map.put("Total", format.format(total));

                    map.put("TodayComplain", s5);
                    map.put("CurrentPendingComplain", s6);
                    map.put("LastPendingComplain", s7);
                    map.put("TotalComplain", s8);

                    homelist.add(map);


                    final int alertno = Integer.parseInt(json.getString("AlertCount"));
                    final int remno = Integer.parseInt(json.getString("RemCount"));
                    final int commentcount = Integer.parseInt(json.getString("ComCount"));
                    final int customercommentcount = Integer.parseInt(json.getString("CustComCount"));


                    Badge_Remider_Alert abc = new Badge_Remider_Alert(remno, alertno);

                    mcount = (OnCountAssignment) getContext();
                    mcount.OnCountAssign(alertno, remno, Integer.parseInt(s6), commentcount, customercommentcount);
                    listView.setAdapter(new HomeDataAdapter(getContext(), homelist));
                    refreshLayout.setRefreshing(false);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
