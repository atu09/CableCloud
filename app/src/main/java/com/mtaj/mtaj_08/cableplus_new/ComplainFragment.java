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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.ashokvarma.bottomnavigation.R.styleable.BottomNavigationBar;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class ComplainFragment extends Fragment {

    private static final String PREF_NAME = "LoginPref";

    ListView lstcomplain;
    BottomNavigationBar bottomNavigationBar;
    ArrayList<HashMap<String,String>> complainlist=new ArrayList<>();
   // StickyListHeadersListView lvcomplain;

    ListView lvcomplain;

    List<ArrayList<HashMap<String,String>>> mergedlist=new ArrayList<>();

    String url;

    ArrayList<HashMap<String,String>> arealist=new ArrayList<>();

    MyExpandableListAdapter expandableListAdapter;

    AreaParent parent;
    CustomerChild child;


    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    TextView tvtotalcomplaint;

    ArrayList<HashMap<String,String>> customerlist=new ArrayList<>();

    List<ComplainLIstClass> details=new ArrayList<>();

    List<AreaParent> explist=new ArrayList<>();

    ArrayList<JSONObject> areajsonlist=new ArrayList<>();

    ArrayList<JSONObject> customerjsonarraylist=new ArrayList<>();

    SwipeRefreshLayout swrefresh;

    String siteurl,uid,cid,aid,eid,URL;
    RequestQueue requestQueue;

     //SimpleAdapter da;

    ComplainDataAdapter da;

    ExpandableListView explistcomplain;

 SharedPreferences pref;

    ComplainDataAdapter complainDataAdapter;

    RelativeLayout rlmain;

    TextView tvnocomplaint;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context con=getActivity();
        View vc;

         /*pref=con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(getContext());*/

        url = getArguments().getString("url");

       /* siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        eid=pref.getString("Entityids", "").toString();*/

       if(url.equals("-"))
       {
           vc = inflater.inflate(R.layout.layout_offline, null);
       }
        else {

           if (pref.getString("RoleId", "").toString().equals("2")) {

               vc = inflater.inflate(R.layout.complain, null);

               URL = siteurl + "/GetComplainListByAreaForAdminCollectionApp";
               CallVolley(URL);

           } else {
               if (pref.getBoolean("IsComplain", true)) {

                   if (url.equals("-")) {
                       vc = inflater.inflate(R.layout.layout_offline, null);
                   } else {
                       //new JSONAsynk().execute(new String[]{url});

                       url=siteurl + "/GetComplainListByAreaForCollectionApp";

                       CallVolleyss(url);

                       vc = inflater.inflate(R.layout.complaint_2, null);
                   }
               } else {
                   vc = inflater.inflate(R.layout.no_access_layout, null);
               }
           }
       }

        return  vc;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context con = getActivity();
         pref= con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.getString("RoleId", "").toString().equals("2") && !url.equals("-") ) {

            swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            lvcomplain = (ListView) view.findViewById(R.id.listView5);
            tvnocomplaint=(TextView)view.findViewById(R.id.textView100);

           // lvcomplain.setAdapter(complainDataAdapter);

            bottomNavigationBar=(BottomNavigationBar)view.findViewById(R.id.bottom_navigation_bar);

            bottomNavigationBar
                    .addItem(new BottomNavigationItem(R.drawable.areaicon, "By Area").setActiveColorResource(R.color.ToolbarColor))
                    .addItem(new BottomNavigationItem(R.drawable.customericon, "By Operator").setActiveColorResource(R.color.ToolbarColor))
                    .addItem(new BottomNavigationItem(R.drawable.ic_search_white_24dp, "Search").setActiveColorResource(R.color.ToolbarColor)).initialise();

            hideKeyboard(lstcomplain, getContext());

            swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    swrefresh.setRefreshing(true);
                    
                    if(bottomNavigationBar.getCurrentSelectedPosition()==0)
                    {
                        URL = siteurl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);

                    }

                    else if(bottomNavigationBar.getCurrentSelectedPosition()==1)
                    {
                        URL = siteurl + "/GetComplainListByUserForAdminCollectionApp";
                        CallVolleys(URL);
                    }

                  //  URL = siteurl + "/GetComplainListByAreaForAdminCollectionApp";
                  // CallVolley(URL);
                }
            });

            lvcomplain.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lvcomplain == null || lvcomplain.getChildCount() == 0) ?
                            0 : lvcomplain.getChildAt(0).getTop();
                    swrefresh.setEnabled((topRowVerticalPosition >= 0));

                }
            });

            bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                @Override
                public void onTabSelected(int position) {

                    if (position == 0) {
                        URL = siteurl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);
                    }
                    if (position == 1) {

                        URL = siteurl + "/GetComplainListByUserForAdminCollectionApp";
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
                        URL = siteurl + "/GetComplainListByAreaForAdminCollectionApp";
                        CallVolley(URL);
                    }
                    if (position == 1) {

                        URL = siteurl + "/GetComplainListByUserForAdminCollectionApp";
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

            lvcomplain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    TextView tvareaname = (TextView) view.findViewById(R.id.textView31);
                    LinearLayout tvnew = (LinearLayout) view.findViewById(R.id.llnew);
                    LinearLayout tvhigh = (LinearLayout) view.findViewById(R.id.llhigh);
                    LinearLayout tvactive = (LinearLayout) view.findViewById(R.id.llactive);
                    LinearLayout tvresolve = (LinearLayout) view.findViewById(R.id.llresolved);

                /*    i.putExtra("Id",arealist.get(position).get("areaId"));
                    i.putExtra("title", arealist.get(position).get("areaname"));*/


                    tvnew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (arealist.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "NEW");
                                i.putExtra("Id", arealist.get(position).get("userId"));
                                i.putExtra("title", arealist.get(position).get("userName"));
                                startActivity(i);

                            }


                            if (arealist.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "NEW");
                                i.putExtra("Id", arealist.get(position).get("areaId"));
                                i.putExtra("title", arealist.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvhigh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (arealist.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "HIGH");
                                i.putExtra("Id", arealist.get(position).get("userId"));
                                i.putExtra("title", arealist.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (arealist.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "HIGH");
                                i.putExtra("Id", arealist.get(position).get("areaId"));
                                i.putExtra("title", arealist.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvactive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (arealist.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "ACTIVE");
                                i.putExtra("Id", arealist.get(position).get("userId"));
                                i.putExtra("title", arealist.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (arealist.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "ACTIVE");
                                i.putExtra("Id", arealist.get(position).get("areaId"));
                                i.putExtra("title", arealist.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                    tvresolve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (arealist.get(position).get("type").equals("User")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "User");
                                i.putExtra("status", "RESOLVED");
                                i.putExtra("Id", arealist.get(position).get("userId"));
                                i.putExtra("title", arealist.get(position).get("userName"));
                                startActivity(i);

                            }

                            if (arealist.get(position).get("type").equals("Area")) {

                                Intent i = new Intent(getContext(), SearchWithCustomerListInComplains.class);
                                i.putExtra("Mode", "Area");
                                i.putExtra("status", "RESOLVED");
                                i.putExtra("Id", arealist.get(position).get("areaId"));
                                i.putExtra("title", arealist.get(position).get("areaname"));
                                startActivity(i);

                            }

                        }
                    });

                }
            });




        }

        else {

            if (pref.getBoolean("IsComplain", true) && !url.equals("-")) {

               // lvcomplain = (ListView) view.findViewById(R.id.listView5);

                rlmain=(RelativeLayout)view.findViewById(R.id.rlmain);

                explistcomplain=(ExpandableListView)view.findViewById(R.id.expandableListView1);

                //explistcomplain.setAdapter(expandableListAdapter);

                tvtotalcomplaint = (TextView) view.findViewById(R.id.textView30);

                swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

                tvnocomplaint=(TextView)view.findViewById(R.id.textView100);

                swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        swrefresh.setRefreshing(true);

                      //  new JSONAsynk().execute(new String[]{url});
                        CallVolleyss(url);

                    }
                });

                explistcomplain.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        int topRowVerticalPosition = (explistcomplain == null || explistcomplain.getChildCount() == 0) ?
                                0 : explistcomplain.getChildAt(0).getTop();
                        swrefresh.setEnabled((topRowVerticalPosition >= 0));

                    }
                });

                explistcomplain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                        String cid=explist.get(groupPosition).getChildren().get(childPosition).getCmpid();
                        String cmid=explist.get(groupPosition).getChildren().get(childPosition).getCustid();

                        //Toast.makeText(getContext(), cid, Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getContext(), cmid, Toast.LENGTH_SHORT).show();


                        Intent i=new Intent(getContext(),ComplainDetails.class);
                        i.putExtra("title",explist.get(groupPosition).getChildren().get(childPosition).getName());
                        i.putExtra("customerId",explist.get(groupPosition).getChildren().get(childPosition).getCustid());
                        i.putExtra("complainId",explist.get(groupPosition).getChildren().get(childPosition).getCmpid());
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

        complainDataAdapter=new ComplainDataAdapter(getContext(),arealist);

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        //expandableListAdapter=new MyExpandableListAdapter(getContext(),explist);

        //setRetainInstance(true);
    }


    public static void hideKeyboard(View view,Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try{
                    jobj = new JSONObject(json);

                    // JSONArray jarrays=new JSONArray(json);

                    // jobj=jarrays.getJSONObject(0);

                    //  org.json.simple.parser.JSONParser jsonparse=new org.json.simple.parser.JSONParser();

                    // jarr =(JSONArray)jsonparse.parse(json);
                    // jobj = jarr.getJSONObject(0);
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
                Toast.makeText(getActivity(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try
            {

                if(json.getString("status").toString().equals("True"))
                {
                    explist.clear();

                    Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                    tvtotalcomplaint.setText("TOTAL COMPLAINTS:  "+json.getString("totalComplain").toString());

                    final JSONArray entityarray = json.getJSONArray("UserComplainInfoList");

                   // Toast.makeText(getContext(), "area lenght="+entityarray.length(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = entityarray.getJSONObject(i);

                       // Toast.makeText(getContext(),e.toString(), Toast.LENGTH_SHORT).show();

                        parent = new AreaParent();

                        String aid = e.getString("areaId");
                        String aname = e.getString("areaname");
                        String acomplaincount = e.getString("areacomplaincount");

                        parent.setName(aname);
                        parent.setCount(acomplaincount);
                        parent.setChildren(new ArrayList<CustomerChild>());


                        JSONArray jsarr=e.getJSONArray("lstCustInfo");

                      //  Toast.makeText(getContext(), "cust lenght="+jsarr.length(), Toast.LENGTH_SHORT).show();

                        for(int j=0;j<Integer.parseInt(acomplaincount);j++)
                        {
                              child = new CustomerChild();

                            JSONObject s = jsarr.getJSONObject(i);

                         //   Toast.makeText(getContext(),s.toString(), Toast.LENGTH_SHORT).show();

                            String cid=s.getString("customerId");
                            String cname=s.getString("Name");
                            String cacno=s.getString("AccNo");
                            String cmqno=s.getString("MqNo");
                            String cadd=s.getString("Address");
                            String cmpid=s.getString("complainId");

                            child.setCustid(cid);
                            child.setCmpid(cmpid);
                            child.setName(cname);
                            child.setAcno(cacno);
                            child.setMqno(cmqno);
                            child.setAddress(cadd);

                            parent.getChildren().add(child);

                          /*  HashMap<String,String> map=new HashMap<>();

                            map.put("customerId",cid);
                            map.put("Name",cname);
                            map.put("AccNo",cacno);
                            map.put("MqNo",cmqno);
                            map.put("Address",cadd);
                            map.put("complainId",cmpid);

                            customerlist.add(map);*/

                        }


                       // details.add(new ComplainLIstClass(aid,aname,acomplaincount,customerlist));

                        explist.add(parent);

                    }

                    expandableListAdapter.notifyDataSetChanged();

                    swrefresh.setRefreshing(false);
                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

    }

    public void CallVolley(String a)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(getActivity(),R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String,String> map=new HashMap<>();
            map.put("contractorId",cid);
            map.put("loginuserId",uid);
            map.put("entityIds",eid);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();
                                arealist.clear();
                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {

                                       // Toast.makeText(getContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                        final JSONArray entityarray = response.getJSONArray("UserComplainInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("type","Area");
                                            map.put("areaId",e.getString("areaId").toString());
                                            map.put("areaname",e.getString("areaname").toString());
                                            map.put("newcomplaincount", e.getString("newcomplaincount").toString());
                                            map.put("highcomplaincount",e.getString("highcomplaincount").toString());
                                            map.put("activecomplaincount",e.getString("activecomplaincount").toString());
                                            map.put("resolvecomplaincount",e.getString("resolvecomplaincount").toString());
                                            map.put("newcomplaincommentcount",e.getString("newcomplaincommentcount").toString());
                                            map.put("highcomplaincommentcount",e.getString("highcomplaincommentcount").toString());
                                            map.put("activecomplaincommentcount",e.getString("activecomplaincommentcount").toString());
                                            map.put("resolvecomplaincommentcount",e.getString("resolvecomplaincommentcount").toString());

                                            arealist.add(map);

                                        }

                                        if(arealist.size()==0)
                                        {
                                            tvnocomplaint.setVisibility(View.VISIBLE);
                                        }

                                       // complainDataAdapter.notifyDataSetChanged();

                                        da = new ComplainDataAdapter(getContext(), arealist);
                                        lvcomplain.setAdapter(da);

                                        swrefresh.setRefreshing(false);
                                    }
                                    else
                                    {
                                       // Toast.makeText(getContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                        if(arealist.size()==0)
                                        {
                                            tvnocomplaint.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                //Toast.makeText(getContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                           // Toast.makeText(getContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(getActivity(),R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(false);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String,String> map=new HashMap<>();
            map.put("contractorId",cid);
            map.put("loginuserId",uid);
            map.put("entityIds",eid);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                arealist.clear();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final JSONArray entityarray = response.getJSONArray("UserComplainInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("type","User");
                                            map.put("userId",e.getString("userId").toString());
                                            map.put("userName",e.getString("userName").toString());
                                            map.put("newcomplaincount", e.getString("newcomplaincount").toString());
                                            map.put("highcomplaincount",e.getString("highcomplaincount").toString());
                                            map.put("activecomplaincount",e.getString("activecomplaincount").toString());
                                            map.put("resolvecomplaincount",e.getString("resolvecomplaincount").toString());

                                            map.put("newcomplaincommentcount",e.getString("newcomplaincommentcount").toString());
                                            map.put("highcomplaincommentcount",e.getString("highcomplaincommentcount").toString());
                                            map.put("activecomplaincommentcount",e.getString("activecomplaincommentcount").toString());
                                            map.put("resolvecomplaincommentcount",e.getString("resolvecomplaincommentcount").toString());


                                            arealist.add(map);

                                        }

                                        if(arealist.size()==0)
                                        {
                                            tvnocomplaint.setVisibility(View.VISIBLE);
                                        }

                                        da = new ComplainDataAdapter(getContext(), arealist);
                                        lvcomplain.setAdapter(da);

                                        swrefresh.setRefreshing(false);
                                    }
                                    else
                                    {
                                        if(arealist.size()==0)
                                        {
                                            tvnocomplaint.setVisibility(View.VISIBLE);
                                        }
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }


    public void CallVolleyss(String a)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(getActivity(),R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(false);
        spload.show();

        //URL=siteurl+"/GetComplainListByAreaForCollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityIds="+pref.getString("Entityids","").toString();

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorId",cid);
        map.put("loginuserId",uid);
        map.put("entityIds",eid);

        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         try {

                        spload.dismiss();

                        try
                        {

                            explist.clear();

                            //Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            if(response.getString("status").toString().equals("True"))
                            {

                                rlmain.setVisibility(View.VISIBLE);

                                tvtotalcomplaint.setText("TOTAL COMPLAINTS:  " + response.getString("totalComplain").toString());

                                final JSONArray entityarray = response.getJSONArray("UserComplainInfoList");

                                // Toast.makeText(getContext(), "area lenght="+entityarray.length(), Toast.LENGTH_SHORT).show();

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = entityarray.getJSONObject(i);

                                    // Toast.makeText(getContext(),e.toString(), Toast.LENGTH_SHORT).show();

                                    //areajsonlist.add(e);

                                   parent = new AreaParent();

                                    String aid = e.getString("areaId");
                                    String aname = e.getString("areaname");
                                    String acomplaincount = e.getString("areacomplaincount");
                                    String commentcount=e.getString("areacommentcount");


                                    parent.setName(aname);
                                    parent.setCount(acomplaincount);
                                    parent.setCommentcount(commentcount);
                                    parent.setChildren(new ArrayList<CustomerChild>());


                                    JSONArray jsarr=e.getJSONArray("lstCustInfo");

                                    //  Toast.makeText(getContext(), "cust lenght="+jsarr.length(), Toast.LENGTH_SHORT).show();

                                    for(int j=0;j<jsarr.length();j++)
                                    {
                                        child = new CustomerChild();

                                        JSONObject s = jsarr.getJSONObject(j);

                                        //   Toast.makeText(getContext(),s.toString(), Toast.LENGTH_SHORT).show();

                                        String cid=s.getString("customerId");
                                        String cname=s.getString("Name");
                                        String cacno=s.getString("AccNo");
                                        String cmqno=s.getString("MqNo");
                                        String cadd=s.getString("Address");
                                        String cmpid=s.getString("complainId");
                                        String commentcounts=s.getString("commentcount");

                                        child.setCustid(cid);
                                        child.setCmpid(cmpid);
                                        child.setName(cname);
                                        child.setAcno(cacno);
                                        child.setMqno(cmqno);
                                        child.setAddress(cadd);
                                        child.setCommentcount(commentcounts);

                                        parent.getChildren().add(child);

                                    }




                                    explist.add(parent);

                                    //expandableListAdapter.notifyDataSetChanged();

                                }

                                expandableListAdapter=new MyExpandableListAdapter(getContext(),explist);
                                explistcomplain.setAdapter(expandableListAdapter);


                                swrefresh.setRefreshing(false);


                            }
                            else
                            {
                                if(explist.size()==0)
                                {
                                    tvnocomplaint.setVisibility(View.VISIBLE);

                                    swrefresh.setRefreshing(false);
                                }

                               //
                               // Toast.makeText(getContext(),response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }



}