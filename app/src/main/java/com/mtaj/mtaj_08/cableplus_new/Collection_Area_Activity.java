package com.mtaj.mtaj_08.cableplus_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
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

public class Collection_Area_Activity extends AppCompatActivity {
    String str = "\u20B9";

    private static final String PREF_NAME = "LoginPref";

    ListView lstcolarea;

    ArrayList<HashMap<String, String>> areadetails = new ArrayList<>();

    SimpleAdapter da;

    private Calendar calendar;
    private int year, cmonth, day;

    EditText edtfrom, edtto;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    TextView tvtotalcol, tvtotaloa;

    String siteurl, uid, cid, aid, eid, URL, name;

    Toolbar toolbar;

    String tempto = "-", tempfrom = "-";

    RelativeLayout rlmain;

    String fromdate, todate;

    int mPage = 0;

    String totalcollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection__area_);

        Intent j = getIntent();
        totalcollection = j.getExtras().getString("Userthismonthcollection");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromdate = (cmonth + 1) + "/" + "1" + "/" + year;
        todate = (cmonth + 1) + "/" + day + "/" + year;

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("selected_uid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        aid = pref.getString("AreaId", "").toString();
        eid = pref.getString("Entityids", "").toString();

        name = pref.getString("Name", "").toString();

        rlmain = (RelativeLayout) findViewById(R.id.contents);

        lstcolarea = (ListView) findViewById(R.id.listcollectionarea);

        tvtotalcol = (TextView) findViewById(R.id.textView28);
        tvtotaloa = (TextView) findViewById(R.id.textView30);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setTitle(pref.getString("Name", "").toString()+" (50)");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

                // Intent i = new Intent(getApplicationContext(), DashBoard.class);
                // startActivity(i);


            }
        });


        URL = siteurl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";

        da = new SimpleAdapter(Collection_Area_Activity.this, areadetails, R.layout.layout_collection_area, new String[]{"AreaName", "TodayCollection", "Collection", "Outstanding"}, new int[]{R.id.textView31, R.id.textView43, R.id.textView47, R.id.textView45});
        lstcolarea.setAdapter(da);

        new JSONAsynk().execute(new String[]{URL});

        tvtotalcol.setText(totalcollection);

        lstcolarea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*                Toast.makeText(Collection_Area_Activity.this,areadetails.get(position).get("AreaId") , Toast.LENGTH_SHORT).show();
                Toast.makeText(Collection_Area_Activity.this,areadetails.get(position).get("AreaName") , Toast.LENGTH_SHORT).show();*/

                Intent i = new Intent(getApplicationContext(), activity_collection_customerdetail.class);
                i.putExtra("aid", areadetails.get(position).get("AreaId"));
                i.putExtra("aname", areadetails.get(position).get("AreaName"));
                i.putExtra("totalcollection", areadetails.get(position).get("Collection"));
                i.putExtra("totaloutstanding", areadetails.get(position).get("Outstanding"));
                i.putExtra("fromdate", fromdate);
                i.putExtra("todate", todate);
                startActivity(i);


            }
        });

        lstcolarea.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                mPage = mPage + 1;

                URL = siteurl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";

                new JSONAsynk().execute(new String[]{URL});

            }

            @Override
            public void onScrolling(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public JSONObject makeHttpRequest(String url) {
        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 50000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpGet httppost = new HttpGet(url);
        try {
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {


            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                if (reader != null) {

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try {
                    jobj = new JSONObject(json);

                    // JSONArray jarrays=new JSONArray(json);

                    // jobj=jarrays.getJSONObject(0);

                    //  org.json.simple.parser.JSONParser jsonparse=new org.json.simple.parser.JSONParser();

                    // jarr =(JSONArray)jsonparse.parse(json);
                    // jobj = jarr.getJSONObject(0);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }


    private class JSONAsynk extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        //public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1, jsn, jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload = new SpotsDialog(Collection_Area_Activity.this, R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();


        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {

                jsonobj = makeHttpRequest(params[0]);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            try {
                String ucount = "0", tc = "0", toa = "0";

                //  Toast.makeText(CustomerListActivity.this, json.toString(), Toast.LENGTH_SHORT).show();


                if (json.getString("status").toString().equals("True")) {
                    rlmain.setVisibility(View.VISIBLE);

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityarray = json.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        HashMap<String, String> map = new HashMap<>();

                        String aid = e.getString("AreaId");
                        String aname = e.getString("AreaName");
                        String acode = e.getString("AreaCode");
                        String aout = e.getString("Outstanding");
                        String acol = e.getString("Collection");
                        String todaycol = e.getString("TodayCollection");
                        String acolcount = e.getString("AreaCollectionCount");


                        map.put("AreaId", aid);
                        map.put("AreaName", aname + " - " + acolcount);
                        map.put("AreaCode", acode);
                        map.put("Outstanding", str + format.format(Double.parseDouble(aout)));
                        map.put("Collection", str + format.format(Double.parseDouble(acol)));
                        map.put("TodayCollection", str + format.format(Double.parseDouble(todaycol)));
                        map.put("AreaCollectionCount", str + acolcount);

                        areadetails.add(map);

                    }


                  /*  da=new SimpleAdapter(Collection_Area_Activity.this,areadetails,R.layout.layout_collection_area,new String[]{"AreaName","TodayCollection","Collection","Outstanding"},new int[]{R.id.textView31,R.id.textView43,R.id.textView47,R.id.textView45});
                    lstcolarea.setAdapter(da);*/

                    da.notifyDataSetChanged();

                    lstcolarea.setTextFilterEnabled(true);

                    ucount = json.getString("UserCollectionCount").toString();

                    toa = json.getString("TotalOutstanding").toString();
                    tc = json.getString("TotalCollection").toString();


                    tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));
                    tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));

                    toolbar.setTitle(name + " (" + ucount + ")");

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                } else {
                   /* DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    tvtotaloa.setText(str+format.format(Double.parseDouble(toa)));
                    tvtotalcol.setText(str+format.format(Double.parseDouble(tc)));

                    toolbar.setTitle(name+" ("+ucount+")");*/

                    da.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(Collection_Area_Activity.this, "Error:++" + e, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(Collection_Area_Activity.this, "Error:++" + ex, Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_datefilters:

                LayoutInflater li = getLayoutInflater();
                final View v = li.inflate(R.layout.dialog_date_search, null);

                edtfrom = (EditText) v.findViewById(R.id.edtfromdate);
                edtto = (EditText) v.findViewById(R.id.edttodate);

                if (!tempfrom.equals("-") && !tempto.equals("-")) {
                    edtto.setText(tempto);
                    edtfrom.setText(tempfrom);
                } else {
                    edtto.setText((cmonth + 1) + "/" + day + "/" + year);
                    edtfrom.setText((cmonth + 1) + "/" + "1" + "/" + year);
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


                MDDialog.Builder mdalert = new MDDialog.Builder(Collection_Area_Activity.this);
                mdalert.setContentView(v);
                mdalert.setTitle("Filter By Date");
                mdalert.setPositiveButton("SEARCH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Date to = new Date(edtto.getText().toString());
                        Date from = new Date(edtfrom.getText().toString());

                        tempfrom = edtfrom.getText().toString();
                        tempto = edtto.getText().toString();

                        if (to.after(from)) {
                            areadetails.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            //URL=siteurl+"/GetCollectionAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate;
                            mPage = 0;
                            URL = siteurl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";
                            new JSONAsynk().execute(new String[]{URL});
                        } else if (to.equals(from)) {
                            areadetails.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            // URL=siteurl+"/GetCollectionAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+eid+"&fromdate="+fromdate+"&todate="+todate;
                            mPage = 0;
                            URL = siteurl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";
                            new JSONAsynk().execute(new String[]{URL});
                        } else {
                            Toast.makeText(Collection_Area_Activity.this, "Enter Valid Filter Dates..", Toast.LENGTH_SHORT).show();
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
                mdalert.setCancelable(false);

                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();



               /* final AlertDialog.Builder alert = new AlertDialog.Builder(Collection_Area_Activity.this);
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

              /*  AlertDialog.Builder builder = new AlertDialog.Builder(Collection_Area_Activity.this));

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
                });

                final AlertDialog dialog = builder.create();

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
                });

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
        if (id == 888) {
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

            edtfrom.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);

        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtto.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);

        }
    };

}
