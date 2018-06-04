package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class CollectionFragment extends Fragment {

    String str = "\u20B9";
    ListView lstuser;

    private static final String PREF_NAME = "LoginPref";

    String url;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    RequestQueue requestQueue;

    TextView tvtodaycol,tvthismonthcol;

    SwipeRefreshLayout swrefresh;

    ArrayList<HashMap<String,String>> userlist=new ArrayList<>();

    CustomAdapter adapter;

    SharedPreferences pref;

    String siteurl,uid,cid,aid,eid,URL;

    RelativeLayout rlmain;

    SimpleAdapter da;

    String tc,toa;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context con=getActivity();
        View vc= inflater.inflate(R.layout.collection, null);;

        pref=con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if(pref.getBoolean("IsBilling",true))
        {
            url=getArguments().getString("url");
            //Toast.makeText(getContext(), url, Toast.LENGTH_SHORT).show();

           if(url.equals("-")) {

               vc = inflater.inflate(R.layout.layout_offline, null);
           }
            else
           {
               CallVolleys(url);
              // new JSONAsynk().execute(new String[]{url});
               vc = inflater.inflate(R.layout.collection, null);
           }

        }

        else
        {
            vc= inflater.inflate(R.layout.no_access_layout,null);
        }

        return  vc;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(pref.getBoolean("IsBilling",true) && !url.equals("-")) {

            lstuser = (ListView) view.findViewById(R.id.listView);

            tvtodaycol = (TextView) view.findViewById(R.id.textView28);
            tvthismonthcol = (TextView) view.findViewById(R.id.textView30);

            rlmain=(RelativeLayout)view.findViewById(R.id.rlmain);

            swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

          //  lstuser.setAdapter(adapter);

            swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    swrefresh.setRefreshing(true);

                    CallVolleys(url);
                   // new JSONAsynk().execute(new String[]{url});


                }
            });

            lstuser.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lstuser == null || lstuser.getChildCount() == 0) ?
                            0 : lstuser.getChildAt(0).getTop();
                    swrefresh.setEnabled((topRowVerticalPosition >= 0));

                }
            });

       /* map.put("UserId", uid);
        map.put("UserName",uname);
        map.put("Usertodaycollection",utodaycol);
        map.put("Userthismonthcollection",uthismonthcol);*/



            // SimpleAdapter da=new SimpleAdapter(getContext(),userlist,R.layout.list_collection_layout,new String[]{"UserName","Usertodaycollection","Userthismonthcollection"},new int[]{R.id.textView2,R.id.textView24,R.id.textView26});
            // lstuser.setAdapter(da);

            lstuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,final int position, long id) {


                    ImageView tv =(ImageView)view.findViewById(R.id.imageView13);
                    if (tv != null) {

                        tv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                Intent i=new Intent(getContext(),MapsActivity.class);
                                i.putExtra("UserId",userlist.get(position).get("UserId"));
                                i.putExtra("UserName",userlist.get(position).get("UserName"));
                                startActivity(i);

                                return false;
                            }
                        });


                    } else {
                    }


                   /* ImageView tv =(ImageView)view.findViewById(R.id.imageView13);
                    if (tv != null) {
                        *//*tv.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(getContext(),MapsActivity.class);
                                i.putExtra("UserId",userlist.get(position).get("UserId"));
                                i.putExtra("UserName",userlist.get(position).get("UserName"));
                                startActivity(i);
                            }
                        });*//*

                        tv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                Intent i=new Intent(getContext(),MapsActivity.class);
                                i.putExtra("UserId",userlist.get(position).get("UserId"));
                                i.putExtra("UserName",userlist.get(position).get("UserName"));
                                startActivity(i);

                                return false;
                            }
                        });


                    } else {
                    }*/

                    CardView cv = (CardView) view.findViewById(R.id.card_view);

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Context con = getActivity();

                            SharedPreferences pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Name", userlist.get(position).get("UserName"));
                            editor.putString("selected_uid", userlist.get(position).get("UserId"));
                            editor.commit();

                            Intent i = new Intent(getContext(), Collection_Area_Activity.class);
                            i.putExtra("Userthismonthcollection",userlist.get(position).get("Userthismonthcollection"));
                            startActivity(i);

                            //Intent i = new Intent(getContext(), activity_collection_customerdetail.class);
                            ///startActivity(i);

                        }
                    });




                }
            });
        }




    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context con=getActivity();

        pref=con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(getContext());

        adapter=new CustomAdapter(getContext(),userlist);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        //setRetainInstance(true);
    }


    public JSONObject makeHttpRequest(String url){
        DefaultHttpClient httpclient = new DefaultHttpClient();
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
                    Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
            }
        }catch (UnsupportedEncodingException e){
            Toast.makeText(getActivity(), "**"+e, Toast.LENGTH_SHORT).show();
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

            spload=new SpotsDialog(getActivity(),R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonobj=makeHttpRequest(params[0]);


            } catch (Exception e) {
               // Toast.makeText(getActivity(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try
            {
                userlist.clear();

                if(json.getString("status").toString().equals("True"))
                {

                   // Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityarray = jsonobj.getJSONArray("lstUserInfoCollectionApp");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        String uid = e.getString("UserId");
                        String uname = e.getString("UserName");
                        String utodaycol = e.getString("Usertodaycollection");
                        String uthismonthcol = e.getString("Userthismonthcollection");


                        HashMap<String,String> map=new HashMap<>();

                        map.put("UserId",uid);
                        map.put("UserName",uname);
                        map.put("Usertodaycollection",format.format(Double.parseDouble(utodaycol)));
                        map.put("Userthismonthcollection",format.format(Double.parseDouble(uthismonthcol)));

                        userlist.add(map);

                    }

                    adapter.notifyDataSetChanged();

                    String tc=json.getString("Usertodaytotalcollection").toString();
                    String toa=json.getString("Userthismonthtotalcollection").toString();

                    tvtodaycol.setText(format.format(Double.parseDouble(tc)));
                    tvthismonthcol.setText(format.format(Double.parseDouble(toa)));

                    swrefresh.setRefreshing(false);




                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

    }

    public void CallVolleys(String a)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(getActivity(),R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(false);
        spload.show();

        //URL=siteurl+"/GetUserlistforcollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityId="+pref.getString("Entityids","").toString();

       /* Toast.makeText(getContext(), cid, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), eid, Toast.LENGTH_SHORT).show();*/

        Map<String,String> maps=new HashMap<String,String>();
        maps.put("contractorid",cid);
        maps.put("loginuserId",uid);
        maps.put("entityId",eid);


        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(maps),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            spload.dismiss();

                            try {
                                userlist.clear();

                                if (response.getString("status").toString().equals("True")) {

                                    rlmain.setVisibility(View.VISIBLE);

                                   // Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                                    DecimalFormat format = new DecimalFormat();
                                    format.setDecimalSeparatorAlwaysShown(false);

                                    final JSONArray entityarray = response.getJSONArray("lstUserInfoCollectionApp");

                                    for (int i = 0; i < entityarray.length(); i++) {
                                        JSONObject e = (JSONObject) entityarray.get(i);

                                        String uid = e.getString("UserId");
                                        String uname = e.getString("UserName");
                                        String utodaycol = e.getString("Usertodaycollection");
                                        String uthismonthcol = e.getString("Userthismonthcollection");


                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("UserId", uid);
                                        map.put("UserName", uname);
                                        map.put("Usertodaycollection", str+format.format(Double.parseDouble(utodaycol)));
                                        map.put("Userthismonthcollection",str+ format.format(Double.parseDouble(uthismonthcol)));
                                        map.put("Userthismonthcollection",str+ format.format(Double.parseDouble(uthismonthcol)));

                                        userlist.add(map);

                                    }

                                    da=new SimpleAdapter(getContext(),userlist,R.layout.list_collection_layout,new String[] {"UserName","Usertodaycollection","Userthismonthcollection"},new int[] {R.id.textView2,R.id.textView24,R.id.textView26});
                                    lstuser.setAdapter(da);
                                    //adapter.notifyDataSetChanged();

                                     tc= response.getString("Usertodaytotalcollection").toString();
                                    toa = response.getString("Userthismonthtotalcollection").toString();

                                    tvtodaycol.setText(str+format.format(Double.parseDouble(tc)));
                                    tvthismonthcol.setText(str+format.format(Double.parseDouble(toa)));

                                    swrefresh.setRefreshing(false);


                                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        }catch (Exception e) {
                              //  Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        spload.dismiss();

                        //NetworkResponse networkResponse = error.networkResponse;

                       // Toast.makeText(getContext(), "errorr++"+networkResponse.statusCode, Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }
}
