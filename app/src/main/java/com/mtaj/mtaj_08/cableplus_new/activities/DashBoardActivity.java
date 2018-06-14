package com.mtaj.mtaj_08.cableplus_new.activities;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
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
import android.view.inputmethod.InputMethodManager;
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
import com.mtaj.mtaj_08.cableplus_new.ConnectivityReceiver;
import com.mtaj.mtaj_08.cableplus_new.DBHelper;
import com.mtaj.mtaj_08.cableplus_new.NonSwipeableViewPager;
import com.mtaj.mtaj_08.cableplus_new.OnCountAssignment;
import com.mtaj.mtaj_08.cableplus_new.R;
import com.mtaj.mtaj_08.cableplus_new.SyncData;
import com.mtaj.mtaj_08.cableplus_new.TestLocationService;
import com.mtaj.mtaj_08.cableplus_new.fragments.CollectionFragment;
import com.mtaj.mtaj_08.cableplus_new.fragments.ComplainFragment;
import com.mtaj.mtaj_08.cableplus_new.fragments.CustomerFragment;
import com.mtaj.mtaj_08.cableplus_new.fragments.HomeFragment;
import com.mtaj.mtaj_08.cableplus_new.fragments.PaymentFragment;
import com.mtaj.mtaj_08.cableplus_new.models.MenuData;
import com.mtaj.mtaj_08.cableplus_new.Customs.MySquareImage;
import com.mtaj.mtaj_08.cableplus_new.adapters.RvAdapter;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbs.android.library.MDDialog;

public class DashBoardActivity extends AppCompatActivity
        implements OnCountAssignment {

    RvAdapter menuAdapter;
    List<MenuData> menuDataList = new ArrayList<>();
    RecyclerView rv_drawer;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    OnCountAssignment mcount;

    Menu mToolbarMenu;

    int Rcount = 0, Acount = 0, Ccount = 0, CustComCount = 0;

    static int Comcount = 0;

    StringBuilder sb = new StringBuilder();

    static StringBuilder sb1 = new StringBuilder();

    private static final String PREF_NAME = "LoginPref";

    public static TabLayout tabLayout;
    public static NonSwipeableViewPager viewPager;
    public static int int_items = 5;

    static String siteurl, uid, cid;
    ArrayList<String> enamelist = new ArrayList<>();
    ArrayList<String> eidlist = new ArrayList<>();

    static String URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    static Bundle bundle = new Bundle();

    RequestQueue requestQueue;

    DBHelper myDB;

    public static boolean isOffline = false;
    private FrameLayout containerView;
    private RelativeLayout toolbarContainer;
    private static final float END_SCALE = 0.7f;
    SharedPreferences pref;
    DrawerLayout drawer;
    public Toolbar toolbar;

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

        mcount = this;

        myDB = new DBHelper(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");

        sb.append("All Entities,");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (!pref.getString("RoleId", "").equals("2")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAllPermissions();
            }

            Intent service = new Intent(DashBoardActivity.this, TestLocationService.class);
            startService(service);
        }


        enamelist.clear();
        try {

            boolean isConnected = ConnectivityReceiver.isConnected();
            if (!isConnected) {

                isOffline = true;

                //Toast.makeText(DashBoardActivity.this, "No Connect..", Toast.LENGTH_SHORT).show();

                enamelist.add("All Entities");

                Cursor c = myDB.getEntityData();

                //Toast.makeText(DashBoardActivity.this,"**"+ c.getCount(), Toast.LENGTH_SHORT).show();

                if (c.getCount() > 0) {
                    if (c.moveToFirst()) {
                        do {

                            String eid = c.getString(c.getColumnIndex("ENTITY_ID"));
                            String ename = c.getString(c.getColumnIndex("ENTITY_NAME"));

                            enamelist.add(ename);
                            eidlist.add(eid);

                        } while (c.moveToNext());
                    }
                }

                for (int i = 0; i < eidlist.size(); i++) {
                    if (sb1.length() > 0)
                        sb1.append(",");
                    sb1.append(eidlist.get(i));
                }
            } else {

                isOffline = false;

                // Toast.makeText(DashBoardActivity.this, " Connect..", Toast.LENGTH_SHORT).show();

                URL = siteurl + "/GetEntityByUser?userId=" + uid;
                final JSONObject jsonobj = makeHttpRequest(URL);
                if (jsonobj.getString("status").toString().equals("True")) {

                    myDB.deleteEntityData();

                    enamelist.add("All Entities");
                    final JSONArray entityarray = jsonobj.getJSONArray("EntityInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        String eid = e.getString("EntityId");
                        String ename = e.getString("EntityName");

                        if (myDB.insertEntity(eid, ename)) {
                            // Toast.makeText(DashBoardActivity.this, "E" + i, Toast.LENGTH_SHORT).show();
                        }

                        enamelist.add(ename);
                        eidlist.add(eid);
                    }

                    for (int i = 0; i < eidlist.size(); i++) {
                        if (sb1.length() > 0)
                            sb1.append(",");
                        sb1.append(eidlist.get(i));
                    }

                } else {

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Toast.makeText(DashBoardActivity.this, "Something Went Wrong... Check Your Internet Connection...", Toast.LENGTH_LONG).show();
        }


        // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);

/*
                final float offsetScale = 1 - diffScaledOffset;
                binding.layoutMap.mapView.setScaleX(offsetScale);
                binding.layoutMap.mapView.setScaleY(offsetScale);
*/

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                //final float xOffsetDiff = containerView.getWidth() * diffScaledOffset / 2;
                //final float xTranslation = xOffset - xOffsetDiff;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        rv_drawer = navigationView.findViewById(R.id.rv_navigationDrawer);


        menuDataList.add(new MenuData("Home", R.drawable.ic_home_black_x24dp));
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

                        if (menuHolder.title.toString().equalsIgnoreCase(toolbar.getTitle().toString())) {
                            return;
                        }

                        toolbar.setTitle(menuData.title);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START, true);
                        }

                        switch (position) {
                            case 0:
                                loadEntities();
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                viewPager.setCurrentItem(position);
                                break;
                            case 5:
                                Intent i = new Intent(getApplicationContext(), SyncData.class);
                                startActivity(i);
                                break;
                            case 6:
                                boolean isConnected = ConnectivityReceiver.isConnected();

                                if (!isConnected) {

                                    if (myDB.ReceiptCount() > 0) {

                                        //Toast.makeText(DashBoardActivity.this, "Logout", Toast.LENGTH_SHORT).show();

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
                                        adb.setMessages(new CharSequence[]{"\n You have " + myDB.ReceiptCount() + " Payment Receipt Left to Sync.." + "\n \n" + "Its not Safe to Logout"});

                                        MDDialog dialog = adb.create();
                                        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                        dialog.show();

                                    } else {
                                        Toast.makeText(DashBoardActivity.this, "Sorry.. You can not Logout in offline mode.!!", Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    if (myDB.ReceiptCount() > 0) {

                                        //Toast.makeText(DashBoardActivity.this, "Logout", Toast.LENGTH_SHORT).show();

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
                                        adb.setMessages(new CharSequence[]{"\n You have " + myDB.ReceiptCount() + " Payment Receipt Left to Sync.." + "\n \n" + "Are you sure want to Logout?"});

                                        MDDialog dialog = adb.create();
                                        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                        dialog.show();

                                    } else {


                                        URL = siteurl + "/UpdateAndroidDeviceId";
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

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        toolbar.setTitleTextColor(Color.WHITE);

        String s1 = pref.getString("SiteURL", "").toString();
        String s2 = pref.getString("Contracotrid", "").toString();
        String s3 = pref.getString("LoginStatus", "").toString();
        String s4 = pref.getString("LoginName", "").toString();

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(s4);
        tvUserName.setTypeface(Typeface.createFromAsset(this.getAssets(), "font/pacifico.ttf"));

    }

    public void loadEntities() {
        LayoutInflater li = getLayoutInflater();
        View vd = li.inflate(R.layout.entitylist_checkbox, null);

        final ListView lv = new ListView(DashBoardActivity.this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setDividerHeight(0);

        final ArrayAdapter<String> da = new ArrayAdapter<String>(DashBoardActivity.this, android.R.layout.simple_list_item_multiple_choice, enamelist);
        lv.setAdapter(da);

        if (sb.length() > 0) {
            String[] animalsArray = sb.toString().split(",");

            for (int i = 0; i < animalsArray.length; i++) {
                if (animalsArray[i].equals("All Entities")) {
                    for (int j = 0; j < lv.getCount(); j++) {
                        lv.setItemChecked(j, true);
                    }
                } else {
                    lv.setItemChecked(da.getPosition(animalsArray[i]), true);
                }

            }
            sb.setLength(0);

        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (lv.getItemAtPosition(position).equals("All Entities")) {
                    if (lv.isItemChecked(position)) {
                        for (int i = 1; i < lv.getCount(); i++) {
                            lv.setItemChecked(i, true);
                        }
                    } else {
                        for (int i = 1; i < lv.getCount(); i++) {
                            lv.setItemChecked(i, false);
                        }
                    }
                } else {
                    if (lv.isItemChecked(0)) {
                        lv.setItemChecked(0, false);
                    }
                }
            }
        });

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(DashBoardActivity.this);
        builderDialog.setView(lv);
        builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START, true);
                }

                if (!isOffline) {

                    String result = "";
                    SparseBooleanArray checked = lv.getCheckedItemPositions();

                    if (checked.size() == 0) {
                        Toast.makeText(DashBoardActivity.this, "Please Select Atleast one Entity...", Toast.LENGTH_LONG).show();
                    } else {

                        for (int i = 0; i < checked.size(); i++) {
                            int position = checked.keyAt(i);

                            if (checked.valueAt(i) && lv.isItemChecked(position)) {
                                if (da.getItem(position).equals("All Entities")) {
                                    //((TextView) findViewById(R.id.tvTitle)).setText(da.getItem(position));
                                    sb.append("All Entities,");
                                    break;
                                } else {
                                    if (sb.length() > 0)
                                        sb.append(",");
                                    sb.append(da.getItem(position));
                                }

                            }
                        }

                        if (!sb.toString().equals("All Entities,") && sb.length() > 0) {
                            //((TextView) findViewById(R.id.tvTitle)).setText(sb.toString());

                            sb1.setLength(0);

                            if (sb.length() > 0) {
                                String[] animalsArray = sb.toString().split(",");
                                for (int i = 0; i < animalsArray.length; i++) {
                                    if (sb1.length() > 0)
                                        sb1.append(",");
                                    sb1.append(eidlist.get(da.getPosition(animalsArray[i]) - 1));
                                }
                            }

                            URL = siteurl + "/GetDashbordHomeForNewCollectionApp?contractorId=" + cid + "&loginuserId=" + uid + "&entityIds=" + sb1.toString();

                            //bundle.putString("entity", sb1.toString());
                            bundle.putString("url", URL);

                            //SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("Entityids");
                            editor.putString("Entityids", sb1.toString());
                            editor.commit();

                            HomeFragment hf = new HomeFragment();
                            hf.setArguments(bundle);

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.detach(hf);
                            ft.attach(hf);
                            ft.replace(R.id.containerView, new TabFragment());
                            ft.commit();
                        } else {
                            sb1.setLength(0);

                            for (int i = 0; i < eidlist.size(); i++) {
                                if (sb1.length() > 0)
                                    sb1.append(",");
                                sb1.append(eidlist.get(i));
                            }

                            URL = siteurl + "/GetDashbordHomeForNewCollectionApp?contractorId=" + cid + "&loginuserId=" + uid + "&entityIds=" + sb1.toString();

                            bundle.putString("url", URL);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("Entityids");
                            editor.putString("Entityids", sb1.toString());
                            editor.commit();

                            HomeFragment hf = new HomeFragment();
                            hf.setArguments(bundle);

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.detach(hf);
                            ft.attach(hf);
                            ft.replace(R.id.containerView, new TabFragment());
                            ft.commit();
                        }


                    }
                } else {
                    Toast.makeText(DashBoardActivity.this, "Sorry.. You are Offline..!! ", Toast.LENGTH_LONG).show();
                }
            }
        });
        builderDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alert = builderDialog.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alert.show();
    }

    public void checkAllPermissions() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        //int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        ///int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);


        if (result == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(DashBoardActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else if (result == PackageManager.PERMISSION_GRANTED) {
            Intent service = new Intent(DashBoardActivity.this, TestLocationService.class);
            startService(service);
        }

            /*if( result1 == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(DashBoardActivity.this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }*/

           /* if(result2 == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(DashBoardActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }

            if(result3 == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(DashBoardActivity.this,
                        new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }

            if(result4 == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(DashBoardActivity.this,
                        new String[]{ Manifest.permission.CALL_PHONE},
                        1);
            }*/

            /*if(result == PackageManager.PERMISSION_DENIED || result1 == PackageManager.PERMISSION_DENIED
                    || result2 == PackageManager.PERMISSION_DENIED || result3 == PackageManager.PERMISSION_DENIED
                    || result4 == PackageManager.PERMISSION_DENIED)
            {


            }*/
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

        if (hasFocus)
            hideSoftKeyboard(DashBoardActivity.this);

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    boolean doubleBackToExitPressedOnce = false;

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

        Rcount = rcount;
        Acount = acount;
        Ccount = ccount;
        Comcount = comcount;
        CustComCount = custcomcount;

        //createCartBadge(Rcount);
        // createCartBadge_alert(Acount);
        //createCartBadge_complaint(Ccount);

        // createCartBadge_customer_comment_count(CustComCount);

        //  createCartBadge_complaint_comment(Comcount);

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu paramMenu) {

        mToolbarMenu = paramMenu;
        //  createCartBadge(Rcount);
        // createCartBadge_alert(Acount);
        //createCartBadge_complaint(Ccount);

        // createCartBadge_customer_comment_count(CustComCount);

        return super.onPrepareOptionsMenu(paramMenu);

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
                    Toast.makeText(DashBoardActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try {
                    jobj = new JSONObject(json);

                } catch (JSONException e) {
                    Toast.makeText(DashBoardActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(DashBoardActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(DashBoardActivity.this, "**" + e, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(DashBoardActivity.this, "SomeThing Went Wrong.. Try Again!", Toast.LENGTH_SHORT).show();
        }
        return jobj;
    }


    public void syncReceipts() {
        Cursor c = myDB.getReceipts();

        if (c != null && c.getCount() > 0) {

            Toast.makeText(getApplicationContext(), "r=" + c.getCount(), Toast.LENGTH_SHORT).show();

            //c.moveToFirst();

                /*if(!c.isClosed())
                {
                    c.close();
                }*/


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

                    //URL = siteurl + "/withdiscount";

                    URL = siteurl + "/withdiscountAndReceiptNo";

                    CallVolley(URL, map, rid);


                } while (c.moveToNext());

            }
        }
    }

    public void CallVolleyUpdateDeviceID(String a) {

        final Dialog spload = Utils.getLoader(this);
        //spload.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(RegistrationActivity.this,response,Toast.LENGTH_LONG).show();
                        try {

                            if (spload.isShowing()) {
                                spload.dismiss();
                            }

                            JSONObject response1 = new JSONObject(response);

                            if (response1.getString("status").equalsIgnoreCase("True")) {

                                //Toast.makeText(Dashboard.this, response1.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear().apply();

                                myDB.ClearAllData();

                                stopService(new Intent(getApplicationContext(), TestLocationService.class));
                                Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (spload.isShowing()) {
                            spload.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "Something went Wrong.. Please Try again..", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "User");
                params.put("id", uid);
                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    public void CallVolley(String a, HashMap<String, String> map1, final String rid) {

        try {


            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map1),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {


                                if (response.getString("status").toString().equals("True")) {
                                        /*String toa = response.getString("TotalOutStandingAmount");

                                        Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
                                        i.putExtra("from", from);
                                        i.putExtra("Oa", toa);
                                        i.putExtra("title",areatitle);
                                        startActivity(i);

                                        finish();*/

                                    Toast.makeText(getApplicationContext(), "Receipt Done.!!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_SHORT).show();


                                    if (myDB.UpdateReceiptStatus(rid)) {
                                        Toast.makeText(getApplicationContext(), "Status Done.!!", Toast.LENGTH_SHORT).show();
                                    }


                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a) {
        JsonObjectRequest obreqs;

        final Dialog spload = Utils.getLoader(this);
        //spload.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", uid);

        obreqs = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (spload.isShowing()) {
                                spload.dismiss();
                            }

                            enamelist.clear();

                            if (response.getString("status").toString().equals("True")) {

                                enamelist.add("All Entities");
                                final JSONArray entityarray = response.getJSONArray("EntityInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    String eid = e.getString("EntityId");
                                    String ename = e.getString("EntityName");

                                    enamelist.add(ename);
                                    eidlist.add(eid);
                                }
                                for (int i = 0; i < eidlist.size(); i++) {
                                    if (sb1.length() > 0)
                                        sb1.append(",");
                                    sb1.append(eidlist.get(i));
                                }

                            } else {
                                Toast.makeText(DashBoardActivity.this, "Something Went Wrong... No data...", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (spload.isShowing()) {
                            spload.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }

    public static class TabFragment extends Fragment {

        final int[] ICONS = new int[]{R.drawable.ic_home_white_24dp, R.drawable.payment, R.drawable.collectiontab, R.drawable.badge_icon_complaincomment_count, R.drawable.ic_person_white_24dp};

        public TabFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            /**
             *Inflate tab_layout and setup Views.
             */
            View x = inflater.inflate(R.layout.tablayout, null);
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (NonSwipeableViewPager) x.findViewById(R.id.viewpager);


            /**
             *Set an Apater for the View Pager
             */
            viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
            viewPager.setOffscreenPageLimit(new MyAdapter(getChildFragmentManager()).getCount() - 1);

            if (isOffline) {
                viewPager.setCurrentItem(1);
            }

            viewPager.setPagingEnabled(false);

            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                    tabLayout.getTabAt(0).setIcon(ICONS[0]);
                    tabLayout.getTabAt(1).setIcon(ICONS[1]);
                    tabLayout.getTabAt(2).setIcon(ICONS[2]);
                    tabLayout.getTabAt(3).setIcon(ICONS[3]);
                    tabLayout.getTabAt(4).setIcon(ICONS[4]);

                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                            if (tab.getPosition() == 0) {
                                viewPager.setCurrentItem(tab.getPosition());
                            } else if (tab.getPosition() == 1) {

                                viewPager.setCurrentItem(tab.getPosition());

                            } else if (tab.getPosition() == 2) {

                                viewPager.setCurrentItem(tab.getPosition());

                            } else if (tab.getPosition() == 3) {

                                viewPager.setCurrentItem(tab.getPosition());

                            } else if (tab.getPosition() == 4) {

                                viewPager.setCurrentItem(tab.getPosition());

                            }

                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {

                            viewPager.setCurrentItem(position);


                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            });

            return x;
        }


        class MyAdapter extends FragmentStatePagerAdapter {
            Bundle bundle;

            public MyAdapter(FragmentManager fm) {
                super(fm);
            }

            /**
             * Return fragment with respect to Position .
             */

            @Override
            public Fragment getItem(int position) {

                boolean isConnected = ConnectivityReceiver.isConnected();

                switch (position) {
                    case 0:

                        bundle = new Bundle();
                        SharedPreferences pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                        HomeFragment hf = new HomeFragment();

                        if (isConnected) {

                            URL = siteurl + "/GetDashbordHomeForNewCollectionApp?contractorId=" + cid + "&loginuserId=" + uid + "&entityIds=" + sb1.toString();

                            //bundle.putString("entity", sb1.toString());
                            bundle.putString("url", URL);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Entityids", sb1.toString());
                            editor.commit();

                            hf.setArguments(bundle);
                            return hf;
                        } else {

                            bundle.putString("url", "-");
                            hf.setArguments(bundle);

                            return hf;
                        }

                    case 1:

                        PaymentFragment pf = new PaymentFragment();
                        if (isConnected) {

                            bundle = new Bundle();
                            pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                            //URL=siteurl+"/GetAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString();
                            URL = siteurl + "/GetAreaByUserForCollectionApp";
                            bundle.putString("url", URL);

                            pf.setArguments(bundle);
                            return pf;
                        } else {

                            bundle = new Bundle();
                            bundle.putString("url", "-");
                            pf.setArguments(bundle);

                            return pf;
                        }

                    case 2:

                        bundle = new Bundle();
                        pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                        CollectionFragment cf = new CollectionFragment();

                        if (isConnected) {

                            //?contractorId="+cid+"&loginuserId="+uid+"&entityId="+pref.getString("Entityids","").toString();
                            // URL=siteurl+"/GetUserlistforcollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityId="+pref.getString("Entityids","").toString();;
                            URL = siteurl + "/GetUserlistforcollectionApp";
                            bundle.putString("url", URL);


                            cf.setArguments(bundle);
                            return cf;
                        } else {
                            bundle.putString("url", "-");
                            cf.setArguments(bundle);

                            return cf;
                        }
                    case 3:

                        bundle = new Bundle();
                        pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                        ComplainFragment cmpf = new ComplainFragment();

                        if (isConnected) {

                            //URL=siteurl+"/GetComplainListByAreaForCollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityIds="+pref.getString("Entityids","").toString();
                            URL = siteurl + "/GetComplainListByAreaForCollectionApp";
                            bundle.putString("url", URL);

                            cmpf.setArguments(bundle);
                            return cmpf;
                        } else {
                            bundle.putString("url", "-");
                            cmpf.setArguments(bundle);

                            return cmpf;
                        }
                    case 4:

                        bundle = new Bundle();
                        CustomerFragment cuf = new CustomerFragment();

                        if (isConnected) {

                            //URL=siteurl+"/GetCustomerDashbordHomeForNewCollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityIds="+pref.getString("Entityids","").toString();
                            URL = siteurl + "/GetCustomerDashbordHomeForNewCollectionApp";
                            bundle.putString("url", URL);

                            cuf.setArguments(bundle);
                            return cuf;
                        } else {
                            bundle.putString("url", "-");
                            cuf.setArguments(bundle);

                            return cuf;
                        }
                }
                return null;
            }

            @Override
            public int getCount() {

                return int_items;

            }

            @Override
            public Parcelable saveState() {
                return null;
            }

            @Override
            public int getItemPosition(Object object) {
                // POSITION_NONE makes it possible to reload the PagerAdapter
                return POSITION_NONE;
            }

            /**
             * This method returns the title of the tab according to the position.
             */

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position) {
                    case 0:
                        return "Home";
                    case 1:
                        return "Payment";
                    case 2:
                        return "Collection";
                    case 3:
                        return "Complaint";
                    case 4:
                        return "Customers";

                }
                return null;
            }
        }
    }

    public class MenuHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public MySquareImage imageView;

        public MenuHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_menu);
            imageView = (MySquareImage) view.findViewById(R.id.iv_menu);
        }
    }

}
