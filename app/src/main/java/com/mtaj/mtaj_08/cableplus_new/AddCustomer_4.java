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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class AddCustomer_4 extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";
    Spinner sppayterm,spmonth,spyear;

    ArrayList<String> paytermlist=new ArrayList<>();
    ArrayList<String> monthlist=new ArrayList<>();
    ArrayList<String> yearlist=new ArrayList<>();

    TextView tvcancel,tvsubmit;

    EditText edtoutstanding,edtdiscount;

    Switch swsmsalert;

    ArrayList<String> details=new ArrayList<>();

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    String siteurl,uid,cid,aid,eid,URL,smsalert="false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_4);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid", "").toString();
        cid=pref.getString("Contracotrid", "").toString();

        Intent j=getIntent();
        details=j.getExtras().getStringArrayList("details");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bill Details");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();

            }
        });


        swsmsalert=(Switch)findViewById(R.id.switch1);

        tvcancel=(TextView)findViewById(R.id.textView28);
        tvsubmit=(TextView)findViewById(R.id.textView30);

        edtoutstanding=(EditText)findViewById(R.id.editText2);
        edtdiscount=(EditText)findViewById(R.id.editText3);

        sppayterm=(Spinner)findViewById(R.id.spinner);
        spmonth=(Spinner)findViewById(R.id.spinner4);
        spyear=(Spinner)findViewById(R.id.spinner5);

        paytermlist.add("Monthly");
        paytermlist.add("Quarterly");
        paytermlist.add("Half Yearly");
        paytermlist.add("Yearly");

        monthlist.add("January");
        monthlist.add("February");
        monthlist.add("March");
        monthlist.add("April");
        monthlist.add("May");
        monthlist.add("June");
        monthlist.add("July");
        monthlist.add("August");
        monthlist.add("September");
        monthlist.add("October");
        monthlist.add("November");
        monthlist.add("December");

        Calendar cal= Calendar.getInstance();
        final SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        final String month_name = month_date.format(cal.getTime());
        final int month_no=cal.get(Calendar.MONTH);
        final int cur_year=cal.get(Calendar.YEAR);


        for(int i=cur_year;i<=2050;i++)
        {
            yearlist.add(String.valueOf(i));
        }

        ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_4.this,android.R.layout.simple_spinner_dropdown_item,paytermlist);
        sppayterm.setAdapter(da1);

        ArrayAdapter<String> da2=new ArrayAdapter<String>(AddCustomer_4.this,android.R.layout.simple_spinner_dropdown_item,monthlist);
        spmonth.setAdapter(da2);

        if((month_no+1)>=monthlist.size())
        {
            spmonth.setSelection(0);
        }
        else {
            spmonth.setSelection(month_no+1);
        }

        ArrayAdapter<String> da3=new ArrayAdapter<String>(AddCustomer_4.this,android.R.layout.simple_spinner_dropdown_item,yearlist);
        spyear.setAdapter(da3);



        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();


            }
        });

        swsmsalert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    smsalert="true";
                }
                else
                {
                    smsalert="false";
                }
            }
        });


        tvsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(ValidateEdittext("Enter OutStanding",edtoutstanding)&&ValidateEdittext("Enter Discount Value",edtdiscount))
                    {
                        if(((spmonth.getSelectedItemPosition()+1)<=month_no) && (spyear.getSelectedItem().toString().equals(String.valueOf(cur_year))))
                        {
                            Toast.makeText(AddCustomer_4.this, "Select Valid Month Or Year", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String nextbilldate=(spmonth.getSelectedItemPosition()+1)+"/"+"01"+"/"+spyear.getSelectedItem().toString();
                            String payterm=String.valueOf(sppayterm.getSelectedItemPosition()+1);

                            URL=siteurl+"/AddCustomerForCollectionApp?contractorId="+ URLEncoder.encode(cid, "UTF-8")+"&loginuserId="+URLEncoder.encode(uid, "UTF-8")+"&areaId="+URLEncoder.encode(details.get(0), "UTF-8")+"&entityId="+URLEncoder.encode(details.get(1), "UTF-8")+"&accountNo="+URLEncoder.encode(details.get(2), "UTF-8")+"&customerName="+URLEncoder.encode(details.get(3), "UTF-8")+"&phone="+URLEncoder.encode(details.get(4), "UTF-8")+
                                    "&email="+URLEncoder.encode(details.get(5), "UTF-8")+"&address="+URLEncoder.encode(details.get(6), "UTF-8")+"&city="+URLEncoder.encode(details.get(7), "UTF-8")+"&district="+URLEncoder.encode(details.get(8), "UTF-8")+"&zipcode="+URLEncoder.encode(details.get(9), "UTF-8")+"&cafNo="+URLEncoder.encode(details.get(10), "UTF-8")+"&connStartDate="+URLEncoder.encode(details.get(11), "UTF-8")+"&connEndDate="+URLEncoder.encode(details.get(12), "UTF-8")+"&birthDate="+URLEncoder.encode(details.get(13), "UTF-8")+
                            "&payterm="+URLEncoder.encode(payterm, "UTF-8")+"&outstanding="+URLEncoder.encode(edtoutstanding.getText().toString(), "UTF-8")+"&discount="+URLEncoder.encode(edtdiscount.getText().toString(), "UTF-8")+"&nextbilldate="+URLEncoder.encode(nextbilldate, "UTF-8")+"&SmsAlert="+URLEncoder.encode(smsalert, "UTF-8");

                            new JSONAsynk().execute(new String[]{URL});

                           // Intent i = new Intent(getApplicationContext(), AddCustomer_2.class);
                          //  startActivity(i);
                        }


                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onBackPressed()
    {

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



    private class JSONAsynk extends AsyncTask<String,String,JSONObject>
    {

        private ProgressDialog pDialog;
        //public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1,jsn,jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(AddCustomer_4.this,R.style.Custom);
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
                //  Toast.makeText(CustomerListActivity.this, json.toString(), Toast.LENGTH_SHORT).show();

                if(json.getString("status").toString().equals("True")) {

                    String custid=json.getString("customerId");

                    Toast.makeText(AddCustomer_4.this, json.getString("message").toString(), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AddCustomer_4.this, AddCustomer_2.class);
                    i.putExtra("CustomerId",custid);
                    startActivity(i);

                    finish();
                }

            }
            catch (JSONException e)
            {
                Toast.makeText(AddCustomer_4.this, "Error:++"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(AddCustomer_4.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }
}
