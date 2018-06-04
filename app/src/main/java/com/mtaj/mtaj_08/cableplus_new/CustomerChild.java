package com.mtaj.mtaj_08.cableplus_new;

/**
 * Created by MTAJ-08 on 9/1/2016.
 */
public class CustomerChild {
private String name;
private String acno;
private String mqno;
    private String address;
    private  String commentcount;

    String custid;
    String cmpid;


    public String getCustid()
    {
        return custid;
    }

    public void setCustid(String id)
    {
        this.custid = id;
    }

    public String getCmpid()
    {
        return cmpid;
    }

    public void setCmpid(String id)
    {
        this.cmpid = id;
    }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getAcno()
        {
            return acno;
        }

        public void setAcno(String text1)
        {
            this.acno = text1;
        }

        public String getMqno()
        {
            return mqno;
        }

        public void setMqno(String text2)
        {
            this.mqno = text2;
        }

        public String getAddress()
        {
        return address;
        }

     public void setAddress(String text2)
        {
        this.address = text2;
        }


    public String getCommentcount()
    {
        return commentcount;
    }

    public void setCommentcount(String text2)
    {
        this.commentcount = text2;
    }
}
