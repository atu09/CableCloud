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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class Comments_List_Activity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    ListView lvcomments;

    ArrayList<HashMap<String,String>> commentsdetails=new ArrayList<>();

    FloatingActionButton fabadd;

    String siteurl,URL,URL1,uid;

     InputStream is = null;
     JSONObject jobj = null;
    String json = "";
     JSONArray jarr = null;

    JSONObject jsonObjectadd;

    JSONObject jsonobj;

    SimpleAdapter da;
    String tag,cmpid,custid,title,from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments__list_);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();

        lvcomments=(ListView)findViewById(R.id.listView5);
        fabadd=(FloatingActionButton)findViewById(R.id.fab);

        Intent j=getIntent();
        cmpid=j.getExtras().getString("Complainid");
        from=j.getExtras().getString("From");
        title=j.getExtras().getString("title");
        custid=j.getExtras().getString("CustomerId");


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), DashBoard.class);
                //startActivity(i);

                onBackPressed();

            }
        });

        if(pref.getString("RoleId", "").toString().equals("2"))
        {
            tag="Web";
        }
        else
        {
            tag="AssignUser";
        }


        URL=siteurl+"/withtag?complainId="+cmpid+"&commentTag="+tag;

        new JSONAsynk().execute(new String[]{URL});


        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li=getLayoutInflater();

                View vs=li.inflate(R.layout.comments_dialog, null);

               final  EditText edtcomment=(EditText)vs.findViewById(R.id.editText20);

                MDDialog.Builder mdalert=new MDDialog.Builder(Comments_List_Activity.this);
                mdalert.setContentView(vs);
                mdalert.setPositiveButton("POST", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try{
                            if(edtcomment.getText().toString().length()>0) {

                                String text=edtcomment.getText().toString();

                                URL1 = siteurl + "/AddComplainCommentForCollectionApp?loginuserId=" + uid + "&complainId=" + cmpid + "&comment=" + URLEncoder.encode(text, "UTF-8");

                                new JSONAsynks().execute(new String[]{URL1});

                            }

                            else
                            {
                                Snackbar.make(v,"Please Enter Comment",Snackbar.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
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

       /* String[] data1=new String[]{"By Rahul Shah","By Kiran","By Umesh","By Technician"};
        String[] data2=new String[]{"jshfjlbskhgfkdsh","sljhvsfhdlflsdl","sljhfljsdh","ljdnbjvldfhnlhn"};
        String[] data3=new String[]{"8/29/2016 08:10","8/27/2016 18:10","8/29/2016 23:10","8/27/2016 01:10"};


        for(int i=0;i<data1.length;i++)
        {
            HashMap<String,String> map=new HashMap<>();


            map.put("name",data1[i]);
            map.put("comments",data2[i]);
            map.put("date",data3[i]);


            commentsdetails.add(map);

        }


        SimpleAdapter da = new SimpleAdapter(Comments_List_Activity.this, commentsdetails, R.layout.layout_comments, new String[]{"name", "comments", "date"}, new int[]{R.id.textView95, R.id.textView96, R.id.textView97});
        lvcomments.setAdapter(da);*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(from.equals("Details"))
        {
            Intent i=new Intent(getApplicationContext(),ComplainDetails.class);
            i.putExtra("title", title);
            i.putExtra("customerId",custid);
            i.putExtra("complainId",cmpid);
            startActivity(i);
        }
        else if(from.equals("Notification"))
        {
            Intent i=new Intent(getApplicationContext(),DashBoard.class);
            startActivity(i);
        }

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


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(Comments_List_Activity.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                //jobj=new JSONObject();
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
               // Toast.makeText(Comments_List_Activity.this, e.getString("message").toString(), Toast.LENGTH_SHORT).show();


                if(e.getString("status").toString().equals("True"))
                {

                    final JSONArray entityarray = e.getJSONArray("lstcomp");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject s = (JSONObject) entityarray.get(i);

                        String uname=s.getString("username");
                        String  comment=s.getString("comment");
                        String date=s.getString("date");

                        HashMap<String,String> map=new HashMap<>();

                        map.put("username",uname);
                        map.put("comment",comment);
                        map.put("date",date);

                        commentsdetails.add(map);

                    }

                    da = new SimpleAdapter(Comments_List_Activity.this, commentsdetails, R.layout.layout_comments, new String[]{"username", "comment", "date"}, new int[]{R.id.textView95, R.id.textView96, R.id.textView97});
                    lvcomments.setAdapter(da);

                }

                // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

            }
            catch (JSONException ex)
            {
                Toast.makeText(Comments_List_Activity.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(Comments_List_Activity.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private class JSONAsynks extends AsyncTask<String,String,JSONObject>
    {

        private ProgressDialog pDialog;
       // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(Comments_List_Activity.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(false);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonObjectadd=new JSONObject();

                json="";

                jsonObjectadd=makeHttpRequest(params[0]);

            } catch (Exception e) {
                e.printStackTrace();
               // Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return  jsonObjectadd;


        }

        @Override
        protected void onPostExecute(JSONObject e) {
            spload.dismiss();

            try
            {

                Toast.makeText(Comments_List_Activity.this, e.getString("message").toString(), Toast.LENGTH_SHORT).show();

                if(e.getString("status").toString().equals("True"))
                {
                    Intent i=new Intent(getApplicationContext(),Comments_List_Activity.class);
                    i.putExtra("Complainid",cmpid);
                    i.putExtra("title",title);
                    i.putExtra("CustomerId",custid);
                    i.putExtra("From",from);
                    startActivity(i);

                    finish();
                }

                // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

            }
            catch (JSONException ex)
            {
                Toast.makeText(Comments_List_Activity.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(Comments_List_Activity.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }
}
