package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class Contact_details extends AppCompatActivity {

    String str = "\u20B9";

    private static final String PREF_NAME = "LoginPref";
    SharedPreferences pref;

    String title,isOsEditable,URL,cid,siteurl,custid,uid,mqno;
    ArrayList<HashMap<String,String>> generaldetails=new ArrayList<>();

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;
    JSONObject jsonobj;

    TextView tvacno,tventity,tvname,tvphone,tvemail,tvarea,tvaddress,tvbdate,tvcaf,tvdiscount,tvnextbill,tvoa,tvacstatus,tvpayterm,tvconstartdate,tvconenddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        mqno=j.getExtras().getString("MQno");
        generaldetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("generaldetails");

        tvacno=(TextView)findViewById(R.id.textView34);
        tventity=(TextView)findViewById(R.id.textView36);
        tvname = (TextView) findViewById(R.id.textView38);
        tvphone=(TextView)findViewById(R.id.textView40);
        tvemail=(TextView)findViewById(R.id.textView42);
        tvarea=(TextView)findViewById(R.id.textView44);
        tvaddress=(TextView)findViewById(R.id.textView46);
        tvbdate=(TextView)findViewById(R.id.textView48);
        tvcaf=(TextView)findViewById(R.id.textView51);
        tvdiscount=(TextView)findViewById(R.id.textView53);
        tvnextbill=(TextView)findViewById(R.id.textView57);
        tvoa=(TextView)findViewById(R.id.textView55);
        tvacstatus=(TextView)findViewById(R.id.textView64);
        tvpayterm=(TextView)findViewById(R.id.textView66);
        tvconstartdate=(TextView)findViewById(R.id.textView60);
        tvconenddate=(TextView)findViewById(R.id.textView62);

        final Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                //startActivity(i);

                onBackPressed();

            }
        });


        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(Contact_details.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);

        }

        isOsEditable=pref.getString("isOutstandingEditable","");
        cid=pref.getString("Contracotrid", "").toString();
        siteurl=pref.getString("SiteURL", "").toString();
        uid=pref.getString("Userid", "").toString();
        custid=generaldetails.get(0).get("CustomerId");

        if(isOsEditable.equals("true"))
        {
            tvoa.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvoa.setTextColor(Color.BLUE);
        }


        tvacno.setText(generaldetails.get(0).get("AccountNo"));
        tventity.setText(generaldetails.get(0).get("AccountEntity"));
        tvname.setText(generaldetails.get(0).get("Name"));
        tvphone.setText(generaldetails.get(0).get("Phone"));
        tvemail.setText(generaldetails.get(0).get("Email"));
        tvarea.setText(generaldetails.get(0).get("Area"));
        tvaddress.setText(generaldetails.get(0).get("Address"));
        tvbdate.setText(generaldetails.get(0).get("BirthDate"));
        tvcaf.setText(generaldetails.get(0).get("CafNo"));
        tvdiscount.setText(generaldetails.get(0).get("Discount"));
        tvnextbill.setText(generaldetails.get(0).get("NextBillMonth"));
        tvoa.setText(str+generaldetails.get(0).get("TotalOutStandingAmount"));
        tvacstatus.setText(generaldetails.get(0).get("AccountStatus"));
        tvpayterm.setText(generaldetails.get(0).get("Payterm"));
        tvconstartdate.setText(generaldetails.get(0).get("ConnStartDate"));
        tvconenddate.setText(generaldetails.get(0).get("ConnEndDate"));


        tvoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOsEditable.equals("true"))
                {

                    LayoutInflater li = getLayoutInflater();
                    final View vs = li.inflate(R.layout.layout_outstanding_update, null);

                    final EditText edtamount=(EditText)vs.findViewById(R.id.editText8);
                    
                    MDDialog.Builder mdalert = new MDDialog.Builder(Contact_details.this);
                    mdalert.setContentView(vs);
                    mdalert.setTitle("Update Outstanding");
                    mdalert.setPositiveButton("SUBMIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            if (edtamount.getText().toString().equals("")) {
                                Toast.makeText(Contact_details.this, "Please Enter Amount..!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                URL = siteurl + "/UpdateOutstandingForCollectionApp?customerid=" + custid + "&createdby=" + uid + "&Amount=" + edtamount.getText().toString();
                                new JSONAsynk().execute(new String[]{URL});
                            }

                        }
                    });
                    mdalert.setNegativeButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                        }
                    });
                    mdalert.setWidthMaxDp(600);
                    mdalert.setShowTitle(true);
                    mdalert.setShowButtons(true);
                    mdalert.setBackgroundCornerRadius(5);

                    MDDialog dialog = mdalert.create();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    dialog.show();

                }

            }
        });


    }

    @Override
    public void onBackPressed()
    {

        finish();
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

            spload=new SpotsDialog(Contact_details.this,R.style.Custom);
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
                // Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            String str = "\u20B9";
            try
            {

                if(json.getString("status").toString().equals("True"))
                {
                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();

                    Intent i=new Intent(getApplicationContext(),CustomerMasterDetailsActivity.class);
                    i.putExtra("cname",title);
                    i.putExtra("A/cNo",generaldetails.get(0).get("AccountNo"));
                    i.putExtra("MQno",mqno);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();
                }


            }
            catch (JSONException e)
            {
                Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }



    public JSONObject makeHttpRequest(String url){

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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


}
