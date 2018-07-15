package com.cable.cloud.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.R;
import com.cable.cloud.helpers.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String str = "\u20B9";

    private static final String PREF_NAME = "LoginPref";

    private Calendar calendar;
    private int year, cmonth, day;
    EditText edtFrom, edtTo;
    String tempTo = "-", tempFrom = "-";

    String siteUrl, uid, cid, aid, eid, URL, name;

    RequestQueue requestQueue;
    String fromDate, toDate;

    ArrayList<HashMap<String, String>> receiptList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent j = getIntent();
        uid = j.getExtras().getString("UserId");
        String uname = j.getExtras().getString("UserName");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(uname);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromDate = (cmonth + 1) + "/" + day + "/" + year;
        toDate = (cmonth + 1) + "/" + day + "/" + year;


        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "");
        cid = pref.getString("Contracotrid", "");
        URL = siteUrl + "/GetBillReceiptsForLongAndLatForCollectionApp";

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CallVolley(URL, fromDate, toDate);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void CallVolley(String a, String fd, String td) {

        final Dialog loader = Utils.getLoader(this);
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("startindex", "0");
        map.put("noofrecords", "20");
        map.put("contractorid", cid);
        map.put("userId", uid);
        map.put("fromdate", fd);
        map.put("todate", td);
        map.put("filter", "");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                final JSONArray entityArray = response.getJSONArray("ListBillReceipt");

                                for (int i = 0; i < entityArray.length(); i++) {
                                    JSONObject e = (JSONObject) entityArray.get(i);

                                    if (e.getString("longitude").equals("") || e.getString("latitude").equals("") || e.getString("longitude").equals("0.0") || e.getString("latitude").equals("0.0")) {

                                    } else {

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("Name", e.getString("Name"));
                                        map.put("Address", e.getString("Address"));
                                        map.put("MQNo", e.getString("MQNo"));
                                        map.put("Receiptdate", e.getString("Receiptdate"));
                                        map.put("PaidAmount", e.getString("PaidAmount"));
                                        map.put("longitude", e.getString("longitude"));
                                        map.put("latitude", e.getString("latitude"));

                                        receiptList.add(map);
                                    }

                                }

                                if (receiptList.size() > 0) {
                                    for (int i = 0; i < receiptList.size(); i++) {
                                        drawMarker(new LatLng(Double.parseDouble(receiptList.get(i).get("latitude")), Double.parseDouble(receiptList.get(i).get("longitude"))),
                                                receiptList.get(i).get("Name"), receiptList.get(i).get("MQNo"), receiptList.get(i).get("Receiptdate") + " of " + str + receiptList.get(i).get("PaidAmount") + "\n" + receiptList.get(i).get("Address"), i + 1);
                                    }

                                } else {
                                    Toast.makeText(MapsActivity.this, "No Locations to Track....", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MapsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(20);
        mMap.setMinZoomPreference(12);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

        LatLng india = new LatLng(20.5937, 78.9629);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    private void drawMarker(LatLng point, String title, String mq, String snip, int pos) {

        String displayTitle = title + " ( " + mq + " ) ";

        MarkerOptions markerOptions = new MarkerOptions();
        Bitmap bitmap;
        markerOptions.position(point);
        if (pos >= 10) {
            String text = String.valueOf(pos);
            bitmap = makeBitmapCenter(this, text);
        } else {
            String text = String.valueOf(pos);
            bitmap = makeBitmapLeft(this, text);
        }

        mMap.addMarker(markerOptions.title(displayTitle).snippet(snip).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection, menu);
        return true;
    }

    public Bitmap makeBitmapCenter(Context context, String text) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.markericon);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(10 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

       /* int x = bitmap.getWidth() - 50; // 10 for padding from right
        int y = bitmap.getHeight()-40;*/

        int x = (bitmap.getWidth() - bounds.width()) / 2 - 5;
        int y = (bitmap.getHeight() + bounds.height()) / 2 - 10;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    public Bitmap makeBitmapLeft(Context context, String text) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.markericon);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(10 * scale);// Text size
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

       /* int x = bitmap.getWidth() - 45; // 10 for padding from right
        int y =  bitmap.getHeight()-40;*/

        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2 - 10;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_datefilters:
                LayoutInflater li = getLayoutInflater();
                final View v = li.inflate(R.layout.dialog_date_search, null);

                edtFrom = (EditText) v.findViewById(R.id.edtfromdate);
                edtTo = (EditText) v.findViewById(R.id.edttodate);

                if (!tempFrom.equals("-") && !tempTo.equals("-")) {
                    edtTo.setText(tempTo);
                    edtFrom.setText(tempFrom);
                } else {
                    edtTo.setText((cmonth + 1) + "/" + day + "/" + year);
                    edtFrom.setText((cmonth + 1) + "/" + day + "/" + year);
                }

                edtFrom.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        showDialog(999);
                        return false;
                    }
                });

                edtTo.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        showDialog(888);
                        return false;
                    }
                });


                MDDialog.Builder mdalert = new MDDialog.Builder(MapsActivity.this);
                mdalert.setContentView(v);
                mdalert.setTitle("Filter By Date");
                mdalert.setPositiveButton("SEARCH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Date to = new Date(edtTo.getText().toString());
                        Date from = new Date(edtFrom.getText().toString());

                        tempFrom = edtFrom.getText().toString();
                        tempTo = edtTo.getText().toString();

                        if (to.after(from)) {
                            CallVolley(URL, edtFrom.getText().toString(), edtTo.getText().toString());
                        } else if (to.equals(from)) {
                            CallVolley(URL, edtFrom.getText().toString(), edtTo.getText().toString());
                        } else {
                            Toast.makeText(MapsActivity.this, "Enter Valid Filter Dates..", Toast.LENGTH_SHORT).show();
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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, cmonth, day);
        } else if (id == 888) {
            return new DatePickerDialog(this, myDateListeners, year, cmonth, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            edtFrom.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            edtTo.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);
        }
    };
}
