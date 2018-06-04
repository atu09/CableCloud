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
 * Created by MTAJ-08 on 8/24/2016.
 */
public class CustomerCommentListAdapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<HashMap<String,String>> userlist=new ArrayList<>();

    String str = "\u20B9";
    public CustomerCommentListAdapter(Context con, ArrayList<HashMap<String,String>> u) {
        // TODO Auto-generated constructor stub

        context=con;
        userlist=u;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvname,tvcomment,tvdate;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();

        if (convertView == null) {

        convertView = inflater.inflate(R.layout.layout_customer_comments, null);
        holder.tvname=(TextView) convertView.findViewById(R.id.textView95);
        holder.tvcomment=(TextView) convertView.findViewById(R.id.textView96);
        holder.tvdate=(TextView) convertView.findViewById(R.id.textView97);

            holder.tvname.setText(userlist.get(position).get("Name&Account"));
            holder.tvcomment.setText("\u2022  "+userlist.get(position).get("Comment"));
            holder.tvdate.setText(userlist.get(position).get("commentDate"));


         convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

}
