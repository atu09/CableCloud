package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ComplainListviaCustomerActivity extends AppCompatActivity {

    ListView listcomplainbycustomer;
    String title,mode,custid;

    private static final String PREF_NAME = "LoginPref";

    ArrayList<HashMap<String,String>> complaindetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> complaindetailss=new ArrayList<>();

    ArrayList<String> useridlist=new ArrayList<>();
    ArrayList<String> usernamelist=new ArrayList<>();


    String siteurl,uid,cid,aid,eid,URL,status;
    RequestQueue requestQueue;

    //SimpleAdapter da;

    ComplainListAdapter da;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_listvia_customer);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);


        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid", "").toString();
        cid=pref.getString("Contracotrid", "").toString();
        eid=pref.getString("Entityids", "").toString();

        Intent j = getIntent();
        title = j.getExtras().getString("title");
        complaindetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("complaindetails");
        mode=j.getExtras().getString("Mode");
        custid=j.getExtras().getString("CustomerId");
        status=j.getExtras().getString("status");

        listcomplainbycustomer=(ListView)findViewById(R.id.listcomplainbycustomer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(getApplicationContext(), DashBoard.class);
                //   startActivity(i);

                onBackPressed();

            }
        });

        if(mode.equals("Complaint"))
        {
            URL=siteurl+"/GetCustomerComplainDetailsForAdminCollectionApp";

            CallVolley(URL);

            if(pref.getString("RoleId", "").toString().equals("2") && status.equals("NEW")) {

                registerForContextMenu(listcomplainbycustomer);
            }
        }
        else {

            SimpleAdapter da = new SimpleAdapter(this, complaindetails, R.layout.layout_complainbycustomer, new String[]{"ComplainId", "Subject"}, new int[]{R.id.textView61, R.id.textView44});
            listcomplainbycustomer.setAdapter(da);
        }

        listcomplainbycustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mode.equals("Complaint")) {

                    Intent i = new Intent(getApplicationContext(), ComplainDetails.class);
                    i.putExtra("title", title);
                    i.putExtra("customerId",custid);
                    i.putExtra("complainId",complaindetailss.get(position).get("ComplainId"));
                    startActivity(i);

                    finish();
                }

            }
        });




    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_complain_assign, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case R.id.action_assign:

                // Toast.makeText(ReminderListActivity.this,reminderdetails.get(info.position).get("ReminderId").toString(), Toast.LENGTH_SHORT).show();
                // reminderdetails.remove(info.position);

                //URL=siteurl+"/GetUpdateStatusReminderForCollectionApp";
                //CallVolleys(URL,reminderdetails.get(info.position).get("ReminderId").toString());

                URL=siteurl+"/GetUserlistfornewcollectionApp";

                CallVolleyUserlist(URL, complaindetailss.get(info.position).get("ComplainId").toString());

                //String[] templist=new String[]{"a","b","c","d"};

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void CallVolleyUserlist(String a, final String cmpid)
    {

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorid",cid);

        final SpotsDialog spload;
        spload=new SpotsDialog(ComplainListviaCustomerActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();


                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final JSONArray entityarray = response.getJSONArray("lstUserInfoCollectionApp");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            useridlist.add(e.getString("UserId").toString());
                                            usernamelist.add(e.getString("Name").toString());

                                        }

                                        if(useridlist.size()>0)
                                        {
                                            final ListView lv=new ListView(ComplainListviaCustomerActivity.this);
                                            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                            lv.setDividerHeight(0);


                                            final ArrayAdapter<String> da=new ArrayAdapter<String>(ComplainListviaCustomerActivity.this,android.R.layout.simple_list_item_single_choice,usernamelist);
                                            lv.setAdapter(da);

                                            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ComplainListviaCustomerActivity.this);
                                            builderDialog.setView(lv);
                                            builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (lv.getCheckedItemPosition()==-1)
                                                    {
                                                        Toast.makeText(ComplainListviaCustomerActivity.this, "Please select atleast one User..!!", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {

                                                        URL=siteurl+"/AssignComplaintForNewCollectionApp";

                                                        HashMap<String,String> map=new HashMap<String, String>();
                                                        map.put("compId",cmpid);
                                                        map.put("userId",useridlist.get(lv.getCheckedItemPosition()));


                                                        CallVolleyAssignComplain(URL,map);

                                                    }
                                                }
                                            });

                                            final AlertDialog alert=builderDialog.create();
                                            alert.setTitle("Select User to Assign Complaint");
                                            alert.setCancelable(true);
                                            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                            alert.show();
                                        }
                                    }

                                    else
                                    {
                                        Toast.makeText(ComplainListviaCustomerActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
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


    public void CallVolleyAssignComplain(String a,HashMap<String,String> map)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(ComplainListviaCustomerActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        Toast.makeText(ComplainListviaCustomerActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
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


    public void CallVolley(String a)
    {

        HashMap<String,String> map=new HashMap<>();
        map.put("customerId",custid);
        map.put("status",status);

        final SpotsDialog spload;
        spload=new SpotsDialog(ComplainListviaCustomerActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();


                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        String cmpid=null;

                                        final JSONArray entityarray = response.getJSONArray("ComplainInfoList");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            HashMap<String,String> map=new HashMap<>();

                                            cmpid=e.getString("ComplainId").toString();

                                            map.put("ComplainId",e.getString("ComplainId").toString());
                                            map.put("Subject",e.getString("Subject").toString());
                                            map.put("message", e.getString("message").toString());
                                            map.put("ComplainStatus", e.getString("ComplainStatus").toString());
                                            map.put("webcommentcount",e.getString("webcommentcount").toString());

                                            complaindetailss.add(map);

                                        }

                                        if(complaindetailss.size()==1) {

                                            Intent i=new Intent(getApplicationContext(),ComplainDetails.class);
                                            i.putExtra("title",title);
                                            i.putExtra("customerId",custid);
                                            i.putExtra("complainId",cmpid);
                                            startActivity(i);

                                            finish();
                                        }
                                        if(complaindetailss.size()>1)
                                        {
                                            da = new ComplainListAdapter(ComplainListviaCustomerActivity.this, complaindetailss);
                                            listcomplainbycustomer.setAdapter(da);
                                        }

                                    }

                                    else
                                    {
                                        Toast.makeText(ComplainListviaCustomerActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
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
