package com.mtaj.mtaj_08.cableplus_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by MTAJ-08 on 11/15/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "LoginPref";

    String siteurl, uid, cid, aid, eid, URL;
    SharedPreferences pref;

    RequestQueue requestQueue;

    Context con;

    DBHelper myDB;
    Cursor c=null;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        con=context;

        myDB=new DBHelper(context);

        pref= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(context);

        siteurl = pref.getString("SiteURL", "").toString();



        String status = NetworkUtil.getConnectivityStatusString(context);

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if(status.equals("Connected to Internet")) {

            try {

                c = myDB.getReceipts();

                if (c != null && c.getCount() > 0) {

                    Toast.makeText(con, "r=" + c.getCount(), Toast.LENGTH_SHORT).show();

                    if (c.moveToFirst()) {

                        do {

                            String rid = c.getString(c.getColumnIndex(DBHelper.PK_RECEIPT_ID));
                            String acno = c.getString(c.getColumnIndex(DBHelper.FK_ACCOUNTNO));
                            String bid = c.getString(c.getColumnIndex(DBHelper.FK_BILL_ID));
                            String chqnumber = c.getString(c.getColumnIndex(DBHelper.CHQNUMBER));
                            String cheqdate = c.getString(c.getColumnIndex(DBHelper.CHQDATE));
                            String cheqbankname = c.getString(c.getColumnIndex(DBHelper.CHQBANKNAME));
                            String email = c.getString(c.getColumnIndex(DBHelper.R_EMAIL));
                            String createdby = c.getString(c.getColumnIndex(DBHelper.CREATEDBY));
                            String sign = c.getString(c.getColumnIndex(DBHelper.SIGNATURE));
                            String receiptdate = c.getString(c.getColumnIndex(DBHelper.RECEIPTDATE));
                            String longitude = c.getString(c.getColumnIndex(DBHelper.LONGITUDE));
                            String lati = c.getString(c.getColumnIndex(DBHelper.LATITUDE));
                            String discount = c.getString(c.getColumnIndex(DBHelper.DISCOUNT));
                            String paidamount = c.getString(c.getColumnIndex(DBHelper.PAID_AMOUNT));
                            String paymentmode = c.getString(c.getColumnIndex(DBHelper.PAYMENT_MODE));
                            String recptNo=c.getString(c.getColumnIndex(DBHelper.RECEIPT_NO));


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

                            //URL = siteurl + "/withdiscount";

                            URL=siteurl+"/withdiscountAndReceiptNo";

                            CallVolley(URL, map, rid);


                        } while (c.moveToNext());

                    }
                }
            }
            catch(Exception e)
            {
                Log.e("BACKGROUND:",e.toString());
            }
        }

    }


    public void CallVolley(String a,HashMap<String,String> map1,final String rid) {

        try {
            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map1),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                //spload.dismiss();

                                try {

                                      //Toast.makeText(con, response.toString(), Toast.LENGTH_SHORT).show();

                                    if (response.getString("status").toString().equals("True")) {

                                        Toast.makeText(con, "Receipt Done.!!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(con, "---", Toast.LENGTH_SHORT).show();


                                        if(myDB.UpdateReceiptStatus(rid))
                                        {
                                            Toast.makeText(con, "Status Done.!!", Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(con, "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(con, "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(con, "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(con, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

}
