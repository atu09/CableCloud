package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

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
import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class AddCustomer_2 extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    FloatingActionButton fabadd;

    TextView tvnext,tvcancel;

    ListView lvpackage;

    ArrayList<HashMap<String,String>> packagelist=new ArrayList<>();
    SimpleAdapter da;

    String siteurl,uid,cid,aid,eid,URL,custid;

    ArrayList<String> packagenames=new ArrayList<String>();
    ArrayList<String> packageids=new ArrayList<String>();

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    JSONArray jsonArray = new JSONArray();
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_2);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();

        URL=siteurl+"/GetpackagelistforCollectionApp?contractorId="+cid;

        Intent j=getIntent();

        custid=j.getExtras().getString("CustomerId");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Package Details");
        toolbar.setTitleTextColor(Color.WHITE);
       // toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getApplicationContext(), AddCustomer_1.class);
               // startActivity(i);

                onBackPressed();

            }
        });*/

        fabadd=(FloatingActionButton)findViewById(R.id.fab);
        tvnext=(TextView)findViewById(R.id.textView30);
        tvcancel=(TextView)findViewById(R.id.textView28);
        lvpackage=(ListView)findViewById(R.id.listView5);

        try {
            jsonobj = makeHttpRequest(URL);

            if (jsonobj.getString("status").equals("True"))
            {
                packagenames.add(" ---- Select Package ----- ");

                final JSONArray entityarray = jsonobj.getJSONArray("PackageInfoList");
                for (int i = 0; i < entityarray.length(); i++) {

                    JSONObject e = (JSONObject) entityarray.get(i);

                    packageids.add(e.getString("packageId"));
                    packagenames.add(e.getString("packagename"));
                }

            }
        }
        catch (JSONException ex)
        {
            Toast.makeText(AddCustomer_2.this, "Error=="+ex, Toast.LENGTH_SHORT).show();
        }

        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li= getLayoutInflater();

                View vs=li.inflate(R.layout.layout_add_customer_package, null);

                final Spinner sppackage=(Spinner)vs.findViewById(R.id.spinner3);
                final EditText edtdno=(EditText)vs.findViewById(R.id.editText20);
                final EditText edtmq=(EditText)vs.findViewById(R.id.editText21);
                final CheckBox swbill=(CheckBox)vs.findViewById(R.id.checkBox);

                swbill.setVisibility(View.GONE);


                ArrayAdapter<String> da1=new ArrayAdapter<String>(AddCustomer_2.this,android.R.layout.simple_spinner_dropdown_item,packagenames);
                sppackage.setAdapter(da1);


                MDDialog.Builder mdalert=new MDDialog.Builder(AddCustomer_2.this);
                mdalert.setContentView(vs);
                mdalert.setTitle("Package Details");
                mdalert.setPositiveButton("ADD", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(sppackage.getSelectedItemPosition()==0)
                        {
                            Toast.makeText(AddCustomer_2.this, "Select Valid Package..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            HashMap<String,String> map=new HashMap<String, String>();

                            map.put("deviceno",edtdno.getText().toString());
                            map.put("mqno",edtmq.getText().toString());
                            map.put("pname", sppackage.getSelectedItem().toString());
                            map.put("pid",packageids.get(sppackage.getSelectedItemPosition()-1));

                            packagelist.add(map);

                            da=new SimpleAdapter(AddCustomer_2.this,packagelist,R.layout.layout_customer_package_list,new String[]{"deviceno","mqno","pname"},new int[]{R.id.textView34,R.id.textView36,R.id.textView31});
                            lvpackage.setAdapter(da);
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


            }
        });

        registerForContextMenu(lvpackage);

        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(packagelist.size()>0) {

                    for (int i = 0; i < packagelist.size(); i++) {
                        try {
                            JSONObject packagedetails = new JSONObject();
                            packagedetails.put("deviceno", packagelist.get(i).get("deviceno"));
                            packagedetails.put("mqno", packagelist.get(i).get("mqno"));
                            packagedetails.put("pid", packagelist.get(i).get("pid"));

                            jsonArray.put(packagedetails);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(AddCustomer_2.this, "JSOn++" + e, Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Toast.makeText(AddCustomer_2.this, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                    //URL=siteurl+"/GetpackagelistforcustomerCollectionApp?contractorId="+cid+"&loginuserId="+uid+"&customerId="+custid+"&lstpkgs="+jsonArray.toString();

                    URL = siteurl + "/GetpackagelistforcustomerCollectionApp";

                    CallVolley(URL);
                }
                else
                {
                    Snackbar.make(v,"No Packages To Add...",Snackbar.LENGTH_LONG).show();
                }

                //new  JSONAsynk().execute(new String[]{URL});
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();


            }
        });
    }

    public void CallVolley(String a)
    {
        final SpotsDialog spload;
        spload=new SpotsDialog(AddCustomer_2.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String,String> map=new HashMap<>();
            map.put("contractorId",cid);
            map.put("loginuserId",uid);
            map.put("customerId",custid);
            map.put("lstpkgs",jsonArray.toString());

            ///Log.e("MAP:",map.toString());

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
                                        Toast.makeText(AddCustomer_2.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(AddCustomer_2.this, AddCustomer_3.class);
                                        i.putExtra("CustomerId",custid);
                                        startActivity(i);

                                        finish();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(AddCustomer_2.this, "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(AddCustomer_2.this, "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(AddCustomer_2.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed()
    {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_skip, menu);

        menu.findItem(R.id.action_skip).setTitle(Html.fromHtml("<font color='#ffffff'>Skip</font>"));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_skip) {

            Intent i = new Intent(AddCustomer_2.this, AddCustomer_3.class);
            i.putExtra("CustomerId",custid);
            startActivity(i);

            finish();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listview_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:

                //Toast.makeText(AddCustomer_2.this, lvpackage.getItemAtPosition(info.position).toString(), Toast.LENGTH_SHORT).show();
                packagelist.remove(info.position);
                da.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public JSONObject makeHttpRequest(String url){
        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 50000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpGet httppost=new HttpGet(url);
        try{
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        try{



            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            try{
                if(reader!=null) {

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try{
                    jobj = new JSONObject(json);

                    // JSONArray jarrays=new JSONArray(json);

                    // jobj=jarrays.getJSONObject(0);

                    //  org.json.simple.parser.JSONParser jsonparse=new org.json.simple.parser.JSONParser();

                    // jarr =(JSONArray)jsonparse.parse(json);
                    // jobj = jarr.getJSONObject(0);
                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
            }
        }catch (UnsupportedEncodingException e){
            Toast.makeText(getApplicationContext(), "**"+e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }


    private class JSONAsynk extends AsyncTask<String,String,JSONObject>
    {

        private ProgressDialog pDialog;
        //public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1,jsn,jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(AddCustomer_2.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {


                jsonobj=makeHttpRequest(params[0]);

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try
            {
                if(json.getString("status").toString().equals("True"))
                {



                }

            }
            catch (JSONException e)
            {
                Toast.makeText(AddCustomer_2.this, "Error:++"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(AddCustomer_2.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }

}
