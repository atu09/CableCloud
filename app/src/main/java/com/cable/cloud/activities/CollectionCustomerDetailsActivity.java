package com.cable.cloud.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

public class CollectionCustomerDetailsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    String str = "\u20B9";
    private static final String PREF_NAME = "LoginPref";

    ListView listView;

    ArrayList<HashMap<String, String>> customerList = new ArrayList<>();

    SimpleAdapter adapter;

    private Calendar calendar;
    private int year, cmonth, day;

    EditText edtfrom, edtto;
    private Context context;

    String siteUrl, uid, cid, aid, eid, URL, tc, toa, areaid, name;

    TextView tvtotalcol, tvtotaloa;

    String tempto = "-", tempfrom = "-";

    String fromdate, todate;

    int mPage = 0;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_customerdetail_rev);

        context = this;

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        fromdate = (cmonth + 1) + "/" + "1" + "/" + year;
        todate = (cmonth + 1) + "/" + day + "/" + year;

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        name = pref.getString("Name", "");
        siteUrl = pref.getString("SiteURL", "");
        uid = pref.getString("selected_uid", "");
        cid = pref.getString("Contracotrid", "");
        aid = pref.getString("AreaId", "");
        eid = pref.getString("Entityids", "");

        listView = (ListView) findViewById(R.id.listcustomerdetail);

        tvtotalcol = (TextView) findViewById(R.id.btnCancel);
        tvtotaloa = (TextView) findViewById(R.id.btnNext);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        URL = siteUrl + "/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=20&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&filterCustomer=";

        new JSONAsync().execute(new String[]{URL});

        adapter = new SimpleAdapter(CollectionCustomerDetailsActivity.this, customerList, R.layout.layout_colletion_customerdetail, new String[]{"Name", "CollectinAmount", "AccountNo", "MQNo", "ReceiptDate"}, new int[]{R.id.textView31, R.id.textView49, R.id.textView43, R.id.textView47, R.id.textView52});
        listView.setAdapter(adapter);

        listView.setTextFilterEnabled(true);

        listView.setOnScrollListener(new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                mPage = mPage + 1;
                URL = siteUrl + "/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=20&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&filterCustomer=";

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
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
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
        adapter.getFilter().filter(newText);
        return true;
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

        Dialog dialog = Utils.getLoader(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return makeHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            dialog.dismiss();

            try {

                if (json.getString("status").equalsIgnoreCase("True")) {

                    String str = "\u20B9";

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    final JSONArray entityarray = json.getJSONArray("CustomerInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        HashMap<String, String> map = new HashMap<>();

                        String cid = e.getString("CustomerId");
                        String cname = e.getString("Name");
                        String caccno = e.getString("AccountNo");
                        String cmqno = e.getString("MQNo");
                        String ccolamt = e.getString("CollectinAmount");
                        String crcptdate = e.getString("ReceiptDate");

                        map.put("CustomerId", cid);
                        map.put("Name", cname);
                        map.put("AccountNo", caccno);
                        map.put("MQNo", cmqno);
                        map.put("CollectinAmount", str + format.format(Double.parseDouble(ccolamt)));
                        map.put("ReceiptDate", crcptdate);

                        customerList.add(map);

                    }

                    tvtotaloa.setText(str + format.format(Double.parseDouble(json.getString("TotalOutstanding"))));
                    tvtotalcol.setText(str + format.format(Double.parseDouble(json.getString("TotalCollection"))));

                    toolbar.setTitle(name + " - " + json.getString("TotalCount"));

                    adapter.notifyDataSetChanged();

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


                MDDialog.Builder mdalert = new MDDialog.Builder(CollectionCustomerDetailsActivity.this);
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
                            customerList.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            //URL=siteURL+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(page)+"&noofrecords=20&contractorid="+contractorId+"&areadId="+areaid+"&userId="+userId+"&entityId="+entities+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";
                            mPage = 0;

                            URL = siteUrl + "/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=20&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&filterCustomer=";

                            new JSONAsync().execute(new String[]{URL});
                        } else if (to.equals(from)) {
                            customerList.clear();

                            fromdate = edtfrom.getText().toString();
                            todate = edtto.getText().toString();

                            //URL=siteURL+"/GetAreawiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex="+String.valueOf(page)+"&noofrecords=20&contractorid="+contractorId+"&areadId="+areaid+"&userId="+userId+"&entityId="+entities+"&fromdate="+fromdate+"&todate="+todate+"&filterCustomer=";
                            mPage = 0;

                            URL = siteUrl + "/GetUserwiseCustomerCollectinByUserNEntityIdsForCollectionApp?startindex=" + String.valueOf(mPage) + "&noofrecords=20&contractorid=" + cid + "&userId=" + uid + "&entityId=" + eid + "&fromdate=" + fromdate + "&todate=" + todate + "&filterCustomer=";

                            new JSONAsync().execute(new String[]{URL});
                        } else {
                            Toast.makeText(CollectionCustomerDetailsActivity.this, "Enter Valid Filter Dates..", Toast.LENGTH_SHORT).show();
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


                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();





               /*final AlertDialog.Builder alert = new AlertDialog.Builder(CollectionCustomerDetailsActivity.this);
                alert.setTitle("Filter By Date");
                alert.setView(v);

                alert.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface loader, int which) {


                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface loader, int which) {

                    }
                });

                AlertDialog loader=alert.create();

                loader.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                loader.show();*/

                /*AlertDialog.Builder builder = new AlertDialog.Builder(CollectionCustomerDetailsActivity.this);

                // setup your loader here...

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface loader, final int which) {
                        // do something
                    }
                });

                builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface loader, final int which) {
                        // do something
                    }
                });*/

               /* final AlertDialog loader = builder.create();

                loader.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface loader) {
                        Button negativeButton = ((AlertDialog) loader).getButton(DialogInterface.BUTTON_NEGATIVE);
                        Button positiveButton = ((AlertDialog) loader).getButton(DialogInterface.BUTTON_POSITIVE);

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
