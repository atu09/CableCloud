package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class CustomerCommentList extends AppCompatActivity {

    RequestQueue requestQueue;
    private static final String PREF_NAME = "LoginPref";

    String siteurl,uid,cid,aid,eid,URL;

    SharedPreferences pref;

    ListView lstcommentlist;

    ArrayList<HashMap<String,String>> Commentdetails=new ArrayList<>();

    SwipeRefreshLayout swrefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_comment_list);


        requestQueue = Volley.newRequestQueue(CustomerCommentList.this);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        lstcommentlist=(ListView)findViewById(R.id.lstCommentList);

        swrefresh = (SwipeRefreshLayout)findViewById(R.id.refresh);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        URL=siteurl+"/GetAllPendingCustomerCommentListForCollectionApp";

        CallVolley(URL);

        lstcommentlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {



                Intent i = new Intent(getApplicationContext(), CustomerDetails.class);
                i.putExtra("cname", Commentdetails.get(position).get("customerName"));
                i.putExtra("A/cNo", Commentdetails.get(position).get("accountNo"));
                i.putExtra("CustomerId", Commentdetails.get(position).get("customerId"));
                i.putExtra("from", "Payment");
                startActivity(i);

            }
        });

        swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swrefresh.setRefreshing(true);

                Commentdetails.clear();

                URL=siteurl+"/GetAllPendingCustomerCommentListForCollectionApp";

                CallVolley(URL);



            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), DashBoard.class);
        startActivity(i);

        finish();
    }

    public void CallVolley(String a)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerCommentList.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            HashMap<String,String> map=new HashMap<>();
            map.put("contractorId",cid);
            map.put("loginUserId",uid);
            map.put("entityIds",eid);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    // Toast.makeText(AlertListActivity.this,response.getString("message"), Toast.LENGTH_SHORT).show();

                                    if(response.getString("status").toString().equals("True"))
                                    {

                                        final JSONArray entityarray = response.getJSONArray("CommentInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            String cid=e.getString("customerId");
                                            String cname=e.getString("customerName");
                                            String acno=e.getString("accountNo");
                                            String commenttext=e.getString("Comment");
                                            String commentdate=e.getString("commentDate");


                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("Comment",commenttext);
                                            map.put("customerId",cid);
                                            map.put("customerName",cname);
                                            map.put("accountNo",acno);
                                            map.put("commentDate",ParsedDate(commentdate));
                                            map.put("Name&Account",cname+"("+acno+")");


                                            Commentdetails.add(map);
                                        }

                                        lstcommentlist.setAdapter(new CustomerCommentListAdapter(CustomerCommentList.this,Commentdetails));

                                        swrefresh.setRefreshing(false);
                                    }
                                    else {

                                        spload.dismiss();

                                        lstcommentlist.setAdapter(new CustomerCommentListAdapter(CustomerCommentList.this,Commentdetails));

                                        Toast.makeText(CustomerCommentList.this,response.getString("message"), Toast.LENGTH_LONG).show();

                                        swrefresh.setRefreshing(false);

                                    }

                                }
                                catch (JSONException e)
                                {
                                    spload.dismiss();

                                    Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_SHORT).show();

                                    swrefresh.setRefreshing(false);
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                spload.dismiss();

                                Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_SHORT).show();

                                swrefresh.setRefreshing(false);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            spload.dismiss();

                            Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_SHORT).show();

                            swrefresh.setRefreshing(false);

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

    public String ParsedDate(String inputdate)
    {
        try {

            DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            Date date = inputFormat.parse(inputdate);
            String outputDateStr = outputFormat.format(date);

            return outputDateStr;
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();

            return inputdate;
        }
    }
}
