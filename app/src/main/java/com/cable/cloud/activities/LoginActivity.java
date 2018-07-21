package com.cable.cloud.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
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

import com.cable.cloud.helpers.ConnectivityReceiver;
import com.cable.cloud.helpers.DBHelper;
import com.cable.cloud.helpers.MyApplication;
import com.cable.cloud.R;
import com.cable.cloud.helpers.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String LOGIN_PREF = "LoginPref";
    //String Url = "http://cableplus.in/service.asmx/GetContractorDetails?OPCode=";
    String Url = "http://master.cable-cloud.com/service.asmx/GetContractorDetails?OPCode=";//new base url

    EditText etUsername, etPassword, etOpCode;
    Button btnLogin;

    String token = "-";
    //RippleBackground loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_revised);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        token = pref.getString("refresh_token", "demo");
        etUsername = (EditText) findViewById(R.id.editText);
        etPassword = (EditText) findViewById(R.id.edtPassword);
        etOpCode = (EditText) findViewById(R.id.OpCode);
        //loader = (RippleBackground) findViewById(R.id.loader);
        btnLogin = (Button) findViewById(R.id.btnlogin);

        etUsername.setText("demo");
        etPassword.setText("demo123");
        etOpCode.setText("demo");

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
    }

    private void init() {

        etOpCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    loginCall();
                    return true;
                }

                return false;
            }
        });

        etUsername.setHintTextColor(etUsername.getHintTextColors().withAlpha(-3));
        etPassword.setHintTextColor(etPassword.getHintTextColors().withAlpha(-3));
        etOpCode.setHintTextColor(etOpCode.getHintTextColors().withAlpha(-3));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCall();
            }
        });
    }

    void loginCall() {
        Utils.closeKeyboard(LoginActivity.this);
        if (ValidateEditText("Enter Username", etUsername) && ValidateEditText("Enter Password", etPassword) && ValidateEditText("Enter OPCode", etOpCode)) {
            String[] params = new String[]{Url, etOpCode.getText().toString(), etUsername.getText().toString(), etPassword.getText().toString(), token};
            new JSONLoginAsync().execute(params);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

/*
        if (!loader.isRippleAnimationRunning()){
            loader.startRippleAnimation();
        }
*/
    }

    @Override
    protected void onPause() {
        super.onPause();

/*
        if (loader.isRippleAnimationRunning()){
            loader.stopRippleAnimation();
        }
*/
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(getApplicationContext(), NoConnectionActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public boolean ValidateEditText(String error, EditText ed) {
        if (ed.getText().toString().isEmpty() || ed.getText().toString().length() == 0) {
            ed.setError(error);
            return false;
        } else {
            return true;
        }
    }

    public JSONObject makeHttpRequest(String url) {

        Utils.checkLog("URL", url, null);
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
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Utils.checkLog("json", sb.toString(), null);
            jsonObject = new JSONObject(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @SuppressLint("StaticFieldLeak")
    private class JSONLoginAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(LoginActivity.this);
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonObject = null;

            try {
                jsonObject = makeHttpRequest(params[0] + params[1]);
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("True")) {

                    final String siteURL = jsonObject.getString("siteURL");
                    String contractorId = jsonObject.getString("contractorId");

                    SharedPreferences pref = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("SiteURL", siteURL);
                    editor.putString("Contracotrid", contractorId);
                    editor.apply();

                    jsonObject = makeHttpRequest(siteURL + "/ValidateUserForCollectionApp?username=" + params[2] + "&password=" + params[3] + "&contractorId=" + contractorId + "&androidDeviceId=" + params[4]);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;

        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            SharedPreferences pref = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            try {

                String message = json.getString("message");

                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                String status = json.getString("status");

                if (status.equalsIgnoreCase("True")) {
                    String name = json.getString("Name");
                    String userId = json.getString("UserId");
                    String roleId = json.getString("RoleId");
                    String roleName = json.getString("RoleName");
                    String outstandingEditable = json.getString("isOutstandingEditable");

                    if (roleId.equals("2")) {
                    } else {
                        final JSONArray entityArray = json.getJSONArray("lstRole");

                        for (int i = 0; i < entityArray.length(); i++) {
                            JSONObject e = (JSONObject) entityArray.get(i);

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
                    editor.putString("Userid", userId);
                    editor.putString("RoleId", roleId);
                    editor.putString("RoleName", roleName);
                    editor.putString("isOutstandingEditable", outstandingEditable);
                    editor.apply();

                    new JSONEntityAsync().execute();

                }
            } catch (Exception e) {
                Log.e("exception", "onPostExecute: " + e.getMessage());
                Toast.makeText(LoginActivity.this, "Something Went  Wrong...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class JSONEntityAsync extends AsyncTask<String, String, Boolean> {

        Dialog loader;
        SharedPreferences pref;
        DBHelper dbHelper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pref = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
            dbHelper = new DBHelper(LoginActivity.this);
            loader = Utils.getLoader(LoginActivity.this);
            loader.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            String siteURL = pref.getString("SiteURL", "");
            String UserId = pref.getString("Userid", "");
            String URL = siteURL + "/GetEntityByUser?userId=" + UserId;

            boolean status = false;
            try {
                JSONObject jsonObject = makeHttpRequest(URL);
                Utils.checkLog("login", jsonObject, null);
                if (jsonObject.getString("status").equalsIgnoreCase("True")) {

                    status = true;
                    dbHelper.deleteEntityData();
                    final JSONArray entityArray = jsonObject.getJSONArray("EntityInfoList");

                    for (int i = 0; i < entityArray.length(); i++) {
                        JSONObject e = (JSONObject) entityArray.get(i);

                        String entityId = e.getString("EntityId");
                        String entityName = e.getString("EntityName");

                        dbHelper.insertEntity(entityId, entityName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return status;

        }

        @Override
        protected void onPostExecute(Boolean status) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            if (status) {
                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.putString("refresh_token", token);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Something Went  Wrong...", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
