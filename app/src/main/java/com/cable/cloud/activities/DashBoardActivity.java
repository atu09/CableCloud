package com.cable.cloud.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.helpers.DBHelper;
import com.cable.cloud.OnCountAssignment;
import com.cable.cloud.R;
import com.cable.cloud.TestLocationService;
import com.cable.cloud.fragments.CollectionFragment;
import com.cable.cloud.fragments.ComplainFragment;
import com.cable.cloud.fragments.CustomerFragment;
import com.cable.cloud.fragments.HomeFragment;
import com.cable.cloud.fragments.PaymentFragment;
import com.cable.cloud.models.MenuData;
import com.cable.cloud.customs.MySquareImage;
import com.cable.cloud.adapters.RvAdapter;
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
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbs.android.library.MDDialog;

import static com.cable.cloud.helpers.DBHelper.ENTITY_NAME;
import static com.cable.cloud.helpers.DBHelper.PK_ENTITY_ID;

public class DashBoardActivity extends AppCompatActivity implements OnCountAssignment {

    RvAdapter menuAdapter;
    List<MenuData> menuDataList = new ArrayList<>();
    RecyclerView rv_drawer;
    NavigationView navigationView;
    Menu toolbarMenu;

    StringBuilder stringBuilder = new StringBuilder();

    String LOGIN_PREF = "LoginPref";
    boolean isOffline = false;

    String siteURL, userId, contractorId;
    ArrayList<String> entityNameList = new ArrayList<>();
    ArrayList<String> entityIdList = new ArrayList<>();

    Bundle bundle = new Bundle();
    RequestQueue requestQueue;

    DBHelper dbHelper;
    boolean doubleBackToExitPressedOnce = false;
    FrameLayout containerView;
    RelativeLayout toolbarContainer;
    SharedPreferences pref;
    DrawerLayout drawer;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dash_board);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        containerView = (FrameLayout) findViewById(R.id.containerView);
        toolbarContainer = (RelativeLayout) findViewById(R.id.toolbarContainer);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        rv_drawer = navigationView.findViewById(R.id.rv_navigationDrawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        pref = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
        siteURL = pref.getString("SiteURL", "");
        userId = pref.getString("Userid", "");
        contractorId = pref.getString("Contracotrid", "");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float xOffset = drawerView.getWidth() * slideOffset;
                toolbarContainer.setTranslationX(xOffset);
                containerView.setTranslationX(xOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        menuDataList.add(new MenuData("Home", R.drawable.ic_home_black));
        menuDataList.add(new MenuData("Payments", R.drawable.rupee));
        menuDataList.add(new MenuData("Collections", R.drawable.money_bag));
        menuDataList.add(new MenuData("Complaints", R.drawable.complain_call));
        menuDataList.add(new MenuData("Customers", R.drawable.group));
        menuDataList.add(new MenuData("Sync", R.drawable.sync_icon));
        menuDataList.add(new MenuData("Logout", R.drawable.logout_icon));

        menuAdapter = new RvAdapter(new RvAdapter.AdapterListener() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.cell_drawer, parent, false);
                return new MenuHolder(itemView);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                final MenuHolder menuHolder = (MenuHolder) holder;

                final MenuData menuData = menuDataList.get(position);

                menuHolder.title.setText(menuData.title);
                menuHolder.title.setTextColor(Color.WHITE);

                menuHolder.imageView.setImageResource(menuData.image);
                menuHolder.imageView.setColorFilter(Color.WHITE);


                menuHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isOffline = !Utils.isInternetAvailable(DashBoardActivity.this);

                        if (menuHolder.title.toString().equalsIgnoreCase(toolbar.getTitle().toString())) {
                            return;
                        }

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START, true);
                        }

                        Bundle bundle = new Bundle();

                        switch (position) {
                            case 0:
                                loadEntities();
                                break;

                            case 1:

                                PaymentFragment paymentFragment = new PaymentFragment();
                                if (!isOffline) {
                                    String URL = siteURL + "/GetAreaByUserForCollectionApp";
                                    bundle.putString("url", URL);
                                } else {
                                    bundle.putString("url", "-");
                                }
                                paymentFragment.setArguments(bundle);
                                loadFragment(paymentFragment, menuData.title);
                                break;

                            case 2:

                                CollectionFragment collectionFragment = new CollectionFragment();
                                if (!isOffline) {
                                    String URL = siteURL + "/GetUserlistforcollectionApp";
                                    bundle.putString("url", URL);
                                } else {
                                    bundle.putString("url", "-");
                                }
                                collectionFragment.setArguments(bundle);
                                loadFragment(collectionFragment, menuData.title);
                                break;

                            case 3:

                                ComplainFragment complainFragment = new ComplainFragment();
                                if (!isOffline) {
                                    String URL = siteURL + "/GetComplainListByAreaForCollectionApp";
                                    bundle.putString("url", URL);
                                } else {
                                    bundle.putString("url", "-");
                                }
                                complainFragment.setArguments(bundle);
                                loadFragment(complainFragment, menuData.title);
                                break;

                            case 4:

                                CustomerFragment customerFragment = new CustomerFragment();
                                if (!isOffline) {
                                    String URL = siteURL + "/GetCustomerDashbordHomeForNewCollectionApp";
                                    bundle.putString("url", URL);
                                } else {
                                    bundle.putString("url", "-");
                                }

                                customerFragment.setArguments(bundle);
                                loadFragment(customerFragment, menuData.title);
                                break;

                            case 5:
                                Intent i = new Intent(getApplicationContext(), SyncDataActivity.class);
                                startActivity(i);
                                break;

                            case 6:
                                if (isOffline) {

                                    if (dbHelper.ReceiptCount() > 0) {

                                        MDDialog.Builder adb = new MDDialog.Builder(DashBoardActivity.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("LOGOUT CONFIRMATION")
                                                .setPositiveButton("SYNC", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // syncReceipts();
                                                    }

                                                })
                                                .setNegativeButton("CLOSE", null);

                                        adb.setWidthMaxDp(600);
                                        adb.setShowTitle(true);
                                        adb.setShowButtons(true);
                                        adb.setShowPositiveButton(false);
                                        adb.setBackgroundCornerRadius(5);
                                        adb.setCancelable(true);
                                        adb.setContentTextSizeDp(16);
                                        adb.setContentPaddingDp(10);
                                        adb.setContentTextColor(Color.BLACK);
                                        adb.setMessages(new CharSequence[]{"\n You have " + dbHelper.ReceiptCount() + " Payment Receipt Left to Sync.." + "\n \n" + "Its not Safe to Logout"});

                                        MDDialog dialog = adb.create();
                                        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                        dialog.show();

                                    } else {
                                        Toast.makeText(DashBoardActivity.this, "Sorry.. You can not Logout in offline mode.!!", Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    if (dbHelper.ReceiptCount() > 0) {

                                        MDDialog.Builder adb = new MDDialog.Builder(DashBoardActivity.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("LOGOUT CONFIRMATION")
                                                .setPositiveButton("SYNC", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        syncReceipts();
                                                    }

                                                })
                                                .setNegativeButton("CANCEL", null);

                                        adb.setWidthMaxDp(600);
                                        adb.setShowTitle(true);
                                        adb.setShowButtons(true);
                                        adb.setBackgroundCornerRadius(5);
                                        adb.setCancelable(true);
                                        adb.setContentTextSizeDp(16);
                                        adb.setContentTextColor(Color.BLACK);
                                        adb.setMessages(new CharSequence[]{"\n You have " + dbHelper.ReceiptCount() + " Payment Receipt Left to Sync.." + "\n \n" + "Are you sure want to Logout?"});

                                        MDDialog dialog = adb.create();
                                        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                        dialog.show();

                                    } else {
                                        String URL = siteURL + "/UpdateAndroidDeviceId";
                                        CallVolleyUpdateDeviceID(URL);
                                    }
                                }
                                break;
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return menuDataList.size();
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }
        });
        rv_drawer.setAdapter(menuAdapter);

        loadOfflineEntities();

        String loginName = pref.getString("LoginName", "");
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(loginName);
        tvUserName.setTypeface(Typeface.createFromAsset(this.getAssets(), "font/pacifico.ttf"));

        if (!pref.getString("RoleId", "").equals("2")) {
            checkAllPermissions();
        }
    }


    public void loadOfflineEntities() {

        entityNameList.clear();
        entityNameList.add("All Entities");
        entityIdList.add("All Entities");

        dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getEntityData();

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String entityId = cursor.getString(cursor.getColumnIndex(PK_ENTITY_ID));
                    String entityName = cursor.getString(cursor.getColumnIndex(ENTITY_NAME));

                    entityNameList.add(entityName);
                    entityIdList.add(entityId);

                } while (cursor.moveToNext());
            }
        }

        for (int i = 1; i < entityIdList.size(); i++) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entityIdList.get(i));
        }
        Utils.checkLog("entity", stringBuilder.toString(), null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        isOffline = !Utils.isInternetAvailable(this);
        HomeFragment homeFragment = new HomeFragment();
        if (!isOffline) {
            String URL = siteURL + "/GetDashbordHomeForNewCollectionApp?contractorId=" + contractorId + "&loginuserId=" + userId + "&entityIds=" + stringBuilder.toString();
            bundle.putString("url", URL);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Entityids", stringBuilder.toString());
            editor.apply();

        } else {
            bundle.putString("url", "-");
        }
        homeFragment.setArguments(bundle);
        loadFragment(homeFragment, "Home");
    }

    public void loadFragment(Fragment fragment, String title) {
        toolbar.setTitle(title);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, fragment);
        fragmentTransaction.commit();
    }

    public void loadEntities() {

        final ListView listView = new ListView(DashBoardActivity.this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setDividerHeight(0);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(DashBoardActivity.this, android.R.layout.simple_list_item_multiple_choice, entityNameList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listView.getItemAtPosition(position).equals("All Entities")) {
                    if (listView.isItemChecked(position)) {
                        for (int i = 1; i < listView.getCount(); i++) {
                            listView.setItemChecked(i, true);
                        }
                    } else {
                        for (int i = 1; i < listView.getCount(); i++) {
                            listView.setItemChecked(i, false);
                        }
                    }
                } else {
                    if (listView.isItemChecked(0)) {
                        listView.setItemChecked(0, false);
                    }
                }
            }
        });

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(DashBoardActivity.this);
        builderDialog.setView(listView);
        builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START, true);
                }

                if (!isOffline) {

                    SparseBooleanArray checked = listView.getCheckedItemPositions();

                    if (checked.size() == 0) {
                        Toast.makeText(DashBoardActivity.this, "Please Select Atleast one Entity...", Toast.LENGTH_LONG).show();
                    } else {

                        stringBuilder.setLength(0);
                        if (adapter.getItem(checked.keyAt(0)).equalsIgnoreCase("All Entities")) {
                            for (int j = 1; j < entityIdList.size(); j++) {
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(entityIdList.get(j));
                            }
                        } else {
                            for (int i = 0; i < checked.size(); i++) {
                                int position = checked.keyAt(i);
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(entityIdList.get(position));
                            }
                        }

                        String URL = siteURL + "/GetDashbordHomeForNewCollectionApp?contractorId=" + contractorId + "&loginuserId=" + userId + "&entityIds=" + stringBuilder.toString();
                        bundle.putString("url", URL);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("Entityids", stringBuilder.toString());
                        editor.apply();

                        HomeFragment homeFragment = new HomeFragment();
                        homeFragment.setArguments(bundle);
                        loadFragment(homeFragment, "Home");

                    }
                } else {
                    Toast.makeText(DashBoardActivity.this, "Sorry.. You are Offline..!! ", Toast.LENGTH_LONG).show();
                }
            }
        });
        builderDialog.setNegativeButton("CANCEL", null);

        final AlertDialog alert = builderDialog.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alert.show();
    }

    public void checkAllPermissions() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (result == PackageManager.PERMISSION_GRANTED) {
            Intent service = new Intent(DashBoardActivity.this, TestLocationService.class);
            startService(service);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (permissions.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) /*|| grantResults[1] == PackageManager.PERMISSION_DENIED ||
                            grantResults[2] == PackageManager.PERMISSION_DENIED || grantResults[3] == PackageManager.PERMISSION_DENIED
                            || grantResults[4] == PackageManager.PERMISSION_DENIED)*/ {
                        Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                    } else {
                        Intent service = new Intent(DashBoardActivity.this, TestLocationService.class);
                        startService(service);
                    }
                }

            }
        }
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            Utils.closeKeyboard(DashBoardActivity.this);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                trimCache();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Utils.popToast(this, "Tap again to exit!");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void trimCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public void OnCountAssign(int acount, int rcount, int ccount, int comcount, int custcomcount) {

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu paramMenu) {

        toolbarMenu = paramMenu;
        return super.onPrepareOptionsMenu(paramMenu);

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

    public void syncReceipts() {
        Cursor c = dbHelper.getReceipts();

        if (c != null && c.getCount() > 0) {

            //Toast.makeText(getApplicationContext(), "r=" + c.getCount(), Toast.LENGTH_SHORT).show();

            if (c.moveToFirst()) {

                do {

                    String rid = c.getString(c.getColumnIndex(DBHelper.PK_RECEIPT_ID));
                    String acno = c.getString(c.getColumnIndex(DBHelper.FK_ACCOUNTNO));
                    String bid = c.getString(c.getColumnIndex(DBHelper.FK_BILL_ID));
                    String chqnumber = c.getString(c.getColumnIndex(DBHelper.CHQNUMBER));
                    String cheqdate = c.getString(c.getColumnIndex(DBHelper.CHQDATE));
                    String cheqbankname = c.getString(c.getColumnIndex(DBHelper.CHQBANKNAME));
                    String email = c.getString(c.getColumnIndex(DBHelper.R_EMAIL));
                    String createdby = c.getString(c.getColumnIndex(DBHelper.CREATEDBY));
                    String sign = c.getString(c.getColumnIndex(DBHelper.SIGNATURE));
                    String receiptdate = c.getString(c.getColumnIndex(DBHelper.RECEIPTDATE));
                    String longitude = c.getString(c.getColumnIndex(DBHelper.LONGITUDE));
                    String lati = c.getString(c.getColumnIndex(DBHelper.LATITUDE));
                    String discount = c.getString(c.getColumnIndex(DBHelper.DISCOUNT));
                    String paidamount = c.getString(c.getColumnIndex(DBHelper.PAID_AMOUNT));
                    String paymentmode = c.getString(c.getColumnIndex(DBHelper.PAYMENT_MODE));
                    String recptNo = c.getString(c.getColumnIndex(DBHelper.RECEIPT_NO));


                    HashMap<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json; charset=utf-8");
                    map.put("accountno", acno);
                    map.put("billid", bid);
                    map.put("paidamount", paidamount);
                    map.put("paymentmode", paymentmode);
                    map.put("chqnumber", chqnumber);
                    map.put("cheqdate", cheqdate);
                    map.put("cheqbankname", cheqbankname);
                    map.put("email", email);
                    map.put("notes", "");
                    map.put("createdby", createdby);
                    map.put("signature", sign);
                    map.put("receiptdate", receiptdate);
                    map.put("longitude", longitude);
                    map.put("latitude", lati);
                    map.put("discount", discount);
                    map.put("isprint", "");
                    map.put("recptNo", recptNo);

                    String URL = siteURL + "/withdiscountAndReceiptNo";
                    CallVolley(URL, map, rid);


                } while (c.moveToNext());

            }
        }
    }

    public void CallVolleyUpdateDeviceID(String a) {

        final Dialog loader = Utils.getLoader(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            if (loader.isShowing()) {
                                loader.dismiss();
                            }

                            JSONObject json = new JSONObject(response);

                            Utils.checkLog("json", json, null);

                            if (json.getString("status").equalsIgnoreCase("True")) {

                                SharedPreferences pref = getSharedPreferences(LOGIN_PREF, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear().apply();
                                dbHelper.ClearAllData();

                                stopService(new Intent(getApplicationContext(), TestLocationService.class));
                                Utils.restartApp(getApplicationContext());
                            }

                        } catch (JSONException e) {
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
                    }}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "User");
                params.put("id", userId);
                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    public void CallVolley(String a, HashMap<String, String> map1, final String rid) {

        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map1),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                Toast.makeText(getApplicationContext(), "Receipt Done.!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_SHORT).show();

                                if (dbHelper.UpdateReceiptStatus(rid)) {
                                    Toast.makeText(getApplicationContext(), "Status Done.!!", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DashBoardActivity.this, "Something Went Wrong...", Toast.LENGTH_LONG).show();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    public void CallVolleys(String a) {

        JsonObjectRequest request;

        final Dialog loader = Utils.getLoader(this);

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (loader.isShowing()) {
                                loader.dismiss();
                            }

                            entityNameList.clear();

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                entityNameList.add("All Entities");
                                final JSONArray entityArray = response.getJSONArray("EntityInfoList");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    String entityId = e.getString("EntityId");
                                    String entityName = e.getString("EntityName");

                                    entityIdList.add(entityId);
                                    entityNameList.add(entityName);
                                }
                                for (int i = 0; i < entityIdList.size(); i++) {
                                    if (stringBuilder.length() > 0)
                                        stringBuilder.append(",");
                                    stringBuilder.append(entityIdList.get(i));
                                }

                            } else {
                                Toast.makeText(DashBoardActivity.this, "No data", Toast.LENGTH_LONG).show();
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

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    public class MenuHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public MySquareImage imageView;

        MenuHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_menu);
            imageView = (MySquareImage) view.findViewById(R.id.iv_menu);
        }
    }

}
