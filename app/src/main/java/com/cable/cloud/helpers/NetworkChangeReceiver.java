package com.cable.cloud.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by MTAJ-08 on 11/15/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "LoginPref";

    String siteUrl, URL;
    SharedPreferences pref;

    RequestQueue requestQueue;

    Context context;

    DBHelper myDB;
    Cursor cursor = null;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        this.context = context;
        this.myDB = new DBHelper(context);
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.requestQueue = Volley.newRequestQueue(context);
        this.siteUrl = pref.getString("SiteURL", "");


        if (Utils.isInternetAvailable(context)) {

            try {

                cursor = myDB.getReceipts();

                if (cursor != null && cursor.getCount() > 0) {

                    Toast.makeText(this.context, "r=" + cursor.getCount(), Toast.LENGTH_SHORT).show();

                    if (cursor.moveToFirst()) {

                        do {

                            String rid = cursor.getString(cursor.getColumnIndex(DBHelper.PK_RECEIPT_ID));
                            String acno = cursor.getString(cursor.getColumnIndex(DBHelper.FK_ACCOUNTNO));
                            String bid = cursor.getString(cursor.getColumnIndex(DBHelper.FK_BILL_ID));
                            String chqnumber = cursor.getString(cursor.getColumnIndex(DBHelper.CHQNUMBER));
                            String cheqdate = cursor.getString(cursor.getColumnIndex(DBHelper.CHQDATE));
                            String cheqbankname = cursor.getString(cursor.getColumnIndex(DBHelper.CHQBANKNAME));
                            String email = cursor.getString(cursor.getColumnIndex(DBHelper.R_EMAIL));
                            String createdby = cursor.getString(cursor.getColumnIndex(DBHelper.CREATEDBY));
                            String sign = cursor.getString(cursor.getColumnIndex(DBHelper.SIGNATURE));
                            String receiptdate = cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPTDATE));
                            String longitude = cursor.getString(cursor.getColumnIndex(DBHelper.LONGITUDE));
                            String lati = cursor.getString(cursor.getColumnIndex(DBHelper.LATITUDE));
                            String discount = cursor.getString(cursor.getColumnIndex(DBHelper.DISCOUNT));
                            String paidamount = cursor.getString(cursor.getColumnIndex(DBHelper.PAID_AMOUNT));
                            String paymentmode = cursor.getString(cursor.getColumnIndex(DBHelper.PAYMENT_MODE));
                            String recptNo = cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_NO));


                            HashMap<String, String> map = new HashMap<>();
                            map.put("Content-Type", "application/json; charset=utf-8");
                            map.put("accountno", acno);
                            map.put("billid", bid);
                            map.put("paidamount", paidamount);
                            map.put("paymentmode", paymentmode);
                            map.put("chqnumber", chqnumber);
                            map.put("cheqdate", cheqdate);
                            map.put("cheqbankname", cheqbankname);
                            map.put("email", email);
                            map.put("notes", "");
                            map.put("createdby", createdby);
                            map.put("signature", sign);
                            map.put("receiptdate", receiptdate);
                            map.put("longitude", longitude);
                            map.put("latitude", lati);
                            map.put("discount", discount);
                            map.put("isprint", "");
                            map.put("recptNo", recptNo);

                            URL = siteUrl + "/withdiscountAndReceiptNo";

                            CallVolley(URL, map, rid);


                        } while (cursor.moveToNext());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void CallVolley(String a, HashMap<String, String> map1, final String rid) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map1),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                Toast.makeText(context, "Receipt Done.!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, "---", Toast.LENGTH_SHORT).show();


                                if (myDB.UpdateReceiptStatus(rid)) {
                                    Toast.makeText(context, "Status Done.!!", Toast.LENGTH_SHORT).show();
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);

        request.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

}
