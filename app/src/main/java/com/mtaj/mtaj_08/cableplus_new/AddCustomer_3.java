package com.mtaj.mtaj_08.cableplus_new;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.carbs.android.library.MDDialog;
import dmax.dialog.SpotsDialog;

public class AddCustomer_3 extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    FloatingActionButton fabadd;

    public int REQUEST_CAMERA=123;

    public int SELECT_IMAGE=345;

    public int SELECT_FILE=888;

    TextView tvnext,tvcancel;

    ListView lvattachlist;

    ArrayList<HashMap<String,String>> attachlist=new ArrayList<>();

     Dialog mBottomSheetDialog;

    SimpleAdapter da;

    JSONArray jsonArray = new JSONArray();
    RequestQueue requestQueue;

    String siteurl,uid,cid,aid,eid,URL,custid,converted64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_3);

        final SharedPreferences pref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);

        siteurl=pref.getString("SiteURL","").toString();
        uid=pref.getString("Userid","").toString();
        cid=pref.getString("Contracotrid","").toString();

        Intent j=getIntent();

        custid=j.getExtras().getString("CustomerId");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Attachments");
        toolbar.setTitleTextColor(Color.WHITE);
       // toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });*/

        fabadd=(FloatingActionButton)findViewById(R.id.fab);
        tvnext=(TextView)findViewById(R.id.textView30);
        tvcancel=(TextView)findViewById(R.id.textView28);

        lvattachlist=(ListView)findViewById(R.id.listView5);

        fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater ().inflate (R.layout.layout_attachment, null);
                TextView txttakephoto = (TextView)view.findViewById( R.id.txt_backup);
                TextView txtDetail = (TextView)view.findViewById( R.id.txt_detail);
                TextView txtfilebrowse = (TextView)view.findViewById( R.id.txt_open);


                mBottomSheetDialog = new Dialog (AddCustomer_3.this, R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView (view);
                mBottomSheetDialog.setCancelable (true);
                mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
                mBottomSheetDialog.show ();


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

        registerForContextMenu(lvattachlist);

        tvnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(attachlist.size()>0) {

                    for (int i = 0; i < attachlist.size(); i++) {
                        try {
                            JSONObject packagedetails = new JSONObject();
                            packagedetails.put("filename", attachlist.get(i).get("filename"));
                            packagedetails.put("uploadfilename", attachlist.get(i).get("uploadfilename"));
                            packagedetails.put("type", attachlist.get(i).get("type"));


                            jsonArray.put(packagedetails);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(AddCustomer_3.this, "JSOn++" + e, Toast.LENGTH_SHORT).show();
                        }
                    }


                    URL = siteurl + "/GetattachmentfileforcustomerCollectionApp";

                    CallVolleys(URL);
                }
                else
                {
                    Snackbar.make(v,"No Attachments to add",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();


            }
        });
    }

    @Override
    public void onBackPressed()
    {

    }



    public void CallVolley(String a)
    {
        final SpotsDialog spload;
        spload=new SpotsDialog(AddCustomer_3.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String,String> map=new HashMap<>();
            map.put("loginuserId",uid);
            map.put("customerId",custid);
            map.put("lstfiles",jsonArray.toString());

            Log.e("ATTACHMENTLOG",map.toString());

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
                                        Toast.makeText(AddCustomer_3.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                        finish();
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
                                Toast.makeText(AddCustomer_3.this, "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(AddCustomer_3.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(AddCustomer_3.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }


    public void CallVolleys(String a)
    {

        final SpotsDialog spload;
        spload=new SpotsDialog(AddCustomer_3.this,R.style.Custom);
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
                                    Toast.makeText(AddCustomer_3.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();

                                    finish();
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
                            Toast.makeText(AddCustomer_3.this, "error--"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        spload.dismiss();

                        //Showing toast
                        Toast.makeText(AddCustomer_3.this, "Something Went Wrong.. ", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("loginuserId",uid);
                map.put("customerId",custid);
                map.put("lstfiles",jsonArray.toString());

                Log.e("ATTACHMENTLOG",map.toString());

                //returning parameters
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
        inflater.inflate(R.menu.menu_listview_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case R.id.action_delete:
                //Toast.makeText(AddCustomer_2.this, lvpackage.getItemAtPosition(info.position).toString(), Toast.LENGTH_SHORT).show();
                attachlist.remove(info.position);
                da.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    private void cameraIntent()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(AddCustomer_3.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CAMERA);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(AddCustomer_3.this,
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

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(AddCustomer_3.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_IMAGE);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(AddCustomer_3.this,
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

            int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(result == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(AddCustomer_3.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_FILE);
            }
            else if(result1 == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(AddCustomer_3.this,
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
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            LayoutInflater li = getLayoutInflater();

            View vs = li.inflate(R.layout.layout_attach_file_displaydesign, null);

            final MDDialog.Builder mdalert = new MDDialog.Builder(AddCustomer_3.this);
            mdalert.setContentView(vs);

            final TextView tvfilebane = (TextView) vs.findViewById(R.id.textView91);


            final EditText edtfilename = (EditText) vs.findViewById(R.id.editText20);


            if (requestCode == SELECT_IMAGE) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                //String path = myFile.getAbsolutePath();
                String displayName = null;


                Uri selectedImageUri = data.getData();
                try {
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selectedImageUri);

                    Bitmap bm = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    converted64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                    // Toast.makeText(CustomerMasterDetailsActivity.this, converted64, Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


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
                }


                mdalert.setPositiveButton("ADD", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mBottomSheetDialog.dismiss();


                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("filename", edtfilename.getText().toString());
                        map.put("displayname", tvfilebane.getText().toString());
                        map.put("uploadfilename", converted64);
                        map.put("type", ".PNG");

                        attachlist.add(map);

                        da = new SimpleAdapter(AddCustomer_3.this, attachlist, R.layout.layout_customer_attachment_list, new String[]{"filename", "displayname"}, new int[]{R.id.textView31, R.id.textView32});
                        lvattachlist.setAdapter(da);
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

                tvfilebane.setText(displayName);
                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();


            }
            // onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {

                final Bitmap photo = (Bitmap) data.getExtras().get("data");
                final String b64 = bitMapToString(photo);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());

                //try {
                // final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());


                mdalert.setPositiveButton("ADD", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mBottomSheetDialog.dismiss();

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("filename", edtfilename.getText().toString());
                        map.put("displayname", tvfilebane.getText().toString());
                        map.put("uploadfilename", b64);
                        map.put("type", ".PNG");

                        attachlist.add(map);

                        da = new SimpleAdapter(AddCustomer_3.this, attachlist, R.layout.layout_customer_attachment_list, new String[]{"filename", "displayname"}, new int[]{R.id.textView31, R.id.textView32});
                        lvattachlist.setAdapter(da);
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

                tvfilebane.setText(formattedDate + ".PNG");

                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();

                // Toast.makeText(CustomerMasterDetailsActivity.this, b64, Toast.LENGTH_SHORT).show();

                   /* Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());

                    Toast.makeText(CustomerMasterDetailsActivity.this, formattedDate + ".PNG", Toast.LENGTH_SHORT).show();

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

                            CallVolleys(URL1, map);*/
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
                    /*);
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
                    tvfilebane.setText(formattedDate + ".PNG");
                    dialog.show();
                }

                catch (Exception e) {
                    e.printStackTrace();
                }*/


            else if (requestCode == SELECT_FILE) {

                try
                {
                    Uri uri = data.getData();
                    Log.d("FILECHOOSER", "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtils.getPath(this, uri);
                    Log.d("FILECHOOSER", "File Path: " + path);

                    //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                    File yourFile = new File(path);
                    
                    if(yourFile.exists())
                    {
                        final String encodeFileToBase64Binarys = encodeFileToBase64Binary(yourFile);
                        
                        //Toast.makeText(this, encodeFileToBase64Binarys, Toast.LENGTH_SHORT).show();

                                        mdalert.setPositiveButton("ADD", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mBottomSheetDialog.dismiss();


                                        HashMap<String, String> map = new HashMap<String, String>();

                                        map.put("filename", edtfilename.getText().toString());
                                        map.put("displayname", tvfilebane.getText().toString());
                                        map.put("uploadfilename", encodeFileToBase64Binarys);
                                        map.put("type", tvfilebane.getText().toString().substring(tvfilebane.getText().toString().lastIndexOf(".")));

                                        attachlist.add(map);

                                        da = new SimpleAdapter(AddCustomer_3.this, attachlist, R.layout.layout_customer_attachment_list, new String[]{"filename", "displayname"}, new int[]{R.id.textView31, R.id.textView32});
                                        lvattachlist.setAdapter(da);
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

                                tvfilebane.setText(yourFile.getName());
                                MDDialog dialog = mdalert.create();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
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




                /*mdalert.setPositiveButton("ADD", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mBottomSheetDialog.dismiss();


                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("filename", edtfilename.getText().toString());
                        map.put("displayname", tvfilebane.getText().toString());
                        map.put("uploadfilename", converted64);
                        map.put("type", ".PNG");

                        attachlist.add(map);

                        da = new SimpleAdapter(AddCustomer_3.this, attachlist, R.layout.layout_customer_attachment_list, new String[]{"filename", "displayname"}, new int[]{R.id.textView31, R.id.textView32});
                        lvattachlist.setAdapter(da);
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

                tvfilebane.setText(displayName);
                MDDialog dialog = mdalert.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();*/


               /* LayoutInflater li=getLayoutInflater();

                View vs=li.inflate(R.layout.layout_attach_file_displaydesign, null);

                MDDialog.Builder mdalert=new MDDialog.Builder(AddCustomer_3.this);
                mdalert.setContentView(vs);

                final TextView tvfilebane=(TextView)vs.findViewById(R.id.textView91);
                tvfilebane.setText(displayName);
                final EditText edtfilename=(EditText)vs.findViewById(R.id.editText20);*/


            }
            //   onCaptureImageResult(data);
        }
    }

    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = loadFile(fileName);
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



    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] b = baos.toByteArray();
        String temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);

        return temp;
    }

}
