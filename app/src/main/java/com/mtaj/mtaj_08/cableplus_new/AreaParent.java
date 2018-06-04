package com.mtaj.mtaj_08.cableplus_new;

import java.util.ArrayList;

/**
 * Created by MTAJ-08 on 9/1/2016.
 */
public class AreaParent {

    private String name;
    private String count;
    private  String Commentcount;



    // ArrayList to store child objects
    private ArrayList<CustomerChild> children;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCount()
    {
        return count;
    }

    public void setCount(String text1)
    {
        this.count = text1;
    }


    public String getCommentcount()
    {
        return Commentcount;
    }

    public void setCommentcount(String text1)
    {
        this.Commentcount = text1;
    }

    public ArrayList<CustomerChild> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<CustomerChild> children)
    {
        this.children = children;
    }


}
