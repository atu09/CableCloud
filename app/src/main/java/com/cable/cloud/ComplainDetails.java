package com.cable.cloud;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.cable.cloud.helpers.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ComplainDetails extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    TextView tvviewcomments;

    String title, custid, cmpid;

    ImageView imdropdown, imphone;

    String siteUrl, uid, cid, aid, eid, URL;

    ArrayList<HashMap<String, String>> Complaindetails = new ArrayList<>();

    TextView tvacno, tvmqno, tvcid, tvcsub, tvdesc, tvphone, tvemail, tvaddress, tvcount;

    TextView tvresolve, tvphoto;

    RequestQueue requestQueue;
    ArrayList<String> userIdList = new ArrayList<>();
    ArrayList<String> userNameList = new ArrayList<>();

    Toolbar toolbar;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_details);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        cid = pref.getString("Contracotrid", "");

        requestQueue = Volley.newRequestQueue(this);

        tvviewcomments = (TextView) findViewById(R.id.textView94);

        tvcount = (TextView) findViewById(R.id.textView102);

        tvviewcomments.setPaintFlags(tvviewcomments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvviewcomments.setText("View Comments");


        Intent j = getIntent();
        title = j.getExtras().getString("title");
        custid = j.getExtras().getString("customerId");
        cmpid = j.getExtras().getString("complainId");

        tvacno = (TextView) findViewById(R.id.textView34);
        tvmqno = (TextView) findViewById(R.id.textView36);
        tvcid = (TextView) findViewById(R.id.textView61);
        tvcsub = (TextView) findViewById(R.id.textView44);
        tvdesc = (TextView) findViewById(R.id.textView46);
        tvphone = (TextView) findViewById(R.id.textView62);
        tvemail = (TextView) findViewById(R.id.textView63);
        tvaddress = (TextView) findViewById(R.id.textView64);

        tvresolve = (TextView) findViewById(R.id.btnResolve);
        tvphoto = (TextView) findViewById(R.id.btnPhoto);

        imdropdown = (ImageView) findViewById(R.id.imageView4);
        imphone = (ImageView) findViewById(R.id.imageView2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        URL = siteUrl + "/GetCustomerComplainDetailsForCollectionApp?complainId=" + cmpid + "&customerId=" + custid + "&userId=" + uid;

        new JSONAsync().execute(new String[]{URL});


        tvviewcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Comments_List_Activity.class);
                i.putExtra("Complainid", cmpid);
                i.putExtra("title", title);
                i.putExtra("CustomerId", custid);
                i.putExtra("From", "Details");
                startActivity(i);

                finish();
            }
        });

        imphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

                        if (result == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(ComplainDetails.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                            startActivity(callIntent);
                        }

                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                        startActivity(callIntent);
                    }


                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        });

        tvphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Complaindetails.get(0).get("image").equals("")) {
                    Snackbar.make(v, "No Image to SHow...", Snackbar.LENGTH_LONG).show();
                } else {

                    byte[] decodedString = Base64.decode(Complaindetails.get(0).get("image"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    Intent i = new Intent(ComplainDetails.this, FullImageActivity.class);
                    i.putExtra("Image", decodedByte);
                    startActivity(i);
                }

            }
        });

        tvresolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ComplainSignatureActivity.class);
                i.putExtra("cname", title);
                i.putExtra("from", "complain");
                i.putExtra("complainId", cmpid);
                i.putExtra("CustomerId", custid);
                startActivity(i);

                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("from");
                editor.apply();

                finish();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == 1 && permissions.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_complain_assign1, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_assign) {

            userIdList.clear();
            userNameList.clear();

            URL = siteUrl + "/GetUserlistfornewcollectionApp";

            CallVolleyUserList(URL, cmpid);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void CallVolleyUserList(String a, final String cmpid) {

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorid", cid);

        final Dialog loader = Utils.getLoader(ComplainDetails.this);
        loader.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {
                                final JSONArray entityArray = response.getJSONArray("lstUserInfoCollectionApp");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    userIdList.add(e.getString("UserId"));
                                    userNameList.add(e.getString("Name"));

                                }

                                if (userIdList.size() > 0) {
                                    final ListView lv = new ListView(ComplainDetails.this);
                                    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                    lv.setDividerHeight(0);


                                    final ArrayAdapter<String> da = new ArrayAdapter<String>(ComplainDetails.this, android.R.layout.simple_list_item_single_choice, userNameList);
                                    lv.setAdapter(da);

                                    final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ComplainDetails.this);
                                    builderDialog.setView(lv);
                                    builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (lv.getCheckedItemPosition() == -1) {
                                                Toast.makeText(ComplainDetails.this, "Please select atleast one User..!!", Toast.LENGTH_LONG).show();
                                            } else {

                                                URL = siteUrl + "/AssignComplaintForNewCollectionApp";

                                                HashMap<String, String> map = new HashMap<String, String>();
                                                map.put("compId", cmpid);
                                                map.put("userId", userIdList.get(lv.getCheckedItemPosition()));

                                                CallVolleyAssignComplain(URL, map);

                                            }
                                        }
                                    });

                                    final AlertDialog alert = builderDialog.create();
                                    alert.setTitle("Select User to Assign Complaint");
                                    alert.setCancelable(true);
                                    alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                    alert.show();
                                }
                            } else {
                                Toast.makeText(ComplainDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        error.printStackTrace();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    public void CallVolleyAssignComplain(String a, HashMap<String, String> map) {


        final Dialog loader = Utils.getLoader(ComplainDetails.this);
        loader.show();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {
                                Toast.makeText(ComplainDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        error.printStackTrace();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        finish();
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

    private class JSONAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(ComplainDetails.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {


                if (jsonObject.getString("status").equalsIgnoreCase("True")) {

                    HashMap<String, String> map = new HashMap<>();

                    String cid = jsonObject.getString("ComplainId");
                    String cname = jsonObject.getString("Name");
                    String caddress = jsonObject.getString("AccountNo");
                    String carea = jsonObject.getString("Phone");
                    String cacno = jsonObject.getString("Email");
                    String cphone = jsonObject.getString("Address");
                    String cemail = jsonObject.getString("Subject");
                    String castatus = jsonObject.getString("Message");
                    String cmq = jsonObject.getString("image");
                    String ctotaloa = jsonObject.getString("mqno");
                    String cmstatus = jsonObject.getString("ComplainStatus");
                    String wcommentcount = jsonObject.getString("webcommentcount");
                    String ucommentcount = jsonObject.getString("usercommentcount");

                    if (cmstatus.equals("RESOLVED")) {
                        tvresolve.setText(cmstatus);
                        tvresolve.setEnabled(false);
                    } else if (cmstatus.equals("NEW")) {
                        toolbar.getMenu().getItem(0).setVisible(true);
                    }

                    map.put("ComplainId", cid);
                    map.put("Name", cname);
                    map.put("AccountNo", caddress);
                    map.put("Phone", carea);
                    map.put("Email", cacno);
                    map.put("Address", cphone);
                    map.put("Subject", cemail);
                    map.put("Message", castatus);
                    map.put("image", cmq);
                    map.put("mqno", ctotaloa);
                    map.put("ComplainStatus", cmstatus);


                    Complaindetails.add(map);

                    tvacno.setText(caddress);
                    tvmqno.setText(ctotaloa);
                    tvcid.setText("CP - " + cid);
                    tvcsub.setText(cemail);
                    tvdesc.setText(castatus);
                    tvphone.setText(carea);
                    tvemail.setText(cacno);
                    tvaddress.setText(cphone);

                    if (pref.getString("RoleId", "").equals("2")) {
                        tvcount.setText(wcommentcount);

                        if (wcommentcount.equals("0")) {
                            tvcount.setVisibility(View.GONE);
                        } else {
                            tvcount.setVisibility(View.VISIBLE);

                            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            tvcount.startAnimation(anim);

                        }
                    } else {
                        tvcount.setText(ucommentcount);


                        if (ucommentcount.equals("0")) {
                            tvcount.setVisibility(View.GONE);
                        } else {
                            tvcount.setVisibility(View.VISIBLE);

                            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            tvcount.startAnimation(anim);

                        }
                    }
                } else {
                    Toast.makeText(ComplainDetails.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
