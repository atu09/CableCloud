package com.cable.cloud.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.Collection_Area_Activity;
import com.cable.cloud.CustomAdapter;
import com.cable.cloud.MapsActivity;
import com.cable.cloud.R;
import com.cable.cloud.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class CollectionFragment extends Fragment {

    String str = "\u20B9";
    ListView lstuser;

    private static final String PREF_NAME = "LoginPref";

    String url;

    RequestQueue requestQueue;

    TextView tvTodayCollection, tvMonthlyCollection;

    SwipeRefreshLayout srLayout;

    ArrayList<HashMap<String, String>> userList = new ArrayList<>();

    CustomAdapter adapter;

    SharedPreferences pref;

    String siteurl, uid, cid, aid, eid, URL;

    RelativeLayout rlmain;

    SimpleAdapter da;

    String tc, toa;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.no_access_layout, null);
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (pref.getBoolean("IsBilling", true)) {
            url = getArguments().getString("url");
            if (url.equalsIgnoreCase("-")) {
                view = inflater.inflate(R.layout.layout_offline, null);
            } else {
                view = inflater.inflate(R.layout.collection, null);
                CallVolleys(url);
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (pref.getBoolean("IsBilling", true) && !url.equals("-")) {

            lstuser = (ListView) view.findViewById(R.id.listView);

            tvTodayCollection = (TextView) view.findViewById(R.id.btnCancel);
            tvMonthlyCollection = (TextView) view.findViewById(R.id.btnNext);

            rlmain = (RelativeLayout) view.findViewById(R.id.rlmain);

            srLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    srLayout.setRefreshing(true);
                    CallVolleys(url);

                }
            });

            lstuser.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int topRowVerticalPosition = (lstuser == null || lstuser.getChildCount() == 0) ?
                            0 : lstuser.getChildAt(0).getTop();
                    srLayout.setEnabled((topRowVerticalPosition >= 0));

                }
            });


            lstuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {


                    ImageView tv = (ImageView) view.findViewById(R.id.imageView13);
                    if (tv != null) {

                        tv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                Intent i = new Intent(getContext(), MapsActivity.class);
                                i.putExtra("userId", userList.get(position).get("userId"));
                                i.putExtra("UserName", userList.get(position).get("UserName"));
                                startActivity(i);

                                return false;
                            }
                        });


                    } else {
                    }


                    CardView cv = (CardView) view.findViewById(R.id.card_view);

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Context con = getActivity();

                            SharedPreferences pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Name", userList.get(position).get("UserName"));
                            editor.putString("selected_uid", userList.get(position).get("userId"));
                            editor.commit();

                            Intent i = new Intent(getContext(), Collection_Area_Activity.class);
                            i.putExtra("Userthismonthcollection", userList.get(position).get("Userthismonthcollection"));
                            startActivity(i);

                        }
                    });


                }
            });
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context con = getActivity();

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new CustomAdapter(getContext(), userList);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();
    }

    public void CallVolleys(String a) {

        final Dialog loader = Utils.getLoader(getActivity());
        loader.show();


        Map<String, String> maps = new HashMap<String, String>();
        maps.put("contractorid", cid);
        maps.put("loginuserId", uid);
        maps.put("entityId", eid);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(maps),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        try {

                            userList.clear();

                            if (response.getString("status").equalsIgnoreCase("True")) {

                                rlmain.setVisibility(View.VISIBLE);

                                DecimalFormat format = new DecimalFormat();
                                format.setDecimalSeparatorAlwaysShown(false);

                                final JSONArray entityarray = response.getJSONArray("lstUserInfoCollectionApp");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    String uid = e.getString("userId");
                                    String uname = e.getString("UserName");
                                    String utodaycol = e.getString("Usertodaycollection");
                                    String uthismonthcol = e.getString("Userthismonthcollection");


                                    HashMap<String, String> map = new HashMap<>();

                                    map.put("userId", uid);
                                    map.put("UserName", uname);
                                    map.put("Usertodaycollection", str + format.format(Double.parseDouble(utodaycol)));
                                    map.put("Userthismonthcollection", str + format.format(Double.parseDouble(uthismonthcol)));
                                    map.put("Userthismonthcollection", str + format.format(Double.parseDouble(uthismonthcol)));

                                    userList.add(map);

                                }

                                da = new SimpleAdapter(getContext(), userList, R.layout.list_collection_layout, new String[]{"UserName", "Usertodaycollection", "Userthismonthcollection"}, new int[]{R.id.textView2, R.id.textView24, R.id.textView26});
                                lstuser.setAdapter(da);
                                //adapter.notifyDataSetChanged();

                                tc = response.getString("Usertodaytotalcollection").toString();
                                toa = response.getString("Userthismonthtotalcollection").toString();

                                tvTodayCollection.setText(str + format.format(Double.parseDouble(tc)));
                                tvMonthlyCollection.setText(str + format.format(Double.parseDouble(toa)));

                                srLayout.setRefreshing(false);

                            } else {
                                Toast.makeText(getContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.dismiss();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(request);

    }
}
