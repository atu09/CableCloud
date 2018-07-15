package com.cable.cloud.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import com.cable.cloud.Attachment_details;
import com.cable.cloud.Bill_details;
import com.cable.cloud.ComplainListviaCustomerActivity;
import com.cable.cloud.Contact_details;
import com.cable.cloud.FileUtils;
import com.cable.cloud.PackageDetails;
import com.cable.cloud.Payment_details;
import com.cable.cloud.R;
import com.cable.cloud.Remider_details;
import com.cable.cloud.helpers.Utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.carbs.android.library.MDDialog;

public class CustomerMasterDetailsActivity extends AppCompatActivity {

    String inrSymbol = "\u20B9";

    private static final String PREF_NAME = "LoginPref";

    CardView cv6, cv7, cv8, cv5, cv3, cv2, cv4, cv1;

    Calendar calendar;
    int year, cmonth, day;

    EditText edtdate, edttext;

    TextView tvpaidamount;
    ImageView ivSmslog, ivAttachment, ivReminders, ivComplaint;

    public int REQUEST_CAMERA = 123;

    public int SELECT_IMAGE = 345;

    public int SELECT_FILE = 888;

    String title, acno, cid, URL, siteurl, MQno, uid, URL1, imageString;
    TextView tvacno, tvmqno, tvarea, tvmakepayment;

    String str = "\u20B9";

    ArrayList<HashMap<String, String>> generaldetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> packagedetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> billdetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> paymnetdetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> complaindetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> remiderdetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> attachmentdetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> smsdetails = new ArrayList<>();


    String converted64 = null;

    SwipeMenuListView lstcomment;
    DrawerLayout drawer_layout;

    ArrayList<HashMap<String, String>> commentlist = new ArrayList<>();
    RequestQueue requestQueue;

    LinearLayout drawer_content;
    ImageView imgselectfile;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_master_details);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        cv1 = (CardView) findViewById(R.id.card_view1);
        cv2 = (CardView) findViewById(R.id.card_view2);
        cv3 = (CardView) findViewById(R.id.card_view3);
        cv4 = (CardView) findViewById(R.id.card_view4);
        cv5 = (CardView) findViewById(R.id.card_view5);
        cv6 = (CardView) findViewById(R.id.card_view6);
        cv7 = (CardView) findViewById(R.id.card_view7);
        cv8 = (CardView) findViewById(R.id.card_view8);

        ivReminders = (ImageView) findViewById(R.id.iv76);
        ivAttachment = (ImageView) findViewById(R.id.iv80);
        ivSmslog = (ImageView) findViewById(R.id.iv78);
        ivComplaint = (ImageView) findViewById(R.id.iv74);

        tvacno = (TextView) findViewById(R.id.textView34);
        tvmqno = (TextView) findViewById(R.id.textView36);
        tvarea = (TextView) findViewById(R.id.textView2);
        tvpaidamount = (TextView) findViewById(R.id.textView28);
        tvmakepayment = (TextView) findViewById(R.id.textView29);

        lstcomment = (SwipeMenuListView) findViewById(R.id.listView8);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_content = (LinearLayout) findViewById(R.id.drawer_content);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        cid = pref.getString("Contracotrid", "");
        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");

        Intent intent = getIntent();
        title = intent.getExtras().getString("cname");
        acno = intent.getExtras().getString("A/cNo");
        MQno = intent.getExtras().getString("MQno");

        URL = siteurl + "/GetCustomerAllDetailsForCollectionApp?contractorid=" + cid + "&accountNo=" + acno;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (pref.getString("RoleId", "").equalsIgnoreCase("2")) {
            ivComplaint.setImageResource(R.drawable.ic_add_black);
            ivComplaint.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        } else if (pref.getBoolean("IsComplain", true)) {
            ivComplaint.setImageResource(R.drawable.ic_add_black);
            ivComplaint.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            ivComplaint.setImageResource(R.drawable.ic_keyboard_arrow_right_black);
            ivComplaint.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.ToolbarColor)));
                deleteItem.setWidth(150);
                deleteItem.setIcon(R.drawable.ic_done_white);
                menu.addMenuItem(deleteItem);
            }
        };

        lstcomment.setMenuCreator(creator);

        tvacno.setText(acno);
        tvmqno.setText(MQno);

        new JSONAsync().execute(new String[]{URL});

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

                Intent i = new Intent(getApplicationContext(), Attachment_details.class);
                i.putExtra("title", title);
                i.putExtra("attachmentdetails", attachmentdetails);
                startActivity(i);
            }
        });

        cv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SmsLogActivity.class);
                i.putExtra("title", title);
                i.putExtra("smsdetails", smsdetails);
                startActivityWithOptions(i);
            }
        });

        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), ComplainListviaCustomerActivity.class);
                i.putExtra("title", title);
                i.putExtra("complaindetails", complaindetails);
                i.putExtra("Mode", "Customers");
                startActivity(i);

            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Bill_details.class);
                i.putExtra("title", title);
                i.putExtra("billdetails", billdetails);
                startActivityWithOptions(i);
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String custid = generaldetails.get(0).get("CustomerId");

                Intent i = new Intent(getApplicationContext(), PackageDetails.class);
                i.putExtra("title", title);
                i.putExtra("packagedetails", packagedetails);
                i.putExtra("CustomerId", custid);
                i.putExtra("A/cNo", acno);
                startActivityWithOptions(i);

            }
        });

        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Payment_details.class);
                i.putExtra("title", title);
                i.putExtra("paymnetdetails", paymnetdetails);
                startActivityWithOptions(i);

            }
        });

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Contact_details.class);
                i.putExtra("title", title);
                i.putExtra("MQno", MQno);
                i.putExtra("generaldetails", generaldetails);
                startActivityWithOptions(i);

            }
        });


        ivComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

                        if (ValidateEditText("Enter Message", edtmessage) && ValidateEditText("Enter Subject", edtsubject)) {
                            URL = siteurl + "/SaveComplainForCustomerApp";

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

                            CallVolleysAddComplaint(URL, map);

                            ad.dismiss();
                        }

                    }
                });
            }
        });

        ivReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        } catch (Exception e) {
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
            }
        });


        ivAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View vi = getLayoutInflater().inflate(R.layout.layout_attachment, null);
                TextView txttakephoto = (TextView) vi.findViewById(R.id.txt_backup);
                TextView txtDetail = (TextView) vi.findViewById(R.id.txt_detail);
                TextView txtfilebrowse = (TextView) vi.findViewById(R.id.txt_open);


                final Dialog mBottomSheetDialog = new Dialog(CustomerMasterDetailsActivity.this, R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView(vi);
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
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
            }
        });

        tvmakepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String custid = generaldetails.get(0).get("CustomerId");


                Intent i = new Intent(CustomerMasterDetailsActivity.this, CustomerOnlineDetailsActivity.class);
                i.putExtra("cname", title);
                i.putExtra("A/cNo", tvacno.getText().toString());
                i.putExtra("CustomerId", custid);
                i.putExtra("from", "Customer");
                startActivity(i);

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
                return false;
            }
        });


    }


    private void cameraIntents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        555);
            } else if (result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        555);
            } else {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 555);
            }

        } else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 555);
        }


    }

    private void galleryIntents() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 5555);
            } else if (result1 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5555);
            } else {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(intent, 5555);
            }

        } else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(intent, 5555);
        }


    }


    public boolean ValidateEditText(String error, EditText ed) {
        if (ed.getText() == null || ed.getText().toString().length() == 0) {
            ed.setError(error);
            return false;
        } else {
            return true;
        }
    }


    public void CallVolleysAddComplaint(String a, final HashMap<String, String> map) {

        final Dialog loader = Utils.getLoader(CustomerMasterDetailsActivity.this);
        loader.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            JSONObject response = new JSONObject(s);
                            if (response.getString("status").equalsIgnoreCase("True")) {

                                Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                Intent intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(intent);

                            } else {
                                Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(CustomerMasterDetailsActivity.this, "error--" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        Toast.makeText(CustomerMasterDetailsActivity.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("COMPLAINTLOG", map.toString());
                return map;
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

                URL = siteurl + "/CompleteCustomerCommentForCollectionApp";
                CallVolleysComplete(URL, commentlist.get(info.position).get("CommentId"), uid);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(CustomerMasterDetailsActivity.this, CustomerListActivity.class);
        startActivity(i);
        finish();
    }

    private void startActivityWithOptions(Intent intent) {
        ActivityOptions transitionActivity = ActivityOptions.makeSceneTransitionAnimation(CustomerMasterDetailsActivity.this);
        startActivity(intent, transitionActivity.toBundle());
    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAMERA);
            } else if (result1 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

    }

    private void galleryIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_IMAGE);
            } else if (result1 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_IMAGE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
            }

        } else {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
        }

    }

    private void fileIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
            } else if (result1 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CustomerMasterDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
            } else {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }

        } else {

            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }

            }

            return;

        } else if (requestCode == SELECT_IMAGE) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
                }

            }

            return;
        } else if (requestCode == SELECT_FILE) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }

            }

            return;
        } else if (requestCode == 555) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 555);
                }

            }

            return;
        } else if (requestCode == 5555) {
            if (permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission is not Granted to Perform Operation..!!", Toast.LENGTH_LONG).show();
                } else {
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


            final LayoutInflater li = getLayoutInflater();

            final View vs = li.inflate(R.layout.layout_attach_file_displaydesign, null);

            final MDDialog.Builder mdalert = new MDDialog.Builder(CustomerMasterDetailsActivity.this);
            mdalert.setContentView(vs);

            final TextView tvfilebane = (TextView) vs.findViewById(R.id.textView91);

            final EditText edtfilename = (EditText) vs.findViewById(R.id.editText20);

            if (requestCode == SELECT_IMAGE) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                //File f=new File(myFile.getAbsolutePath());
                //String path = getRealPathFromURI(getApplicationContext(),uri);
                String displayName = null;
                String extension = null;


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
                    extension = displayName.substring(displayName.lastIndexOf("."));
                }

                mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String encodeds = null;
                        try {

                           /* encodeds = filetoBAse64(smallImagePath);
                            Toast.makeText(CustomerMasterDetailsActivity.this, encodeds, Toast.LENGTH_SHORT).show();*/

                            String custid = generaldetails.get(0).get("CustomerId");

                            URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+userId;

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("customerId", custid);
                            map.put("filename", edtfilename.getText().toString());
                            map.put("type", ".jpg");
                            map.put("Note", "");
                            map.put("UploadFile", converted64);
                            map.put("loginUserId", uid);

                            if (converted64.equals(null)) {
                                Toast.makeText(CustomerMasterDetailsActivity.this, "BAse 64 null", Toast.LENGTH_SHORT).show();
                            } else {

                                // Toast.makeText(CustomerMasterDetailsActivity.this, converted64, Toast.LENGTH_SHORT).show();

                                CallVolleysAttachment(URL1, map);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // URL1=siteURL+"/AddFileAttachmentCustomerForCollectionApp?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+userId;

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

                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                tvfilebane.setText(displayName);
                dialog.show();

            }
            // onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {

                try {
                    // final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    final Bitmap photo = (Bitmap) data.getExtras().get("data");
                    final String b64 = bitMapToString(photo);

                    // Toast.makeText(CustomerMasterDetailsActivity.this, b64, Toast.LENGTH_SHORT).show();


                    mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String custid = generaldetails.get(0).get("CustomerId");

                            URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+userId;

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
                } catch (Exception e)

                {
                    e.printStackTrace();
                }


            } else if (requestCode == SELECT_FILE) {
                try {

                    Uri uri = data.getData();
                    Log.d("FILECHOOSER", "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtils.getPath(this, uri);
                    Log.d("FILECHOOSER", "File Path: " + path);

                    //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                    final File yourFile = new File(path);

                    if (yourFile.exists()) {
                        final String encodeFileToBase64Binarys = encodeFileToBase64Binary(yourFile);

                        //Toast.makeText(this, encodeFileToBase64Binarys, Toast.LENGTH_SHORT).show();


                        mdalert.setPositiveButton("ATTACH", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String custid = generaldetails.get(0).get("CustomerId");

                                URL1 = siteurl + "/AddFileAttachmentCustomerForCollectionApp";//?customerId="+custid+"&filename="+edtfilename.getText().toString()+"&Note="+"&UploadFile="+tvfilebane.getText().toString()+"&loginUserId="+userId;

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("customerId", custid);
                                map.put("filename", edtfilename.getText().toString());
                                map.put("type", tvfilebane.getText().toString().substring(tvfilebane.getText().toString().lastIndexOf(".")));
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
                    } else {
                        Toast.makeText(this, "File Not Exist..", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 555) {
                try {
                    // final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    final Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgselectfile.setImageBitmap(photo);
                    final String b64 = bitMapToString(photo);
                    imageString = b64;

                    //Toast.makeText(ComplainListActivity.this, b64, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            } else if (requestCode == 5555) {

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
                    imageString = converted64;


                    //Toast.makeText(ComplainListActivity.this, converted64, Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        //   onCaptureImageResult(data);
    }


    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = loadFile(fileName);
        byte[] encoded = Base64.encodeBase64(bytes);
        return new String(encoded);
    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] b = baos.toByteArray();

        return android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
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
            edtdate.setText(arg3 + "/" + (arg2 + 1) + "/" + arg1);
        }
    };

    public void CallVolleysComplete(String a, String cid, String usid) {


        final Dialog loader = Utils.getLoader(CustomerMasterDetailsActivity.this);
        loader.show();

        try {

            HashMap<String, String> map = new HashMap<>();
            map.put("commentId", cid);
            map.put("userId", usid);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (loader.isShowing()) {
                                loader.dismiss();
                            }

                            try {

                                if (response.getString("status").equalsIgnoreCase("True")) {
                                    Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();


                                    Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                                    i.putExtra("cname", title);
                                    i.putExtra("A/cNo", acno);
                                    i.putExtra("MQno", MQno);
                                    startActivity(i);

                                    finish();
                                }

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (loader.isShowing()) {
                                loader.dismiss();
                            }

                            Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a, HashMap<String, String> map) {

        final Dialog loader = Utils.getLoader(CustomerMasterDetailsActivity.this);
        loader.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }


                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);

    }

    public void CallVolleysAttachment(String a, final HashMap<String, String> map) {

        final Dialog loader = Utils.getLoader(CustomerMasterDetailsActivity.this);
        loader.show();

        StringRequest request = new StringRequest(Request.Method.POST, a, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                try {

                    JSONObject response = new JSONObject(s);
                    if (response.getString("status").equalsIgnoreCase("True")) {
                        Toast.makeText(CustomerMasterDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(CustomerMasterDetailsActivity.this, "error--" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
                Toast.makeText(CustomerMasterDetailsActivity.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.e("ATTACHMENTLOG", map.toString());
                return map;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(6000000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
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
            HttpEntity httpEntity = httpresponse.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));

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

        Dialog loader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader = Utils.getLoader(CustomerMasterDetailsActivity.this);
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

                    String areaName = json.getString("Area");

                    for (int i = 0; i < 1; i++) {

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
                        map.put("TotalOutStandingAmount", format.format(Double.parseDouble(json.getString("TotalOutStandingAmount"))));
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

                        String cmid = e.getString("CommentId");
                        String comment = e.getString("Comment");

                        HashMap<String, String> map = new HashMap<>();
                        map.put("CommentId", cmid);
                        map.put("Comment", comment);

                        commentlist.add(map);
                    }

                    final SimpleAdapter das = new SimpleAdapter(CustomerMasterDetailsActivity.this, commentlist, R.layout.layout_customer_comment, new String[]{"Comment"}, new int[]{R.id.textView96});
                    lstcomment.setAdapter(das);

                    if (commentlist.size() > 0) {
                        drawer_layout.openDrawer(drawer_content);
                    } else {
                        TextView tvEmpty = (TextView) findViewById(R.id.textView103);
                        lstcomment.setEmptyView(tvEmpty);
                    }

                    tvarea.setText(areaName);
                    tvpaidamount.setText(inrSymbol + format.format(Double.parseDouble(json.getString("TotalOutStandingAmount"))));

                } else {
                    Toast.makeText(CustomerMasterDetailsActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error:++" + ex, Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void loadpaymentdetails(JSONArray a) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (a.length() > 0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("Receiptno", e.getString("Receiptno"));
                    map.put("ReceiptDate", e.getString("ReceiptDate"));
                    map.put("BillMonth", e.getString("BillMonth"));
                    map.put("TotalBill", str + e.getString("TotalBill"));
                    map.put("PaidAmount", str + format.format(Double.parseDouble(e.getString("PaidAmount"))));
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

    public void loadpackagedetails(JSONArray a, JSONArray b, JSONArray c) {
        if (a.length() > 0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("PkgId", e.getString("PkgId"));
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

    public void loadbilldetails(JSONArray a) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (a.length() > 0) {
            try {
                for (int i = 0; i < a.length(); i++) {
                    JSONObject e = (JSONObject) a.get(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("BillMonth", e.getString("BillMonth"));
                    map.put("PrevOutstanding", str + format.format(Double.parseDouble(e.getString("PrevOutstanding"))));
                    map.put("CurrentOutstanding", str + format.format(Double.parseDouble(e.getString("CurrentOutstanding"))));
                    map.put("TotalOutstanding", str + format.format(Double.parseDouble(e.getString("TotalOutstanding"))));
                    map.put("TotalBillAmount", e.getString("TotalBillAmount"));

                    billdetails.add(map);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loadremiderdetails(JSONArray a) {
        if (a.length() > 0) {
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

    public void loadattachmentdetails(JSONArray a) {
        if (a.length() > 0) {
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

    public void loadsmsdetails(JSONArray a) {
        if (a.length() > 0) {
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

    public void loadcomplaindetails(JSONArray a) {
        if (a.length() > 0) {
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
