package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
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

import dmax.dialog.SpotsDialog;

public class AreaListInCustomers extends AppCompatActivity {
    private static final String PREF_NAME = "LoginPref";

    ArrayList<HashMap<String,String>> arealist=new ArrayList<>();

    ListView listareaincustomers;

    String siteurl,uid,cid,aid,eid,URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    RelativeLayout rlmain;

    TextView tvaddcustomer;

    int mPage=0;

    SimpleAdapter da;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list_in_customers);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        requestQueue= Volley.newRequestQueue(this);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        eid=pref.getString("Entityids","").toString();

        URL=siteurl+"/GetCustomersByAreaForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString()+"&startindex="+String.valueOf(mPage)+"&noofrecords=20";

        listareaincustomers=(ListView)findViewById(R.id.listareaincustomers);

        tvaddcustomer=(TextView)findViewById(R.id.textView30);

        rlmain=(RelativeLayout)findViewById(R.id.content);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Area List");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);



        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Intent i = new Intent(getApplicationContext(), DashBoard.class);
                // startActivity(i);

                onBackPressed();
            }
        });

        new JSONAsynk().execute(new String[]{URL});


        /*URL=siteurl+"/GetCustomersByAreaForCollectionApp";

        HashMap<String,String> map=new HashMap<String, String>();
        map.put("contractorId",cid);
        map.put("userId",uid);
        map.put("entityId",pref.getString("Entityids","").toString());
        map.put("startindex",String.valueOf(mPage));
        map.put("noofrecords","20");


        CallVolleyArea(URL,map);*/

        da=new SimpleAdapter(AreaListInCustomers.this,arealist,R.layout.arealist_customers,new String[]{"AreaName","TotalCustomer"},new int[]{R.id.textView2,R.id.textView24});
        listareaincustomers.setAdapter(da);

        listareaincustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                editor.remove("AreaId");
                editor.remove("from");

                editor.putString("AreaStatus", "true");
                editor.putString("AreaName", arealist.get(position).get("AreaName"));
                editor.putString("AreaId", arealist.get(position).get("AreaId"));
                editor.putString("from", "Customer");
                editor.commit();

                Intent i = new Intent(getApplicationContext(), CustomerListActivity.class);
                startActivity(i);

                finish();
            }
        });

        tvaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), AddCustomer_1.class);
                startActivity(i);
            }
        });

        listareaincustomers.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                mPage = mPage+1;

                URL=siteurl+"/GetCustomersByAreaForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString()
                        +"&startindex="+String.valueOf(mPage)+"&noofrecords=20";

                new JSONAsynk().execute(new String[]{URL});

                /*URL=siteurl+"/GetCustomersByAreaForCollectionApp";

                HashMap<String,String> map=new HashMap<String, String>();
                map.put("contractorId",cid);
                map.put("userId",uid);
                map.put("entityId",pref.getString("Entityids","").toString());
                map.put("startindex",String.valueOf(mPage));
                map.put("noofrecords","20");


                CallVolleyArea(URL,map);*/

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

        });

    }




    @Override
    public void onBackPressed()
    {

        finish();
    }

    public JSONObject makeHttpRequest(String url){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost=new HttpGet(url);
        httppost.setHeader(HTTP.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=UTF-8");
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
                    Toast.makeText(AreaListInCustomers.this, "No data", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AreaListInCustomers.this, "**"+e, Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                Toast.makeText(AreaListInCustomers.this, "**"+e, Toast.LENGTH_SHORT).show();
            }
        }catch (UnsupportedEncodingException e){
            Toast.makeText(AreaListInCustomers.this, "**"+e, Toast.LENGTH_SHORT).show();
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

            spload=new SpotsDialog(AreaListInCustomers.this,R.style.Custom);
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
            spload.dismiss();

            try
            {

                if(json.getString("status").toString().equals("True"))
                {
                    rlmain.setVisibility(View.VISIBLE);

                    final JSONArray entityarray = jsonobj.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        String aid = e.getString("AreaId");
                        String aname = TextUtils.htmlEncode(e.getString("AreaName"));
                        String totalcust = e.getString("TotalCustomer");

                        HashMap<String,String> map=new HashMap<>();

                        map.put("AreaId",aid);
                        map.put("AreaName",aname);
                        map.put("TotalCustomer",totalcust);

                        arealist.add(map);

                    }

                    da.notifyDataSetChanged();


                }
                else
                {

                    Toast.makeText(AreaListInCustomers.this,json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }




    public void CallVolleyArea(String a,HashMap<String,String> map)
    {
        try {

            final SpotsDialog  spload=new SpotsDialog(AreaListInCustomers.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                try
                                {

                                    spload.dismiss();

                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        rlmain.setVisibility(View.VISIBLE);

                                        final JSONArray entityarray = response.getJSONArray("AreaInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            String aid = e.getString("AreaId");
                                            String aname = TextUtils.htmlEncode(e.getString("AreaName"));
                                            String totalcust = e.getString("TotalCustomer");

                                            HashMap<String,String> map=new HashMap<>();

                                            map.put("AreaId",aid);
                                            map.put("AreaName",aname);
                                            map.put("TotalCustomer",totalcust);

                                            arealist.add(map);

                                        }

                                        da.notifyDataSetChanged();


                                    }
                                    else
                                    {

                                        Toast.makeText(AreaListInCustomers.this,response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                //Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

}
