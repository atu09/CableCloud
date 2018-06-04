package com.mtaj.mtaj_08.cableplus_new;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;


/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class CustomerFragment extends Fragment {
    private static final String PREF_NAME = "LoginPref";

    SearchView searchView;

    CardView cvarea;

    LinearLayout llarea;

    TextView tvaddcustomer;

    String url;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    TextView tvtotalarea,tvtotalcustomer;

    String siteurl,uid,cid,aid,eid,URL;

    SwipeRefreshLayout swrefresh;
    EditText etsearch;

     SharedPreferences pref;

    RequestQueue requestQueue;

    ListView lvcustomer;

    SimpleAdapter da;

    ArrayList<HashMap<String,String>> customerdetails=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context con=getActivity();
        View vc;

        final SharedPreferences pref=con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        url=getArguments().getString("url");

        if(pref.getBoolean("IsCustomer",true))
        {
           if(url.equals("-"))
           {
               vc = inflater.inflate(R.layout.layout_offline, null);
           }
            else {
                      // new JSONAsynk().execute(new String[]{url});
               CallVolleys(url);
               vc= inflater.inflate(R.layout.customers, container,false);
           }

        }

        else
        {
            vc= inflater.inflate(R.layout.no_access_layout,container,false);
        }



        return vc;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context con = getActivity();

       pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);



        if(pref.getBoolean("IsCustomer",true) && !url.equals("-")) {

            cvarea = (CardView) view.findViewById(R.id.card_view2);

            tvaddcustomer = (TextView) view.findViewById(R.id.textView30);

            lvcustomer=(ListView)view.findViewById(R.id.listView7);

            /*searchView = (SearchView) view.findViewById(R.id.searchView);
            searchView.setQueryHint("Search Customers");
            searchView.onActionViewExpanded();
            searchView.setIconifiedByDefault(false);


            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = (TextView) searchView.findViewById(id);
            textView.setTextColor(Color.WHITE);
            textView.setHintTextColor(Color.WHITE);*/

            swrefresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            etsearch= (EditText) view.findViewById(R.id.editText20);

            swrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    swrefresh.setRefreshing(true);
                           // new JSONAsynk().execute(new String[]{url});
                    CallVolleys(url);
                }
            });

            etsearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    try{

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        URL = siteurl + "/SearchCustomerForCollectionApp?startindex=0&noofrecords=10000000&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + URLEncoder.encode(v.getText().toString(), "UTF-8");

                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("from");
                        editor.putString("from", "Search");
                        editor.putString("URL", URL);
                        editor.commit();

                        Intent i = new Intent(getContext(), CustomerListActivity.class);
                        startActivity(i);

                        return true;
                    }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    return false;
                }
            });


           /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    URL = siteurl + "/SearchCustomerForCollectionApp?startindex=0&noofrecords=10000000&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + query;

                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("from");
                    editor.putString("from", "Search");
                    editor.putString("URL", URL);
                    editor.commit();

                    Intent i = new Intent(getContext(), CustomerListActivity.class);
                    startActivity(i);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });*/



            lvcustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i = new Intent(getContext(), AreaListInCustomers.class);
                    startActivity(i);

                }
            });

            tvaddcustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getContext(), AddCustomer_1.class);
                    startActivity(i);

                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);

        requestQueue = Volley.newRequestQueue(getContext());

        final Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        eid=pref.getString("Entityids","").toString();
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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



            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);

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

                e.printStackTrace();

                //Toast.makeText(getActivity(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return  jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try
            {

                if(json.getString("status").toString().equals("True"))
                {

                    ///Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                  String ta=json.getString("TotalArea").toString();
                   String tc=json.getString("TotalCustomers").toString();





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
        spload.setCancelable(true);
        spload.show();

       // URL=siteurl+"/GetCustomerDashbordHomeForNewCollectionApp?contractorId="+cid+"&loginuserId="+uid+"&entityIds="+pref.getString("Entityids","").toString();

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorId",cid);
        map.put("loginuserId",uid);
        map.put("entityIds",eid);


        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         try {

                        spload.dismiss();

                        try
                        {
                            customerdetails.clear();

                            if(response.getString("status").toString().equals("True"))
                            {

                                ///Toast.makeText(getContext(), json.toString(), Toast.LENGTH_SHORT).show();

                                String ta=response.getString("TotalArea").toString();
                                String tc=response.getString("TotalCustomers").toString();
                                String tab=response.getString("TotalAssignDevice").toString();
                                String tuab=response.getString("TotalUnAssignDevice").toString();


                                HashMap<String,String> map=new HashMap<>();

                                map.put("TotalArea",ta);
                                map.put("TotalCustomers", tc);
                                map.put("TotalAssignDevice", tab);
                                map.put("TotalUnAssignDevice", tuab);

                                customerdetails.add(map);

                                da=new SimpleAdapter(getContext(),customerdetails,R.layout.layout_customer,new String[]{"TotalArea","TotalCustomers","TotalAssignDevice"},new int[]{R.id.textView65,R.id.textView8,R.id.textView10});

                                lvcustomer.setAdapter(da);

                                swrefresh.setRefreshing(false);


                                // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                               // Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                             e.printStackTrace();
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }


}
