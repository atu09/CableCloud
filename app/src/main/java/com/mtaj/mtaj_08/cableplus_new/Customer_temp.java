package com.mtaj.mtaj_08.cableplus_new;

/**
 * Created by MTAJ-08 on 8/29/2016.
 */
public class Customer_temp {

    String cname;
    String acno;
    String mqno;
    String address;

    public Customer_temp(String name, String ano,String mno,String addres) {
        this.cname = name;
        this.acno = ano;
        this.mqno = mno;
        this.address = addres;
    }

    public String getName() {
        return cname;
    }

    public String getAcno() {
        return acno;
    }

    public String getMqno() {
        return mqno;
    }

    public String getAddress() {
        return address;
    }
}

