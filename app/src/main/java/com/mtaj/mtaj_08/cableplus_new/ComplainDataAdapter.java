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
 * Created by MTAJ-08 on 9/4/2016.
 */
public class ComplainDataAdapter  extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<HashMap<String,String>> arealist=new ArrayList<>();


    public ComplainDataAdapter(Context con,ArrayList<HashMap<String,String>> u) {
        // TODO Auto-generated constructor stub

        context=con;
        arealist=u;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {        // TODO Auto-generated method stub
        return arealist.size();
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
        TextView tvareaname,tvnew,tvhigh,tvactive,tvresolve,tvnewcount,tvhighcount,tvactivecount,tvresolvecount;
        LinearLayout llnewcount,llhighcount,llactivecount,llresolvecount;


    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder;
        View vi = convertView;


        if (convertView == null) {

            vi = inflater.inflate(R.layout.layout_complainlist, null);

            holder = new Holder();

            holder.tvareaname = (TextView) vi.findViewById(R.id.textView31);
            holder.tvnew = (TextView) vi.findViewById(R.id.New_count);
            holder.tvhigh = (TextView) vi.findViewById(R.id.high_count);
            holder.tvactive = (TextView) vi.findViewById(R.id.active_count);
            holder.tvresolve = (TextView) vi.findViewById(R.id.resolved_count);

            holder.tvnewcount=(TextView)vi.findViewById(R.id.txtnewcount);
            holder.tvhighcount=(TextView)vi.findViewById(R.id.txthighcount);
            holder.tvactivecount=(TextView)vi.findViewById(R.id.txtactivecount);
            holder.tvresolvecount=(TextView)vi.findViewById(R.id.txtresolvecount);

            holder.llnewcount=(LinearLayout)vi.findViewById(R.id.llnewcount);
            holder.llhighcount=(LinearLayout)vi.findViewById(R.id.llhighcount);
            holder.llactivecount=(LinearLayout)vi.findViewById(R.id.llactivecount);
            holder.llresolvecount=(LinearLayout)vi.findViewById(R.id.llresolvecount);

            vi.setTag(holder);

        }
        else {
            holder = (Holder) vi.getTag();
        }

        if(arealist.size()<=0) {
        }
        else {

            if(arealist.get(position).get("type").equals("Area"))
            {
                holder.tvareaname.setText(arealist.get(position).get("areaname"));
            }
            else if(arealist.get(position).get("type").equals("User"))
            {
                holder.tvareaname.setText(arealist.get(position).get("userName"));
            }

            holder.tvnew.setText(arealist.get(position).get("newcomplaincount"));
            holder.tvhigh.setText(arealist.get(position).get("highcomplaincount"));
            holder.tvactive.setText(arealist.get(position).get("activecomplaincount"));
            holder.tvresolve.setText(arealist.get(position).get("resolvecomplaincount"));

            if(arealist.get(position).get("newcomplaincommentcount").equals("0"))
            {
                holder.tvnewcount.setVisibility(View.GONE);
            }
            else
            {
                holder.tvnewcount.setText(arealist.get(position).get("newcomplaincommentcount"));

                holder.tvnewcount.setVisibility(View.VISIBLE);

                Animation anim= AnimationUtils.loadAnimation(context, R.anim.rotate);
                holder.llnewcount.startAnimation(anim);

            }

            if(arealist.get(position).get("highcomplaincommentcount").equals("0"))
            {
                holder.tvhighcount.setVisibility(View.GONE);
            }
            else
            {
                holder.tvhighcount.setText(arealist.get(position).get("highcomplaincommentcount"));

                holder.tvhighcount.setVisibility(View.VISIBLE);

                Animation anim= AnimationUtils.loadAnimation(context, R.anim.rotate);
                holder.llhighcount.startAnimation(anim);

            }

            if(arealist.get(position).get("activecomplaincommentcount").equals("0"))
            {
                holder.tvactivecount.setVisibility(View.GONE);
            }
            else
            {
                holder.tvactivecount.setText(arealist.get(position).get("activecomplaincommentcount"));

                holder.tvactivecount.setVisibility(View.VISIBLE);

                Animation anim= AnimationUtils.loadAnimation(context, R.anim.rotate);
                holder.llactivecount.startAnimation(anim);

            }

            if(arealist.get(position).get("resolvecomplaincommentcount").equals("0"))
            {
                holder.tvresolvecount.setVisibility(View.GONE);
            }
            else
            {
                holder.tvresolvecount.setText(arealist.get(position).get("resolvecomplaincommentcount"));

                holder.tvresolvecount.setVisibility(View.VISIBLE);

                Animation anim= AnimationUtils.loadAnimation(context, R.anim.rotate);
                holder.llresolvecount.startAnimation(anim);

            }



        }

        return vi;
    }

}
