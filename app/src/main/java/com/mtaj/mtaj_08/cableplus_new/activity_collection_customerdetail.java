package com.mtaj.mtaj_08.cableplus_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class activity_collection_customerdetail extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String str = "\u20B9";
    private static final String PREF_NAME = "LoginPref";

    ListView lvcustomer;

    ArrayList<HashMap<String,String>> customerlist=new ArrayList<>();

    SimpleAdapter da;

    private Calendar calendar;
    private int year, cmonth, day;

    EditText edtfrom,edtto;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    String siteurl,uid,cid,aid,eid,URL,tc,toa,areaid,name;

    TextView tvtotalcol,tvtotaloa;

    String tempto="-",tempfrom="-";

    RelativeLayout rlmain;

    String fromdate,todate;

    int mPage=0;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_collection_customerdetail);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromdate=(cmonth + 1) + "/" + "1" + "/" + year;
        todate=(cmonth + 1) + "/" + day + "/" + year;

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        name=pref.getString("Name", "").toString();

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("selected_uid","").toString();
        cid=pref.getString("Contracotrid","").toString();
        aid=pref.getString("AreaId","").toString();
        eid=pref.getString("Entityids","").toString();

        lvcustomer=(ListView)findViewById(R.id.listcustomerdetail);

        tvtotalcol=(TextView)findViewById(R.id.textView28);
        tvtotaloa=(TextView)findViewById(R.id.textView30);

        rlmain=(RelativeLayout)findViewById(R.id.content);


       /* Intent j=getIntent();
        final String areatitle=j.getExtras().getString("aname");
        areaid=j.getExtras().getString("aid");
        tc=j.getExtras().getString("totalcollection");
        toa=j.getExtras().getString("totaloutstanding");
        fromdate=j.getExtras().getString("fromdate");
        todate=j.getExtras().getString("todate");*/


        //tvtotaloa.setText(toa);
        //tvtotalcol.setText(tc);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Intent i = new Intent(getApplicationContext(), Collection_Area_Activity.class);
                //startActivity(i);
                onBackPressed();
            }
        });



        //URL=siteurl+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&areadId="+areaid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";


        URL=siteurl+"/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";

        new JSONAsynk().execute(new String[]{URL});

        da = new SimpleAdapter(activity_collection_customerdetail.this, customerlist, R.layout.layout_colletion_customerdetail, new String[]{"Name", "CollectinAmount", "AccountNo", "MQNo", "ReceiptDate"}, new int[]{R.id.textView31, R.id.textView49, R.id.textView43, R.id.textView47, R.id.textView52});
        lvcustomer.setAdapter(da);

        lvcustomer.setTextFilterEnabled(true);

        lvcustomer.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                mPage = mPage+1;

                //URL=siteurl+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&areadId="+areaid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";

                URL=siteurl+"/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";

                new JSONAsynk().execute(new String[]{URL});

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    @Override
    public void onBackPressed()
    {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_detail, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.searchcollection);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(Color.WHITE);

        searchView.setFocusable(false);

        searchView.setQueryHint("Search Customers");
        searchView.setIconifiedByDefault(false);

        searchView.setFocusableInTouchMode(true);


        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        da.getFilter().filter(newText);
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

            spload=new SpotsDialog(activity_collection_customerdetail.this,R.style.Custom);
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
                //  Toast.makeText(CustomerListActivity.this, json.toString(), Toast.LENGTH_SHORT).show();
                if(json.getString("status").toString().equals("True"))
                {

                   // Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_SHORT).show();

                    rlmain.setVisibility(View.VISIBLE);

                    String str = "\u20B9";

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityarray = json.getJSONArray("CustomerInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        HashMap<String,String> map=new HashMap<>();

                        String cid = e.getString("CustomerId");
                        String cname = e.getString("Name");
                        String caccno = e.getString("AccountNo");
                        String cmqno = e.getString("MQNo");
                        String ccolamt = e.getString("CollectinAmount");
                        String crcptdate = e.getString("ReceiptDate");

                        map.put("CustomerId",cid);
                        map.put("Name",cname);
                        map.put("AccountNo",caccno);
                        map.put("MQNo",cmqno);
                        map.put("CollectinAmount",str+format.format(Double.parseDouble(ccolamt)));
                        map.put("ReceiptDate",crcptdate);

                        customerlist.add(map);

                    }

                    tvtotaloa.setText(str+format.format(Double.parseDouble(json.getString("TotalOutstanding"))));
                    tvtotalcol.setText(str+format.format(Double.parseDouble(json.getString("TotalCollection"))));

                    toolbar.setTitle(name+" - "+json.getString("TotalCount"));


                       /* da = new SimpleAdapter(activity_collection_customerdetail.this, customerlist, R.layout.layout_colletion_customerdetail, new String[]{"Name", "CollectinAmount", "AccountNo", "MQNo", "ReceiptDate"}, new int[]{R.id.textView31, R.id.textView49, R.id.textView43, R.id.textView47, R.id.textView52});
                        lvcustomer.setAdapter(da);*/

                    da.notifyDataSetChanged();

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show()
                }
                else
                {
                    da.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();
                }


            }
            catch (JSONException e)
            {
                Toast.makeText(activity_collection_customerdetail.this, "JSON:++"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(activity_collection_customerdetail.this, "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

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
                    edtto.setText(todate);
                    edtfrom.setText(fromdate);
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


                MDDialog.Builder mdalert=new MDDialog.Builder(activity_collection_customerdetail.this);
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
                            customerlist.clear();

                            fromdate=edtfrom.getText().toString();
                            todate=edtto.getText().toString();

                            //URL=siteurl+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&areadId="+areaid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";
                            mPage=0;

                            URL=siteurl+"/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";

                            new JSONAsynk().execute(new String[]{URL});
                        }
                        else if(to.equals(from))
                        {
                            customerlist.clear();

                            fromdate=edtfrom.getText().toString();
                            todate=edtto.getText().toString();

                            //URL=siteurl+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&areadId="+areaid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";
                            mPage=0;

                            URL=siteurl+"/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(mPage)+"&noofrecords=20&contractorid="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";

                            new JSONAsynk().execute(new String[]{URL});
                        }
                        else
                        {
                            Toast.makeText(activity_collection_customerdetail.this, "Enter Valid Filter Dates..", Toast.LENGTH_SHORT).show();
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





               /*final AlertDialog.Builder alert = new AlertDialog.Builder(activity_collection_customerdetail.this);
                alert.setTitle("Filter By Date");
                alert.setView(v);

                alert.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog=alert.create();

                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();*/

                /*AlertDialog.Builder builder = new AlertDialog.Builder(activity_collection_customerdetail.this);

                // setup your dialog here...

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // do something
                    }
                });

                builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // do something
                    }
                });*/

               /* final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                        // this not working because multiplying white background (e.g. Holo Light) has no effect
                        //negativeButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);

                        final Drawable negativeButtonDrawable = getResources().getColor(CO);
                        final Drawable positiveButtonDrawable = getResources().getDrawable(R.drawable.alert_dialog_button_light_green);
                        if (Build.VERSION.SDK_INT >= 16) {
                            negativeButton.setBackground(negativeButtonDrawable);
                            positiveButton.setBackground(positiveButtonDrawable);
                        } else {
                            negativeButton.setBackgroundDrawable(negativeButtonDrawable);
                            positiveButton.setBackgroundDrawable(positiveButtonDrawable);
                        }

                        negativeButton.invalidate();
                        positiveButton.invalidate();
                    }
                });*/

                // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT))



               /* LinearLayout ll=(LinearLayout)findViewById(R.id.lldatesearch);

                if(ll.isShown())
                {
                    ll.setVisibility(View.GONE);



                }
                else
                {
                    ll.setVisibility(View.VISIBLE);

                }*/
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
