package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import org.apache.commons.codec.binary.Hex;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.apologize.js_app.APP_JPMenu;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Calendar;


/**
 * Created by Apologize on 2015/12/3.
 */
public class BaseActivity extends AppCompatActivity {
    protected boolean isNetConnection = false;
    public static boolean connectCancell = false;//判斷抓資料時按到首頁或返回鍵用
    InputMethodManager imm;//手機鍵盤啟動,顯示,隱藏

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//         TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean onTouch(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    public View.OnTouchListener endedit = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            return false;
        }
    };
    public static char[] bitmapToHex(Bitmap bitmap) {
        char[] result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).toCharArray();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在此方法設定以下
        //1.限制手機橫向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //2.去標題
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //3.全銀幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //4.進入畫面，先不要跳出鍵盤
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Common.openingActivity.add(this);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //onStart 畫面呈現至使用者，但不可操作
        //在此方法檢查以下
        //1.檢查網路連線(Manifest使用者權限)
        isNetConnection = NetWork.CheckNetWorkState(this);
    }

    @Override
    protected void onDestroy() {
        Common.openingActivity.remove(this);
        super.onDestroy();
    }

    public void onPrve(View view){
        connectCancell = true;
        finish();
    }

    public String dateToText(String date){
        if(date.length() == 8){
            return date.substring(0,4) + "/" + date.substring(4,6) + "/" + date.substring(6,8);
        }
        return date;
    }

    public String timeToText(String time){
        if(time.length() == 4){
            return time.substring(0,2) + ":" + time.substring(2,4);
        }else if(time.length() == 6){
            return time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
        }
        return time;
    }



    public void onJPMune(View view){
        for (int i=0;i<Common.openingActivity.size();i++)
        {
            connectCancell = true;
            if(Common.openingActivity.get(i).getClass().getName().equals(APP_JPMenu.class.getName()))continue;
            if(!Common.openingActivity.get(i).isFinishing())
                Common.openingActivity.get(i).finish();

        }
    }

    public void CallPhone(String tel) {
        if(tel.equals(""))return;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivity(intent);
        }
    }

    public void SendMail(String mail) {
        if(mail.equals(""))return;
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + mail));
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivity(intent);
        }
    }

    public void Map(String location) {
        if(location.equals(""))return;
        //使用Geocoder將中文地址轉換為經緯度
        //Locale.getDefault()，取得使用者手機中所選區域(台灣)
        //Geocoder geo = new Geocoder(context, Locale.getDefault());
        //第二個參數"1"，代表指回傳1個匹配結果
        //List<Address>addresses = geo.getFromLocationName(location,1);
        //Latitude=緯度，Longitude=經度
        //intent.setData(Uri.parse("geo:"+addresses.get(0).getLatitude()+","+addresses.get(0).getLongitude()));
        //以上方法無法規劃路線，與旗標
        try {
            //q=輸入查詢的完整地址或經緯度，如果需要標明可在結尾加上()，於()中輸入表示名稱
            //z=地圖比例大小，可輸入 1-18
            //t=模式，沒輸入值時為預設地圖；h為衛星圖加路線；p為地形圖
            //output=embed 指定Google Map為崁入模式
            String str = "http://maps.google.com.tw/maps?f=q&hl=zh-TW&geocode=&q="+ URLEncoder.encode(location, "UTF-8")+"&z=5&output=embed&t=p";
            Uri uri = Uri.parse(str);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void ToastError(String message){
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void DialogeError(String title,String message){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.error)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void DialogeSuccess(String title,String message){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.success)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }



    public String GetDate(int i) {
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
    public String GetMonthFirstDay(int i) {
        String year = "",month = "";
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH)+1 < 10)
            month = "0"+(calendar.get(Calendar.MONTH)+1);
        else
            month = ""+(calendar.get(Calendar.MONTH)+1);


        if(i == 1){//民國
            year = ""+(calendar.get(Calendar.YEAR)-1911);
        }else {//西元
            year = ""+calendar.get(Calendar.YEAR);
        }
        return year+month+"01";
    }
    public String DateAddLine(String date){
        String str = "";
        try{
            if(date.length() == 8){
                str = date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
            }else {
                str = date.substring(0,3)+"/"+date.substring(3,5)+"/"+date.substring(5,7);
            }
        }catch (Exception ex)
        {
            Log.d("Error", ex.toString());
        }
        return str;
    }
    public String DateRemoveLineToTWN(String date){
        String str="";
        if(date.length() == 10){
            str = String.valueOf(Integer.valueOf(date.substring(0, 4)) - 1911);
            str += date.substring(5,7) + date.substring(8,10);
        }else  if (date.length() == 9){
            str += date.substring(0,3) +date.substring(4,6) + date.substring(7,9);
        }
        return str;
    }



    public String GetMS(){
        String str = "#,##0";
        for (int i=0;i < Common.MS;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetMF(){
        String str = "#,##0";
        for (int i=0;i < Common.MF;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetQ(){
        String str = "#,##0";
        for (int i=0;i < Common.Q;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetMST(){
        String str = "#,##0";
        for (int i=0;i < Common.MST;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetMFT(){
        String str = "#,##0";
        for (int i=0;i < Common.MFT;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetTS(){
        String str = "#,##0";
        for (int i=0;i < Common.TS;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetTF(){
        String str = "#,##0";
        for (int i=0;i < Common.TF;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetTPS(){
        String str = "#,##0";
        for (int i=0;i < Common.TPS;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetTPF(){
        String str = "#,##0";
        for (int i=0;i < Common.TPF;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }

    public String GetM(){
        String str = "#,##0";
        for (int i=0;i < Common.M;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }

    public String GetPoint4(){
        String str = "#,##0";
        for (int i=0;i < 4;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }
    public String GetPoint6(){
        String str = "#,##0";
        for (int i=0;i < 6;i++)
        {
            if(i == 0)str+=".";
            str+="0";
        }
        return str;
    }

    public String FormatToString(Object obj,String format ){
        DecimalFormat f = new SelfFormat(format);
        try{
            if (obj instanceof String)
                return f.format(f.parse(obj.toString()).doubleValue());
            else  if (obj instanceof Double)
                return f.format(obj);
            else
                return f.format(0);
        }catch (Exception ex){
            return f.format(0);
        }
    }

    public double FormatToDouble(Object obj,String format ){
        DecimalFormat f = new SelfFormat(format);
        try{
            if (obj instanceof String)
                return f.parse(obj.toString()).doubleValue();
            else if (obj instanceof Double)
                return f.parse(f.format((double)obj)).doubleValue();
            else
                return f.parse(f.format(0)).doubleValue();
        }catch (Exception ex){
            return Double.valueOf(f.format(0));
        }
    }

    public String ChangeChar(String s) {
        Log.d("heyCheckIn", "preString="+s);
        char c[] = s.toCharArray();
        char c1[] = {'U','u','V','v','W','w','X','x','Y','y','Z','z','G','g','H','h','I','i','J','j','K','k','L','l','M','m','N','n','O','o','P','p','Q','q','R','r','S','s','T','t','_','A','a','B','b','0','1','2','3','4','5','C','c','D','d','E','e','F','f','6','7','8','9'};
        char c2[] = {'_','0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        for(int i = 0; i < c.length; i++){
            Log.d("heyCheckIn", "["+i+"] = "+c[i]);
            for(int j = 0; j < c1.length; j++){
                if (c[i] == c1[j]){
                    c[i] = c2[j];
                }
            }
            Log.d("heyCheckIn", "["+i+"] = "+c[i]);
        }
        Log.d("heyCheckIn", "newString="+new String(c));
        return new String(c);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            Log.d("heyCheckIn", "MD5str="+hexString.toString());
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
