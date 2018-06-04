package com.mtaj.mtaj_08.cableplus_new;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTAJ-08 on 8/30/2016.
 */
public class ComplainLIstClass {

    String aid,aname,acount;
    ArrayList<HashMap<String,String>> Custlist=new ArrayList<>();

    public ComplainLIstClass(String id,String name,String count,ArrayList<HashMap<String,String>> list)
    {
        this.aid=id;
        this.aname=name;
        this.acount=count;
        this.Custlist=list;

    }

    public String getAid()
    {
        return  aid;
    }

    public String getAname()
    {
        return  aname;
    }

    public String getAcount()
    {
        return acount;
    }

    public ArrayList<HashMap<String,String>> getCustlist()
    {
        return Custlist;
    }

}
