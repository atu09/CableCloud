package com.mtaj.mtaj_08.cableplus_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class AddCustomer_1 extends AppCompatActivity {

    ArrayList<String> areaids=new ArrayList<>();
    ArrayList<String> areanames=new ArrayList<>();
    ArrayList<String> entityids=new ArrayList<>();
    ArrayList<String> Entityname=new ArrayList<>();

    Spinner sparea,spentity;


    TextView tvcancel,tvnext;

    EditText edtstartdate,edtenddate,edtbdate,edtname,edtacno,edtphno,edtaddress,edtemail,edtcity,edtdistrict,edtzip,edtcaf;

    private Calendar calendar;
    private int year, cmonth, day;

    private static final String PREF_NAME = "LoginPref";
    static String siteurl,uid,URL,URL1,cid,eid;


    static InputStream is = null;
    static JSONObject jobj = null;
    static JSONObject jobj1 = null;
    static String json = "";
    static JSONArray jarr = null;


    ArrayList<String> details=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_1);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        eid=pref.getString("Entityids", "").toString();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("General Details");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), DashBoard.class);
                //startActivity(i);

                onBackPressed();

            }
        });

        edtstartdate=(EditText)findViewById(R.id.editText10);
        edtenddate=(EditText)findViewById(R.id.editText11);
        edtbdate=(EditText)findViewById(R.id.editText12);

        edtacno=(EditText)findViewById(R.id.editText2);
        edtname=(EditText)findViewById(R.id.editText3);
        edtphno=(EditText)findViewById(R.id.editText13);
        edtaddress=(EditText)findViewById(R.id.editText15);
        edtemail=(EditText)findViewById(R.id.editText14);
        edtcity=(EditText)findViewById(R.id.editText16);
        edtdistrict=(EditText)findViewById(R.id.editText17);
        edtzip=(EditText)findViewById(R.id.editText18);
        edtcaf=(EditText)findViewById(R.id.editText19);


        sparea=(Spinner)findViewById(R.id.spinner);
        spentity=(Spinner)findViewById(R.id.spinner2);

        tvcancel=(TextView)findViewById(R.id.textView28);
        tvnext=(TextView)findViewById(R.id.textView30);

        areanames.add("---  Select Area ---");
        Entityname.add("--- Select Entity ---");


        ArrayAdapter<String> da=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,areanames);
        da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sparea.setAdapter(da);

        ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,Entityname);
        spentity.setAdapter(da1);

        /*sparea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (areanames.size() < 2) {

                    URL1 = siteurl + "/GetAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + pref.getString("Entityids", "").toString();

                    final JSONObject jsonobj1 = makeHttpRequest(URL1);
                    try {

                        areanames.clear();
                        areaids.clear();

                        if (jsonobj1.getString("status").toString().equals("True")) {


                            final JSONArray entityarray = jsonobj1.getJSONArray("AreaInfoList");

                            for (int i = 0; i < entityarray.length(); i++) {
                                JSONObject e = (JSONObject) entityarray.get(i);

                                String aid = e.getString("AreaId");
                                String aname = e.getString("AreaName");

                                areaids.add(aid);
                                areanames.add(aname);

                            }

                            ArrayAdapter<String> da = new ArrayAdapter<String>(AddCustomer_1.this, android.R.layout.simple_spinner_dropdown_item, areanames);
                            da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sparea.setAdapter(da);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ArrayAdapter<String> da = new ArrayAdapter<String>(AddCustomer_1.this, android.R.layout.simple_spinner_dropdown_item, areanames);
                    da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sparea.setAdapter(da);
                }


                return false;
            }
        });*/

        spentity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Entityname.size() <= 1) {

                    URL=siteurl+"/GetEntityByUser?userId="+uid;

                    final JSONObject jsonobj = makeHttpRequest(URL);
                    try {

                        if (jsonobj.getString("status").toString().equals("True")) {


                            final JSONArray entityarray = jsonobj.getJSONArray("EntityInfoList");

                            for (int i = 0; i < entityarray.length(); i++) {
                                JSONObject e = (JSONObject) entityarray.get(i);

                                String eid = e.getString("EntityId");
                                String ename = e.getString("EntityName");

                                entityids.add(eid);
                                Entityname.add(ename);

                            }

                            ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,Entityname);
                            spentity.setAdapter(da1);

                        }
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                } else {
                    ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,Entityname);
                    spentity.setAdapter(da1);
                }


                return false;
            }
        });


        spentity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spentity.getSelectedItem().toString().equals("--- Select Entity ---"))
                {

                }
                else
                {

                    final String enid=entityids.get(position-1);

                    //URL1 = siteurl + "/GetAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + enid;

                    //if (areanames.size() < 0) {

                        //URL1 = siteurl + "/GetAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + enid+"&startindex=0&noofrecords=10000";

                    URL1 = siteurl +"/GetCustomersByAreaForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+enid+"&startindex=0"+"&noofrecords=10000";

                        final JSONObject jsonobj1 = makeHttpRequest(URL1);
                        try {

                            areanames.clear();
                            areaids.clear();

                            if (jsonobj1.getString("status").toString().equals("True")) {

                                final JSONArray entityarray = jsonobj1.getJSONArray("AreaInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    String aid = e.getString("AreaId");
                                    String aname = e.getString("AreaName");

                                    areaids.add(aid);
                                    areanames.add(aname);
                                }

                                ArrayAdapter<String> da = new ArrayAdapter<String>(AddCustomer_1.this, android.R.layout.simple_spinner_dropdown_item, areanames);
                                da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sparea.setAdapter(da);

                                String Acno=jsonobj1.getString("CustAccNo");

                                edtacno.setText(Acno);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                   // }

                    /*else {
                        ArrayAdapter<String> da = new ArrayAdapter<String>(AddCustomer_1.this, android.R.layout.simple_spinner_dropdown_item, areanames);
                        da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sparea.setAdapter(da);
                    }*/



                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //URL=siteurl+"/GetEntityByUser?userId="+uid;
       /* final JSONObject jsonobj = makeHttpRequest(URL);

        final JSONObject jsonobj1 = makeHttpRequest(URL1);

        try {

            if (jsonobj.getString("status").toString().equals("True")) {


                final JSONArray entityarray = jsonobj.getJSONArray("EntityInfoList");

                for (int i = 0; i < entityarray.length(); i++) {
                    JSONObject e = (JSONObject) entityarray.get(i);

                    String eid = e.getString("EntityId");
                    String ename = e.getString("EntityName");

                    entityids.add(eid);
                    Entityname.add(ename);

                }
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        try {

            if (jsonobj1.getString("status").toString().equals("True")) {


                final JSONArray entityarray = jsonobj1.getJSONArray("AreaInfoList");

                for (int i = 0; i < entityarray.length(); i++) {
                    JSONObject e = (JSONObject) entityarray.get(i);

                    String aid = e.getString("AreaId");
                    String aname = e.getString("AreaName");

                    areaids.add(aid);
                    areanames.add(aname);

                }
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }



        ArrayAdapter<String> da=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,areanames);
        da.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sparea.setAdapter(da);

        ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_1.this,android.R.layout.simple_spinner_dropdown_item,Entityname);
        spentity.setAdapter(da1);*/

        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        if(ValidateEdittext("Enter AccountNo.",edtacno)&&ValidateEdittext("Enter CustomerName",edtname)&&ValidateEdittext("Enter PhoneNumber",edtphno)&&ValidateEdittext("Enter Address",edtaddress))
        {

            details.add(areaids.get(sparea.getSelectedItemPosition()));

            details.add(entityids.get(spentity.getSelectedItemPosition() - 1));

            details.add(edtacno.getText().toString());
            details.add(edtname.getText().toString());
            details.add(edtphno.getText().toString());
            details.add(edtemail.getText().toString());
            details.add(edtaddress.getText().toString());
            details.add(edtcity.getText().toString());
            details.add(edtdistrict.getText().toString());
            details.add(edtzip.getText().toString());
            details.add(edtcaf.getText().toString());
            details.add(edtstartdate.getText().toString());
            details.add(edtenddate.getText().toString());
            details.add(edtbdate.getText().toString());

            //Toast.makeText(AddCustomer_1.this, details.toString(), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getApplicationContext(), AddCustomer_4.class);
            i.putExtra("details",details);
            startActivity(i);

            finish();
        }



            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              finish();


            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        /*edtstartdate.setText((cmonth + 1) + "/" + day + "/" + year);
        edtenddate.setText((cmonth + 1) + "/" + day + "/" + year);
        edtbdate.setText((cmonth + 1) + "/" + day + "/" + year);*/

        edtstartdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDialog(999);

                return false;
            }
        });

        edtenddate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDialog(888);

                return false;
            }
        });

        edtbdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDialog(777);

                return false;
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public boolean ValidateEdittext(String error,EditText ed)
    {
        if(ed.getText().toString()==null || ed.getText().toString().length()==0)
        {
            ed.setError(error);
            return  false;
        }
        else
        {
            return true;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, cmonth, day);
        }
        if(id==888)
        {
            return new DatePickerDialog(this, myDateListeners, year, cmonth, day);
        }
        if(id==777)
        {
            return new DatePickerDialog(this, myDateListenerss, year, cmonth, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtstartdate.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtenddate.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListenerss = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtbdate.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };

    public JSONObject makeHttpRequest(String url){
        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 50000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
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
                    Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
            }
        }catch (UnsupportedEncodingException e){
            Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }

   /* public void CallVolleys(String a)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(AddCustomer_1.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(false);
        spload.show();

        URL1 = siteurl + "/GetAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + pref.getString("Entityids", "").toString();

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorId",cid);
        map.put("userId", uid);
        map.put("entityId",eid);


        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {
                            areadetails.clear();

                            //  Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            if (response.getString("status").toString().equals("True")) {
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
                                    map.put("Outstanding", format.format(Double.parseDouble(Aoa)));
                                    map.put("Collection", format.format(Double.parseDouble(Acol)));

                                    areadetails.add(map);

                                }

                                // da = new SimpleAdapter(getContext(), areadetails, R.layout.arealist, new String[]{"AreaName", "Collection", "Outstanding"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                                // lvarealist.setAdapter(da);

                                //lvarealist.setAdapter(new PaymentAreaListAdapter(getContext(),areadetails));


                                paymentAreaListAdapter.notifyDataSetChanged();

                                String tc = response.getString("TotalCollection").toString();
                                String toa = response.getString("TotalOutstanding").toString();

                                tvtotalcol.setText(format.format(Double.parseDouble(tc)));
                                tvtotaloa.setText(format.format(Double.parseDouble(toa)));

                                swrefresh.setRefreshing(false);

                                // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                           *//* } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            }*//*
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }*/
}
