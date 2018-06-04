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
public class CustomAdapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<HashMap<String,String>> userlist=new ArrayList<>();

    String str = "\u20B9";
    public CustomAdapter(Context con,ArrayList<HashMap<String,String>> u) {
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
        TextView tvname,tvyear,tvaddress,tvpayterm;
     /*   ImageView img;*/
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();

        if (convertView == null) {

        convertView = inflater.inflate(R.layout.layout_alert, null);
        holder.tvname=(TextView) convertView.findViewById(R.id.textView88);
        holder.tvyear=(TextView) convertView.findViewById(R.id.textView89);
        holder.tvaddress=(TextView) convertView.findViewById(R.id.textadress);
        holder.tvpayterm=(TextView) convertView.findViewById(R.id.textView);

           /* map.put("Name&Account",cname+"("+acno+")");
            map.put("Amount",amount);
            map.put("Address",addres);
            map.put("payterm",payterm);
            map.put("packagename",pname);
            map.put("packageenddate",pdate);
            map.put("mono",mqno);
            map.put("type",type);*/

            if(userlist.get(position).get("type").equals("Payment"))
                {
                    holder.tvname.setText(userlist.get(position).get("Name&Account"));
                    holder.tvyear.setText(userlist.get(position).get("Amount"));
                    holder.tvaddress.setText(userlist.get(position).get("Address"));
                    holder.tvpayterm.setText(userlist.get(position).get("payterm"));

                }
                else
                {
                    holder.tvname.setText(userlist.get(position).get("Name&Account"));
                    holder.tvyear.setText(userlist.get(position).get("packageenddate"));
                    holder.tvaddress.setText(userlist.get(position).get("packagename"));
                    holder.tvpayterm.setText(userlist.get(position).get("mono"));
                }

            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

}
