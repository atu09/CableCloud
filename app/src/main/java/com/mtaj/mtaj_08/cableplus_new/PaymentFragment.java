package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
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

import android.widget.RelativeLayout;
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
import com.mtaj.mtaj_08.cableplus_new.helpers.MovableFloatingActionButton;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;
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
public class PaymentFragment extends Fragment {

    ListView lvarealist;

    private static final String PREF_NAME = "LoginPref";

    String url;

    String str = "\u20B9";

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    ArrayList<HashMap<String, String>> areadetails = new ArrayList<HashMap<String, String>>();

    TextView tvtotalcol, tvtotaloa;

    MovableFloatingActionButton fabsearch;

    String siteurl, uid, cid, aid, eid, URL;

    RequestQueue requestQueue;

    SwipeRefreshLayout swrefresh;

    SimpleAdapter da;

    PaymentAreaListAdapter paymentAreaListAdapter;

    SharedPreferences pref;

    RelativeLayout rlmain;

    int mPage = 0;

    DBHelper myDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context con = getActivity();

        View vc = inflater.inflate(R.layout.payment, null);

        final SharedPreferences pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.getBoolean("IsBilling", true)) {
            url = getArguments().getString("url");

            //Toast.makeText(getContext(), url, Toast.LENGTH_SHORT).show();

            if (url.equals("-")) {
                //Toast.makeText(getContext(),"A="+myDB.AreaCount(), Toast.LENGTH_SHORT).show();

                //loadOfflineData();

                vc = inflater.inflate(R.layout.payment, null);

            } else {
                CallVolleys(url);
                //  new JSONAsynk().execute(new String[]{url});
                vc = inflater.inflate(R.layout.payment, null);

            }

            //vc.setBackground(Utils.getGradientDrawable(getContext(), R.color.colorPrimaryLight, R.color.colorPrimary, R.color.colorPrimaryDark, GradientDrawable.Orientation.TOP_BOTTOM));

        } else {
            vc = inflater.inflate(R.layout.no_access_layout, null);
        }

        return vc;
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (pref.getBoolean("IsBilling", true)) {

            lvarealist = (ListView) view.findViewById(R.id.listView);
            fabsearch = (MovableFloatingActionButton) view.findViewById(R.id.fab);

            tvtotalcol = (TextView) view.findViewById(R.id.textView28);
            tvtotaloa = (TextView) view.findViewById(R.id.textView30);

            swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            rlmain = (RelativeLayout) view.findViewById(R.id.rlmain);

            // paymentAreaListAdapter =new PaymentAreaListAdapter(getContext(),areadetails);

            //  lvarealist.setAdapter(paymentAreaListAdapter);

            if (url.equals("-")) {

                myDB = new DBHelper(getContext());

                double totalos = 0.0, totalcol = 0.0;

                Cursor c = myDB.getAreas();

                //swrefresh.setEnabled(false);

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
                            /*map.put("Outstanding", str+Aoa);
                            map.put("Collection", str+Acol);*/
                            map.put("Outstanding", str + format.format(Double.parseDouble(Aoa)));
                            map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                            areadetails.add(map);

                        } while (c.moveToNext());
                    }

                    ///Toast.makeText(getContext(),"**"+ areadetails.size(), Toast.LENGTH_SHORT).show();

                    rlmain.setVisibility(View.VISIBLE);

                    da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                    lvarealist.setAdapter(da);

                    myDB.close();

                    /*if(areadetails.size()>0)
                    {
                        for(int k=0;k<areadetails.size();k++)
                        {
                            totalos=totalos+Long.parseLong(areadetails.get(k).get("Outstanding"));
                            totalcol=totalcol+Long.parseLong(areadetails.get(k).get("Collection"));
                        }
                    }*/

                    String tc = String.valueOf(totalcol);
                    String toa = String.valueOf(totalos);

                    tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                    tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                    swrefresh.setEnabled(false);

                }

            } else {

                da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                lvarealist.setAdapter(da);
            }


            swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (!url.equals("-")) {

                        if (swrefresh.isEnabled()) {

                            swrefresh.setRefreshing(true);

                            areadetails.clear();

                            mPage = 0;

                            //new JSONAsynk().execute(new String[]{url});
                            CallVolleys(url);
                        } else {
                            swrefresh.setRefreshing(false);
                        }
                    } else {
                        areadetails.clear();
                        double totalos = 0.0, totalcol = 0.0;

                        Cursor c = myDB.getAreas();

                        //swrefresh.setEnabled(false);

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
                            /*map.put("Outstanding", str+Aoa);
                            map.put("Collection", str+Acol);*/
                                    map.put("Outstanding", str + format.format(Double.parseDouble(Aoa)));
                                    map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                                    areadetails.add(map);

                                } while (c.moveToNext());
                            }

                            // Toast.makeText(getContext(), "**" + areadetails.size(), Toast.LENGTH_SHORT).show();

                            rlmain.setVisibility(View.VISIBLE);

                            da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                            lvarealist.setAdapter(da);

                            /*if(areadetails.size()>0)
                            {
                                for(int k=0;k<areadetails.size();k++)
                                {
                                    totalos=totalos+Long.parseLong(areadetails.get(k).get("Outstanding"));
                                    totalcol=totalcol+Long.parseLong(areadetails.get(k).get("Collection"));
                                }
                            }*/

                            String tc = String.valueOf(totalcol);
                            String toa = String.valueOf(totalos);

                            tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                            tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                            swrefresh.setRefreshing(false);

                        }
                    }
                }
            });

          /*  lvarealist.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    float y = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE: {

                        }
                        break;
                        case MotionEvent.ACTION_DOWN: {
                            swrefresh.setEnabled(false);



                        }
                        case MotionEvent.ACTION_UP: {
                            if(lvarealist.getFirstVisiblePosition()==0)
                            {
                                swrefresh.setEnabled(true);
                                swrefresh.setRefreshing(true);
                            }
                        }

                    }

                    return false;
                }
            });
*/




          /*  lvarealist.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lvarealist == null || lvarealist.getChildCount() == 0) ?
                            0 : lvarealist.getChildAt(0).getTop();
                    swrefresh.setEnabled((topRowVerticalPosition >= 0));

                }
            });
*/

            lvarealist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Context con = getActivity();

                    SharedPreferences pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("from");
                    editor.putString("AreaStatus", "true");
                    editor.putString("AreaId", areadetails.get(position).get("AreaId"));
                    editor.putString("AreaName", areadetails.get(position).get("AreaName"));
                    editor.putString("Collection", areadetails.get(position).get("Collection"));
                    editor.putString("from", "Payment");
                    editor.commit();

                /*map.put("AreaId",aid);
                map.put("AreaName",aname);
                map.put("AreaCode",acode);
                map.put("Outstanding",Aoa);
                map.put("Collection",Acol);*/

                    // Toast.makeText(getContext(), areadetails.get(position).get("AreaId") + "--" + areadetails.get(position).get("AreaCode"), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getContext(), CustomerListActivity.class);
                    startActivity(i);
                }
            });

            lvarealist.setOnScrollListener(new InfiniteScrollListener(1) {
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

                    if (lvarealist.getChildAt(0) != null) {
                        swrefresh.setEnabled(lvarealist.getFirstVisiblePosition() == 0 && lvarealist.getChildAt(0).getTop() == 0);
                    }

                    super.onScrollStateChanged(view, scrollState);
                }
            });

            fabsearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Toast.makeText(getContext(), "Search...", Toast.LENGTH_SHORT).show();

                    LayoutInflater li = LayoutInflater.from(getContext());

                    View vs = li.inflate(R.layout.layput_dialog_search_payment, null);

                    EditText etsearch = (EditText) vs.findViewById(R.id.editText20);

                    AlertDialog.Builder dialogBuilder = new
                            AlertDialog.Builder(getActivity());

               /* RelativeLayout titleLayout = new RelativeLayout(getContext());
                SearchView sv = new SearchView(getContext());
                sv.onActionViewExpanded();
                titleLayout.addView(vs);*/

                    dialogBuilder.setView(vs);

                    etsearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                                if (!url.equals("-")) {

                                    URL = siteurl + "/SearchCustomerForCollectionApp";

                                    //Toast.makeText(getContext(), v.getText().toString(), Toast.LENGTH_SHORT).show();

                                    CallVolley(URL, v.getText().toString());
                                } else {
                                    try {

                                        Cursor c = myDB.SearchCustomer(v.getText().toString());

                                        //Toast.makeText(getContext(), "s=" + c.getCount(), Toast.LENGTH_LONG).show();

                                        if (c != null && c.getCount() > 0) {
                                            if (c.moveToFirst()) {

                                                String name = c.getString(c.getColumnIndex(myDB.NAME));
                                                String acno = c.getString(c.getColumnIndex(myDB.ACCOUNTNO));
                                                String cid = c.getString(c.getColumnIndex(myDB.PK_CUSTOMER_IDD));
                                                String bid = c.getString(c.getColumnIndex(myDB.BILL_ID));

                                                // Toast.makeText(getContext(), name + acno + cid + bid, Toast.LENGTH_SHORT).show();

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

        // paymentAreaListAdapter=new PaymentAreaListAdapter(getContext(),areadetails);

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

    }


    public JSONObject makeHttpRequest(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(url);
        try {
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {


            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                if (reader != null) {

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try {
                    jobj = new JSONObject(json);

                    // JSONArray jarrays=new JSONArray(json);

                    // jobj=jarrays.getJSONObject(0);

                    //  org.json.simple.parser.JSONParser jsonparse=new org.json.simple.parser.JSONParser();

                    // jarr =(JSONArray)jsonparse.parse(json);
                    // jobj = jarr.getJSONObject(0);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "**" + e, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), "**" + e, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), "**" + e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }

    public void CallVolley(String a, String text) {


        final SpotsDialog spload;
        spload = new SpotsDialog(getActivity(), R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String, String> map = new HashMap<>();
            map.put("startindex", "0");
            map.put("noofrecords", "10000");
            map.put("contractorid", cid);
            map.put("userId", uid);
            map.put("entityId", eid);
            map.put("filterCustomer", text);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        String title = null, acno = null, custid = null;
                                        final JSONArray entityarray = response.getJSONArray("CustomerInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

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

                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(getContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    private class JSONAsynk extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1, jsn, jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload = new SpotsDialog(getActivity(), R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonobj = makeHttpRequest(params[0]);


            } catch (Exception e) {
                //Toast.makeText(getActivity(), "--" + e, Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

            return jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try {
                areadetails.clear();

                // Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                if (json.getString("status").toString().equals("True")) {
                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    //  Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                    final JSONArray entityarray = jsonobj.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        String aid = e.getString("AreaId");
                        String aname = e.getString("AreaName");
                        String acode = e.getString("AreaCode");
                        String Aoa = e.getString("Outstanding");
                        String Acol = e.getString("Collection");

                        HashMap<String, String> map = new HashMap<>();

                        map.put("AreaId", aid);
                        map.put("AreaName", aname);
                        map.put("AreaCode", acode);
                        map.put("Outstanding", format.format(Double.parseDouble(Aoa)));
                        map.put("Collection", format.format(Double.parseDouble(Acol)));

                        areadetails.add(map);

                    }

                    // da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                    // lvarealist.setAdapter(da);

                    //lvarealist.setAdapter(new PaymentAreaListAdapter(getContext(),areadetails));


                    paymentAreaListAdapter.notifyDataSetChanged();

                    String tc = json.getString("TotalCollection").toString();
                    String toa = json.getString("TotalOutstanding").toString();

                    tvtotalcol.setText(format.format(Double.parseDouble(tc)));
                    tvtotaloa.setText(format.format(Double.parseDouble(toa)));

                    swrefresh.setRefreshing(false);

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void CallVolleys(String a) {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload = new SpotsDialog(getActivity(), R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);

        spload.show();

        //jsonobj=makeHttpRequest(params[0]);
        //  URL=siteurl+"/GetAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString();


        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("userId", uid);
        map.put("entityId", eid);
        map.put("startindex", String.valueOf(mPage));
        map.put("noofrecords", "10");


        obreqs = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {


                            //  Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            if (response.getString("status").toString().equals("True")) {

                                rlmain.setVisibility(View.VISIBLE);
                                DecimalFormat format = new DecimalFormat();
                                format.setDecimalSeparatorAlwaysShown(false);

                                //  Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                                final JSONArray entityarray = response.getJSONArray("AreaInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

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

                                    areadetails.add(map);

                                }

                                        /* da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                                         lvarealist.setAdapter(da);*/

                                da.notifyDataSetChanged();

                                //lvarealist.setAdapter(new PaymentAreaListAdapter(getContext(),areadetails));

                                //  paymentAreaListAdapter=new PaymentAreaListAdapter(getContext(),areadetails);

                                // lvarealist.setAdapter(new PaymentAreaListAdapter(getContext(),areadetails));

                                // ((BaseAdapter)lvarealist.getAdapter()).notifyDataSetChanged();

                                String tc = response.getString("TotalCollection").toString();
                                String toa = response.getString("TotalOutstanding").toString();

                                tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));
                                tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));

                                swrefresh.setRefreshing(false);

                                // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                           /* } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }

    public void loadOfflineData() {
        Cursor c = myDB.getAreas();

        //swrefresh.setEnabled(false);

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
                    // map.put("Outstanding", str+format.format(Double.parseDouble(Aoa)));
                    ///map.put("Collection", str + format.format(Double.parseDouble(Acol)));

                    areadetails.add(map);

                } while (c.moveToNext());
            }

            Toast.makeText(getContext(), "**" + areadetails.size(), Toast.LENGTH_SHORT).show();

        }


    }


}