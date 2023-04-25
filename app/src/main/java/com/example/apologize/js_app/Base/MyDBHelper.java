package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.namespace.R;

//import com.example.apologize.js_app.R;

/**
 * Created by wks5111 on 2017/4/20.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "stock.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    final private static String CreateTxt = "CREATE TABLE scritd("
                                         +"_id INTEGER PRIMARY KEY,"
                                         +"taname TEXT,"
                                         +"sc01 TEXT,"
                                         +"sc02 TEXT,"
                                         +"sc03 TEXT,"
                                         +"sc04 TEXT,"
                                         +"sc05 TEXT,"
                                         +"sc06 TEXT,"
                                         +"sc07 TEXT,"
                                         +"sc08 TEXT,"
                                         +"sc09 TEXT);";


    // 建構子，在一般的應用都不需要修改
    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        db.execSQL(CreateTxt);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS scritd");
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

    public static boolean rawQuery(Context context, String name, String function){
        boolean canWork = false;
        try{
            String str = "";
            switch (function){
                case "新增": str = "sc01";break;
                case "修改": str = "sc02";break;
                case "查詢": str = "sc03";break;
                case "刪除": str = "sc04";break;
                case "列印": str = "sc05";break;
                case "複製": str = "sc06";break;
                case "瀏覽": str = "sc07";break;
            }
            SQLiteDatabase db = new MyDBHelper(context).getReadableDatabase();
            Cursor c = db.rawQuery("select " + str + " from scritd where taname=?", new String[]{name});

            if(c.getCount() > 0){
                c.moveToFirst();
                if(c.getString(0).equals("V")) {
                    canWork = true;
                }
            }

            if(canWork == false){
                new AlertDialog.Builder(context)
                        .setTitle("無功能權限")
                        .setIcon(R.drawable.error)
                        .setMessage("您" + name + "[" + function + "]功能未開放!!!")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }

            c.close();
            db.close();
        }catch (Exception ex){
            Toast.makeText(context,ex.toString(),Toast.LENGTH_LONG).show();
        }finally {
            return  canWork;
        }
    }
}
