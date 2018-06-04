package com.mtaj.mtaj_08.cableplus_new;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by MTAJ-08 on 11/11/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    //Database name
    public static final String DATABASE_NAME = "test44";

    //Entity Table
    public static final String ENTITYTABLE = "EntityTable";

    public static final String PK_ENTITY_ID = "ENTITY_ID";
    public static final String ENTITYNAME = "ENTITY_NAME";

    //Area Table

    public static final String AREATABLE = "AreaTable";

    public static final String PK_AREA_ID = "AREA_ID";
    public static final String AREANAME= "AREA_NAME";
    public static final String AREACODE= "AREA_CODE";
    public static final String AREATOTALOUTSTANDING= "AREA_OUTSTANDING";
    public static final String AREACOLLECTION= "AREA_COLLECTION";
    public static final String AREA_TOTAL_CUSTOMER= "TOTAL_CUSTOMER";

    //Custoemr TAble

    public static final String CUSTOMERTABLE = "CustomerTable";

    public static final String PK_CUSTOMER_IDD = "CUSTOMER_ID";
    public static final String ACCOUNTNO= "ACCOUNTNO";
    public static final String NAME= "NAME";
    public static final String ADDRESS= "ADDRESS";
    public static final String AREA= "AREA";
    public static final String PHONE= "PHONE";
    public static final String EMAIL= "EMAIL";
    public static final String ACCOUNTSTATUS= "ACCOUNTSTATUS";
    public static final String MQNO= "MQNO";
    public static final String TOTAL_OUTSTANDING= "TOTAL_OUTSTANDING";
    public static final String REMAIN_OUTSTANDING= "REMAIN_OUTSTANDING";
    public static final String COMMENT_COUNT= "COMMENT_COUNT";
    public static final String BILL_ID= "BILL_ID";
    public static final String FK_AREA_ID= "FK_AREA_ID";

    //Receipt Table

    public static final String RECEIPTTABLE = "ReceiptTable";

    public static final String RECEIPT_NO = "RECEIPT_NO";
    public static final String PK_RECEIPT_ID = "PK_RECEIPT_ID";
    public static final String FK_CUSTOMER_ID = "FK_CUSTOMER_ID";
    public static final String FK_ACCOUNTNO= "FK_ACCOUNTNO";
    public static final String FK_BILL_ID= "FK_BILL_ID";
    public static final String PAID_AMOUNT= "PAD_AMOUNT";
    public static final String PAYMENT_MODE= "PAYMENT_MODE";
    public static final String CHQNUMBER= "CHQNUMBER";
    public static final String CHQDATE= "CHQDATE";
    public static final String CHQBANKNAME= "CHQBANKNAME";
    public static final String R_EMAIL= "R_EMAIL";
    public static final String CREATEDBY= "CREATEDBY";
    public static final String SIGNATURE= "SIGNATURE";
    public static final String RECEIPTDATE= "RECEIPTDATE";
    public static final String LONGITUDE= "LONGITUDE";
    public static final String LATITUDE= "LATITUDE";
    public static final String DISCOUNT= "DISCOUNT";
    public static final String IS_PRINT= "IS_PRINT";
    public static final String IS_SYNC= "IS_SYNC";

    Context con;
    //SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        con=context;

        //db=getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            String CREATE_ENTITY_TABLE = "CREATE TABLE " + ENTITYTABLE +
                    "(" +
                    "ID" + " INTEGER AUTO INCREMENT," +
                    PK_ENTITY_ID + " TEXT PRIMARY KEY " + "," +
                    ENTITYNAME + " TEXT" +
                    ")";

            String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + CUSTOMERTABLE +
                    "(" +
                    "ID" + " INTEGER AUTO INCREMENT," +
                    PK_CUSTOMER_IDD + " TEXT PRIMARY KEY," +
                    NAME + " TEXT" + "," +
                    ADDRESS + " TEXT" + "," +
                    AREA + " TEXT" + "," +
                    ACCOUNTNO + " TEXT" + "," +
                    PHONE + " TEXT" + "," +
                    EMAIL + " TEXT" + "," +
                    ACCOUNTSTATUS + " TEXT" + "," +
                    MQNO + " TEXT" + "," +
                    TOTAL_OUTSTANDING + " TEXT" + "," +
                    REMAIN_OUTSTANDING + " TEXT" + "," +
                    COMMENT_COUNT + " TEXT" + "," +
                    BILL_ID + " TEXT" + "," +
                    FK_AREA_ID + " TEXT "+
                    ")";

            String CREATE_AREA_TABLE = "CREATE TABLE " + AREATABLE +
                    "(" +
                    "ID" + " INTEGER AUTO INCREMENT," +
                    PK_AREA_ID + " TEXT PRIMARY KEY " + "," +
                    AREANAME + " TEXT" + "," +
                    AREACODE + " TEXT" + "," +
                    AREATOTALOUTSTANDING + " TEXT" + "," +
                    AREACOLLECTION + " TEXT" +
                    ")";

            String CREATE_RECEIPT_TABLE = "CREATE TABLE " + RECEIPTTABLE +
                    "(" +
                    PK_RECEIPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FK_CUSTOMER_ID + " TEXT REFERENCES " + CUSTOMERTABLE + "," +
                    FK_ACCOUNTNO + " TEXT " + "," +
                    FK_BILL_ID + " TEXT " + "," +
                    PAID_AMOUNT + " TEXT " + "," +
                    PAYMENT_MODE + " TEXT " + "," +
                    CHQNUMBER + " TEXT " + "," +
                    CHQDATE + " TEXT " + "," +
                    CHQBANKNAME + " TEXT " + "," +
                    R_EMAIL + " TEXT " + "," +
                    CREATEDBY + " TEXT " + "," +
                    SIGNATURE + " TEXT " + "," +
                    RECEIPTDATE + " TEXT " + "," +
                    LONGITUDE + " TEXT " + "," +
                    LATITUDE + " TEXT " + "," +
                    DISCOUNT + " TEXT " + "," +
                    IS_PRINT + " TEXT " + "," +
                    IS_SYNC + " TEXT " +","+
                    RECEIPT_NO + " TEXT" +
                    ")";

            db.execSQL(CREATE_ENTITY_TABLE);
            db.execSQL(CREATE_AREA_TABLE);
            db.execSQL(CREATE_CUSTOMER_TABLE);
            db.execSQL(CREATE_RECEIPT_TABLE);
            db.setLockingEnabled(false);

           // Toast.makeText(con, "SUCCESS!!", Toast.LENGTH_LONG).show();

            Log.e("CABLE PLUS DB", "SUCCESS!!");
        }
        catch (Exception e)
        {
            Toast.makeText(con, e.toString(), Toast.LENGTH_LONG).show();

            Log.e("CABLE PLUS DB",e.toString());
        }
        /*finally {

            db.close();
        }*/


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }

    public  boolean insertEntity(String eid,String ename)
    {

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(PK_ENTITY_ID,eid);
        contentValues.put(ENTITYNAME, ename);

        if(db.insert(ENTITYTABLE,null,contentValues)>0)
        {
            //db.close();
            return true;
        }
        else
        {
            //db.close();
            return false;
        }
    }

    public boolean insertArea(String Aid,String Aname,String Acode,String Aos,String Acol)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(PK_AREA_ID,Aid);
        contentValues.put(AREANAME,Aname);
        contentValues.put(AREACODE, Acode);
        contentValues.put(AREATOTALOUTSTANDING,Aos);
        contentValues.put(AREACOLLECTION,Acol);

        if(db.insert(AREATABLE,null,contentValues)>0)
        {
            //db.close();
            return true;
        }
        else
        {
            //db.close();
            return false;
        }


       // return true;
    }

    public boolean insertCustomer(String Cid,String name,String address,String area,String acno,String phone,String email,String astatus,String mqno,String os,String ccount,String bid,String aid)
    {

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();

        contentValues.put(PK_CUSTOMER_IDD,Cid);
        contentValues.put(NAME,name);
        contentValues.put(ADDRESS,address);
        contentValues.put(AREA,area);
        contentValues.put(ACCOUNTNO,acno);
        contentValues.put(PHONE,phone);
        contentValues.put(EMAIL,email);
        contentValues.put(ACCOUNTSTATUS,astatus);
        contentValues.put(MQNO,mqno);
        contentValues.put(TOTAL_OUTSTANDING,os);
        contentValues.put(REMAIN_OUTSTANDING,"0");
        contentValues.put(COMMENT_COUNT, ccount);
        contentValues.put(BILL_ID,bid);
        contentValues.put(FK_AREA_ID, aid);

        if(db.insert(CUSTOMERTABLE,null,contentValues)>0)
        {
            //db.close();
            return true;
        }
        else
        {
            //db.close();
            return false;
        }
    }

    public void deleteData()
    {
        SQLiteDatabase db=this.getWritableDatabase();

        db.delete(AREATABLE,null,null);
        db.delete(CUSTOMERTABLE,null,null);

        //db.close();
    }

    public void deleteReceiptData()
    {
        SQLiteDatabase db=this.getWritableDatabase();

        db.delete(RECEIPTTABLE,null,null);

        //db.close();
    }

    public  void deleteEntityData()
    {
        SQLiteDatabase db=this.getWritableDatabase();

        db.delete(ENTITYTABLE,null,null);

        //db.close();
    }

    public int EntityCount()
    {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from "+ ENTITYTABLE,null);
        int numRows=c.getCount();

        //db.close();
        return numRows;
    }

    public int CustomerCount()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CUSTOMERTABLE);
        //db.close();
        return numRows;
    }

    public  int AreaCount()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, AREATABLE);
        //db.close();
        return numRows;
    }

    public  int ReceiptCount()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+ RECEIPTTABLE+" where "+ IS_SYNC +" = "+"'false'",null);
        int numRows=c.getCount();

        //db.close();
        return numRows;
    }

    public Cursor getEntityData()
    {
        SQLiteDatabase db=this.getReadableDatabase() ;

        Cursor c = db.rawQuery("select * from " + ENTITYTABLE, null);

        //db.close();
        return  c;
    }

    public Cursor getAreas()
    {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from "+ AREATABLE +" where CAST("+ AREATOTALOUTSTANDING + " as REAL) > 0.0",null);


        return  c;
    }

    public Cursor getCustomers(String aid)
    {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from "+ CUSTOMERTABLE + " where " +FK_AREA_ID + " = "+aid+" AND CAST("+TOTAL_OUTSTANDING+" as REAL) > 0.0",null);


        return  c;
    }


    public Cursor getCustomersfromCustomerId(String cid)
    {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from "+ CUSTOMERTABLE + " where " +PK_CUSTOMER_IDD + " = "+cid,null);

        return  c;
    }

    public boolean insertReceipt(String Cid,String acno,String billid,String paidamount,String paymode,String chqnumber,String chqdate,String chqbankname,String remail,String createdby,String sign,String rdate,String longi,String lati,String dis,String is_print,String is_sync,String recptNo)
    {
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            //db=getWritableDatabase();

        /*PK_RECEIPT_ID + " INTEGER AUTO INCREMENT PRIMARY KEY," +
                FK_CUSTOMER_ID + " TEXT REFERENCES " + CUSTOMERTABLE + "," +
                FK_ACCOUNTNO + " TEXT " + "," +
                FK_BILL_ID + " TEXT " + "," +
                PAID_AMOUNT + " TEXT " + "," +
                PAYMENT_MODE + " TEXT " + "," +
                CHQNUMBER + " TEXT " + "," +
                CHQDATE + " TEXT " + "," +
                CHQBANKNAME + " TEXT " + "," +
                R_EMAIL + " TEXT " + "," +
                CREATEDBY + " TEXT " + "," +
                SIGNATURE + " TEXT " + "," +
                RECEIPTDATE + " TEXT " + "," +
                LONGITUDE + " TEXT " + "," +
                LATITUDE + " TEXT " + "," +
                DISCOUNT + " TEXT " + "," +
                IS_PRINT + " TEXT " + "," +
                IS_SYNC + " TEXT " +*/


            ContentValues contentValues = new ContentValues();

            contentValues.put(FK_CUSTOMER_ID, Cid);
            contentValues.put(FK_ACCOUNTNO, acno);
            contentValues.put(FK_BILL_ID, billid);
            contentValues.put(PAID_AMOUNT, paidamount);
            contentValues.put(PAYMENT_MODE, paymode);
            contentValues.put(CHQNUMBER, chqnumber);
            contentValues.put(CHQDATE, chqdate);
            contentValues.put(CHQBANKNAME, chqbankname);
            contentValues.put(R_EMAIL, remail);
            contentValues.put(CREATEDBY, createdby);
            contentValues.put(SIGNATURE, sign);
            contentValues.put(RECEIPTDATE, rdate);
            contentValues.put(LONGITUDE, longi);
            contentValues.put(LATITUDE, lati);
            contentValues.put(DISCOUNT, dis);
            contentValues.put(IS_PRINT, is_print);
            contentValues.put(IS_SYNC, is_sync);
            contentValues.put(RECEIPT_NO, recptNo);

            long count = db.insert(RECEIPTTABLE, null, contentValues);

            if (count > 0) {
                // db.close();

                return true;
            } else {
                //db.close();

                return false;
            }
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public String getcustomerOutstanding(String custid)
    {
        String os=null;

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select TOTAL_OUTSTANDING from "+ CUSTOMERTABLE + " where " +PK_CUSTOMER_IDD + " = "+custid,null);
        if(c.getCount()>0)
        {
            if(c.moveToFirst())
            {

                do {

                    os=c.getString(c.getColumnIndex(TOTAL_OUTSTANDING));
                }
                while (c.moveToNext());
            }
        }

        //db.close();
        return  os;
    }

    public String getCustomerArea(String custid)
    {
        String area=null;

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select FK_AREA_ID from "+ CUSTOMERTABLE + " where " +PK_CUSTOMER_IDD + " = "+custid,null);
        if(c.getCount()>0)
        {
            if(c.moveToFirst())
            {

                do {

                    area=c.getString(c.getColumnIndex(FK_AREA_ID));
                }
                while (c.moveToNext());
            }
        }
        //db.close();

        return  area;
    }


    public boolean UpdateOutstanding(String custid,String paidamount,String Discount)
    {
        String os=getcustomerOutstanding(custid);
        String areaid=getCustomerArea(custid);

        double ros=Double.parseDouble(os)-Double.parseDouble(paidamount)-Double.parseDouble(Discount);

        ContentValues contentValues=new ContentValues();
        contentValues.put(TOTAL_OUTSTANDING,String.valueOf(ros));

        SQLiteDatabase db=this.getWritableDatabase();

        if(db.update(CUSTOMERTABLE, contentValues, " CUSTOMER_ID = ? ", new String[]{custid})>0 && UpdateAreaOutstanding(areaid, paidamount, Discount))
        {
            //db.close();
            return true;
        }
        else
        {
            //db.close();
            return false;
        }


    }


    public boolean UpdateAreaOutstanding(String areaid,String paidamount,String Discount)
    {
        String col=null,os=null;

        SQLiteDatabase db=this.getWritableDatabase();

       /* public static final String AREATOTALOUTSTANDING= "AREA_OUTSTANDING";
        public static final String AREACOLLECTION= "AREA_COLLECTION";*/

        Cursor c = db.rawQuery("select AREA_OUTSTANDING,AREA_COLLECTION from " + AREATABLE + " where " + PK_AREA_ID + " = " + areaid, null);
        if(c!=null && c.getCount()>0)
        {
            if(c.moveToFirst())
            {

                do {

                    os=c.getString(c.getColumnIndex(AREATOTALOUTSTANDING));
                    col=c.getString(c.getColumnIndex(AREACOLLECTION));
                }
                while (c.moveToNext());
            }
        }
        else
        {
            return false;
        }

        double tos=Double.parseDouble(os)-Double.parseDouble(paidamount)-Double.parseDouble(Discount);
        double tcol=Double.parseDouble(col)+Double.parseDouble(paidamount);


        ContentValues contentValues=new ContentValues();
        contentValues.put(AREATOTALOUTSTANDING,String.valueOf(tos));
        contentValues.put(AREACOLLECTION,String.valueOf(tcol));

        db=this.getWritableDatabase();

        if(db.update(AREATABLE, contentValues, "AREA_ID = ? ", new String[]{areaid})>0)
        {
            //db.close();
            return true;
        }
        else
        {
            //db.close();
            return false;
        }


    }

    public Cursor getReceipts()
    {
        Cursor c=null;

        SQLiteDatabase db=this.getReadableDatabase();

        if(ReceiptCount()>0)
        {
            c = db.rawQuery("select * from "+ RECEIPTTABLE+" where "+ IS_SYNC +" = "+"'false'",null);
        }

        //db.close();

        return c;
    }

    public boolean UpdateReceiptStatus(String rid)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(IS_SYNC,"true");

        SQLiteDatabase db=this.getReadableDatabase();

        if(db.update(RECEIPTTABLE, contentValues, " PK_RECEIPT_ID = ? AND IS_SYNC = ?", new String[]{rid, "false"})>0)
        {
            //db.close();
            return true;
        }
        else
        {
           // db.close();
            return false;
        }

    }

    public void tempReceiptDelete()
    {
        SQLiteDatabase db=this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + RECEIPTTABLE);

        String CREATE_RECEIPT_TABLE = "CREATE TABLE " + RECEIPTTABLE +
                "(" +
                PK_RECEIPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FK_CUSTOMER_ID + " TEXT REFERENCES " + CUSTOMERTABLE + "," +
                FK_ACCOUNTNO + " TEXT " + "," +
                FK_BILL_ID + " TEXT " + "," +
                PAID_AMOUNT + " TEXT " + "," +
                PAYMENT_MODE + " TEXT " + "," +
                CHQNUMBER + " TEXT " + "," +
                CHQDATE + " TEXT " + "," +
                CHQBANKNAME + " TEXT " + "," +
                R_EMAIL + " TEXT " + "," +
                CREATEDBY + " TEXT " + "," +
                SIGNATURE + " TEXT " + "," +
                RECEIPTDATE + " TEXT " + "," +
                LONGITUDE + " TEXT " + "," +
                LATITUDE + " TEXT " + "," +
                DISCOUNT + " TEXT " + "," +
                IS_PRINT + " TEXT " + "," +
                IS_SYNC + " TEXT " +
                ")";

        db.execSQL(CREATE_RECEIPT_TABLE);

    }


    public void ClearAllData()
    {
        deleteEntityData();
        deleteData();
        deleteReceiptData();


       /* db.execSQL("DROP TABLE IF EXISTS "+ ENTITYTABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ AREATABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ CUSTOMERTABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ RECEIPTTABLE);*/

    }

    public Cursor SearchCustomer(String text)
    {

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from "+ CUSTOMERTABLE + " where " +ACCOUNTNO + " like '"+text+"' OR "+MQNO+" like '"+text+"' OR "+PHONE+" = '"+text+"'",null);

        //db.close();
        return  c;
    }



}
