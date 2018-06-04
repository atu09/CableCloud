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
 * Created by MTAJ-08 on 8/29/2016.
 */
public class ComplainListAdapter extends BaseAdapter  {

    private LayoutInflater inflater;

    ArrayList<HashMap<String,String>> complaindetails=new ArrayList<>();

    Context con;


    public ComplainListAdapter(Context context,ArrayList<HashMap<String,String>> c) {
        inflater = LayoutInflater.from(context);
        complaindetails=c;
        con=context;
    }

    @Override
    public int getCount() {
        return complaindetails.size();
    }

    @Override
    public Object getItem(int position) {
        return complaindetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       ViewHolder holder;


        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_complainbycustomer, parent, false);

            holder.textcmpid = (TextView) convertView.findViewById(R.id.textView61);
            holder.textsubject = (TextView) convertView.findViewById(R.id.textView44);
            holder.textmsg = (TextView) convertView.findViewById(R.id.textView98);
            holder.txtcount=(TextView)convertView.findViewById(R.id.textView102);
            holder.llcount=(LinearLayout)convertView.findViewById(R.id.llcount);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.textcmpid.setText("CP - "+complaindetails.get(position).get("ComplainId"));
        holder.textsubject.setText(complaindetails.get(position).get("Subject"));
        holder.textmsg.setText(complaindetails.get(position).get("ComplainStatus"));

        holder.txtcount.setText(complaindetails.get(position).get("webcommentcount"));

        if(complaindetails.get(position).get("webcommentcount").equals("0"))
        {
            holder.txtcount.setVisibility(View.GONE);
        }
        else
        {
            holder.txtcount.setVisibility(View.VISIBLE);

            Animation anim= AnimationUtils.loadAnimation(con, R.anim.rotate);
            holder.llcount.startAnimation(anim);

        }

        return convertView;
    }

    class ViewHolder {
        TextView textcmpid;
        TextView textsubject;
        TextView textmsg;
        TextView txtcount;
        LinearLayout llcount;

    }


}
