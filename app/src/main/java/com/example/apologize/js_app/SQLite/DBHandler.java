package com.example.apologize.js_app.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CADLAB on 2016/8/11.
 */
public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "inventsInfo";
    // Contacts table name
    private static final String TABLE_INVENTS = "invents";
    // Shops Table Columns names
    private static final String KEY_ID = "Id";
    private static final String KEY_ITNO = "Itno";
    private static final String KEY_ITNAME = "Itname";
    private static final String KEY_ITNOUDF = "Itnoudf";
    private static final String KEY_ITUNIT = "Itunit";
    private static final String KEY_IN = "IN";
    private static final String KEY_AN = "AN";
    private static final String KEY_IAN = "IAN";
    private static final String KEY_IBP = "IBP";
    private static final String KEY_MONEY = "Money";
    private static final String KEY_EXPLAN = "Explan";
    private static final String KEY_PRODUCTCOMPOSITION = "ProductComposition";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_INVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ITNO + " TEXT,"
                + KEY_ITNAME + " TEXT,"
                + KEY_ITNOUDF + " TEXT,"
                + KEY_ITUNIT + " TEXT,"
                + KEY_IN + " DOUBLE,"
                + KEY_AN + " DOUBLE,"
                + KEY_IAN + " DOUBLE,"
                + KEY_IBP + " DOUBLE,"
                + KEY_MONEY + " DOUBLE,"
                + KEY_EXPLAN + " TEXT,"
                + KEY_PRODUCTCOMPOSITION + " TEXT" + " )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTS);
        // Creating tables again
        onCreate(db);
    }

    // Adding new invent
    public void addInvent(Invent invent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITNO, invent.getItno());
        values.put(KEY_ITNAME, invent.getItname());
        values.put(KEY_ITNOUDF, invent.getItnoudf());
        values.put(KEY_ITUNIT, invent.getItunit());
        values.put(KEY_IN, invent.getIN());
        values.put(KEY_AN, invent.getAN());
        values.put(KEY_IAN, invent.getIAN());
        values.put(KEY_IBP, invent.getIBP());
        values.put(KEY_MONEY, invent.getMoney());
        values.put(KEY_EXPLAN, invent.getExplan());
        values.put(KEY_PRODUCTCOMPOSITION, invent.getProductComposition());

        // Inserting Row
        db.insert(TABLE_INVENTS, null, values);
        db.close(); // Closing database connection
    }
    // Getting one invent
    public Invent getInvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INVENTS, new String[]{KEY_ID, KEY_ITNO, KEY_ITNAME, KEY_ITNOUDF, KEY_ITUNIT,
                        KEY_IN, KEY_AN, KEY_IAN, KEY_IBP, KEY_MONEY, KEY_EXPLAN, KEY_PRODUCTCOMPOSITION},
                        KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Invent contact = new Invent(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                Double.parseDouble(cursor.getString(5)),
                Double.parseDouble(cursor.getString(6)),
                Double.parseDouble(cursor.getString(7)),
                Double.parseDouble(cursor.getString(8)),
                Double.parseDouble(cursor.getString(9)),
                cursor.getString(10),
                cursor.getString(11));
        // return invent
        return contact;
    }
    // Getting All Invents
    public List<Invent> getAllInvents() {
        List<Invent> inventList = new ArrayList<Invent>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_INVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Invent invent = new Invent();
                invent.setId(Integer.parseInt(cursor.getString(0)));
                invent.setItno(cursor.getString(1));
                invent.setItname(cursor.getString(2));
                invent.setItnoudf(cursor.getString(3));
                invent.setItunit(cursor.getString(4));
                invent.setIN(Double.parseDouble(cursor.getString(5)));
                invent.setAN(Double.parseDouble(cursor.getString(6)));
                invent.setIAN(Double.parseDouble(cursor.getString(7)));
                invent.setIBP(Double.parseDouble(cursor.getString(8)));
                invent.setMoney(Double.parseDouble(cursor.getString(9)));
                invent.setExplan(cursor.getString(10));
                invent.setProductComposition(cursor.getString(11));
                // Adding contact to list
                inventList.add(invent);
            } while (cursor.moveToNext());
        }

        // return contact list
        return inventList;
    }
    // Getting invents Count
    public int getShopsCount() {
        String countQuery = "SELECT * FROM " + TABLE_INVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating a invent
    public int updateInvent(Invent invent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITNO, invent.getItno());
        values.put(KEY_ITNAME, invent.getItname());
        values.put(KEY_ITNOUDF, invent.getItnoudf());
        values.put(KEY_ITUNIT, invent.getItunit());
        values.put(KEY_IN, invent.getIN());
        values.put(KEY_AN, invent.getAN());
        values.put(KEY_IAN, invent.getIAN());
        values.put(KEY_IBP, invent.getIBP());
        values.put(KEY_MONEY, invent.getMoney());
        values.put(KEY_EXPLAN, invent.getExplan());
        values.put(KEY_PRODUCTCOMPOSITION, invent.getProductComposition());

        // updating row
        return db.update(TABLE_INVENTS, values, KEY_ID + " = ?",
        new String[]{String.valueOf(invent.getId())});
    }

    // Deleting a invent
    public void deleteInvent(Invent invent) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTS, KEY_ID + " = ?",
        new String[] { String.valueOf(invent.getId()) });
        db.close();
    }
}
