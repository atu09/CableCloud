package com.mtaj.mtaj_08.cableplus_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String str = "\u20B9";

    private static final String PREF_NAME = "LoginPref";

    private Calendar calendar;
    private int year, cmonth, day;
    EditText edtfrom, edtto;
    String tempto = "-", tempfrom = "-";

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    String siteurl, uid, cid, aid, eid, URL, name;

    RequestQueue requestQueue;
    String fromdate, todate;

    ArrayList<LatLng> latlonglist = new ArrayList<>();

    LatLng lt;

    ArrayList<HashMap<String, String>> receiptlist = new ArrayList<>();
    int m = 0;

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromdate = (cmonth + 1) + "/" + day + "/" + year;
        todate = (cmonth + 1) + "/" + day + "/" + year;


        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        cid = pref.getString("Contracotrid", "").toString();

        requestQueue = Volley.newRequestQueue(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        URL = siteurl + "/GetBillReceiptsForLongAndLatForCollectionApp";

        CallVolley(URL, fromdate, todate);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                //startActivity(i);

                onBackPressed();
            }
        });
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }


    @Override
    public void onBackPressed()
    {

        finish();
    }

    public void CallVolley(String a,String fd,String td)
    {
        final SpotsDialog spload;
        spload=new SpotsDialog(MapsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String,String> map=new HashMap<>();
            map.put("startindex","0");
            map.put("noofrecords","100000");
            map.put("contractorid",cid);
            map.put("userId",uid);
            map.put("fromdate",fd);
            map.put("todate",td);
            map.put("filter","");

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final PolylineOptions polylineOptions = new PolylineOptions();

                                        LatLng ll=null,lls=null;
                                        //Toast.makeText(MapsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                        final JSONArray entityarray = response.getJSONArray("ListBillReceipt");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            if(e.getString("longitude").equals("") || e.getString("latitude").equals("") || e.getString("longitude").equals("0.0") || e.getString("latitude").equals("0.0") )
                                            {

                                            }
                                            else {

                                                HashMap<String, String> map = new HashMap<>();

                                                map.put("Name", e.getString("Name"));
                                                map.put("Address", e.getString("Address"));
                                                map.put("MQNo",e.getString("MQNo"));
                                                map.put("Receiptdate", e.getString("Receiptdate"));
                                                map.put("PaidAmount", e.getString("PaidAmount"));
                                                map.put("longitude", e.getString("longitude"));
                                                map.put("latitude", e.getString("latitude"));

                                                receiptlist.add(map);
                                            }

                                        }

                                        if(receiptlist.size()>0) {
                                            

                                            for (int i = 0; i < receiptlist.size(); i++) {
                                                //Toast.makeText(MapsActivity.this, "name="+receiptlist.get(i).get("Name")+","+"lcation:"+receiptlist.get(i).get("latitude")+","+receiptlist.get(i).get("longitude"), Toast.LENGTH_SHORT).show();
                                                drawMarker(new LatLng(Double.parseDouble(receiptlist.get(i).get("latitude")), Double.parseDouble(receiptlist.get(i).get("longitude"))),
                                                        receiptlist.get(i).get("Name"),receiptlist.get(i).get("MQNo"), receiptlist.get(i).get("Receiptdate")+" of "+str+receiptlist.get(i).get("PaidAmount")+"\n"+receiptlist.get(i).get("Address"),i+1);
                                            }





                                            //  for (int j = 0; j < receiptlist.size()-1; j++) {

                                            // LatLng ll=new LatLng(Double.parseDouble(receiptlist.get(i).get("latitude").toString()),Double.parseDouble(receiptlist.get(i).get("longitude").toString()));
                                            // latlonglist.add(ll);

                                           /* if (receiptlist.size() >= 2) {

                                                for ( m = 0; m < receiptlist.size(); m = m + 7) {


                                                    ll = new LatLng(Double.parseDouble(receiptlist.get(m).get("latitude").toString()), Double.parseDouble(receiptlist.get(0).get("longitude").toString()));

                                                    lls = new LatLng(Double.parseDouble(receiptlist.get(m+7).get("latitude").toString()), Double.parseDouble(receiptlist.get(receiptlist.size() - 1).get("longitude").toString()));

                                                    String url = getDirectionsUrl(ll, lls,m,m+7);

                                                    String data = downloadUrl(url);

                                                    JSONObject jObject;
                                                    List<List<HashMap<String, String>>> routes = null;

                                                    try {
                                                        jObject = new JSONObject(data);
                                                        DirectionsJSONParser parser = new DirectionsJSONParser();

                                                        // Starts parsing data
                                                        routes = parser.parse(jObject);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    ArrayList<LatLng> points = null;
                                                    PolylineOptions lineOptions = null;
                                                    MarkerOptions markerOptions = new MarkerOptions();

                                                    // Traversing through all the routes
                                                    for (int i = 0; i < routes.size(); i++) {
                                                        points = new ArrayList<LatLng>();
                                                        lineOptions = new PolylineOptions();

                                                        // Fetching i-th route
                                                        List<HashMap<String, String>> path = routes.get(i);

                                                        // Fetching all the points in i-th route
                                                        for (int k = 0; k < path.size(); k++) {
                                                            HashMap<String, String> point = path.get(k);

                                                            double lat = Double.parseDouble(point.get("lat"));
                                                            double lng = Double.parseDouble(point.get("lng"));
                                                            LatLng position = new LatLng(lat, lng);

                                                            points.add(position);
                                                        }

                                                        // Adding all the points in the route to LineOptions
                                                        lineOptions.addAll(points);
                                                        lineOptions.width(5);
                                                        lineOptions.color(Color.RED);
                                                    }

                                                    // Drawing polyline in the Google Map for the i-th route
                                                    mMap.addPolyline(lineOptions);

                                                }

                                                *//*int n=7-(m-receiptlist.size());
                                                for(int p=0;p<1;p++)
                                                {
                                                    ll = new LatLng(Double.parseDouble(receiptlist.get(receiptlist.size()-n).get("latitude").toString()), Double.parseDouble(receiptlist.get(0).get("longitude").toString()));

                                                    lls = new LatLng(Double.parseDouble(receiptlist.get(receiptlist.size()-1).get("latitude").toString()), Double.parseDouble(receiptlist.get(receiptlist.size() - 1).get("longitude").toString()));

                                                    String url = getDirectionsUrl(ll, lls,receiptlist.size()-n,receiptlist.size()-1);

                                                    String data = downloadUrl(url);

                                                    JSONObject jObject;
                                                    List<List<HashMap<String, String>>> routes = null;

                                                    try {
                                                        jObject = new JSONObject(data);
                                                        DirectionsJSONParser parser = new DirectionsJSONParser();

                                                        // Starts parsing data
                                                        routes = parser.parse(jObject);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    ArrayList<LatLng> points = null;
                                                    PolylineOptions lineOptions = null;
                                                    MarkerOptions markerOptions = new MarkerOptions();

                                                    // Traversing through all the routes
                                                    for (int i = 0; i < routes.size(); i++) {
                                                        points = new ArrayList<LatLng>();
                                                        lineOptions = new PolylineOptions();

                                                        // Fetching i-th route
                                                        List<HashMap<String, String>> path = routes.get(i);

                                                        // Fetching all the points in i-th route
                                                        for (int k = 0; k < path.size(); k++) {
                                                            HashMap<String, String> point = path.get(k);

                                                            double lat = Double.parseDouble(point.get("lat"));
                                                            double lng = Double.parseDouble(point.get("lng"));
                                                            LatLng position = new LatLng(lat, lng);

                                                            points.add(position);
                                                        }

                                                        // Adding all the points in the route to LineOptions
                                                        lineOptions.addAll(points);
                                                        lineOptions.width(5);
                                                        lineOptions.color(Color.RED);
                                                    }

                                                    // Drawing polyline in the Google Map for the i-th route
                                                    mMap.addPolyline(lineOptions);
                                                }*//*
                                            }*/
                                            spload.dismiss();
                                        }
                                        
                                        else
                                        {
                                            Toast.makeText(MapsActivity.this, "No Locations to Track....", Toast.LENGTH_SHORT).show();
                                            spload.dismiss();
                                        }


                                      //  }


                                      //  polylineOptions.addAll(latlonglist);
                                      //  mMap.addPolyline(polylineOptions);

                                            /* mMap.addPolyline(new PolylineOptions()
                                                    .add(new LatLng(Double.parseDouble(receiptlist.get(2).get("latitude")),Double.parseDouble(receiptlist.get(2).get("longitude"))),
                                                            new LatLng(Double.parseDouble(receiptlist.get(3).get("latitude")), Double.parseDouble(receiptlist.get(3).get("longitude"))))
                                                    .width(5)
                                                    .color(Color.GREEN));*/



                                    }

                                    else
                                    {
                                        spload.dismiss();

                                        Toast.makeText(MapsActivity.this,response.getString("message"), Toast.LENGTH_SHORT).show();

                                    }

                                }
                                catch (JSONException e)
                                {
                                    spload.dismiss();

                                    Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();

                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                spload.dismiss();
                                //Toast.makeText(MapsActivity.this, "error--"+e, Toast.LENGTH_LONG).show();
                                Log.e("EXCEPTION", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            spload.dismiss();

                           // Toast.makeText(MapsActivity.this, "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(MapsActivity.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    private String getDirectionsUrl(LatLng origin,LatLng dest,int from,int to){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        String waypoints = "";
        for(int i=from+1;i<to;i++){
            LatLng point  = new LatLng(Double.parseDouble(receiptlist.get(i).get("latitude").toString()),Double.parseDouble(receiptlist.get(i).get("longitude").toString()));
            if(i==1)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            java.net.URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng india = new LatLng(20.5937, 78.9629);
        //mMap.addMarker(new MarkerOptions().position(india).title("India"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
    }

    private void drawMarker(LatLng point,String title,String mq,String snip,int pos){

        String displaytitle=title+" ( "+mq+" ) ";


        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        Bitmap bitmap;

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        if(pos>=10)
        {
            String text = String.valueOf(pos);
           bitmap = makeBitmap(this, text);
        }
        else
        {
            String text = String.valueOf(pos);
            bitmap = makeBitmap1(this, text);
        }


        // Adding marker on the Google Map
        mMap.addMarker(markerOptions.title(displaytitle).snippet(snip).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


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


    public Bitmap makeBitmap(Context context, String text)
    {
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

        int x = (bitmap.getWidth() - bounds.width()) / 2-5;
        int y = (bitmap.getHeight() + bounds.height()) / 2-10;

        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }

    public Bitmap makeBitmap1(Context context, String text)
    {
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
        int y = (bitmap.getHeight() + bounds.height()) / 2-10;

        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_datefilters:
                LayoutInflater li = getLayoutInflater();
                final View v = li.inflate(R.layout.dialog_date_search, null);

                edtfrom=(EditText)v.findViewById(R.id.edtfromdate);
                edtto=(EditText)v.findViewById(R.id.edttodate);

                if(!tempfrom.equals("-") && !tempto.equals("-")) {
                    edtto.setText(tempto);
                    edtfrom.setText(tempfrom);
                }
                else
                {
                    edtto.setText((cmonth + 1) + "/" + day + "/" + year);
                    edtfrom.setText((cmonth + 1) + "/" + day + "/" + year);
                }

                edtfrom.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        showDialog(999);

                        return false;
                    }
                });

                edtto.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        showDialog(888);

                        return false;
                    }
                });


                MDDialog.Builder mdalert=new MDDialog.Builder(MapsActivity.this);
                mdalert.setContentView(v);
                mdalert.setTitle("Filter By Date");
                mdalert.setPositiveButton("SEARCH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Date to=new Date(edtto.getText().toString());
                        Date from=new Date(edtfrom.getText().toString());

                        tempfrom=edtfrom.getText().toString();
                        tempto=edtto.getText().toString();

                        if(to.after(from))
                        {
                            CallVolley(URL,edtfrom.getText().toString(),edtto.getText().toString());
                        }

                        else if(to.equals(from))
                        {
                            CallVolley(URL, edtfrom.getText().toString(), edtto.getText().toString());
                        }
                        else
                        {
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

                MDDialog dialog=mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
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
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtfrom.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtto.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };
}
