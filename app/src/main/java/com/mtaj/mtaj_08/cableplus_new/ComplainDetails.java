package com.mtaj.mtaj_08.cableplus_new;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

import dmax.dialog.SpotsDialog;

public class ComplainDetails extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    TextView tvviewcomments;

    String title,custid,cmpid;

    ImageView imdropdown,imphone;

    RelativeLayout rlmain;



    LinearLayout llcdetails,llphoto,llmarkresolve;

    String siteurl,uid,cid,aid,eid,URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    ArrayList<HashMap<String,String>> Complaindetails=new ArrayList<>();

    SimpleAdapter da;

    TextView tvacno,tvmqno,tvcid,tvcsub,tvdesc,tvphone,tvemail,tvaddress,tvcount;
            LinearLayout llcount;

    TextView tvresolve;

    RequestQueue requestQueue;
    ArrayList<String> useridlist=new ArrayList<>();
    ArrayList<String> usernamelist=new ArrayList<>();

     Toolbar toolbar;

     SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_details);

        pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid", "").toString();

        requestQueue= Volley.newRequestQueue(this);

        rlmain=(RelativeLayout)findViewById(R.id.content);

        tvviewcomments=(TextView)findViewById(R.id.textView94);

        tvcount=(TextView)findViewById(R.id.textView102);

        llcount=(LinearLayout)findViewById(R.id.llcount);

        tvviewcomments.setPaintFlags(tvviewcomments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvviewcomments.setText("View Comments");


        Intent j = getIntent();
        title = j.getExtras().getString("title");
        custid = j.getExtras().getString("customerId");
        cmpid = j.getExtras().getString("complainId");

      /*  Toast.makeText(ComplainDetails.this, custid, Toast.LENGTH_SHORT).show();
        Toast.makeText(ComplainDetails.this, cmpid, Toast.LENGTH_SHORT).show();*/

        llcdetails=(LinearLayout)findViewById(R.id.llcdetails);
        llphoto=(LinearLayout)findViewById(R.id.llphoto);
        llmarkresolve=(LinearLayout)findViewById(R.id.llmarkresolve);

        tvacno=(TextView)findViewById(R.id.textView34);
        tvmqno=(TextView)findViewById(R.id.textView36);
        tvcid=(TextView)findViewById(R.id.textView61);
        tvcsub=(TextView)findViewById(R.id.textView44);
        tvdesc=(TextView)findViewById(R.id.textView46);
        tvphone=(TextView)findViewById(R.id.textView62);
        tvemail=(TextView)findViewById(R.id.textView63);
        tvaddress=(TextView)findViewById(R.id.textView64);

        tvresolve=(TextView)findViewById(R.id.textView30);

        imdropdown=(ImageView)findViewById(R.id.imageView4);
        imphone=(ImageView)findViewById(R.id.imageView2);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(getApplicationContext(), DashBoard.class);
                //  startActivity(i);
                onBackPressed();

            }
        });

        URL=siteurl+"/GetCustomerComplainDetailsForCollectionApp?complainId="+cmpid+"&customerId="+custid+"&userId="+uid;

        new JSONAsynk().execute(new String[]{URL});


        tvviewcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),Comments_List_Activity.class);
                i.putExtra("Complainid",cmpid);
                i.putExtra("title",title);
                i.putExtra("CustomerId",custid);
                i.putExtra("From","Details");
                startActivity(i);

                finish();
            }
        });

        imdropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (llcdetails.isShown()) {
                    llcdetails.setVisibility(View.GONE);
                    imdropdown.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);


                } else {
                    llcdetails.setVisibility(View.VISIBLE);
                    imdropdown.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                }

            }
        });

        imphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

                        if(result == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(ComplainDetails.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                        }
                        else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                            startActivity(callIntent);
                        }

                    }
                    else
                    {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                        startActivity(callIntent);
                    }




                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }

            }
        });

        llphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Complaindetails.get(0).get("image").toString().equals(""))
                {
                    Snackbar.make(v,"No Image to SHow...",Snackbar.LENGTH_LONG).show();
                }
                else {

                    byte[] decodedString = Base64.decode(Complaindetails.get(0).get("image").toString(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    Intent i = new Intent(ComplainDetails.this, FullImageActivity.class);
                    i.putExtra("Image", decodedByte);
                    startActivity(i);
                }


                /*AlertDialog.Builder adbuild=new AlertDialog.Builder(ComplainDetails.this);

                LayoutInflater li=getLayoutInflater();
                View vs=li.inflate(R.layout.layout_view_photo_dialog,null);
                adbuild.setView(vs);

               final ImageView im=(ImageView)vs.findViewById(R.id.imageView10);

               // im.setImageResource(R.drawable.applicationbackground);


                byte[] decodedString = Base64.decode(Complaindetails.get(0).get("image").toString(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                im.setImageBitmap(decodedByte);

                Button btnclose=(Button)vs.findViewById(R.id.button_close);

               final AlertDialog ad=adbuild.create();
                ad.requestWindowFeature(Window.FEATURE_NO_TITLE);

               // ad.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //ad.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

               // ad.setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.ic_clear_black_36dp);
                ad.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                ad.setCancelable(true);


                else
                {
                    ad.show();
                }


                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        //ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.cableappbackground);
                        float imageWidthInPX = (float)im.getWidth();

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                                Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                        im.setLayoutParams(layoutParams);


                    }
                });

                btnclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad.dismiss();
                    }
                });*/

            }
        });

        llmarkresolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ComplainSignatureActivity.class);
                i.putExtra("cname",title);
                i.putExtra("from", "complain");
                i.putExtra("complainId", cmpid);
                i.putExtra("CustomerId",custid);
                startActivity(i);

                SharedPreferences pref= getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.remove("from");
                editor.commit();

                finish();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if(permissions.length>0)
                {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + tvphone.getText().toString()));
                        startActivity(callIntent);
                    }


                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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

            useridlist.clear();
            usernamelist.clear();

            URL=siteurl+"/GetUserlistfornewcollectionApp";

            CallVolleyUserlist(URL, cmpid);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void CallVolleyUserlist(String a, final String cmpid)
    {

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorid",cid);

        final SpotsDialog spload;
        spload=new SpotsDialog(ComplainDetails.this,R.style.Custom);
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

                                        if(useridlist.size()>0)
                                        {
                                            final ListView lv=new ListView(ComplainDetails.this);
                                            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                            lv.setDividerHeight(0);


                                            final ArrayAdapter<String> da=new ArrayAdapter<String>(ComplainDetails.this,android.R.layout.simple_list_item_single_choice,usernamelist);
                                            lv.setAdapter(da);

                                            final AlertDialog.Builder builderDialog = new AlertDialog.Builder(ComplainDetails.this);
                                            builderDialog.setView(lv);
                                            builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if (lv.getCheckedItemPosition()==-1)
                                                    {
                                                        Toast.makeText(ComplainDetails.this, "Please select atleast one User..!!", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {

                                                        URL=siteurl+"/AssignComplaintForNewCollectionApp";

                                                        HashMap<String,String> map=new HashMap<String, String>();
                                                        map.put("compId",cmpid);
                                                        map.put("userId",useridlist.get(lv.getCheckedItemPosition()));


                                                        CallVolleyAssignComplain(URL,map);

                                                    }
                                                }
                                            });

                                            final AlertDialog alert=builderDialog.create();
                                            alert.setTitle("Select User to Assign Complaint");
                                            alert.setCancelable(true);
                                            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                                            alert.show();
                                        }
                                    }

                                    else
                                    {
                                        Toast.makeText(ComplainDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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

    public void CallVolleyAssignComplain(String a,HashMap<String,String> map)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(ComplainDetails.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

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
                                        Toast.makeText(ComplainDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed()
    {
        finish();
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
       // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1,jsn,jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(ComplainDetails.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonobj=makeHttpRequest(params[0]);

            } catch (Exception e) {
               e.printStackTrace();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject e) {
            spload.dismiss();

            try
            {
                  //
                  // Toast.makeText(ComplainDetails.this, e.toString(), Toast.LENGTH_SHORT).show();


                if(e.getString("status").toString().equals("True"))
                {

                    rlmain.setVisibility(View.VISIBLE);

                        HashMap<String,String> map=new HashMap<>();

                        String cid = e.getString("ComplainId");
                        String cname = e.getString("Name");
                        String caddress = e.getString("AccountNo");
                        String carea = e.getString("Phone");
                        String cacno = e.getString("Email");
                        String cphone = e.getString("Address");
                        String cemail = e.getString("Subject");
                        String castatus = e.getString("Message");
                        String cmq = e.getString("image");
                        String ctotaloa = e.getString("mqno");
                        String cmstatus=e.getString("ComplainStatus");
                        String wcommentcount=e.getString("webcommentcount");
                        String ucommentcount=e.getString("usercommentcount");

                    if(cmstatus.equals("RESOLVED")) {
                        tvresolve.setText(cmstatus);
                        llmarkresolve.setEnabled(false);
                    }
                    else if(cmstatus.equals("NEW"))
                    {
                        toolbar.getMenu().getItem(0).setVisible(true);
                    }

                        map.put("ComplainId",cid);
                        map.put("Name",cname);
                        map.put("AccountNo",caddress);
                        map.put("Phone",carea);
                        map.put("Email",cacno);
                        map.put("Address",cphone);
                        map.put("Subject",cemail);
                        map.put("Message",castatus);
                    map.put("image",cmq);
                    map.put("mqno",ctotaloa);
                        map.put("ComplainStatus", cmstatus);


                        Complaindetails.add(map);

                    tvacno.setText(caddress);
                    tvmqno.setText(ctotaloa);
                    tvcid.setText("CP - "+cid);
                    tvcsub.setText(cemail);
                    tvdesc.setText(castatus);
                    tvphone.setText(carea);
                    tvemail.setText(cacno);
                    tvaddress.setText(cphone);

                    if(pref.getString("RoleId", "").toString().equals("2"))
                    {
                        tvcount.setText(wcommentcount);

                        if(wcommentcount.equals("0"))
                        {
                            tvcount.setVisibility(View.GONE);
                        }
                        else
                        {
                            tvcount.setVisibility(View.VISIBLE);

                            Animation anim= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            llcount.startAnimation(anim);

                        }
                    }
                    else
                    {
                        tvcount.setText(ucommentcount);


                        if(ucommentcount.equals("0"))
                        {
                            tvcount.setVisibility(View.GONE);
                        }
                        else
                        {
                            tvcount.setVisibility(View.VISIBLE);

                            Animation anim= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            llcount.startAnimation(anim);

                        }
                    }
                    }

                else
                {

                    Toast.makeText(ComplainDetails.this,e.getString("message"), Toast.LENGTH_SHORT).show();
                }

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

            }
            catch (JSONException ex)
            {
                Toast.makeText(ComplainDetails.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(ComplainDetails.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }
}
