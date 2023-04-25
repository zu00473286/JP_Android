package com.example.apologize.js_app.Base;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Apologize on 2015/12/4.
 */
public class Common {

    public static String APIKEY = "AIzaSyCyOjDtNAxn8tb-WTPjgRqOugUcnruEI0o";

    public static boolean haveRepairFunction = false;
    public static String ip = "";
    public static String port = "";
    public static String db_name = "";
    public static String db_user = "";
    public static String db_pw = "";
    public static String cn = "";

    public static boolean needPhoto = true;
    public static boolean needLocation = true;

    public static String user_id = "";
    public static String user_name = "";
    public static int user_date = 1;
    public static boolean user_ShopPrice;
    public static boolean user_SalePrice;

    public static String year1 = "";
    public static String year2 = "";

    public static int MS = 0;//銷貨單價小數
    public static int MST = 0;//銷貨單據小數
    public static int TS = 0;//銷項稅額小數
    public static int M = 0;//本幣金額小數

    public static int MF = 0;//進貨單價小數
    public static int MFT = 0;//進貨單據小數
    public static int TF = 0;//進項稅額小數
    public static int Q = 0;//庫存數量小數

    public static int TPS = 0;//銷項金額小數
    public static int TPF = 0;//進項金額小數

    public static int NoAdd = 0;//單據編碼方式 1:民國年月日+流水號 2:民國年月+流水號 3:西元年月日+流水號 4:西元年月+流水號

    public static double Vers = 2.0;

    public static String jpip = "";
    public static String jpport = "";
    public static String jpdb_name = "";
    public static String jpdb_user = "";
    public static String jpdb_pw = "";
    public static String jpcn = "";
    public static String cnjp = "";

    public static String jpuser_id = "";         //打卡帳號
    public static String jpuser_name = "";
    public static String jpuser_empid = "";      //員工編號
    public static String jpuser_empname = "";   //員工姓名

    public static String workday = "";         //到職日
    public static String leaveSet = "";         //特休狀態1-整年度;2-到職日3.會計年度制
    public static String NianduBegin = "";     //會計年度開始日期
    public static String standardhour = "";     //基本時數
    public static String absenceno = "";       //基本時數
    
    public static String checkrange = "200";
    public static String latitude = "";
    public static String longitude = "";

    public static String ontimeD = "";
    public static String offtimeD = "";
    public static String restime1D = "";
    public static String restime2D = "";
    public static String addontimeD = "";
    public static String addofftimeD = "";
    public static String gooutD = "";
    public static String gobackD = "";
    public static  ArrayList<String> leave = new ArrayList<>();
    public static  ArrayList<String> leaveno = new ArrayList<>();

    //因為App_Menu後開啟的頁面都有回首頁，所以App_Menu後開啟的Activity都會加入此泛型陣列
    //若App_Menu->1->2，由2按下回首頁，檢查此泛型內容關閉1
    public static ArrayList<Activity>openingActivity = new ArrayList<Activity>();

    public static String GetDate(int i) {
        String year = "",month = "",day = "";
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH)+1 < 10)
            month = "0"+(calendar.get(Calendar.MONTH)+1);
        else
            month = ""+(calendar.get(Calendar.MONTH)+1);

        if(calendar.get(Calendar.DAY_OF_MONTH) < 10)
            day = "0"+calendar.get(Calendar.DAY_OF_MONTH);
        else
            day = ""+calendar.get(Calendar.DAY_OF_MONTH);

        if(i == 1){//民國
            year = ""+(calendar.get(Calendar.YEAR)-1911);
        }else {//西元
            year = ""+calendar.get(Calendar.YEAR);
        }
        return year+month+day;
    }

    public static String toLanguage(String string){
        return string;
    }

    public static String GetFormatStr(int point){
        String str = "###,###,##0";
        for (int i=0;i<point;i++){
            if(i==0)str+=".";
            str+="0";
        }
        return str;
    }

    public static String DateToTWD(String date) {

        if(date.length() == 0){
            return "";
        }

        if(date.length() == 7){
            return  date;
        }else {
            int i = Integer.parseInt(date.substring(0,4)) - 1911;
            return i+date.substring(4,6)+date.substring(6);
        }
    }

    public static String DateToUSD(String date) {

        if(date.length() == 0){
            return "";
        }

        if(date.length() == 7){
            int i = Integer.parseInt(date.substring(0,3)) + 1911;
            return i+date.substring(3,5)+date.substring(5);
        }else {
            return  date;
        }
    }

    public static boolean IsDatetime(String date,Context context){
        try{
            String str = "";
            if(date.length() == 7){
                str = DateToUSD(date);
            }else if(date.length() == 8) {
                str = date;
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setLenient(false);
            format.parse(str);
            return  true;
        }catch (Exception e){
            Toast.makeText(context,"日期格式錯誤",Toast.LENGTH_LONG).show();
            return  false;
        }
    }

    public static String GetUTF8(String str,int Maxlength){
        String result = "";
        int nowLen = 0;
        for (char c:str.toCharArray()){
            if(c >=0x20 && c<=0x7E){// 字元值 32~126 是 ASCII 半形字元的範圍
                nowLen++;
            }else {
                nowLen+=2;// 非半形字元
            }
            if(nowLen <= Maxlength)
                result+=c;
            else
                break;
        }
        return result;
    }
}
