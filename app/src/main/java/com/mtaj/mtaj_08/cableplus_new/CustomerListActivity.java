package com.mtaj.mtaj_08.cableplus_new;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class CustomerListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String PREF_NAME = "LoginPref";

    ListView lvcustomer;

    ArrayList<HashMap<String,String>> customerdetails=new ArrayList<>();

   // SimpleAdapter da;
    CustomerListAdapter cda;

    RelativeLayout rv,footer;

    String from;

    String siteurl,uid,cid,aid,eid,URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    TextView tvtotalcol,tvtotaloa;
    String str = "\u20B9";

    RelativeLayout rlmain;

    TextView txtnocustomer;

    boolean loadingMore = false;

    int start=0;
    int last=1000;

    int mPage=0;

    SharedPreferences pref;

    Typeface tf;

    DBHelper myDB;
    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        hideKeyboard(CustomerListActivity.this);


         pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        myDB=new DBHelper(this);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        aid=pref.getString("AreaId","").toString();
        eid=pref.getString("Entityids","").toString();

        Intent j=getIntent();

          isConnected = ConnectivityReceiver.isConnected();


       // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


       //Intent j=getIntent();
      //  String areatitle=j.getExtras().getString("AreaName");
        from=pref.getString("from","").toString();

        if(from.equals("Customer") ) {
            URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=10"+"&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";

        }
            else if(from.equals("Payment") ) {

            URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=10"+"&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";

        }
        else if(from.equals("Search"))
        {
            URL=pref.getString("URL","").toString();
        }

        lvcustomer=(ListView)findViewById(R.id.listcustomer);

        tvtotalcol=(TextView)findViewById(R.id.textView28);
        tvtotaloa=(TextView)findViewById(R.id.textView30);

        rlmain=(RelativeLayout)findViewById(R.id.rlmain);
        txtnocustomer=(TextView)findViewById(R.id.textView99);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pref.getString("AreaName", "").toString());
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        footer=(RelativeLayout)findViewById(R.id.footer);

        if(from.equals("Customer") || from.equals("Search"))
        {
            footer.setVisibility(View.GONE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(getApplicationContext(), DashBoard.class);
                //  startActivity(i);
                onBackPressed();
            }
        });

        tvtotalcol.setText(pref.getString("Collection", "").toString());

        if(isConnected) {

            new JSONAsynk().execute(new String[]{URL});

            cda=new CustomerListAdapter(getApplicationContext(),customerdetails);
        }
        else
        {
            Cursor c=myDB.getCustomers(aid);

            //swrefresh.setEnabled(false);

            DecimalFormat format = new DecimalFormat("#");
            format.setDecimalSeparatorAlwaysShown(false);

            if(c.getCount()>0)
            {
                if(c.moveToFirst())
                {
                    do {

                        String cid = c.getString(c.getColumnIndex("CUSTOMER_ID"));
                        String cname = c.getString(c.getColumnIndex("NAME"));
                        String caddress = c.getString(c.getColumnIndex("ADDRESS"));
                        String carea = c.getString(c.getColumnIndex("AREA"));
                        String cacno = c.getString(c.getColumnIndex("ACCOUNTNO"));
                        String cphone = c.getString(c.getColumnIndex("PHONE"));
                        String cemail = c.getString(c.getColumnIndex("EMAIL"));
                        String castatus = c.getString(c.getColumnIndex("ACCOUNTSTATUS"));
                        String cmq = c.getString(c.getColumnIndex("MQNO"));
                        String ctotaloa = c.getString(c.getColumnIndex("TOTAL_OUTSTANDING"));
                        String commentCount=c.getString(c.getColumnIndex("COMMENT_COUNT"));
                        String billid=c.getString(c.getColumnIndex("BILL_ID"));


                        HashMap<String,String> map=new HashMap<>();
                        map.put("CustomerId",cid);
                        map.put("Name",cname);
                        map.put("Address",caddress);
                        map.put("Area",carea);
                        map.put("AccountNo",cacno);
                        map.put("Phone",cphone);
                        map.put("Email",cemail);
                        map.put("AccountStatus",castatus);
                        map.put("MQNo",cmq);
                        //map.put("TotalOutStandingAmount",ctotaloa);
                        map.put("TotalOutStandingAmount",format.format(Double.parseDouble(ctotaloa)));
                        map.put("CustCommentCount",commentCount);
                        map.put("billId",billid);

                        customerdetails.add(map);

                    }while (c.moveToNext());
                }

                long totalos=0;

                if(customerdetails.size()>0)
                {
                    for(int k=0;k<customerdetails.size();k++)
                    {
                        totalos=totalos+Long.parseLong(customerdetails.get(k).get("TotalOutStandingAmount"));
                    }
                }


                String toa=String.valueOf(totalos);

                tvtotaloa.setText(str+format.format(Double.parseDouble(toa)));

               // Toast.makeText(getApplicationContext(),"**"+ customerdetails.size(), Toast.LENGTH_SHORT).show();

                cda=new CustomerListAdapter(getApplicationContext(),customerdetails);

                rlmain.setVisibility(View.VISIBLE);

            }
        }


        //da=new SimpleAdapter(CustomerListActivity.this,customerdetails,R.layout.customerlist,new String[]{"Name","TotalOutStandingAmount","AccountNo","MQNo","Address","CustCommentCount"},new int[]{R.id.textView31,R.id.textView32,R.id.textView34,R.id.textView36,R.id.textView37,R.id.textView102});

        lvcustomer.setAdapter(cda);

        lvcustomer.setTextFilterEnabled(true);

        lvcustomer.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
//                if (!isSearch) {
//                    mpage = page;
////                    callWebLocation(page);
//                    locationLoadMoreWebService(page);
//                }

                if(isConnected) {
                    mPage = mPage + 1;

                    if (from.equals("Customer")) {
                        URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";

                    } else if (from.equals("Payment")) {

                        URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";

                    }

                    new JSONAsynk().execute(new String[]{URL});
                }

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

      /*  lvcustomer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if((visibleItemCount == 20)){

                    start=start+20;
                    last=last+20;

                    if(from.equals("Customer") ) {
                        URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex="+String.valueOf(start)+"&noofrecords="+String.valueOf(last)+"&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";
                    }
                    else if(from.equals("Payment") ) {

                        URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(start)+"&noofrecords="+String.valueOf(last)+"&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=";

                    }
                    else if(from.equals("Search"))
                    {

                        URL=pref.getString("URL","").toString();
                    }

                    new JSONAsynk().execute(new String[]{URL});

                }

            }
        });*/

       /* lvcustomer.setOnTouchListener(new View.OnTouchListener() {
            float height;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float height = event.getY();
                if (action == MotionEvent.ACTION_DOWN) {
                    this.height = height;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (this.height < height) {
                        getSupportActionBar().show();

                        //Toast.makeText(getApplicationContext(), "Scrolled up...", Toast.LENGTH_SHORT).show();
                    } else if (this.height > height) {


                        getSupportActionBar().hide();

                        // Toast.makeText(getApplicationContext(), "Scrolled Down...", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });*/


        lvcustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> ss = (HashMap<String, String>) cda.getItem(position);


                if(isConnected) {

                    if (from.equals("Customer")) {
                        Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("MQno", ss.get("MQNo"));
                        startActivity(i);

                        finish();

                    }
                    if (from.equals("Payment")) {

                        Intent i = new Intent(getApplicationContext(), CustomerDetails.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("CustomerId", ss.get("CustomerId"));
                        i.putExtra("from", "Payment");
                        startActivity(i);

                        finish();
                    }
                    if (from.equals("Search")) {

                        Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                        i.putExtra("cname", ss.get("Name"));
                        i.putExtra("A/cNo", ss.get("AccountNo"));
                        i.putExtra("MQno", ss.get("MQNo"));
                        startActivity(i);

                        finish();
                    }
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), CustomerDetail_Offline.class);
                    i.putExtra("cname", ss.get("Name"));
                    i.putExtra("A/cNo", ss.get("AccountNo"));
                    i.putExtra("CustomerId", ss.get("CustomerId"));
                    i.putExtra("billId",ss.get("billId"));
                    //i.putExtra("TotalOutStandingAmount",ss.get("TotalOutStandingAmount"));
                    startActivity(i);

                    finish();
                }

            }
        });

    }


    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences.Editor editor = pref.edit();
        editor.remove("AreaStatus");
        editor.remove("AreaId");
        editor.remove("AreaName");
        editor.apply();

       /* editor.putString("AreaStatus", "true");
        editor.putString("AreaId", areadetails.get(position).get("AreaId"));
        editor.putString("AreaName", areadetails.get(position).get("AreaName"));*/





       /* Intent intent = new Intent(this,DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)*/;
        //startActivity(new Intent(CustomerListActivity.this, DashBoard.class));
        finish();

    }

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(Color.WHITE);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setTextColor(Color.WHITE);


        searchView.setFocusable(false);

        searchView.setQueryHint("Search Customers");
        searchView.setIconifiedByDefault(false);
        searchView.setIconifiedByDefault(false);

        searchView.setFocusableInTouchMode(true);


        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_ADJUST_PAN);



    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if(isConnected) {

            try{

            mPage = 0;

            if (from.equals("Customer")) {
                URL = siteurl + "/GetAreawiseCustomerDetailsByForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + URLEncoder.encode( query, "UTF-8");

            } else if (from.equals("Payment")) {

                customerdetails.clear();

                URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=10" + "&contractorid=" + cid + "&areadId=" + aid + "&userId=" + uid + "&entityId=" + eid + "&filterCustomer=" + URLEncoder.encode( query, "UTF-8");
            }

            new JSONAsynk().execute(new String[]{URL});

       /* da=new SimpleAdapter(CustomerListActivity.this,customerdetails,R.layout.customerlist,new String[]{"Name","TotalOutStandingAmount","AccountNo","MQNo","Address","CustCommentCount"},new int[]{R.id.textView31,R.id.textView32,R.id.textView34,R.id.textView36,R.id.textView37,R.id.textView102});
        lvcustomer.setAdapter(da);*/

            cda = new CustomerListAdapter(getApplicationContext(), customerdetails);

            //da=new SimpleAdapter(CustomerListActivity.this,customerdetails,R.layout.customerlist,new String[]{"Name","TotalOutStandingAmount","AccountNo","MQNo","Address","CustCommentCount"},new int[]{R.id.textView31,R.id.textView32,R.id.textView34,R.id.textView36,R.id.textView37,R.id.textView102});

            lvcustomer.setAdapter(cda);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {

            try {

                customerdetails.clear();

                Cursor c = myDB.SearchCustomer(query);

                //Toast.makeText(getContext(), "s=" + c.getCount(), Toast.LENGTH_LONG).show();

                if (c != null && c.getCount() > 0) {
                    if (c.moveToFirst()) {

                        DecimalFormat format = new DecimalFormat("#");
                        format.setDecimalSeparatorAlwaysShown(false);

                        String cid = c.getString(c.getColumnIndex("CUSTOMER_ID"));
                        String cname = c.getString(c.getColumnIndex("NAME"));
                        String caddress = c.getString(c.getColumnIndex("ADDRESS"));
                        String carea = c.getString(c.getColumnIndex("AREA"));
                        String cacno = c.getString(c.getColumnIndex("ACCOUNTNO"));
                        String cphone = c.getString(c.getColumnIndex("PHONE"));
                        String cemail = c.getString(c.getColumnIndex("EMAIL"));
                        String castatus = c.getString(c.getColumnIndex("ACCOUNTSTATUS"));
                        String cmq = c.getString(c.getColumnIndex("MQNO"));
                        String ctotaloa = c.getString(c.getColumnIndex("TOTAL_OUTSTANDING"));
                        String commentCount=c.getString(c.getColumnIndex("COMMENT_COUNT"));
                        String billid=c.getString(c.getColumnIndex("BILL_ID"));


                        HashMap<String,String> map=new HashMap<>();
                        map.put("CustomerId",cid);
                        map.put("Name",cname);
                        map.put("Address",caddress);
                        map.put("Area",carea);
                        map.put("AccountNo",cacno);
                        map.put("Phone",cphone);
                        map.put("Email",cemail);
                        map.put("AccountStatus",castatus);
                        map.put("MQNo",cmq);
                        //map.put("TotalOutStandingAmount",ctotaloa);
                        map.put("TotalOutStandingAmount",format.format(Double.parseDouble(ctotaloa)));
                        map.put("CustCommentCount",commentCount);
                        map.put("billId",billid);

                        customerdetails.add(map);
                    }


                    cda=new CustomerListAdapter(getApplicationContext(),customerdetails);

                    rlmain.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "No Customer found..!", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("Error:", e.toString());
            }



           // Toast.makeText(CustomerListActivity.this, "Sorry..  You Are Offline..!", Toast.LENGTH_LONG).show();
        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

       /* if (TextUtils.isEmpty(newText)) {
            lvcustomer.clearTextFilter();
        }
        else {
            lvcustomer.setFilterText(newText.toString());
        }*/

        //cda.getFilter().filter(newText);

        return true;
        
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

            spload=new SpotsDialog(CustomerListActivity.this,R.style.Custom);
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
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try
            {
                if(json.getString("status").toString().equals("True"))
                {

                    //Toast.makeText(CustomerListActivity.this, json.toString(), Toast.LENGTH_LONG).show();

                    rlmain.setVisibility(View.VISIBLE);

                    String str = "\u20B9",commentCount="0";

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityarray = json.getJSONArray("CustomerInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);


                        String cid = e.getString("CustomerId");
                        String cname = e.getString("Name");
                        String caddress = e.getString("Address");
                        String carea = e.getString("Area");
                        String cacno = e.getString("AccountNo");
                        String cphone = e.getString("Phone");
                        String cemail = e.getString("Email");
                        String castatus = e.getString("AccountStatus");
                        String cmq = e.getString("DeviceNo");
                        String ctotaloa = e.getString("TotalOutStandingAmount");
                        String csmartcard=e.getString("SmartCardNo");
                        String PackageName= (!TextUtils.isEmpty(e.getString("PackageName")) && !e.getString("PackageName").equals("null")) ? e.getString("PackageName") : " ";

                        if(!from.equals("Search"))
                        {
                            commentCount=e.getString("CustCommentCount");
                        }



                        HashMap<String,String> map=new HashMap<>();
                        map.put("CustomerId",cid);
                        map.put("Name",cname);
                        map.put("Address",caddress);
                        map.put("Area",carea);
                        map.put("AccountNo",cacno);
                        map.put("Phone",cphone);
                        map.put("Email",cemail);
                        map.put("AccountStatus",castatus);
                        map.put("MQNo",cmq);
                        map.put("SmartCardNo",csmartcard);
                        map.put("TotalOutStandingAmount",format.format(Double.parseDouble(ctotaloa)));
                        map.put("CustCommentCount",commentCount);
                        map.put("PackageName",PackageName);

                        customerdetails.add(map);
                    }

                     cda.notifyDataSetChanged();

                    /*da=new SimpleAdapter(CustomerListActivity.this,customerdetails,R.layout.customerlist,new String[]{"Name","TotalOutStandingAmount","AccountNo","MQNo","Address"},new int[]{R.id.textView31,R.id.textView32,R.id.textView34,R.id.textView36,R.id.textView37});
                    lvcustomer.setAdapter(da);*/


                    String toa=json.getString("TotalOutstanding").toString();

                    tvtotaloa.setText(str+format.format(Double.parseDouble(toa)));

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();

                  /*  if(da.getCount()==0) {

                        txtnocustomer.setVisibility(View.VISIBLE);
                    }*/
                    //finish();
                }


            }
            catch (JSONException e)
            {
                Toast.makeText(CustomerListActivity.this, "Error:++"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(CustomerListActivity.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }
}
