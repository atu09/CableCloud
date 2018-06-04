package com.mtaj.mtaj_08.cableplus_new;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.apache.commons.codec.binary.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;
import rebus.bottomdialog.BottomDialog;

public class CustomerMasterDetailsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    CardView cv6,cv7,cv8,cv5,cv3,cv2,cv4,cv1;

    Calendar calendar;
    int year,cmonth,day;

    EditText edtdate,edttext;

    TextView tvreminder,tvattachment,tvcomplaint;

    EditText edtpaidamount;

    private BottomDialog dialog;

        public int REQUEST_CAMERA=123;

    public int SELECT_IMAGE=345;

    public int SELECT_FILE=888;


    String title,acno,cid,URL,siteurl,MQno,uid,URL1,imageString;

    TextView tvacno,tvmqno,tvarea,tvmakepayment;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    String str = "\u20B9";

    ArrayList<HashMap<String,String>> generaldetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> packagedetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> billdetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> paymnetdetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> complaindetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> remiderdetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> attachmentdetails=new ArrayList<>();
    ArrayList<HashMap<String,String>> smsdetails=new ArrayList<>();


    RelativeLayout rlmain;

    String smallImagePath;
    String converted64=null;

    SwipeMenuListView lstcomment;
    DrawerLayout dl;

    ArrayList<HashMap<String,String>> commentlist=new ArrayList<>();
    RequestQueue requestQueue;

    LinearLayout lldr;
    ImageView imgselectfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_master_details);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        cid=pref.getString("Contracotrid", "").toString();
        siteurl=pref.getString("SiteURL", "").toString();
        uid=pref.getString("Userid", "").toString();

        Intent j=getIntent();
       title=j.getExtras().getString("cname");
        acno=j.getExtras().getString("A/cNo");
        MQno=j.getExtras().getString("MQno");

        URL=siteurl+"/GetCustomerAllDetailsForCollectionApp?contractorid="+cid+"&accountNo="+acno;

        cv6=(CardView)findViewById(R.id.card_view6);
        cv7=(CardView)findViewById(R.id.card_view7);
        cv8=(CardView)findViewById(R.id.card_view8);
        cv5=(CardView)findViewById(R.id.card_view5);
        cv3=(CardView)findViewById(R.id.card_view3);
        cv2=(CardView)findViewById(R.id.card_view2);
        cv4=(CardView)findViewById(R.id.card_view4);
        cv1=(CardView)findViewById(R.id.card_view1);

        tvreminder=(TextView)findViewById(R.id.textView77);
        tvattachment=(TextView)findViewById(R.id.textView81);
        tvcomplaint=(TextView)findViewById(R.id.textView75);

        rlmain=(RelativeLayout)findViewById(R.id.content);

        edtpaidamount=(EditText)findViewById(R.id.editText5);

        tvacno=(TextView)findViewById(R.id.textView34);
        tvmqno=(TextView)findViewById(R.id.textView36);
        tvarea=(TextView)findViewById(R.id.textView2);

        tvmakepayment=(TextView)findViewById(R.id.textView29);

        lstcomment=(SwipeMenuListView)findViewById(R.id.listView8);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);

        lldr=(LinearLayout)findViewById(R.id.drawer_content);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                //  startActivity(i);

                onBackPressed();
            }
        });

        if(pref.getString("RoleId","").toString().equals("2"))
        {
            Drawable image = this.getResources().getDrawable(R.drawable.ic_add_black_24dp );
            image.setBounds( 0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight() );

            tvcomplaint.setCompoundDrawablesWithIntrinsicBounds(null,null,image,null);
            tvcomplaint.setEnabled(true);
        }
        else if(pref.getBoolean("IsComplain",true))
        {
            Drawable image = this.getResources().getDrawable(R.drawable.ic_add_black_24dp );
            image.setBounds( 0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight() );

            tvcomplaint.setCompoundDrawablesWithIntrinsicBounds(null,null,image,null);
            tvcomplaint.setEnabled(true);
        }

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

        tvacno.setText(acno);
        tvmqno.setText(MQno);

        new JSONAsynk().execute(new String[]{URL});



        registerForContextMenu(lstcomment);

        cv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Remider_details.class);
                i.putExtra("title", title);
                i.putExtra("remiderdetails", remiderdetails);
                startActivity(i);

            }
        });


        cv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),Attachment_details.class);
                i.putExtra("title",title);
                i.putExtra("attachmentdetails",attachmentdetails);
                startActivity(i);
            }
        });

        cv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    Intent i=new Intent(getApplicationContext(),Sms_log.class);
                    i.putExtra("title",title);
                    i.putExtra("smsdetails",smsdetails);
                    startActivity(i);

                }
                else {

                    Intent i = new Intent(getApplicationContext(),Sms_log.class);
                    i.putExtra("title", title);
                    i.putExtra("smsdetails",smsdetails);
                    startActivityWithOptions(i);
                }

            }
        });

        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),ComplainListviaCustomerActivity.class);
                i.putExtra("title",title);
                i.putExtra("complaindetails",complaindetails);
                i.putExtra("Mode","Customers");
                startActivity(i);

            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    Intent i=new Intent(getApplicationContext(),Bill_details.class);
                    i.putExtra("title",title);
                    i.putExtra("billdetails",billdetails);
                    startActivity(i);

                }
                else {

                    Intent i=new Intent(getApplicationContext(),Bill_details.class);
                    i.putExtra("title",title);
                    i.putExtra("billdetails",billdetails);
                    startActivityWithOptions(i);
                }




            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    String custid = generaldetails.get(0).get("CustomerId");

                    Intent i=new Intent(getApplicationContext(),PackageDetails.class);
                    i.putExtra("title",title);
                    i.putExtra("packagedetails",packagedetails);
                    i.putExtra("CustomerId", custid);
                    i.putExtra("A/cNo", acno);
                    startActivity(i);

                }
                else {

                    String custid = generaldetails.get(0).get("CustomerId");

                    Intent i=new Intent(getApplicationContext(),PackageDetails.class);
                    i.putExtra("title",title);
                    i.putExtra("packagedetails",packagedetails);
                    i.putExtra("CustomerId", custid);
                    i.putExtra("A/cNo", acno);
                    startActivityWithOptions(i);
                }



            }
        });

        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    Intent i=new Intent(getApplicationContext(),Payment_details.class);
                    i.putExtra("title",title);
                    i.putExtra("paymnetdetails",paymnetdetails);
                    startActivity(i);

                }
                else {

                    Intent i=new Intent(getApplicationContext(),Payment_details.class);
                    i.putExtra("title",title);
                    i.putExtra("paymnetdetails",paymnetdetails);
                    startActivityWithOptions(i);
                }



            }
        });

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    Intent i=new Intent(getApplicationContext(),Contact_details.class);
                    i.putExtra("title",title);
                    i.putExtra("MQno",MQno);
                    i.putExtra("generaldetails",generaldetails);
                    startActivity(i);

                }
                else {

                    Intent i=new Intent(getApplicationContext(),Contact_details.class);
                    i.putExtra("title",title);
                    i.putExtra("MQno",MQno);
                    i.putExtra("generaldetails",generaldetails);
                    startActivityWithOptions(i);
                }



            }
        });


        tvcomplaint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= tvcomplaint.getRight() - tvcomplaint.getTotalPaddingRight()) {
                        // your action for drawable click event


                        LayoutInflater li = getLayoutInflater();
                        View vd = li.inflate(R.layout.dialog_new_complaint, null);

                        AlertDialog.Builder adb = new AlertDialog.Builder(CustomerMasterDetailsActivity.this);
                        adb.setView(vd);

                        final AlertDialog ad = adb.create();
                        ad.show();

                        imgselectfile = (ImageView) vd.findViewById(R.id.img_btn_add_file);
                        final EditText edtsubject = (EditText) vd.findViewById(R.id.et_subject);
                        final EditText edtmessage = (EditText) vd.findViewById(R.id.et_message);
                        Button btnsubmit = (Button) vd.findViewById(R.id.button5);

                        imgselectfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMasterDetailsActivity.this);
                                builder.setSingleChoiceItems(R.array.choose_image_option, 0, null);
                                builder.setTitle("Choose Address");
                                builder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int index) {
                                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                                switch (selectedPosition) {
                                                    case 0:

                                                        cameraIntents();

                                                        break;
                                                    case 1:

                                                        galleryIntents();

                                                        break;
                                                }
                                            }
                                        });
                                builder.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                dialog.dismiss();
                                            }
                                        });
                                builder.show();


                            }
                        });


                        btnsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ValidateEdittext("Enter Message", edtmessage) && ValidateEdittext("Enter Subject", edtsubject)) {
                                    URL = siteurl + "/SaveComplainForCustomerApp";


                                    /*map.put("Name", json.getString("Name"));
                                    map.put("Address", json.getString("Address"));
                                    map.put("Area", json.getString("Area"));
                                    map.put("AccountNo", json.getString("AccountNo"));
                                    map.put("Phone", json.getString("Phone"));
                                    map.put("Email", json.getString("Email"));*/

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("contractorid", cid);
                                    map.put("accountNo", acno);
                                    map.put("name", generaldetails.get(0).get("Name"));
                                    map.put("phone", generaldetails.get(0).get("Phone"));
                                    map.put("email", generaldetails.get(0).get("Email"));
                                    map.put("subject", edtsubject.getText().toString());
                                    map.put("message", edtmessage.getText().toString());
                                    map.put("image", imageString);

                                    Log.e("MAP", map.toString());

                                    CallVolleysAddcomplaint(URL, map);

                                    ad.dismiss();
                                }

                            }
                        });


                    }
                }

                return true;
            }
        });



        tvreminder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= tvreminder.getRight() - tvreminder.getTotalPaddingRight()) {
                        // your action for drawable click event

                        LayoutInflater li = getLayoutInflater();
                        View vv = li.inflate(R.layout.reminder_dialog, null);

                        edtdate = (EditText) vv.findViewById(R.id.editText8);
                        edttext = (EditText) vv.findViewById(R.id.editText7);

                        calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        cmonth = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);

                        edtdate.setText((cmonth + 1) + "/" + day + "/" + year);

                        edtdate.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                showDialog(999);

                                return false;
                            }
                        });


                        MDDialog.Builder mdalert = new MDDialog.Builder(CustomerMasterDetailsActivity.this);
                        mdalert.setContentView(vv);
                        mdalert.setTitle("Reminders");
                        mdalert.setPositiveButton("SUBMIT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {

                                String custid = generaldetails.get(0).get("CustomerId");

                                URL1 = siteurl + "/AddReminderCustomerForCollectionApp?customerId=" + custid + "&reminderDate=" + edtdate.getText().toString() + "&Note=" + URLEncoder.encode(edttext.getText().toString(), "UTF-8") + "&loginUserId=" + uid;

                                    JSONObject jsonObject1 = makeHttpRequest(URL1);
                                    if (jsonObject1.getString("status").equals("True")) {
                                        Toast.makeText(CustomerMasterDetailsActivity.this, jsonObject1.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(CustomerMasterDetailsActivity.this, "JSON++" + e, Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    Toast.makeText(CustomerMasterDetailsActivity.this, "Error++" + e, Toast.LENGTH_SHORT).show();
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

                        return true;
                    }
                }
                return true;
            }
        });

        tvattachment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= tvattachment.getRight() - tvattachment.getTotalPaddingRight()) {
                        // your action for drawable click event


                        View view = getLayoutInflater ().inflate(R.layout.layout_attachment, null);
                        TextView txttakephoto = (TextView)view.findViewById( R.id.txt_backup);
                        TextView txtDetail = (TextView)view.findViewById( R.id.txt_detail);
                        TextView txtfilebrowse = (TextView)view.findViewById( R.id.txt_open);


                        final Dialog mBottomSheetDialog = new Dialog (CustomerMasterDetailsActivity.this, R.style.MaterialDialogSheet);
                        mBottomSheetDialog.setContentView(view);
                        mBottomSheetDialog.setCancelable(true);
                        mBottomSheetDialog.getWindow ().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mBottomSheetDialog.getWindow ().setGravity(Gravity.BOTTOM);
                        mBottomSheetDialog.show();


                        txttakephoto.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                cameraIntent();
                            }
                        });

                        txtDetail.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                galleryIntent();
                            }
                        });

                        txtfilebrowse.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                fileIntent();
                            }
                        });
                        return true;
                    }
                }
                return true;
            }
        });

        tvmakepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String custid = generaldetails.get(0).get("CustomerId");


                Intent i = new Intent(CustomerMasterDetailsActivity.this, CustomerDetails.class);
                i.putExtra("cname", title);
                i.putExtra("A/cNo", tvacno.getText().toString());
                i.putExtra("CustomerId", custid);
                i.putExtra("from", "Customer");
                startActivity(i);

                //finish();

            }
        });

        lstcomment.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        URL = siteurl + "/CompleteCustomerCommentForCollectionApp";

                        CallVolleysComplete(URL, commentlist.get(position).get("CommentId").toString(), uid);

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


    }


    private void cameraIntents()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        555);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        555);
            }
            else {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 555);
            }

        }
        else
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 555);
        }



    }

    private void galleryIntents()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        5555);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        5555);
            }
            else {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(intent, 5555);
            }

        }
        else
        {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(intent, 5555);
        }



    }


    public boolean ValidateEdittext(String error,EditText ed)
    {
        if(ed.getText().toString()==null || ed.getText().toString().length()==0)
        {
            ed.setError(error);
            return  false;
        }
        else
        {
            return true;
        }
    }


    public void CallVolleyAddcomplaint(String a,HashMap<String,String> map)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        //jsonobj=makeHttpRequest(params[0]);
        //  URL=siteurl+"/GetAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString();

        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {


                            // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            if (response.getString("status").toString().equals("True")) {

                                Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                Intent intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(intent);

                            }
                            else
                            {
                                Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CustomerMasterDetailsActivity.this, "error--" + e, Toast.LENGTH_LONG).show();
                        }

                           /* } */

                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(CustomerMasterDetailsActivity.this, "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }



    public void CallVolleysAddcomplaint(String a,final HashMap<String,String> map)
    {

        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            spload.dismiss();

                            JSONObject response=new JSONObject(s);

                            try
                            {
                                if (response.getString("status").toString().equals("True")) {

                                    Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                    Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);

                                }
                                else
                                {
                                    Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
                            }

                            // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(CustomerMasterDetailsActivity.this, "error--"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        spload.dismiss();

                        //Showing toast
                        Toast.makeText(CustomerMasterDetailsActivity.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map1=new HashMap<>();
                map1=map;


                Log.e("COMPLAINTLOG",map1.toString());

                //returning parameters
                return map1;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(6000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(stringRequest);
    }




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


                URL=siteurl+"/CompleteCustomerCommentForCollectionApp";

                CallVolleysComplete(URL, commentlist.get(info.position).get("CommentId").toString(), uid);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(CustomerMasterDetailsActivity.this, CustomerListActivity.class);
        startActivity(i);

        finish();
    }

    private void startActivityWithOptions(Intent intent) {
        ActivityOptions transitionActivity =
                ActivityOptions.makeSceneTransitionAnimation(CustomerMasterDetailsActivity.this);
        startActivity(intent, transitionActivity.toBundle());
    }



    private void cameraIntent()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CAMERA);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA);
            }
            else {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }

        }
        else
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

    }

    private void galleryIntent()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_IMAGE);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SELECT_IMAGE);
            }
            else {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_IMAGE);
            }

        }
        else
        {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_IMAGE);
        }

    }

    private void fileIntent()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_FILE);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SELECT_FILE);
            }
            else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }

        }
        else
        {

            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(requestCode == REQUEST_CAMERA )
        {
            if(permissions.length>0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }

            }

            return;

        }
        else if(requestCode == SELECT_IMAGE)
        {
            if(permissions.length>0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_IMAGE);
                }

            }

            return;
        }
        else if(requestCode == SELECT_FILE)
        {
            if(permissions.length>0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }

            }

            return;
        }

        else if(requestCode == 555)
        {
            if(permissions.length>0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 555);
                }

            }

            return;
        }

        else if(requestCode == 5555)
        {
            if(permissions.length>0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(intent, 5555);
                }

            }

            return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {



           final  LayoutInflater li=getLayoutInflater();

            final View vs=li.inflate(R.layout.layout_attach_file_displaydesign, null);

           final MDDialog.Builder mdalert=new MDDialog.Builder(CustomerMasterDetailsActivity.this);
            mdalert.setContentView(vs);

            final TextView tvfilebane=(TextView)vs.findViewById(R.id.textView91);

            final EditText edtfilename=(EditText)vs.findViewById(R.id.editText20);

            if (requestCode == SELECT_IMAGE)
            {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                //File f=new File(myFile.getAbsolutePath());
                //String path = getRealPathFromURI(getApplicationContext(),uri);
                String displayName=null;
                String extension=null;


                /*Bitmap bmp=(Bitmap)data.getExtras().get("data");
                final String base64=bitMapToString(bmp);*/



               /* Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();


                Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                final String base64=bitMapToString(yourSelectedImage);*/

               /* Uri selectedImageUri = data.getData();
               String selectedImagePath = getPaths(selectedImageUri);*/

                Uri selectedImageUri = data.getData();
                try {
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selectedImageUri);

                    Bitmap bm = BitmapFactory.decodeStream(inputStream);

                   // converted64=bitMapToString(bm);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    byte[] byteArray = stream.toByteArray();
                    converted64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                   // Toast.makeText(CustomerMasterDetailsActivity.this, converted64, Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

               /* String paths = getPath(uri);

                String encodeds = convertFileToString(paths);
                // extension = displayName.substring(displayName.lastIndexOf("."));
                Toast.makeText(CustomerMasterDetailsActivity.this, encodeds, Toast.LENGTH_SHORT).show();*/

               /* try {
                    InputStream imInputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);
                    smallImagePath= saveGalaryImageOnLitkat(bitmap);
                   // smallImagePath=bitMapToString(bitmap);

                   // Toast.makeText(CustomerMasterDetailsActivity.this, smallImagePath, Toast.LENGTH_SHORT).show();

                    *//*logoImg.setImageBitmap(BitmapFactory.decodeFile(smallImagePath));
                   encodeImage();*//*
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
*/

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                    extension=displayName.substring(displayName.lastIndexOf("."));
                }

                mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String encodeds = null;
                        try {

                           /* encodeds = filetoBAse64(smallImagePath);
                            Toast.makeText(CustomerMasterDetailsActivity.this, encodeds, Toast.LENGTH_SHORT).show();*/

                            String custid = generaldetails.get(0).get("CustomerId");

                            URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+uid;

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("customerId", custid);
                            map.put("filename", edtfilename.getText().toString());
                            map.put("type", ".jpg");
                            map.put("Note", "");
                            map.put("UploadFile", converted64);
                            map.put("loginUserId", uid);

                            if(converted64.equals(null))
                            {
                                Toast.makeText(CustomerMasterDetailsActivity.this, "BAse 64 null", Toast.LENGTH_SHORT).show();
                            }
                            else {

                               // Toast.makeText(CustomerMasterDetailsActivity.this, converted64, Toast.LENGTH_SHORT).show();

                                CallVolleysAttachment(URL1, map);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // URL1=siteurl+"/AddFileAttachmentCustomerForCollectionApp?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+uid;

                       /* try {

                            JSONObject jsonObject1 = makeHttpRequest(URL1);
                            if (jsonObject1.getString("status").equals("True")) {
                                Toast.makeText(CustomerMasterDetailsActivity.this,"File Successfully Attached.....", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(CustomerMasterDetailsActivity.this, "Error++"+e, Toast.LENGTH_SHORT).show();
                        }*/

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
                tvfilebane.setText(displayName);
                dialog.show();

            }
               // onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
            {

                try {
                   // final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    final Bitmap photo = (Bitmap) data.getExtras().get("data");
                    final String b64 = bitMapToString(photo);

                   // Toast.makeText(CustomerMasterDetailsActivity.this, b64, Toast.LENGTH_SHORT).show();



                    mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String custid = generaldetails.get(0).get("CustomerId");

                            URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+uid;

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("customerId", custid);
                            map.put("filename", edtfilename.getText().toString());
                            map.put("type", ".PNG");
                            map.put("Note", "");
                            map.put("UploadFile", b64);
                            map.put("loginUserId", uid);

                            CallVolleysAttachment(URL1, map);
                       /* try {

                            JSONObject jsonObject1 = makeHttpRequest(URL1);
                            if (jsonObject1.getString("status").equals("True")) {
                                Toast.makeText(CustomerMasterDetailsActivity.this,"File Successfully Attached.....", Toast.LENGTH_SHORT).show();
                            }
                        }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(CustomerMasterDetailsActivity.this, "Error++"+e, Toast.LENGTH_SHORT).show();
                        }*/

                    }
                });
                    mdalert.setNegativeButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }
                    });

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());

                    Toast.makeText(CustomerMasterDetailsActivity.this, formattedDate + ".PNG", Toast.LENGTH_SHORT).show();

                    mdalert.setWidthMaxDp(600);
                    mdalert.setShowTitle(true);
                    mdalert.setShowButtons(true);
                    mdalert.setBackgroundCornerRadius(5);

                    MDDialog dialog = mdalert.create();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    tvfilebane.setText(formattedDate + ".PNG");
                    dialog.show();
               }

                catch (Exception e)

                    {
                        e.printStackTrace();
                    }


            }
            else if(requestCode==SELECT_FILE)
            {
                try
                {

                    Uri uri = data.getData();
                    Log.d("FILECHOOSER", "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtils.getPath(this, uri);
                    Log.d("FILECHOOSER", "File Path: " + path);

                    //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                    final File yourFile = new File(path);

                    if(yourFile.exists())
                    {
                        final String encodeFileToBase64Binarys = encodeFileToBase64Binary(yourFile);

                        //Toast.makeText(this, encodeFileToBase64Binarys, Toast.LENGTH_SHORT).show();


                        mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String custid = generaldetails.get(0).get("CustomerId");

                                URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+uid;

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("customerId", custid);
                                map.put("filename", edtfilename.getText().toString());
                                map.put("type",tvfilebane.getText().toString().substring(tvfilebane.getText().toString().lastIndexOf(".")));
                                map.put("Note", "");
                                map.put("UploadFile", encodeFileToBase64Binarys);
                                map.put("loginUserId", uid);

                                CallVolleysAttachment(URL1, map);

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
                        tvfilebane.setText(yourFile.getName());
                        dialog.show();
                    }
                    else
                    {
                        Toast.makeText(this, "File Not Exist..", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            else if(requestCode==555)
            {
                try {
                    // final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    final Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgselectfile.setImageBitmap(photo);
                    final String b64 = bitMapToString(photo);
                    imageString=b64;

                    //Toast.makeText(ComplainListActivity.this, b64, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            }
            else if(requestCode==5555)
            {

                Uri selectedImageUri = data.getData();
                try {
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selectedImageUri);

                    Bitmap bm = BitmapFactory.decodeStream(inputStream);

                    imgselectfile.setImageBitmap(bm);

                    // converted64=bitMapToString(bm);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    byte[] byteArray = stream.toByteArray();
                    String converted64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                    imageString=converted64;


                    //Toast.makeText(ComplainListActivity.this, converted64, Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
             //   onCaptureImageResult(data);
        }



    public String getPaths(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private String getFilePathFromContentUri(Uri selectedVideoUri,
                                             ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    private File temp_path;
    private final int COMPRESS = 100;
    private String saveGalaryImageOnLitkat(Bitmap bitmap) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);

           /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
            byte[] b = baos.toByteArray();
            String temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);*/
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

           // return temp;
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    private String savefile(String extension) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + extension;
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
           // bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public String convertFileToString(String pathOnSdCard){

        String strFile=null;

        File file=new File(pathOnSdCard);

        try {

            byte[] data = loadFile(file);//Convert any file, image or video into byte array

            strFile = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);//Convert byte array into string

        } catch (IOException e) {

            e.printStackTrace();

        }

        return strFile;

    }


    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = loadFile(fileName);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        return encodedString;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] b = baos.toByteArray();
        String temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);

        return temp;
    }

    public String filetoBAse64(String filepath) throws FileNotFoundException {
        InputStream inputStream = null;//You can get an inputStream using any IO API
        inputStream = new FileInputStream(filepath);
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream output64 = new Base64OutputStream(output, android.util.Base64.DEFAULT);
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
                output64.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String attachedFile = output.toString();
        return  attachedFile;
    }



    private String encodeFileToBase64Binary(String fileName)
            throws IOException {

        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, cmonth, day);
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

            edtdate.setText(arg3+"/"+(arg2+1)+"/"+arg1);
        }
    };

    public void CallVolleysComplete(String a,String cid,String usid)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            HashMap<String,String> map=new HashMap<>();
            map.put("commentId",cid);
            map.put("userId", usid);

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
                                        Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();


                                        Intent i = new Intent(getApplicationContext(),CustomerMasterDetailsActivity.class);
                                        i.putExtra("cname",title);
                                        i.putExtra("A/cNo",acno);
                                        i.putExtra("MQno",MQno);
                                        startActivity(i);

                                        finish();
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

    public void CallVolleys(String a,HashMap<String,String> map)
    {
        JsonObjectRequest obreqs;

        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        //jsonobj=makeHttpRequest(params[0]);
        //  URL=siteurl+"/GetAreaByUserForCollectionApp?contractorId="+cid+"&userId="+uid+"&entityId="+pref.getString("Entityids","").toString();



        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        spload.dismiss();

                        try {

                            //  Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();

                            if (response.getString("status").toString().equals("True")) {
                                Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                }


                            else
                            {
                                Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                           /* } catch (Exception e) {
                                Toast.makeText(getContext(), "error--" + e, Toast.LENGTH_LONG).show();
                            }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        spload.dismiss();

                        Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreqs);

    }

    public void CallVolleysAttachment(String a,final HashMap<String,String> map)
    {

        final SpotsDialog spload;
        spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            spload.dismiss();

                            JSONObject response=new JSONObject(s);

                            try
                            {
                                if(response.getString("status").toString().equals("True"))
                                {
                                    Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
                            }

                            // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(CustomerMasterDetailsActivity.this, "error--"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        spload.dismiss();

                        //Showing toast
                        Toast.makeText(CustomerMasterDetailsActivity.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map1=new HashMap<>();
                map1=map;


                Log.e("ATTACHMENTLOG",map1.toString());

                //returning parameters
                return map1;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(6000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(stringRequest);
    }



    public JSONObject makeHttpRequest(String url){

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

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
        //public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1,jsn,jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(CustomerMasterDetailsActivity.this,R.style.Custom);
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
                    rlmain.setVisibility(View.VISIBLE);

                    DecimalFormat format = new DecimalFormat();
                    format.setDecimalSeparatorAlwaysShown(false);

                    String areaname=json.getString("Area");

                    for(int i=0;i<1;i++) {

                        HashMap<String, String> map = new HashMap<>();
                        map.put("CustomerId", json.getString("CustomerId"));
                        map.put("Name", json.getString("Name"));
                        map.put("Address", json.getString("Address"));
                        map.put("Area", json.getString("Area"));
                        map.put("AccountNo", json.getString("AccountNo"));
                        map.put("Phone", json.getString("Phone"));
                        map.put("Email", json.getString("Email"));
                        map.put("Payterm", json.getString("Payterm"));
                        map.put("NetworkName", json.getString("NetworkName"));
                        map.put("AccountStatus", json.getString("AccountStatus"));
                        map.put("City", json.getString("City"));
                        map.put("Zipcode", json.getString("Zipcode"));
                        map.put("TotalOutStandingAmount",  format.format(Double.parseDouble(json.getString("TotalOutStandingAmount"))));
                        map.put("AccountEntity", json.getString("AccountEntity"));
                        map.put("BirthDate", json.getString("BirthDate"));
                        map.put("CafNo", json.getString("CafNo"));
                        map.put("Discount", json.getString("Discount"));
                        map.put("NextBillMonth", json.getString("NextBillMonth"));
                        map.put("ConnStartDate", json.getString("ConnStartDate"));
                        map.put("ConnEndDate", json.getString("ConnEndDate"));

                        generaldetails.add(map);
                    }

                    final JSONArray payarray = json.getJSONArray("lstBillReceiptInfo");
                    final JSONArray packagearray1 = json.getJSONArray("lstBasePkgInfo");
                    final JSONArray packagearray2 = json.getJSONArray("lstAddOnPkgInfo");
                    final JSONArray packagearray3 = json.getJSONArray("lstAlaCartePkgInfo");
                    final JSONArray remiderarray = json.getJSONArray("lstReminderInfo");
                    final JSONArray billarray = json.getJSONArray("lstBillInfo");
                    final JSONArray smsarray = json.getJSONArray("lstSMSInfo");
                    final JSONArray complainarray = json.getJSONArray("lstComplainInfo");
                    final JSONArray attachmentarray = json.getJSONArray("lstAttachmetnInfo");

                    loadpaymentdetails(payarray);
                    loadpackagedetails(packagearray1, packagearray2, packagearray3);
                    loadremiderdetails(remiderarray);
                    loadbilldetails(billarray);
                    loadsmsdetails(smsarray);
                    loadcomplaindetails(complainarray);
                    loadattachmentdetails(attachmentarray);


                    final JSONArray entityarray1 = json.getJSONArray("lstCommentInfo");

                    for (int i = 0; i < entityarray1.length(); i++) {
                        JSONObject e = (JSONObject) entityarray1.get(i);

                        String cmid=e.getString("CommentId");
                        String comment=e.getString("Comment");

                        HashMap<String,String> map=new HashMap<>();
                        map.put("CommentId",cmid);
                        map.put("Comment",comment);

                        commentlist.add(map);
                    }

                    final SimpleAdapter das=new SimpleAdapter(CustomerMasterDetailsActivity.this,commentlist,R.layout.layout_customer_comment,new String[]{"Comment"},new int[]{R.id.textView96});
                    lstcomment.setAdapter(das);

                    if(commentlist.size()>0) {
                        dl.openDrawer(lldr);
                    }
                    else
                    {
                        TextView tvempty=(TextView)findViewById(R.id.textView103);

                        lstcomment.setEmptyView(tvempty);
                    }


                    /*billid=json.getString("BillId");
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

                    final JSONArray entityarray = json.getJSONArray("lstBillReceiptCustomerInfo");

                    for (int i = 0; i < entityarray.length(); i++) {
                        JSONObject e = (JSONObject) entityarray.get(i);

                        HashMap<String,String> map=new HashMap<>();

                        String bmonth = e.getString("BillMonth");
                        String oa = e.getString("Outstanding");
                        String bamount = e.getString("BillAmount");

                        map.put("BillMonth",bmonth);
                        map.put("Outstanding",oa);
                        map.put("BillAmount",bamount);

                        months.add(map);

                    }*/


                    tvarea.setText(areaname);
                    edtpaidamount.setText(format.format(Double.parseDouble(json.getString("TotalOutStandingAmount").toString())));




                    // Toast.makeText(getContext(),s1+"--"+s2+"--"+s3+"--"+s4+"--"+s5+"--"+s6+"--"+s7+"--"+s8, Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Toast.makeText(CustomerMasterDetailsActivity.this,json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e)
            {
                Toast.makeText(getApplicationContext(), "Error:***"+e, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Error:++"+ex, Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void loadpaymentdetails(JSONArray a)
    {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("Receiptno", e.getString("Receiptno"));
                    map.put("ReceiptDate", e.getString("ReceiptDate"));
                    map.put("BillMonth", e.getString("BillMonth"));
                    map.put("TotalBill",str+ e.getString("TotalBill"));
                    map.put("PaidAmount",str+format.format(Double.parseDouble(e.getString("PaidAmount"))));
                    map.put("PayMode", e.getString("PayMode"));
                    map.put("BankName", e.getString("BankName"));
                    map.put("ChequeDate", e.getString("ChequeDate"));
                    map.put("ChequeNo", e.getString("ChequeNo"));

                    paymnetdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadpackagedetails(JSONArray a,JSONArray b,JSONArray c)
    {
        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgId",e.getString("PkgId"));
                    map.put("PkgName", e.getString("PkgName"));
                    map.put("Price", e.getString("Price"));
                    map.put("DeviceNo", e.getString("DeviceNo"));
                    map.put("MQNo", e.getString("MQNo"));
                    map.put("Ptype", e.getString("Ptype"));
                    map.put("SmartCardNo", e.getString("SmartCardNo"));

                    packagedetails.add(map);

                }

                for (int i = 0; i < b.length(); i++) {
                    JSONObject e = (JSONObject) b.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgName", e.getString("PACKAGE_NAME"));
                    map.put("Price", e.getString("PACKAGE_PRICE"));
                    map.put("DeviceNo", e.getString("DEVICE_NAME"));

                    packagedetails.add(map);

                }

                for (int i = 0; i < c.length(); i++) {
                    JSONObject e = (JSONObject) c.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgName", e.getString("PACKAGE_NAME"));
                    map.put("Price", e.getString("PACKAGE_PRICE"));
                    map.put("DeviceNo", e.getString("DEVICE_NAME"));


                    packagedetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadbilldetails(JSONArray a)
    {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("BillMonth", e.getString("BillMonth"));
                    map.put("PrevOutstanding",str+format.format(Double.parseDouble(e.getString("PrevOutstanding"))));
                    map.put("CurrentOutstanding",str+format.format(Double.parseDouble(e.getString("CurrentOutstanding"))) );
                    map.put("TotalOutstanding",str+ format.format(Double.parseDouble(e.getString("TotalOutstanding"))));
                    map.put("TotalBillAmount", e.getString("TotalBillAmount"));

                    billdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadremiderdetails(JSONArray a)
    {
        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("ReminderId", e.getString("ReminderId"));
                    map.put("ReminderDate", e.getString("ReminderDate"));
                    map.put("Note", e.getString("Note"));
                    map.put("Status", e.getString("Status"));

                    remiderdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadattachmentdetails(JSONArray a)
    {
        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("AttachmnetId", e.getString("AttachmnetId"));
                    map.put("Filename", e.getString("Filename"));
                    map.put("UploadedFile", e.getString("UploadedFile"));
                    map.put("Note", e.getString("Note"));

                    attachmentdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadsmsdetails(JSONArray a)
    {
        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("MobileNo", e.getString("MobileNo"));
                    map.put("SMSDate", e.getString("SMSDate"));
                    map.put("Message", e.getString("Message"));

                    smsdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadcomplaindetails(JSONArray a)
    {
        if(a.length()>0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("ComplainId", e.getString("ComplainId"));
                    map.put("Name", e.getString("Name"));
                    map.put("AccountNo", e.getString("AccountNo"));
                    map.put("Phone", e.getString("Phone"));
                    map.put("Email", e.getString("Email"));
                    map.put("Subject", e.getString("Subject"));
                    map.put("Message", e.getString("Message"));
                    map.put("Address", e.getString("Address"));
                    map.put("ComplainStatus", e.getString("ComplainStatus"));
                    map.put("Notes", e.getString("Notes"));
                    map.put("image", e.getString("image"));
                    map.put("status", e.getString("status"));
                    map.put("assignedUser", e.getString("assignedUser"));

                    complaindetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
