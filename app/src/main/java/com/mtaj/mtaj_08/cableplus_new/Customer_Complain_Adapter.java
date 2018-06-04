package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTAJ-08 on 8/31/2016.
 */
public class Customer_Complain_Adapter extends BaseAdapter {
    private LayoutInflater inflater;


    Context cont;
    ArrayList<HashMap<String, String>> customerlist = new ArrayList<>();


    public Customer_Complain_Adapter(Context c, ArrayList<HashMap<String, String>> list) {
        inflater = LayoutInflater.from(c);
        cont = c;
        customerlist = list;
    }

    @Override
    public int getCount() {
        return customerlist.size();
    }

    @Override
    public Object getItem(int position) {
        return customerlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
              //  R.id.textView31, R.id.textView34, R.id.textView36, R.id.textView37

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.layout_customerlist, parent, false);
                holder.textname = (TextView) convertView.findViewById(R.id.textView31);
                holder.textacno = (TextView) convertView.findViewById(R.id.textView34);
                holder.textmqno = (TextView) convertView.findViewById(R.id.textView36);
                holder.textaddres= (TextView) convertView.findViewById(R.id.textView37);
                holder.tvcommentcount=(TextView)convertView.findViewById(R.id.textView102);

                holder.llcount=(LinearLayout)convertView.findViewById(R.id.llcount);


                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textname.setText(customerlist.get(position).get("Name"));
            holder.textacno.setText(customerlist.get(position).get("AccountNo"));
            holder.textmqno.setText(customerlist.get(position).get("MQNo"));
            holder.textaddres.setText(customerlist.get(position).get("Address"));

            if(customerlist.get(position).get("CustCommentCount").equals("0"))
            {
                holder.tvcommentcount.setVisibility(View.GONE);
            }
            else
            {
                holder.tvcommentcount.setText(customerlist.get(position).get("CustCommentCount"));

                holder.tvcommentcount.setVisibility(View.VISIBLE);

                Animation anim= AnimationUtils.loadAnimation(cont, R.anim.rotate);
                holder.llcount.startAnimation(anim);

            }
            return convertView;
        }
    class ViewHolder {
        TextView textname;
        TextView textacno;
        TextView textmqno;
        TextView textaddres;
        TextView tvcommentcount;
        LinearLayout llcount;


    }

    }



