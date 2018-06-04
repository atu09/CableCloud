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
 * Created by MTAJ-08 on 9/2/2016.
 */
public class PaymentAreaListAdapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<HashMap<String,String>> homelist=new ArrayList<>();


    public PaymentAreaListAdapter(Context con,ArrayList<HashMap<String,String>> u) {
        // TODO Auto-generated constructor stub

        context=con;
        homelist=u;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {        // TODO Auto-generated method stub
        return homelist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return homelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvareaname,tvcollection,tvoa;


    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.arealist, null);
            holder.tvareaname=(TextView)convertView.findViewById(R.id.textView2);
            holder.tvcollection=(TextView)convertView.findViewById(R.id.textView24);
            holder.tvoa=(TextView)convertView.findViewById(R.id.textView26);

            String str = "\u20B9";

            holder.tvareaname.setText(homelist.get(position).get("AreaName"));
            holder.tvcollection.setText(str+homelist.get(position).get("Collection"));
            holder.tvoa.setText(str+homelist.get(position).get("Outstanding"));


            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
