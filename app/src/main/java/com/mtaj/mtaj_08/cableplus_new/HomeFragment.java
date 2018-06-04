package com.mtaj.mtaj_08.cableplus_new;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class HomeFragment  extends Fragment {

    String Entity,url;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

     JSONObject jsonobj;

    OnCountAssignment mcount;
    SharedPreferences pref;
    private static final String PREF_NAME = "LoginPref";


    TextView tvtodaycol,tvcurmonthbill,tvlastmonthoa,tvtotaloutstanding;
    TextView tvtodaycomplain,tvcurmonthpend,tvlastmonthpend,tvtotalpendcomplain;

    SwipeRefreshLayout swrefresh;
    ListView lvhomelist;
    FloatingActionButton fabtracking;

    ArrayList<HashMap<String,String>> homelist=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      //  Entity=getArguments().getString("entity");

        View vc=inflater.inflate(R.layout.home,null);

        if(pref.getBoolean("IsDashboard",true)) {

            url = getArguments().getString("url");

            if(url.equals("-"))
            {
                vc = inflater.inflate(R.layout.layout_offline, null);
            }
            else {

                new JSONAsynk().execute(new String[]{url});

                vc = inflater.inflate(R.layout.home, null);
            }
        }
        else
        {
            vc= inflater.inflate(R.layout.no_access_layout,null);
        }

        return vc;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        /*CardView cv=(CardView)view.findViewById(R.id.card_view);
        cv.setCardBackgroundColor(Color.parseColor("#aa0000"));

        CardView cv1=(CardView)view.findViewById(R.id.card_view1);
        cv1.setCardBackgroundColor(Color.parseColor("#e59400"));*/

        if(pref.getBoolean("IsDashboard",true) && !url.equals("-")) {

            lvhomelist = (ListView) view.findViewById(R.id.listView6);

            swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            fabtracking=(FloatingActionButton)view.findViewById(R.id.fabtrack);


            lvhomelist.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lvhomelist == null || lvhomelist.getChildCount() == 0) ?
                            0 : lvhomelist.getChildAt(0).getTop();
                    swrefresh.setEnabled((topRowVerticalPosition >= 0));

                }
            });

            swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    swrefresh.setRefreshing(true);

                    new JSONAsynk().execute(new String[]{url});

                }
            });

            fabtracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(getContext(),Map_User_Tracking.class);
                    startActivity(i);

                }
            });

        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //setRetainInstance(true);



    }

    public void setOnDetailsDefinedListener(OnCountAssignment listener) {
        mcount = listener;
    }

    public JSONObject makeHttpRequest(String url){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost=new HttpGet(url);
        try{
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        try{

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            try{
                if(reader!=null) {

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                try{
                    jobj = new JSONObject(json);

                }catch (JSONException e){
                    Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
            }
        }catch (UnsupportedEncodingException e){
            Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }

    private class JSONAsynk extends AsyncTask<String,String,JSONObject>
    {

        private ProgressDialog pDialog;
       // public DotProgressBar dtprogoress;

        SpotsDialog spload;
        JSONObject jsn1,jsn,jsnmain;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(getActivity(),R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonobj=makeHttpRequest(params[0]);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {

           // hideSoftKeyboard(getActivity());
            spload.dismiss();

                try
                {
                    if(json.getString("status").toString().equals("True"))
                    {
                        homelist.clear();

                        DecimalFormat format = new DecimalFormat();
                        format.setDecimalSeparatorAlwaysShown(false);

                        String s1=json.getString("TodayCollection").toString();
                        String s2=json.getString("CurrentMonthBill").toString();
                        String s3=json.getString("Lastmonthoutstandingamount").toString();
                        String s4=json.getString("TotalOutstanding").toString();
                        String s9=json.getString("ThisMonthCollection").toString();


                        Double total=Double.parseDouble(s2)+Double.parseDouble(s3);


                        String s5=json.getString("TodayComplain").toString();
                        String s6=json.getString("CurrentPendingComplain").toString();
                        String s7=json.getString("LastPendingComplain").toString();
                        String s8=json.getString("TotalComplain").toString();

                        HashMap<String,String> map=new HashMap<>();

                        map.put("TodayCollection",format.format(Double.parseDouble(s1)));
                        map.put("CurrentMonthBill", format.format(Double.parseDouble(s2)));
                        map.put("Lastmonthoutstandingamount",format.format(Double.parseDouble(s3)));
                        map.put("TotalOutstanding",format.format(Double.parseDouble(s4)));
                        map.put("ThisMonthCollection",format.format(Double.parseDouble(s9)));
                        map.put("Total",format.format(total));

                        map.put("TodayComplain",s5);
                        map.put("CurrentPendingComplain",s6);
                        map.put("LastPendingComplain",s7);
                        map.put("TotalComplain",s8);

                        homelist.add(map);


                        final int alertno=Integer.parseInt(json.getString("AlertCount"));
                        final int remno=Integer.parseInt(json.getString("RemCount"));
                        final int commentcount=Integer.parseInt(json.getString("ComCount"));
                        final int customercommentcount=Integer.parseInt(json.getString("CustComCount"));


                        Badge_Remider_Alert abc=new Badge_Remider_Alert(remno,alertno);

                        mcount=(OnCountAssignment)getContext();
                        mcount.OnCountAssign(alertno, remno,Integer.parseInt(s6),commentcount,customercommentcount);

                        lvhomelist.setAdapter(new HomeDataAdapter(getContext(),homelist));
                        // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                      /*  tvtodaycol.setText(format.format(Double.parseDouble(s1)));
                        tvcurmonthbill.setText(String.valueOf(format.format(Double.parseDouble(s2))));
                        tvlastmonthoa.setText(format.format(Double.parseDouble(s3)));
                        tvtotaloutstanding.setText(format.format(Double.parseDouble(s4)));

                        tvtodaycomplain.setText(s5);
                        tvcurmonthpend.setText(s6);
                        tvlastmonthpend.setText(s7);
                        tvtotalpendcomplain.setText(s8);*/

                        swrefresh.setRefreshing(false);

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }





}
