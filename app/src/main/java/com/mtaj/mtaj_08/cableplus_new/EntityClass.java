package com.mtaj.mtaj_08.cableplus_new;

import java.util.ArrayList;

/**
 * Created by MTAJ-08 on 9/2/2016.
 */
public class EntityClass {

    private ArrayList<String> enamelist=new ArrayList<>();
    private ArrayList<String> eidlist=new ArrayList<>();

    public ArrayList<String> getEnamelist()
    {
        return enamelist;
    }

    public void setEnamelist(ArrayList<String> list)
    {
        this.enamelist = list;
    }

    public ArrayList<String> getEidlist()
    {
        return eidlist;
    }

    public void setEidlist(ArrayList<String> lists)
    {
        this.eidlist = lists;
    }
}
