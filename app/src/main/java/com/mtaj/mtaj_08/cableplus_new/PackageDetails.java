package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class PackageDetails extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    ListView lvpackage;

    ImageView imdno,immqno;

    EditText edtdno,edtmqno;

    ArrayList<HashMap<String,String>> packagelist=new ArrayList<>();

    ArrayList<HashMap<String,String>> packagedetails=new ArrayList<>();

    String siteurl,uid,cid,aid,eid,URL,custid,acno;
    String title;

    RequestQueue requestQueue;

    SimpleAdapter da;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    JSONArray jsonArray = new JSONArray();

    ArrayList<String> packagenames=new ArrayList<String>();
    ArrayList<String> packageids=new ArrayList<String>();

    boolean isGenerateBill=false;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();

        requestQueue = Volley.newRequestQueue(this);

        lvpackage=(ListView)findViewById(R.id.listView4);

        edtdno=(EditText)findViewById(R.id.editText4);
        edtmqno=(EditText)findViewById(R.id.editText5);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        packagedetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("packagedetails");
        custid=j.getExtras().getString("CustomerId");
        acno=j.getExtras().getString("A/cNo");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                //startActivity(i);

                onBackPressed();

            }
        });



        da=new SimpleAdapter(PackageDetails.this,packagedetails,R.layout.layout_package_details,new String[]{"PkgName","Price","DeviceNo","SmartCardNo"},new int[]{R.id.textView31,R.id.textView32,R.id.textView34,R.id.textView36});
        lvpackage.setAdapter(da);


        lvpackage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                URL=siteurl+"/GetpackagelistforCollectionApp";

                HashMap<String,String> map=new HashMap<String, String>();
                map.put("contractorId",cid);

                CallVolleysGetPackage(URL,map,position);

                return false;
            }
        });


        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(PackageDetails.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);
        }


    }



    public void CallVolleys(String a,HashMap<String,String> map)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(PackageDetails.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        //jsonobj=makeHttpRequest(params[0]);
        //  URL=siteurl+"/GetAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString();



        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {



                            if (response.getString("status").toString().equals("True")) {

                                packagedetails.clear();

                                Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                final JSONArray entityarray1 = response.getJSONArray("lstPackages");
                                loadpackagedetails(entityarray1);

                                da.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                           /* } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        spload.dismiss();

                        Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }

    public void loadpackagedetails(JSONArray a)
    {
        String str = "\u20B9";

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgId",e.getString("PkgId"));
                    map.put("PkgName", e.getString("PkgName"));
                    map.put("Price",str+format.format(Double.parseDouble(e.getString("Price"))));
                    map.put("DeviceNo", e.getString("DeviceNo"));
                    map.put("MQNo", e.getString("MQNo"));
                    map.put("Ptype", e.getString("Ptype"));
                    map.put("SmartCardNo", e.getString("SmartCardNo"));

                    packagedetails.add(map);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onBackPressed()
    {

        finish();
    }

    public void CallVolleysGetPackage(String a,HashMap<String,String> map,final int position)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(PackageDetails.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();


        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {

                            if (response.getString("status").equals("True"))
                            {
                                packagenames.add(" ---- Select Package ----- ");

                                final JSONArray entityarray = response.getJSONArray("PackageInfoList");
                                for (int i = 0; i < entityarray.length(); i++) {

                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    packageids.add(e.getString("packageId"));
                                    packagenames.add(e.getString("packagename"));
                                }



                                LayoutInflater li = getLayoutInflater();

                                View vs = li.inflate(R.layout.layout_add_customer_package, null);

                                final Spinner sppackage = (Spinner) vs.findViewById(R.id.spinner3);
                                final EditText edtdno = (EditText) vs.findViewById(R.id.editText20);
                                final EditText edtmq = (EditText) vs.findViewById(R.id.editText21);

                                final CheckBox swbill=(CheckBox)vs.findViewById(R.id.checkBox);

                                //sppackage.setVisibility(View.GONE);
                                //edtmq.setVisibility(View.GONE);

                                swbill.setVisibility(View.GONE);

                                edtdno.setText(packagedetails.get(position).get("DeviceNo"));
                                edtmq.setText(packagedetails.get(position).get("SmartCardNo"));

                                ArrayAdapter<String> da1 = new ArrayAdapter<String>(PackageDetails.this, android.R.layout.simple_spinner_dropdown_item, packagenames);
                                sppackage.setAdapter(da1);


                                MDDialog.Builder mdalert = new MDDialog.Builder(PackageDetails.this);
                                mdalert.setContentView(vs);
                                mdalert.setTitle("Edit Package Details");
                                mdalert.setPositiveButton("DONE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (edtdno.getText().toString() == null || edtdno.getText().toString().length() == 0) {
                                            Toast.makeText(PackageDetails.this, "Enter Valid DeviceNo..", Toast.LENGTH_SHORT).show();
                                        } else {
                                            URL = siteurl + "/UpdatePackageDetailsForCollectionAppIdr";

                                            HashMap<String, String> map = new HashMap<String, String>();
                                            map.put("contractorId", cid);
                                            map.put("loginuserid", uid);
                                            map.put("custId", custid);
                                            map.put("custpkgid", packagedetails.get(position).get("PkgId"));
                                            map.put("deviceNo", edtdno.getText().toString());
                                            map.put("smartcardno",edtmq.getText().toString());
                                            map.put("pkgId",packageids.get(sppackage.getSelectedItemPosition()-1));
                                            map.put("Isgenertabill",String.valueOf(isGenerateBill));

                                            Log.e("MAP:",map.toString());

                                            CallVolleys(URL, map);
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


                                if (packagedetails.get(position).get("Ptype").equals("0")) {
                                    MDDialog dialog = mdalert.create();
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                    dialog.show();
                                }

                               /* swbill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                        if(isChecked)
                                        {
                                            isGenerateBill=isChecked;
                                        }
                                        else
                                        {
                                            isGenerateBill=isChecked;
                                        }
                                    }
                                });*/

                            }
                        }
                        catch (JSONException ex)
                        {
                            Log.e("JSONERROR:",ex.toString());

                            //Toast.makeText(PackageDetails.this, "Error=="+ex, Toast.LENGTH_SHORT).show();
                        }

                        catch (Exception e) {

                            Log.e("ERROR:",e.toString());

                            //Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_LONG).show();
                        }

                           /* } */
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        spload.dismiss();

                        Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }
}
