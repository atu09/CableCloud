package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MTAJ-08 on 9/1/2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter
{
    Context cont;

    private LayoutInflater inflater;

    List<AreaParent> explist=new ArrayList<>();

    AreaParent  parent=new AreaParent();
    CustomerChild child=new CustomerChild();

    public MyExpandableListAdapter(Context c,List<AreaParent> l)
    {
        cont=c;
        explist=l;
        // Create Layout Inflator
        inflater = LayoutInflater.from(cont);
    }


    // This Function used to inflate parent rows view

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView)
    {

        View col=convertView;

        parent = (AreaParent) getGroup(groupPosition);

        if(col==null) {

            //ViewHolderss holder=new ViewHolderss();

            // Inflate grouprow.xml file for parent rows

            LayoutInflater layoutInflater = (LayoutInflater) this.cont
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            col = layoutInflater.inflate(R.layout.layout_list_header, parentView, false);

           /* holder.tvAname=(TextView)col.findViewById(R.id.textView92);
            holder.tvAcount=(TextView)col.findViewById(R.id.textView93);

            col.setTag(holder);*/

            // Get grouprow.xml file elements and set values

           /* ((TextView) convertView.findViewById(R.id.textView92)).setText(parent.getName());
            ((TextView) convertView.findViewById(R.id.textView93)).setText(parent.getCount());*/

        }

        ((TextView) col.findViewById(R.id.textView92)).setText(parent.getName());
        ((TextView) col.findViewById(R.id.textView93)).setText(parent.getCount());
        ((TextView)col.findViewById(R.id.textView102)).setText(parent.getCommentcount());

        if(parent.getCommentcount().equals("0"))
        {
            ((TextView)col.findViewById(R.id.textView102)).setVisibility(View.GONE);
        }
        else
        {
            ((TextView)col.findViewById(R.id.textView102)).setVisibility(View.VISIBLE);

        }



       /* ViewHolderss holder=(ViewHolderss) col.getTag();

        holder.tvAname.setText(parent.getName());
        holder.tvAcount.setText(parent.getCount());*/

        return col;
    }


    // This Function used to inflate child rows view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parentView)
    {
        View row = convertView;

        child = (CustomerChild)getChild(groupPosition,childPosition);

        if(row ==null)
        {
            //parent= (AreaParent) getGroup(groupPosition);

            // Inflate childrow.xml file for child rows

            LayoutInflater layoutInflater = (LayoutInflater) this.cont
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.layout_customerlist, parentView, false);

           // View myView = row.findViewById(R.id.myView);

           /* ViewHolders holder = new ViewHolders();

            holder. tvname=(TextView)row.findViewById(R.id.textView31);

            holder. tvacno=(TextView)row.findViewById(R.id.textView34);

            holder. tvmqno=(TextView)row.findViewById(R.id.textView36);

            holder. tvaddress=(TextView)row.findViewById(R.id.textView37);

            holder. tv1=(TextView)row.findViewById(R.id.textView32);

            holder. tv2=(TextView)row.findViewById(R.id.textView102);

            row.setTag(holder);*/

           // convertView = inflater.inflate(R.layout.customerlist, parentView, false);

            // Get childrow.xml file elements and set values

           /* ((TextView) convertView.findViewById(R.id.textView31)).setText(child.getName());
            ((TextView) convertView.findViewById(R.id.textView34)).setText(child.getAcno());
            ((TextView) convertView.findViewById(R.id.textView36)).setText(child.getMqno());
            ((TextView) convertView.findViewById(R.id.textView37)).setText(child.getAddress());
            ((TextView) convertView.findViewById(R.id.textView32)).setVisibility(View.GONE);
            ((TextView) convertView.findViewById(R.id.textView102)).setVisibility(View.GONE);*/

        }

        ((TextView) row.findViewById(R.id.textView31)).setText(child.getName());
        ((TextView) row.findViewById(R.id.textView34)).setText(child.getAcno());
        ((TextView) row.findViewById(R.id.textView36)).setText(child.getMqno());
        ((TextView) row.findViewById(R.id.textView37)).setText(child.getAddress());
       // ((TextView) row.findViewById(R.id.textView32)).setVisibility(View.VISIBLE);

        ((TextView) row.findViewById(R.id.textView102)).setText(child.getCommentcount());

        if(child.getCommentcount().equals("0"))
        {
            ((TextView)row.findViewById(R.id.textView102)).setVisibility(View.GONE);
        }
        else
        {
            ((TextView)row.findViewById(R.id.textView102)).setVisibility(View.VISIBLE);

            Animation anim= AnimationUtils.loadAnimation(cont, R.anim.rotate);
            ((LinearLayout)row.findViewById(R.id.llcount)).startAnimation(anim);

        }

        /*ViewHolders holder = (ViewHolders) row.getTag();

        holder.tvname.setText(child.getName());
        holder.tvacno.setText(child.getAcno());
        holder.tvmqno.setText(child.getMqno());
        holder.tvaddress.setText(child.getAddress());

        holder.tv1.setVisibility(View.GONE);
        holder.tv2.setVisibility(View.GONE);*/

        return row;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
        return explist.get(groupPosition).getChildren().get(childPosition);
    }

    //Call when child row clicked
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        int size=0;
        if(explist.get(groupPosition).getChildren()!=null)
            size = explist.get(groupPosition).getChildren().size();
        return size;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        Log.i("Parent", groupPosition + "=  getGroup ");

        return explist.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return explist.size();
    }

    //Call when parent row clicked
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

   /* @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }*/

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    class ViewHolders {
        TextView tvname,tvacno,tvmqno,tvaddress,tv1,tv2;

    }

    class ViewHolderss {
        TextView tvAname,tvAcount;
    }
}




