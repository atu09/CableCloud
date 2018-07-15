package com.cable.cloud.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import com.cable.cloud.InfiniteScrollListener;
import com.cable.cloud.R;
import com.cable.cloud.customs.MDDialog;
import com.cable.cloud.helpers.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CollectionAreaActivity extends AppCompatActivity {
    String str = "\u20B9";

    private static final String PREF_NAME = "LoginPref";

    ListView listView;

    ArrayList<HashMap<String, String>> areaDetails = new ArrayList<>();

    SimpleAdapter adapter;

    private Calendar calendar;
    private int year, cmonth, day;

    EditText edtfrom, edtto;

    TextView tvtotalcol, tvtotaloa;

    String siteUrl, uid, cid, aid, eid, URL, name;

    Toolbar toolbar;

    String tempto = "-", tempfrom = "-";

    String fromdate, todate;

    int mPage = 0;

    String totalcollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_area_revised);

        Intent j = getIntent();
        totalcollection = j.getExtras().getString("Userthismonthcollection");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromdate = (cmonth + 1) + "/" + "1" + "/" + year;
        todate = (cmonth + 1) + "/" + day + "/" + year;

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteUrl = pref.getString("SiteURL", "");
        uid = pref.getString("selected_uid", "");
        cid = pref.getString("Contracotrid", "");
        aid = pref.getString("AreaId", "");
        eid = pref.getString("Entityids", "");
        name = pref.getString("Name", "");

        listView = (ListView) findViewById(R.id.listcollectionarea);

        tvtotalcol = (TextView) findViewById(R.id.btnCancel);
        tvtotaloa = (TextView) findViewById(R.id.btnNext);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setTitle(pref.getString("Name", "").toString()+" (50)");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        URL = siteUrl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";

        adapter = new SimpleAdapter(CollectionAreaActivity.this, areaDetails, R.layout.layout_collection_area, new String[]{"AreaName", "TodayCollection", "Collection", "Outstanding"}, new int[]{R.id.textView31, R.id.textView43, R.id.textView47, R.id.textView45});
        listView.setAdapter(adapter);

        new JSONAsync().execute(new String[]{URL});

        tvtotalcol.setText(totalcollection);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), CollectionCustomerDetailsActivity.class);
                i.putExtra("aid", areaDetails.get(position).get("AreaId"));
                i.putExtra("aname", areaDetails.get(position).get("AreaName"));
                i.putExtra("totalcollection", areaDetails.get(position).get("Collection"));
                i.putExtra("totaloutstanding", areaDetails.get(position).get("Outstanding"));
                i.putExtra("fromdate", fromdate);
                i.putExtra("todate", todate);
                startActivity(i);


            }
        });

        listView.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                mPage = mPage + 1;

                URL = siteUrl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";

                new JSONAsync().execute(new String[]{URL});

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

        JSONObject jsonObject = null;

        try {

            HttpParams httpParameters = new BasicHttpParams();

            int timeoutConnection = 500000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            int timeoutSocket = 500000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpresponse = httpclient.execute(httpGet);
            HttpEntity httpentity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonObject = new JSONObject(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private class JSONAsync extends AsyncTask<String, String, JSONObject> {

        Dialog loader = Utils.getLoader(CollectionAreaActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (loader.isShowing()) {
                loader.dismiss();
            }

            try {

                if (json.getString("status").equalsIgnoreCase("True")) {

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityArray = json.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityArray.length(); i++) {
                        JSONObject e = (JSONObject) entityArray.get(i);

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

                        areaDetails.add(map);

                    }

                    adapter.notifyDataSetChanged();

                    listView.setTextFilterEnabled(true);

                    String ucount = json.getString("UserCollectionCount");
                    String toa = json.getString("TotalOutstanding");
                    String tc = json.getString("TotalCollection");


                    tvtotaloa.setText(str + format.format(Double.parseDouble(toa)));
                    tvtotalcol.setText(str + format.format(Double.parseDouble(tc)));

                    toolbar.setTitle(name + " (" + ucount + ")");

                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception ex) {
                ex.printStackTrace();
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


                MDDialog.Builder mdalert = new MDDialog.Builder(CollectionAreaActivity.this);
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
                            areaDetails.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            //URL=siteURL+"/GetCollectionAreaByUserForCollectionApp?contractorId="+contractorId+"&userId="+userId+"&entityId="+entities+"&fromdate="+fromdate+"&todate="+todate;
                            mPage = 0;
                            URL = siteUrl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";
                            new JSONAsync().execute(new String[]{URL});
                        } else if (to.equals(from)) {
                            areaDetails.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            // URL=siteURL+"/GetCollectionAreaByUserForCollectionApp?contractorId="+contractorId+"&userId="+userId+"&entityId="+entities+"&fromdate="+fromdate+"&todate="+todate;
                            mPage = 0;
                            URL = siteUrl + "/GetCollectionAreaByUserForCollectionApp?contractorId=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&startindex=" + String.valueOf(mPage) + "&noofrecords=10";
                            new JSONAsync().execute(new String[]{URL});
                        } else {
                            Toast.makeText(CollectionAreaActivity.this, "Enter Valid Filter Dates..", Toast.LENGTH_SHORT).show();
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
            edtfrom.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);

        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            edtto.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);

        }
    };

}
