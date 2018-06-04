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
 * Created by MTAJ-08 on 11/7/2016.
 */
public class CustomerListAdapter extends BaseAdapter {

    Context cont;
    ArrayList<HashMap<String,String>> mylist;
    private static LayoutInflater inflater=null;

    String str = "\u20B9";


    public CustomerListAdapter(Context con, ArrayList<HashMap<String,String>> list) {

        /********** Take passed values **********/

       cont=con;
        mylist=list;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater)con.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.customerlist, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textname = (TextView) vi.findViewById(R.id.textView31);
            holder.textos = (TextView) vi.findViewById(R.id.textView32);
            holder.textacno = (TextView) vi.findViewById(R.id.textView34);
            holder.textmqno = (TextView) vi.findViewById(R.id.textView36);
            holder.textaddres=(TextView)vi.findViewById(R.id.textView37);
            holder.textcount = (TextView) vi.findViewById(R.id.textView102);
            holder.textsmartcard= (TextView) vi.findViewById(R.id.textView38);
            holder.llcount=(LinearLayout)vi.findViewById(R.id.llcount);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(mylist.size()<=0)
        {
        }
        else
        {

            /************  Set Model values in Holder elements ***********/

            holder.textname.setText( mylist.get(position).get("Name"));
            holder.textos.setText(str+ mylist.get(position).get("TotalOutStandingAmount"));
            holder.textacno.setText( mylist.get(position).get("AccountNo"));
            holder.textmqno.setText( mylist.get(position).get("MQNo"));
            holder.textaddres.setText( mylist.get(position).get("Address"));
            holder.textcount.setText(mylist.get(position).get("CustCommentCount"));
            holder.textsmartcard.setText(mylist.get(position).get("PackageName"));

            if(mylist.get(position).get("CustCommentCount").equals("0"))
            {
                holder.textcount.setVisibility(View.GONE);
            }
            else
            {
                holder.textcount.setVisibility(View.VISIBLE);

                Animation anim=AnimationUtils.loadAnimation(cont,R.anim.rotate);
                holder.llcount.startAnimation(anim);

            }




            /******** Set Item Click Listner for LayoutInflater for each row *******/
        }
        return vi;

    }



    public static class ViewHolder{

        public TextView textname;
        public TextView textos;
        public TextView textacno;
        public TextView textmqno;
        public TextView textaddres;
        public TextView textcount;
        public TextView textsmartcard;
        public LinearLayout llcount;


    }
}
