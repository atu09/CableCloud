package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTAJ-08 on 8/29/2016.
 */
public class Temp_Adapter  extends BaseAdapter {
    private ArrayList<HashMap<String,String>> custarray;
    private LayoutInflater inflater;
    private static final int TYPE_PERSON = 0;
    private static final int TYPE_DIVIDER = 1;

    public Temp_Adapter(Context context, ArrayList<HashMap<String,String>> personArray) {
        this.custarray = personArray;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return custarray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return custarray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_PERSON and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Customer_temp) {
            return TYPE_PERSON;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PERSON);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_PERSON:
                    convertView = inflater.inflate(R.layout.customerlist, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.layout_list_header, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_PERSON:
                Customer_temp person = (Customer_temp)getItem(position);
                TextView textname = (TextView) convertView.findViewById(R.id.textView31);
                TextView textacno = (TextView) convertView.findViewById(R.id.textView34);
                TextView textmqno = (TextView) convertView.findViewById(R.id.textView36);
                TextView textaddress = (TextView) convertView.findViewById(R.id.textView37);
                textname.setText(person.getName());
                textacno.setText(person.getAcno());
                textmqno.setText(person.getMqno());
                textaddress.setText(person.getAddress());
                break;
            case TYPE_DIVIDER:
                TextView textarea = (TextView) convertView.findViewById(R.id.textView92);
                TextView textcount = (TextView) convertView.findViewById(R.id.textView93);

                textarea.setText(custarray.get(position).get("Aname"));
                textcount.setText(custarray.get(position).get("count"));

                break;
        }

        return convertView;
    }
}