package com.mtaj.mtaj_08.cableplus_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomerDetail_Offline extends AppCompatActivity {
    String siteurl,cid,uid,acno,title,URL,URL1,custid,fromm,tos;
    String str = "\u20B9";

    TextView tvacno,tvmqno,tvphone,tvaddress,tvemail,tvtotaloa,txtmakepayment;
    RadioGroup rgpayment;

    EditText edtdate,edtbankname,edtchqno,edtdiscount,edamount;

    ImageView imdropdown,imphoneedit,imemailedit,imadressedit,imdiscount;

    RadioButton rbcash,rbcheque;

    boolean isedited=false;

    String billid,accno,paymode="1",paidamount,cheqdate="",cheqno="",bankname="",rdate="";

    private Calendar calendar;
    private int year, cmonth, day;

    DBHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail__offline);

        Intent j=getIntent();
        title=j.getExtras().getString("cname");
        acno=j.getExtras().getString("A/cNo");
        custid=j.getExtras().getString("CustomerId");
        billid=j.getExtras().getString("billId");
        //tos=j.getExtras().getString("TotalOutStandingAmount");


        myDB=new DBHelper(this);

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


        imdiscount=(ImageView)findViewById(R.id.imageViewdis);

        edamount=(EditText)findViewById(R.id.editText5);
        edtdiscount=(EditText)findViewById(R.id.editText22);

        tvacno=(TextView)findViewById(R.id.textView34);
        tvmqno=(TextView)findViewById(R.id.textView36);
        tvtotaloa=(TextView)findViewById(R.id.textView50);
        tvphone=(TextView)findViewById(R.id.txtphone);
        tvemail=(TextView)findViewById(R.id.txtemail);
        tvaddress=(TextView)findViewById(R.id.txtaddress);

        rbcash=(RadioButton)findViewById(R.id.radioButton);
        rbcheque=(RadioButton)findViewById(R.id.radioButton2);

        rgpayment=(RadioGroup)findViewById(R.id.radiogroup1);

        txtmakepayment=(TextView)findViewById(R.id.textView29);

        calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        rdate = sdf.format(calendar.getTime());


        binddata(custid);

        imdiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isedited){

                    int amount =Integer.parseInt(edamount.getText().toString())-Integer.parseInt(edtdiscount.getText().toString());

                    edamount.setText(String.valueOf(amount));

                    imdiscount.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    isedited=false;
                    edtdiscount.setEnabled(false);
                }
                else
                {
                    imdiscount.setImageResource(R.drawable.ic_done_black_24dp);
                    isedited=true;
                    edtdiscount.setEnabled(true);
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


                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    cmonth = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    edtdate.setText((cmonth + 1) + "/" + day + "/" + year);


                    edtdate.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            hideSoftKeyboard(edtdate);

                            showDialog(999);

                            return false;
                        }
                    });


                    final AlertDialog.Builder alert = new AlertDialog.Builder(CustomerDetail_Offline.this);
                    alert.setTitle("Cheque Details");
                    alert.setView(v);

                    // alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            bankname = edtbankname.getText().toString();
                            cheqno = edtchqno.getText().toString();
                            cheqdate = edtdate.getText().toString();

                            if (bankname.equals("") || cheqno.equals("") || cheqdate.equals("")) {
                                Toast.makeText(CustomerDetail_Offline.this, "Enter Valid Details...", Toast.LENGTH_LONG).show();
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


        rgpayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int id = rgpayment.getCheckedRadioButtonId();
                RadioButton rb1 = (RadioButton) findViewById(id);

                if (rb1.getText().toString().equals("Cash")) {
                    paymode = "1";
                }
                if (rb1.getText().toString().equals("Cheque")) {
                    paymode = "2";
                }


            }
        });

        txtmakepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(edamount.getText().toString()) <= 0) {
                    Snackbar.make(v, "Enter Valid Amount", Snackbar.LENGTH_LONG);
                } else {

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
                    i.putExtra("email", tvemail.getText().toString());
                    i.putExtra("CustomerId", custid);
                    i.putExtra("receiptDate",rdate);
                    i.putExtra("Notes","");
                    startActivity(i);

                    finish();
                }
            }
        });




    }

    public void binddata(String cid) {
        Cursor c = myDB.getCustomersfromCustomerId(cid);

        //swrefresh.setEnabled(false);

        DecimalFormat format = new DecimalFormat("#");
        format.setDecimalSeparatorAlwaysShown(false);

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    String csid = c.getString(c.getColumnIndex("CUSTOMER_ID"));
                    String cname = c.getString(c.getColumnIndex("NAME"));
                    String caddress = c.getString(c.getColumnIndex("ADDRESS"));
                    String carea = c.getString(c.getColumnIndex("AREA"));
                    String cacno = c.getString(c.getColumnIndex("ACCOUNTNO"));
                    String cphone = c.getString(c.getColumnIndex("PHONE"));
                    String cemail = c.getString(c.getColumnIndex("EMAIL"));
                    String castatus = c.getString(c.getColumnIndex("ACCOUNTSTATUS"));
                    String cmq = c.getString(c.getColumnIndex("MQNO"));
                    String ctotaloa = c.getString(c.getColumnIndex("TOTAL_OUTSTANDING"));
                    String commentCount = c.getString(c.getColumnIndex("COMMENT_COUNT"));
                    //String billid = c.getString(c.getColumnIndex("BILL_ID"));


                    tvacno.setText(cacno);
                    tvmqno.setText(cmq);
                    tvphone.setText(cphone);
                    tvemail.setText(cemail);
                    tvaddress.setText(caddress);
                    tvtotaloa.setText(str+format.format(Double.parseDouble(ctotaloa)));
                    edamount.setText(format.format(Double.parseDouble(ctotaloa)));


                } while (c.moveToNext());
            }
        }
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

            edtdate.setText((arg2+1)+"/"+arg3+"/"+arg1);
        }
    };


    public void hideSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.RESULT_HIDDEN);
        }
    }
}
