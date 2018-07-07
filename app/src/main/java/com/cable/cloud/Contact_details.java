package com.cable.cloud;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cable.cloud.R;
import com.cable.cloud.activities.CustomerMasterDetailsActivity;
import com.cable.cloud.helpers.Utils;

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
import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;

public class Contact_details extends AppCompatActivity {

    String inrSymbol = "\u20B9";

    private static final String PREF_NAME = "LoginPref";
    SharedPreferences pref;

    String title, isOsEditable, URL, cid, siteurl, custid, uid, mqno;
    ArrayList<HashMap<String, String>> generaldetails = new ArrayList<>();

    TextView tvacno, tventity, tvphone, tvemail, tvarea, tvaddress, tvbdate, tvcaf, tvdiscount, tvnextbill, tvoa, tvacstatus, tvpayterm, tvconstartdate, tvconenddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        Intent j = getIntent();
        title = j.getExtras().getString("title");
        mqno = j.getExtras().getString("MQno");
        generaldetails = (ArrayList<HashMap<String, String>>) j.getSerializableExtra("generaldetails");

        tvacno = (TextView) findViewById(R.id.textView34);
        tventity = (TextView) findViewById(R.id.textView36);
        tvphone = (TextView) findViewById(R.id.textView40);
        tvemail = (TextView) findViewById(R.id.textView42);
        tvarea = (TextView) findViewById(R.id.textView44);
        tvaddress = (TextView) findViewById(R.id.textView46);
        tvbdate = (TextView) findViewById(R.id.textView48);
        tvcaf = (TextView) findViewById(R.id.textView51);
        tvdiscount = (TextView) findViewById(R.id.textView53);
        tvnextbill = (TextView) findViewById(R.id.textView57);
        tvoa = (TextView) findViewById(R.id.textView55);
        tvacstatus = (TextView) findViewById(R.id.textView64);
        tvpayterm = (TextView) findViewById(R.id.textView66);
        tvconstartdate = (TextView) findViewById(R.id.textView60);
        tvconenddate = (TextView) findViewById(R.id.textView62);

        LinearLayout layoutShow = (LinearLayout) findViewById(R.id.layoutShow);
        final LinearLayout layoutDetails = (LinearLayout) findViewById(R.id.layoutDetails);
        final TextView tvShow = (TextView) findViewById(R.id.tvShow);
        final ImageView ivShow = (ImageView) findViewById(R.id.ivShow);

        layoutShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutDetails.getVisibility() == View.VISIBLE){
                    layoutDetails.setVisibility(View.GONE);
                    tvShow.setText("Show Customer Details");
                    ivShow.setRotation(0);
                } else {
                    layoutDetails.setVisibility(View.VISIBLE);
                    tvShow.setText("Hide Customer Details");
                    ivShow.setRotation(180);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight = TransitionInflater.from(Contact_details.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);

        }

        isOsEditable = pref.getString("isOutstandingEditable", "");
        cid = pref.getString("Contracotrid", "");
        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        custid = generaldetails.get(0).get("CustomerId");

        if (isOsEditable.equals("true")) {
            tvoa.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvoa.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        tvacno.setText(generaldetails.get(0).get("AccountNo"));
        tventity.setText(generaldetails.get(0).get("AccountEntity"));
        //tvname.setText(generaldetails.get(0).get("Name"));
        tvphone.setText(generaldetails.get(0).get("Phone"));
        tvemail.setText(generaldetails.get(0).get("Email"));
        tvarea.setText(generaldetails.get(0).get("Area"));
        tvaddress.setText(generaldetails.get(0).get("Address"));
        tvbdate.setText(generaldetails.get(0).get("BirthDate"));
        tvcaf.setText(generaldetails.get(0).get("CafNo"));
        tvdiscount.setText(generaldetails.get(0).get("Discount"));
        tvnextbill.setText(generaldetails.get(0).get("NextBillMonth"));
        tvoa.setText(inrSymbol + generaldetails.get(0).get("TotalOutStandingAmount"));
        tvacstatus.setText(generaldetails.get(0).get("AccountStatus"));
        tvpayterm.setText(generaldetails.get(0).get("Payterm"));
        tvconstartdate.setText(generaldetails.get(0).get("ConnStartDate"));
        tvconenddate.setText(generaldetails.get(0).get("ConnEndDate"));


        tvoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOsEditable.equals("true")) {

                    LayoutInflater li = getLayoutInflater();
                    final View vs = li.inflate(R.layout.layout_outstanding_update, null);

                    final EditText edtamount = (EditText) vs.findViewById(R.id.editText8);
                    MDDialog.Builder mdalert = new MDDialog.Builder(Contact_details.this);
                    mdalert.setContentView(vs);
                    mdalert.setTitle("Update Outstanding");
                    mdalert.setPositiveButton("SUBMIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (edtamount.getText().toString().equals("")) {
                                Toast.makeText(Contact_details.this, "Please Enter Amount..!", Toast.LENGTH_SHORT).show();
                            } else {
                                URL = siteurl + "/UpdateOutstandingForCollectionApp?customerid=" + custid + "&createdby=" + uid + "&Amount=" + edtamount.getText().toString();
                                new JSONAsync().execute(new String[]{URL});
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
    public void onBackPressed() {
        finish();
    }

    private class JSONAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(Contact_details.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {

                if (json.getString("status").equalsIgnoreCase("True")) {
                    Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                    i.putExtra("cname", title);
                    i.putExtra("A/cNo", generaldetails.get(0).get("AccountNo"));
                    i.putExtra("MQno", mqno);
                    startActivity(i);
                    finish();
                }
                Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }

        }

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
            HttpEntity httpEntity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));

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
}
