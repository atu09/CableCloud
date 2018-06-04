package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
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
public class HomeDataAdapter  extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<HashMap<String,String>> homelist=new ArrayList<>();

    SharedPreferences pref;
    private static final String PREF_NAME = "LoginPref";


    public HomeDataAdapter(Context con,ArrayList<HashMap<String,String>> u) {
        // TODO Auto-generated constructor stub

        context=con;
        homelist=u;

        pref = con.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

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
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvtodaycol,tvcurmonthbill,tvlastmonthoa,tvtotaloutstanding,tvremaining,tvthismonth;
        TextView tvtodaycomplain,tvcurmonthpend,tvlastmonthpend,tvtotalpendcomplain;
        CardView cvtrack;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.layout_list_home, null);
        holder.tvtodaycol=(TextView)convertView.findViewById(R.id.textView20);
        holder.tvcurmonthbill=(TextView)convertView.findViewById(R.id.textView7);
        holder.tvlastmonthoa=(TextView)convertView.findViewById(R.id.textView9);
        holder.tvtotaloutstanding=(TextView)convertView.findViewById(R.id.textView56);
            holder.tvremaining=(TextView)convertView.findViewById(R.id.textView11);
            holder.tvthismonth=(TextView)convertView.findViewById(R.id.textView58);


        holder.tvtodaycomplain=(TextView)convertView.findViewById(R.id.textView22);
        holder.tvcurmonthpend=(TextView)convertView.findViewById(R.id.textView15);
        holder.tvlastmonthpend=(TextView)convertView.findViewById(R.id.textView17);
        holder.tvtotalpendcomplain=(TextView)convertView.findViewById(R.id.textView19);
            holder.cvtrack=(CardView)convertView.findViewById(R.id.card_view2);

            String str = "\u20B9";

            holder.tvtodaycol.setText(str+homelist.get(position).get("TodayCollection"));
            holder.tvcurmonthbill.setText(str+homelist.get(position).get("CurrentMonthBill"));
            holder.tvlastmonthoa.setText(str+homelist.get(position).get("Lastmonthoutstandingamount"));
            holder.tvtotaloutstanding.setText(str+homelist.get(position).get("Total"));

            holder.tvremaining.setText(str+homelist.get(position).get("TotalOutstanding"));
            holder.tvthismonth.setText(str+homelist.get(position).get("ThisMonthCollection"));

            holder.tvtodaycomplain.setText(homelist.get(position).get("TodayComplain"));
            holder.tvcurmonthpend.setText(homelist.get(position).get("CurrentPendingComplain"));
            holder.tvlastmonthpend.setText(homelist.get(position).get("LastPendingComplain"));
            holder.tvtotalpendcomplain.setText(homelist.get(position).get("TotalComplain"));

            if(pref.getString("RoleId","").equals("2"))
            {
                holder.cvtrack.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.cvtrack.setVisibility(View.GONE);
            }

            holder.cvtrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(context,Map_User_Tracking.class);
                    context.startActivity(i);

                }
            });

            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

}
