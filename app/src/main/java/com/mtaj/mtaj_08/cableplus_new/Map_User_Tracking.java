package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class Map_User_Tracking extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    MapView mMapView;
    private GoogleMap mMap;
    TextView tvselectuser;

    ArrayList<String> useridlist=new ArrayList<>();
    ArrayList<String> usernamelist=new ArrayList<>();

    String siteurl,uid,cid,aid,eid,URL;

    SharedPreferences pref;

    RequestQueue requestQueue;

    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map__user__tracking);

        pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        requestQueue= Volley.newRequestQueue(this);
        geocoder = new Geocoder(this, Locale.getDefault());


        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid", "").toString();


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Track User");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        if(!checkPlayServices())
        {
            finish();
        }


        View rootView = getWindow().getDecorView().getRootView();

        Snackbar.make(rootView,"Please Select User to Track Location...",Snackbar.LENGTH_LONG).show();

        tvselectuser=(TextView)findViewById(R.id.txtselect);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        tvselectuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                useridlist.clear();
                usernamelist.clear();

                URL=siteurl+"/GetUserlistfornewcollectionApp";

                CallVolleyUserlist(URL);
            }
        });

       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setTrafficEnabled(true);
                mMap.setIndoorEnabled(true);

                // For showing a move to my location button
                //mMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng guj = new LatLng(22.2587, 71.1924);
                mMap.addMarker(new MarkerOptions().position(guj).title("Gujarat"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(guj).zoom(9).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    public void CallVolleyUserlist(String a)
    {

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorid",cid);

        final SpotsDialog spload;
        spload=new SpotsDialog(Map_User_Tracking.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();



        try {
            //jsonobj=makeHttpRequest(params[0]);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final JSONArray entityarray = response.getJSONArray("lstUserInfoCollectionApp");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            useridlist.add(e.getString("UserId").toString());
                                            usernamelist.add(e.getString("Name").toString());

                                        }

                                        if(usernamelist.size()>0)
                                        {
                                            final ListView lv=new ListView(Map_User_Tracking.this);
                                            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                            lv.setDividerHeight(0);


                                            final ArrayAdapter<String> da=new ArrayAdapter<String>(Map_User_Tracking.this,android.R.layout.simple_list_item_single_choice,usernamelist);
                                            lv.setAdapter(da);

                                            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(Map_User_Tracking.this);
                                            builderDialog.setView(lv);
                                            builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (lv.getCheckedItemPosition()==-1)
                                                    {
                                                        Toast.makeText(Map_User_Tracking.this, "Please select atleast one User..!!", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        URL=siteurl+"/GetUserlistlocationfornewcollectionApp";

                                                        CallVolleyUserLocation(URL,useridlist.get(lv.getCheckedItemPosition()));
                                                    }
                                                }
                                            });

                                            final AlertDialog alert=builderDialog.create();
                                            alert.setTitle("Select User");
                                            alert.setCancelable(true);
                                            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                            alert.show();
                                        }
                                    }

                                    else
                                    {
                                        Toast.makeText(Map_User_Tracking.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleyUserLocation(String a,String uid)
    {

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorid",cid);
        map.put("userid",uid);

        final SpotsDialog spload;
        spload=new SpotsDialog(Map_User_Tracking.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();



        try {
            //jsonobj=makeHttpRequest(params[0]);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final JSONArray entityarray = response.getJSONArray("lstUserInfoCollectionApp");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);


                                            String uname=e.getString("Name");
                                            String lat=e.getString("Latitude");
                                            String longi=e.getString("Longitude");

                                            String locationSnippet=getAddress(Double.parseDouble(lat), Double.parseDouble(longi));

                                            //mMap.setMyLocationEnabled(true);

                                            mMap.clear();

                                            // For dropping a marker at a point on the Map
                                            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));
                                            mMap.addMarker(new MarkerOptions().position(latLng).title(uname).snippet(locationSnippet));

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

                                            // For zooming automatically to the location of the marker
                                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).build();
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        }

                                    }

                                    else
                                    {
                                        Toast.makeText(Map_User_Tracking.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }



    public String getAddress(Double lat,Double longi)
    {
        String fulladdress="";
        String address="";

        try {

            addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            StringBuilder strReturnedAddress = new StringBuilder("");

            for(int i=0;i<addresses.get(0).getMaxAddressLineIndex();i++)
            {
                strReturnedAddress.append(addresses.get(0).getAddressLine(i));
            }
            address=strReturnedAddress.toString();

            /* // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            fulladdress=address+","+city+"-"+postalCode+","+state;*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return address;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    /* @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng india = new LatLng(22.3074, 73.1726);
        mMap.addMarker(new MarkerOptions().position(india).title("MTAJ SOLUTIONS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }*/
}
