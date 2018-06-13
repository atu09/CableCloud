package com.mtaj.mtaj_08.cableplus_new;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

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

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String PREF_NAME = "LoginPref";

    final String NAMESPACE = "http://tempuri.org/";
    final String URL = "http://cableplus.in/service.asmx";
    final String SOAP_ACTION = "http://tempuri.org/GetContractorDetails";
    final String METHOD_NAME = "GetContractorDetails";

    String Url = "http://cableplus.in/service.asmx/GetContractorDetails?OPCode=";

    EditText edtusername, edtpassword, edtuopcode;
    Button btnlogin;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    String token = "-";
    private CardView cardLogo;
    private CardView cardLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_revised);

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
        token = FirebaseInstanceId.getInstance().getToken();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (pref.getString("LoginStatus", "").equals("login")) {
            startActivity(new Intent(getApplicationContext(), DashBoard.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }

        edtusername = (EditText) findViewById(R.id.editText);
        edtpassword = (EditText) findViewById(R.id.edtPassword);
        edtuopcode = (EditText) findViewById(R.id.OpCode);
        cardLogo = (CardView) findViewById(R.id.cardLogo);
        cardLogin = (CardView) findViewById(R.id.cardLogin);
        btnlogin = (Button) findViewById(R.id.btnlogin);

        edtusername.setText("hjbrc");
        edtpassword.setText("hjbrc@123");
        edtuopcode.setText("hjbrc");

        initAnimation();
    }


    private void init() {


        edtuopcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // handle next button

                    //s Toast.makeText(LoginActivity.this, "token-"+token, Toast.LENGTH_SHORT).show();

                    if (ValidateEdittext("Enter Username", edtusername) && ValidateEdittext("Enter Password", edtpassword) && ValidateEdittext("Enter OPCOde", edtuopcode)) {
                        new JSONAsynk().execute(new String[]{Url, edtuopcode.getText().toString(), edtusername.getText().toString(), edtpassword.getText().toString(), token});
                        Utils.closeKeyboard(LoginActivity.this);
                    }

                    return true;
                }

                return false;
            }
        });

        edtusername.setHintTextColor(edtusername.getHintTextColors().withAlpha(-3));
        edtpassword.setHintTextColor(edtpassword.getHintTextColors().withAlpha(-3));
        edtuopcode.setHintTextColor(edtuopcode.getHintTextColors().withAlpha(-3));

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();

                if (ValidateEdittext("Enter Username", edtusername) && ValidateEdittext("Enter Password", edtpassword) && ValidateEdittext("Enter OPCOde", edtuopcode)) {

                    //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

                    new JSONAsynk().execute(new String[]{Url, edtuopcode.getText().toString(), edtusername.getText().toString(), edtpassword.getText().toString(), token});


                }

            }
        });
    }

    private void initAnimation() {

        Animation bottomUpCardLogo = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bottom_up_anim);
        cardLogo.setVisibility(View.VISIBLE);
        cardLogo.startAnimation(bottomUpCardLogo);

        bottomUpCardLogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation bottomUp = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bottom_up_anim);
                cardLogin.setVisibility(View.VISIBLE);
                cardLogin.startAnimation(bottomUp);

                bottomUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        init();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token = intent.getStringExtra("token");
            if (token != null) {
                //send token to your server or what you want to do
                //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);


    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";

            // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";

            Intent i = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(i);
            color = Color.RED;
        }

        //Snackbar snackbar = Snackbar
        //     .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);



      /*  View sbView = Toast.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();*/
    }


    public boolean ValidateEdittext(String error, EditText ed) {
        if (ed.getText().toString().isEmpty() || ed.getText().toString().length() == 0) {
            ed.setError(error);
            return false;
        } else {
            return true;
        }
    }


    public JSONObject makeHttpRequest(String url) {

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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
                    Toast.makeText(LoginActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(LoginActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(LoginActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }


    private class JSONAsynk extends AsyncTask<String, String, JSONObject> {

        Dialog spload;
        JSONObject jsn1, jsn, jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload = Utils.getLoader(LoginActivity.this);
            spload.show();

      /* pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setIndeterminate(true);
       // pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressanimation));
        pDialog.show();*/

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsn = makeHttpRequest(params[0] + params[1]);

                String s3 = jsn.getString("message");
                String s4 = jsn.getString("status");

                if (s4.equals("True")) {

                    final String s1 = jsn.getString("siteURL");
                    String s2 = jsn.getString("contractorId");

                    SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("SiteURL", s1);
                    editor.putString("Contracotrid", s2);
                    editor.commit();


                    jsn1 = makeHttpRequest(s1 + "/ValidateUserForCollectionApp?username=" + params[2] + "&password=" + params[3] + "&contractorId=" + s2 + "&androidDeviceId=" + params[4]);

                    //String msg = jsn1.getString("message");

                    //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                    jsnmain = jsn1;

                } else {

                    jsnmain = jsn;

                    // Toast.makeText(LoginActivity.this, s3, Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsnmain;


        }

        @Override
        protected void onPostExecute(JSONObject json) {

            spload.dismiss();

            SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            try {

                String msg = json.getString("message");

                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                String status = json.getString("status");

                if (status.equals("True")) {
                    String name = json.getString("Name");
                    String uid = json.getString("UserId");
                    String rid = json.getString("RoleId");
                    String rname = json.getString("RoleName");
                    String oseditable = json.getString("isOutstandingEditable");

                    if (rid.equals("2")) {
                    } else {
                        final JSONArray entityarray = json.getJSONArray("lstRole");

                        for (int i = 0; i < entityarray.length(); i++) {
                            JSONObject e = (JSONObject) entityarray.get(i);

                            String fname = e.getString("formname");
                            String selected = e.getString("isselected");

                            if (fname.equals("Customer")) {
                                if (selected.equals("True")) {
                                    editor.putBoolean("IsCustomer", true).apply();
                                }
                                if (selected.equals("False")) {
                                    editor.putBoolean("IsCustomer", false).apply();
                                }
                            }
                            if (fname.equals("Complain")) {
                                if (selected.equals("True")) {
                                    editor.putBoolean("IsComplain", true).apply();
                                }
                                if (selected.equals("False")) {
                                    editor.putBoolean("IsComplain", false).apply();
                                }

                            }
                            if (fname.equals("Billing")) {
                                if (selected.equals("True")) {
                                    editor.putBoolean("IsBilling", true).apply();
                                }
                                if (selected.equals("False")) {
                                    editor.putBoolean("IsBilling", false).apply();
                                }

                            }
                            if (fname.equals("Dashboard")) {
                                if (selected.equals("True")) {
                                    editor.putBoolean("IsDashboard", true).apply();
                                }
                                if (selected.equals("False")) {
                                    editor.putBoolean("IsDashboard", false).apply();
                                }
                            }

                        }

                    }
                    editor.putString("LoginStatus", "login");
                    editor.putString("LoginName", name);
                    editor.putString("Userid", uid);
                    editor.putString("RoleId", rid);
                    editor.putString("RoleName", rname);
                    editor.putString("isOutstandingEditable", oseditable);
                    editor.apply();

                    // Toast.makeText(LoginActivity.this,pref.getString("isOutstandingEditable",""), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashBoard.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
            } catch (JSONException ex) {
                Toast.makeText(LoginActivity.this, "**" + ex, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Something Went  Wrong...", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
