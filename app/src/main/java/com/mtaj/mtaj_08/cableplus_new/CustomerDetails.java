package com.mtaj.mtaj_08.cableplus_new;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class CustomerDetails extends AppCompatActivity {


    ImageView imdropdown, imphoneedit, imemailedit, imadressedit, imdiscount, imageViewdropdown;

    RadioButton rbcash, rbcheque;

    EditText edphone, edemail, edaddress, edamount;

    ListView lvosdetails;

    ArrayList<HashMap<String, String>> months = new ArrayList<>();
    ArrayList<HashMap<String, String>> packagedetails = new ArrayList<>();

    TextView txtmakepayment;

    TableRow tblrow;
    LinearLayout llosdetails;
    private Calendar calendar;
    private int year, cmonth, day;
    EditText edtdate, edtbankname, edtchqno, edtdiscount, edtrdate, edtnotes;
    FloatingActionButton fabcomment;

    private static final String PREF_NAME = "LoginPref";

    String siteurl, cid, uid, acno, title, URL, URL1, custid, from, receiptDate = "-", isOsEditable;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    ScrollView scroll;

    JSONObject jsonobj;

    TextView tvacno, tvmqno, tvlastbill, tvlastpayment, tvcurrentbill, tvpaydate, tvtotaloa, txtarea, txtsmartcard;

    ArrayList<HashMap<String, String>> customerdetails = new ArrayList<>();

    String billid, accno, paymode = "1", paidamount, cheqdate = "", cheqno = "", bankname = "", notes;

    RadioGroup rgpayment;

    boolean isedited = false;

    RelativeLayout rlmain;

    SharedPreferences pref;

    SwipeMenuListView lstcomment;
    //SlidingUpPanelLayout splayout;

    DrawerLayout dl;

    ArrayList<HashMap<String, String>> commentlist = new ArrayList<>();
    RequestQueue requestQueue;

    android.widget.RelativeLayout lldr;
    CardView cvpackage;

    ArrayList<String> areaidList = new ArrayList<>();
    ArrayList<String> areanameList = new ArrayList<>();

    TextView txtname, txtaccountno;
    private TextInputLayout labelEditText8;
    private TextInputLayout labelEditText6;
    private TextInputLayout labelEditText7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);


        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(CustomerDetails.this);

        isOsEditable = pref.getString("isOutstandingEditable", "");
        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();

        Intent j = getIntent();
        title = j.getExtras().getString("cname");
        acno = j.getExtras().getString("A/cNo");
        custid = j.getExtras().getString("CustomerId");
        from = j.getExtras().getString("from");


        Init();

        final Drawable drawable = edphone.getBackground(); // get current EditText drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        // edamount.setBackground(drawable);

        //splayout=(SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        // splayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        receiptDate = (cmonth + 1) + "/" + day + "/" + year;

        //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        //receiptDate = sdf.format(calendar.getTime());

        // Log.e("RECIEPTDATE",receiptDate);


        URL = siteurl + "/GetCustomerPaymentDetailsNet9ForCollectionApp?customerId=" + custid;

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.ToolbarColor)));
                // set item width
                deleteItem.setWidth(150);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_done_white_18dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        lstcomment.setMenuCreator(creator);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Intent i = new Intent(getApplicationContext(), CustomerListActivity.class);
                //   startActivity(i);

                onBackPressed();


            }
        });

        new JSONAsynk().execute(new String[]{URL});

        registerForContextMenu(lstcomment);

        setListViewHeightBasedOnChildren(lvosdetails);

        if (isOsEditable.equals("true")) {
            imadressedit.setVisibility(View.VISIBLE);
            imphoneedit.setVisibility(View.VISIBLE);
            imemailedit.setVisibility(View.VISIBLE);

            final Drawable drawable1 = edphone.getBackground(); // get current EditText drawable
            drawable1.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

            final Drawable drawable11 = edemail.getBackground(); // get current EditText drawable
            drawable11.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

            final Drawable drawable12 = edaddress.getBackground(); // get current EditText drawable
            drawable12.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        } else {

            imadressedit.setVisibility(View.GONE);
            imphoneedit.setVisibility(View.GONE);
            imemailedit.setVisibility(View.GONE);

            final Drawable drawable1 = edphone.getBackground(); // get current EditText drawable
            drawable1.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            final Drawable drawable11 = edemail.getBackground(); // get current EditText drawable
            drawable11.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            final Drawable drawable12 = edaddress.getBackground(); // get current EditText drawable
            drawable12.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        }


        imadressedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isedited) {
                    try {
                        String addresss = edaddress.getText().toString();

                        URL1 = siteurl + "/UpdateCustomerAddress?customerid=" + custid + "&createdby=" + uid + "&address=" + URLEncoder.encode(addresss, "UTF-8");

                        JSONObject jsonobj = makeHttpRequest(URL1);
                        if (jsonobj.getString("status").toString().equals("True")) {
                            Toast.makeText(CustomerDetails.this, jsonobj.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            imadressedit.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                            isedited = false;
                            edaddress.setEnabled(false);
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(CustomerDetails.this, "Error+" + ex, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException ex) {
                        Toast.makeText(CustomerDetails.this, "Error+" + ex, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    edaddress.setEnabled(true);
                    edaddress.requestFocus();
                    showSoftKeyboard(edaddress);
                    imadressedit.setImageResource(R.drawable.ic_done_black_24dp);
                    isedited = true;

                }
            }
        });

        imphoneedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  edphone.setBackground(drawable);
                //  edamount.setBackground(drawable);

                //imphoneedit.getDrawable()==getResources().getDrawable(R.drawable.ic_done_black_24dp

                if (isedited) {
                    //do work here
                    URL1 = siteurl + "/UpdateCustomerPhone?customerid=" + custid + "&createdby=" + uid + "&phone=" + edphone.getText().toString();

                    try {
                        JSONObject jsonobj = makeHttpRequest(URL1);
                        if (jsonobj.getString("status").toString().equals("True")) {
                            Toast.makeText(CustomerDetails.this, jsonobj.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            imphoneedit.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                            isedited = false;
                            edphone.setEnabled(false);
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(CustomerDetails.this, "Error+" + ex, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    edphone.setEnabled(true);
                    edphone.requestFocus();
                    showSoftKeyboard(edphone);
                    imphoneedit.setImageResource(R.drawable.ic_done_black_24dp);
                    isedited = true;

                }
            }
        });

        imdiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isedited) {

                    int amount = Integer.parseInt(edamount.getText().toString()) - Integer.parseInt(edtdiscount.getText().toString());

                    edamount.setText(String.valueOf(amount));

                    imdiscount.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    isedited = false;
                    edtdiscount.setEnabled(false);
                } else {
                    imdiscount.setImageResource(R.drawable.ic_done_black_24dp);
                    isedited = true;
                    edtdiscount.setEnabled(true);
                    edtdiscount.requestFocus();
                }

            }
        });

        imemailedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isedited) {
                    //do work here
                    URL1 = siteurl + "/UpdateCustomerEmail?customerid=" + custid + "&createdby=" + uid + "&email=" + edemail.getText().toString();

                    try {
                        JSONObject jsonobj = makeHttpRequest(URL1);
                        if (jsonobj.getString("status").toString().equals("True")) {
                            Toast.makeText(CustomerDetails.this, jsonobj.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            imemailedit.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                            isedited = false;
                            edemail.setEnabled(false);
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(CustomerDetails.this, "Error+" + ex, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    edemail.setEnabled(true);
                    edemail.requestFocus();
                    showSoftKeyboard(edemail);
                    imemailedit.setImageResource(R.drawable.ic_done_black_24dp);
                    isedited = true;

                }

            }
        });


        imdropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (lvosdetails.isShown()) {
                    llosdetails.setVisibility(View.GONE);
                    tblrow.setVisibility(View.GONE);
                    lvosdetails.setVisibility(View.GONE);
                    imdropdown.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);


                } else {
                    scroll.scrollTo(0, scroll.getMaxScrollAmount());

                    llosdetails.setVisibility(View.VISIBLE);
                    tblrow.setVisibility(View.VISIBLE);
                    lvosdetails.setVisibility(View.VISIBLE);
                    imdropdown.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                }
            }
        });


        rbcheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {

                if (rbcheque.isChecked()) {

                    LayoutInflater li = getLayoutInflater();
                    final View v = li.inflate(R.layout.chequedialoglist, null);

                    edtdate = (EditText) v.findViewById(R.id.editText8);
                    edtbankname = (EditText) v.findViewById(R.id.editText6);
                    edtchqno = (EditText) v.findViewById(R.id.editText7);
                    edtrdate = (EditText) v.findViewById(R.id.editText9);
                    edtnotes = (EditText) v.findViewById(R.id.editText23);


                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    cmonth = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    edtdate.setText((cmonth + 1) + "/" + day + "/" + year);
                    edtbankname.setText(bankname);
                    edtchqno.setText(cheqno);

                    edtrdate.setHint("Select Date");

                    edtrdate.setText(receiptDate);

                    edtdate.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            hideSoftKeyboard(edtdate);

                            showDialog(999);

                            return false;
                        }
                    });

                    edtrdate.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            hideSoftKeyboard(edtdate);

                            showDialog(888);

                            return false;
                        }
                    });


                    final AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetails.this);
                    alert.setTitle("Cheque Details");
                    alert.setCancelable(false);
                    alert.setView(v);

                    // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            bankname = edtbankname.getText().toString();
                            cheqno = edtchqno.getText().toString();
                            cheqdate = edtdate.getText().toString();
                            receiptDate = edtrdate.getText().toString();
                            notes = edtnotes.getText().toString();

                            if (bankname.equals("") || cheqno.equals("") || cheqdate.equals("")) {
                                Toast.makeText(CustomerDetails.this, "Enter Valid Details...", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                            rbcash.setChecked(true);

                        }
                    });

                    AlertDialog dialog = alert.create();

                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

                    dialog.show();
                }

            }
        });


        rbcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {

                if (rbcash.isChecked()) {

                    LayoutInflater li = getLayoutInflater();
                    final View v = li.inflate(R.layout.chequedialoglist, null);

                    edtdate = (EditText) v.findViewById(R.id.editText8);
                    edtbankname = (EditText) v.findViewById(R.id.editText6);
                    edtchqno = (EditText) v.findViewById(R.id.editText7);
                    edtrdate = (EditText) v.findViewById(R.id.editText9);
                    edtnotes = (EditText) v.findViewById(R.id.editText23);

                    labelEditText8 = (TextInputLayout) v.findViewById(R.id.labelEditText8);
                    labelEditText6 = (TextInputLayout) v.findViewById(R.id.labelEditText6);
                    labelEditText7 = (TextInputLayout) v.findViewById(R.id.labelEditText7);

                    edtdate.setVisibility(View.GONE);
                    edtbankname.setVisibility(View.GONE);
                    edtchqno.setVisibility(View.GONE);

                    labelEditText8.setVisibility(View.GONE);
                    labelEditText6.setVisibility(View.GONE);
                    labelEditText7.setVisibility(View.GONE);


                    edtrdate.setHint("Select Date");

                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    cmonth = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    edtrdate.setText(receiptDate);

                    edtbankname.setText(bankname);
                    edtchqno.setText(cheqno);


                    edtrdate.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            hideSoftKeyboard(edtdate);

                            showDialog(888);

                            return false;
                        }
                    });


                    final AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetails.this);
                    alert.setTitle("Date of Payment");
                    alert.setCancelable(false);
                    alert.setView(v);

                    // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            receiptDate = edtrdate.getText().toString();
                            notes = edtnotes.getText().toString();

                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alert.create();

                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

                    dialog.show();
                }

            }
        });


        rgpayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int id = rgpayment.getCheckedRadioButtonId();
                RadioButton rb1 = (RadioButton) findViewById(id);

                if (rb1.getText().toString().equals("Cash")) {
                    paymode = "1";

                    bankname = "";
                    cheqdate = "";
                    cheqno = "";

                }
                if (rb1.getText().toString().equals("Cheque")) {
                    paymode = "2";
                }


            }
        });

        lstcomment.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        URL = siteurl + "/CompleteCustomerCommentForCollectionApp";

                        CallVolleys(URL, commentlist.get(position).get("CommentId").toString(), uid);

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        txtmakepayment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View vs) {

                try {

                    if (Integer.parseInt(edamount.getText().toString()) <= 0) {
                        Snackbar.make(vs, "Enter Valid Amount", Snackbar.LENGTH_LONG).show();
                    } else if (paymode.equals("0")) {
                        Snackbar.make(vs, "Please Select Payment Mode", Snackbar.LENGTH_LONG).show();
                    } else {

                        if (paymode.equals("2") && (bankname.equals("") || cheqno.equals("") || cheqdate.equals(""))) {
                            LayoutInflater li = getLayoutInflater();
                            final View v = li.inflate(R.layout.chequedialoglist, null);

                            edtdate = (EditText) v.findViewById(R.id.editText8);
                            edtbankname = (EditText) v.findViewById(R.id.editText6);
                            edtchqno = (EditText) v.findViewById(R.id.editText7);
                            edtnotes = (EditText) v.findViewById(R.id.editText23);
                            edtrdate = (EditText) v.findViewById(R.id.editText9);

                            calendar = Calendar.getInstance();
                            year = calendar.get(Calendar.YEAR);
                            cmonth = calendar.get(Calendar.MONTH);
                            day = calendar.get(Calendar.DAY_OF_MONTH);


                            edtdate.setText((cmonth + 1) + "/" + day + "/" + year);
                            edtbankname.setText(bankname);
                            edtchqno.setText(cheqno);

                            edtrdate.setHint("Select Date");

                            edtrdate.setText(receiptDate);

                            edtdate.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    hideSoftKeyboard(edtdate);

                                    showDialog(999);

                                    return false;
                                }
                            });

                            edtrdate.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    hideSoftKeyboard(edtdate);

                                    showDialog(888);

                                    return false;
                                }
                            });


                            final AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetails.this);
                            alert.setTitle("Cheque Details");
                            alert.setCancelable(false);
                            alert.setView(v);

                            // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bankname = edtbankname.getText().toString();
                                    cheqno = edtchqno.getText().toString();
                                    cheqdate = edtdate.getText().toString();
                                    receiptDate = edtrdate.getText().toString();
                                    notes = edtnotes.getText().toString();

                                    if (bankname.equals("") || cheqno.equals("") || cheqdate.equals("")) {
                                        Toast.makeText(CustomerDetails.this, "Enter Valid Details...", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                    rbcash.setChecked(true);

                                }
                            });

                            AlertDialog dialog = alert.create();

                            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

                            dialog.show();
                        } else if (paymode.equals("1") && receiptDate.equals("")) {
                            LayoutInflater li = getLayoutInflater();
                            final View v = li.inflate(R.layout.chequedialoglist, null);

                            edtdate = (EditText) v.findViewById(R.id.editText8);
                            edtbankname = (EditText) v.findViewById(R.id.editText6);
                            edtchqno = (EditText) v.findViewById(R.id.editText7);
                            edtrdate = (EditText) v.findViewById(R.id.editText9);
                            edtnotes = (EditText) v.findViewById(R.id.editText23);

                            labelEditText8 = (TextInputLayout) v.findViewById(R.id.labelEditText8);
                            labelEditText6 = (TextInputLayout) v.findViewById(R.id.labelEditText6);
                            labelEditText7 = (TextInputLayout) v.findViewById(R.id.labelEditText7);

                            edtdate.setVisibility(View.GONE);
                            edtbankname.setVisibility(View.GONE);
                            edtchqno.setVisibility(View.GONE);

                            labelEditText8.setVisibility(View.GONE);
                            labelEditText6.setVisibility(View.GONE);
                            labelEditText7.setVisibility(View.GONE);

                            edtrdate.setHint("Select Date");

                            calendar = Calendar.getInstance();
                            year = calendar.get(Calendar.YEAR);
                            cmonth = calendar.get(Calendar.MONTH);
                            day = calendar.get(Calendar.DAY_OF_MONTH);

                            edtrdate.setText(receiptDate);

                            edtbankname.setText(bankname);
                            edtchqno.setText(cheqno);


                            edtrdate.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    hideSoftKeyboard(edtdate);

                                    showDialog(888);

                                    return false;
                                }
                            });


                            final AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetails.this);
                            alert.setTitle("Date of Payment");
                            alert.setCancelable(false);
                            alert.setView(v);

                            // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    receiptDate = edtrdate.getText().toString();
                                    notes = edtnotes.getText().toString();

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            });

                            AlertDialog dialog = alert.create();

                            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

                            dialog.show();
                        } else {
                            //Toast.makeText(CustomerDetails.this, receiptDate, Toast.LENGTH_SHORT).show();


                            Intent i = new Intent(getApplicationContext(), CustomerSignatureActivity.class);
                            i.putExtra("cname", title);
                            i.putExtra("from", "Payment");
                            i.putExtra("billid", billid);
                            i.putExtra("acno", acno);
                            i.putExtra("paidamount", edamount.getText().toString());
                            i.putExtra("Discount", edtdiscount.getText().toString());
                            i.putExtra("paymentmode", paymode);
                            i.putExtra("chqno", cheqno);
                            i.putExtra("chqdate", cheqdate);
                            i.putExtra("bankname", bankname);
                            i.putExtra("email", edemail.getText().toString());
                            i.putExtra("CustomerId", custid);
                            i.putExtra("receiptDate", receiptDate);
                            i.putExtra("Notes", notes);
                            startActivity(i);

                            finish();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        cvpackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(CustomerDetails.this, PackageDetails.class);
                i.putExtra("title", title);
                i.putExtra("packagedetails", packagedetails);
                i.putExtra("CustomerId", custid);
                i.putExtra("A/cNo", accno);
                startActivity(i);

            }
        });


        txtarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areaidList.clear();
                areanameList.clear();

                //URL=siteurl+"/GetCustomersByAreaForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString()+"&startindex=0&noofrecords=1000000";

                //URL=siteurl+"/GetAllAreaForCollectionApp?contractorId="+cid;

                URL = siteurl + "/GetAreaByUser?userId=" + uid;

                new JSONAsynks().execute(new String[]{URL});


            }
        });

        fabcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LayoutInflater li = getLayoutInflater();
                View vs = li.inflate(R.layout.layout_add_comment, null);
                final EditText edtcomment = (EditText) vs.findViewById(R.id.editText5);
                txtname = (TextView) vs.findViewById(R.id.txtname);
                txtaccountno = (TextView) vs.findViewById(R.id.txtaccountno);

                txtname.setText(title);
                txtaccountno.setText(acno);


                MDDialog.Builder mdalert = new MDDialog.Builder(CustomerDetails.this);
                mdalert.setContentView(vs);
                mdalert.setTitle("Add Note");

                mdalert.setPositiveButton("Add Note", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        URL = siteurl + "/AddCustomerCommentAtPaymentTimeForCollectionApp";

                        HashMap<String, String> map = new HashMap<>();
                        map.put("loginuserId", uid);
                        map.put("customerId", custid);
                        map.put("comment", edtcomment.getText().toString());

                        CallVolleysAddComment(URL, map);

                    }
                });


                mdalert.setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


                mdalert.setWidthMaxDp(600);
                mdalert.setShowTitle(true);
                mdalert.setShowButtons(true);
                mdalert.setContentTextSizeDp(16);
                mdalert.setBackgroundCornerRadius(5);


                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();

            }
        });



       /* rbcheque.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    LayoutInflater li = getLayoutInflater();
                    final View v = li.inflate(R.layout.chequedialoglist, null);


                    AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetails.this);
                    alert.setTitle("Cheque Details");
                    alert.setView(v);

                    // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.show();
                }
            }
        });*/

    }

    public void Init() {

        rlmain = (RelativeLayout) findViewById(R.id.rlmain);
        scroll = (ScrollView) findViewById(R.id.scrollView);

        imdropdown = (ImageView) findViewById(R.id.imageView4);
        imphoneedit = (ImageView) findViewById(R.id.imageView5);
        imemailedit = (ImageView) findViewById(R.id.imageView6);
        imadressedit = (ImageView) findViewById(R.id.imageView9);
        imdiscount = (ImageView) findViewById(R.id.imageViewdis);
        imageViewdropdown = (ImageView) findViewById(R.id.imageViewdropdown);


        lvosdetails = (ListView) findViewById(R.id.listView2);

        tblrow = (TableRow) findViewById(R.id.tblrow);

        llosdetails = (LinearLayout) findViewById(R.id.llosdetails);

        edphone = (EditText) findViewById(R.id.editText4);
        edemail = (EditText) findViewById(R.id.editText3);
        edaddress = (EditText) findViewById(R.id.editText9);
        edamount = (EditText) findViewById(R.id.editText5);
        edtdiscount = (EditText) findViewById(R.id.editText22);

        tvacno = (TextView) findViewById(R.id.textView34);
        tvmqno = (TextView) findViewById(R.id.textView36);
        txtsmartcard = (TextView) findViewById(R.id.textView10);

        tvlastbill = (TextView) findViewById(R.id.textView43);
        tvlastpayment = (TextView) findViewById(R.id.textView45);
        tvcurrentbill = (TextView) findViewById(R.id.textView47);
        tvpaydate = (TextView) findViewById(R.id.textView48);
        tvtotaloa = (TextView) findViewById(R.id.textView50);
        txtarea = (TextView) findViewById(R.id.txtarea);

        rbcash = (RadioButton) findViewById(R.id.radioButton);
        rbcheque = (RadioButton) findViewById(R.id.radioButton2);

        rgpayment = (RadioGroup) findViewById(R.id.radiogroup1);

        txtmakepayment = (TextView) findViewById(R.id.textView29);

        lstcomment = (SwipeMenuListView) findViewById(R.id.listView8);
        dl = (DrawerLayout) findViewById(R.id.drawer_layout);

        lldr = (android.widget.RelativeLayout) findViewById(R.id.drawer_content);

        cvpackage = (CardView) findViewById(R.id.card_viewpackage);

        fabcomment = (FloatingActionButton) findViewById(R.id.fabcomment);

    }


    @Override
    public void onBackPressed() {
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        if (pref.getString("AreaStatus", "").toString().equals("true")) {

            if (from.equals("Payment")) {

                Intent i = new Intent(CustomerDetails.this, CustomerListActivity.class);
                startActivity(i);

                finish();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, cmonth, day);
        } else if (id == 888) {
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

            edtdate.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListeners = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            edtrdate.setText((arg2 + 1) + "/" + arg3 + "/" + arg1);
        }
    };


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminder_complete, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case R.id.action_complete:


                URL = siteurl + "/CompleteCustomerCommentForCollectionApp";

                CallVolleys(URL, commentlist.get(info.position).get("CommentId").toString(), uid);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.RESULT_HIDDEN);
        }
    }


    public JSONObject makeHttpRequest(String url) {

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient();
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
        // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1, jsn, jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload = new SpotsDialog(CustomerDetails.this, R.style.Custom);
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
                // Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
            }

            return jsonobj;


        }

        @Override
        protected void onPostExecute(JSONObject json) {
            spload.dismiss();

            String str = "\u20B9";
            try {

                Log.e("RESPONSE", json.toString());

                if (json.getString("status").toString().equals("True")) {
                    rlmain.setVisibility(View.VISIBLE);

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    billid = json.getString("BillId");
                    accno = json.getString("AccountNo");
                    String mqno = json.getString("MQNo");
                    String phone = json.getString("Phone");
                    String email = json.getString("Email");
                    String address = json.getString("Address");
                    String lastbill = json.getString("PreviousOutStandingAmount");
                    String lastpayment = json.getString("LastPayment");
                    String currentbil = json.getString("CurrentOutStandingAmount");
                    String paydate = json.getString("LastPaymentDate");
                    String totaloa = json.getString("TotalOutStandingAmount");
                    String areaname = json.getString("AreaName").toString();
                    String deviceno = json.getString("DeviceNo").toString();
                    String smartcardno = json.getString("SmartCardNo").toString();


                    final JSONArray entityarray = json.getJSONArray("lstBillReceiptCustomerInfo");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        HashMap<String, String> map = new HashMap<>();

                        String bmonth = e.getString("BillMonth");
                        String oa = e.getString("Outstanding");
                        String bamount = e.getString("BillAmount");
                        String PaidAmount = e.getString("PaidAmount");

                        map.put("BillMonth", bmonth);
                        map.put("Outstanding", str + format.format(Double.parseDouble(oa)));
                        map.put("BillAmount", str + format.format(Double.parseDouble(bamount)));
                        map.put("PaidAmount", str + format.format(Double.parseDouble(PaidAmount)));

                        months.add(map);

                    }


                    final JSONArray entityarray2 = json.getJSONArray("lstBasePkgInfo");
                    loadpackagedetails(entityarray2);


                    final JSONArray entityarray1 = json.getJSONArray("lstCommentInfo");

                    for (int i = 0; i < entityarray1.length(); i++) {
                        JSONObject e = (JSONObject) entityarray1.get(i);

                        String cmid = e.getString("CommentId");
                        String comment = e.getString("Comment");

                        HashMap<String, String> map = new HashMap<>();
                        map.put("CommentId", cmid);
                        map.put("Comment", comment);

                        commentlist.add(map);
                    }

                    final SimpleAdapter das = new SimpleAdapter(CustomerDetails.this, commentlist, R.layout.layout_customer_comment, new String[]{"Comment"}, new int[]{R.id.textView96});
                    lstcomment.setAdapter(das);


                    tvacno.setText(acno);
                    tvmqno.setText(deviceno);
                    txtsmartcard.setText(smartcardno);
                    edphone.setText(phone);
                    edemail.setText(email);
                    edaddress.setText(address);
                    tvlastbill.setText(str + lastbill);
                    tvlastpayment.setText(str + format.format(Double.parseDouble(lastpayment)));
                    tvcurrentbill.setText(str + currentbil);
                    tvpaydate.setText(paydate);
                    tvtotaloa.setText(str + totaloa);
                    txtarea.setText(areaname);

                    edamount.setText(totaloa);

                    final SimpleAdapter da = new SimpleAdapter(CustomerDetails.this, months, R.layout.outstandingdetaillayout, new String[]{"BillMonth", "BillAmount", "PaidAmount"}, new int[]{R.id.textView57, R.id.textView58, R.id.textView59});
                    lvosdetails.setAdapter(da);

                    setListViewHeightBasedOnChildren(lvosdetails);

                    TextView tvempty = (TextView) findViewById(R.id.textView103);

                    if (commentlist.size() > 0) {

                        tvempty.setVisibility(View.GONE);

                        dl.openDrawer(lldr);

                    } else {

                        lstcomment.setEmptyView(tvempty);

                        tvempty.setVisibility(View.VISIBLE);
                    }

                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), json.getString("message").toString(), Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error:++" + ex, Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void loadpackagedetails(JSONArray a) {
        String str = "\u20B9";

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (a.length() > 0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgId", e.getString("PkgId"));
                    map.put("PkgName", e.getString("PkgName"));
                    map.put("Price", str + format.format(Double.parseDouble(e.getString("Price"))));
                    map.put("DeviceNo", e.getString("DeviceNo"));
                    map.put("MQNo", e.getString("MQNo"));
                    map.put("Ptype", e.getString("Ptype"));
                    map.put("SmartCardNo", e.getString("SmartCardNo"));

                    packagedetails.add(map);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void CallVolleys(String a, String cid, String usid) {


        final SpotsDialog spload;
        spload = new SpotsDialog(CustomerDetails.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            HashMap<String, String> map = new HashMap<>();
            map.put("commentId", cid);
            map.put("userId", usid);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        Toast.makeText(CustomerDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(getApplicationContext(), CustomerDetails.class);
                                        i.putExtra("cname", title);
                                        i.putExtra("A/cNo", acno);
                                        i.putExtra("CustomerId", custid);
                                        i.putExtra("from", "Payment");
                                        startActivity(i);

                                        finish();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    private class JSONAsynks extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1, jsn, jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload = new SpotsDialog(CustomerDetails.this, R.style.Custom);
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

                if (json.getString("status").toString().equals("True")) {
                    rlmain.setVisibility(View.VISIBLE);

                    final JSONArray entityarray = jsonobj.getJSONArray("AreaInfoList");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        String aid = e.getString("AreaId");
                        String aname = TextUtils.htmlEncode(e.getString("AreaName"));

                        areaidList.add(aid);
                        areanameList.add(aname);

                    }

                    if (areaidList.size() > 0) {
                        final ListView lv = new ListView(CustomerDetails.this);
                        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        lv.setDividerHeight(0);


                        final ArrayAdapter<String> da = new ArrayAdapter<String>(CustomerDetails.this, android.R.layout.simple_list_item_single_choice, areanameList);
                        lv.setAdapter(da);

                        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(CustomerDetails.this);
                        builderDialog.setView(lv);
                        builderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (lv.getCheckedItemPosition() == -1) {
                                    Toast.makeText(CustomerDetails.this, "Please select atleast one Area..!!", Toast.LENGTH_LONG).show();
                                } else {

                                    URL = siteurl + "/UpdateAreaForCollectionApp";

                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("contractorId", cid);
                                    map.put("UserId", uid);
                                    map.put("CustomerId", custid);
                                    map.put("AreaId", areaidList.get(lv.getCheckedItemPosition()));


                                    CallVolleyUpdateArea(URL, map);

                                }
                            }
                        });

                        final AlertDialog alert = builderDialog.create();
                        alert.setTitle("Select Area");
                        alert.setCancelable(true);
                        alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                        alert.show();
                    }


                } else {

                    Toast.makeText(CustomerDetails.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void CallVolleyUpdateArea(String a, HashMap<String, String> map) {


        final SpotsDialog spload;
        spload = new SpotsDialog(CustomerDetails.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        Toast.makeText(CustomerDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(getApplicationContext(), CustomerDetails.class);
                                        i.putExtra("cname", title);
                                        i.putExtra("A/cNo", acno);
                                        i.putExtra("CustomerId", custid);
                                        i.putExtra("from", "Payment");
                                        startActivity(i);

                                        finish();

                                    } else {
                                        Toast.makeText(CustomerDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleysAddComment(String a, HashMap<String, String> map) {


        final SpotsDialog spload;
        spload = new SpotsDialog(CustomerDetails.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        Toast.makeText(CustomerDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(getApplicationContext(), CustomerDetails.class);
                                        i.putExtra("cname", title);
                                        i.putExtra("A/cNo", acno);
                                        i.putExtra("CustomerId", custid);
                                        i.putExtra("from", "Payment");
                                        startActivity(i);

                                        finish();
                                    } else {
                                        Toast.makeText(CustomerDetails.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

}
