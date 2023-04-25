package com.example.apologize.js_app.CheckInWork;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.CheckInWork.Leave.LeaveAudit;
import com.example.apologize.js_app.R;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CheckInWorkMenu extends BaseActivity {
    SharedPreferences sp;
    ArrayList<String> employeid_list,employename_list,no_list,dakaphoto_list,dakalnglat_list;
    int 年,月,日;
    TextView yearmonthday;
    TextClock timeset;
    String time = "";
    Button Locate;
    Button 上班打卡,下班打卡,休息時間1起,休息時間1終,加班上班,加班下班;
    LinearLayout LocateLinearLayout,one,two,three,four;
    String lat = "", lng = "";
    Handler myHandler = new Handler();
    DBSearch SearchAttenddbSearch = new DBSearch();
    DBSearch InsertGpsIfodbSearch = new DBSearch();
    DBSearch UpdateAttenddbSearch = new DBSearch();
    DBSearch SearchRecordCarddbSearch = new DBSearch();
    DBSearch InsertRecordCarddbSearch = new DBSearch();
    DBSearch UpdateRecordCarddbSearch = new DBSearch();
    boolean dakaOK = false;
    static boolean callsp = false;
    ProgressDialog dialog;
    String distance = "0";
    String updateattendstr = "";
    String updaterecordcardstr = "";
    Bitmap resizedBitmap;

    static boolean 民國TF = false;
    static boolean 上班打卡TF = true;
    static boolean 下班打卡TF = true;
    static boolean 休息時間1起TF = true;
    static boolean 休息時間1終TF = true;
    static boolean 加班上班TF = true;
    static boolean 加班下班TF = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_work_menu);
        findviews();
        if (Common.needLocation) {
            requestUserLocation();
        }
    }

    @Override
    protected void onStart() {
        Log.d("heyCheckInWorkMenu", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
        if (Common.needPhoto) {
            requestUserLocation();
        }
        sp = getSharedPreferences("CheckIn",MODE_PRIVATE);
        if (sp.getString("first","").equals("")) {
            上班打卡.setVisibility(View.VISIBLE);
            下班打卡.setVisibility(View.VISIBLE);
            休息時間1起.setVisibility(View.VISIBLE);
            休息時間1終.setVisibility(View.VISIBLE);
            加班上班.setVisibility(View.VISIBLE);
            加班下班.setVisibility(View.VISIBLE);
        } else {
            if (callsp) {
                CheckInWorkMenu.民國TF = sp.getBoolean("民國TF", true);
                CheckInWorkMenu.上班打卡TF = sp.getBoolean("上班打卡TF", true);
                CheckInWorkMenu.下班打卡TF = sp.getBoolean("下班打卡TF", true);
                CheckInWorkMenu.休息時間1起TF = sp.getBoolean("休息時間1起TF", true);
                CheckInWorkMenu.休息時間1終TF = sp.getBoolean("休息時間1終TF", true);
                CheckInWorkMenu.加班上班TF = sp.getBoolean("加班上班TF", true);
                CheckInWorkMenu.加班下班TF = sp.getBoolean("加班下班TF", true);
            }
            if (CheckInWorkMenu.民國TF == true) {
                sp.edit().putBoolean("民國TF", true).commit();
            } else {
                sp.edit().putBoolean("民國TF", false).commit();
            }
            if (sp.getBoolean("民國TF", true) == true) {
                if (月 >= 9) {
                    if (日 > 9) {
                        yearmonthday.setText((年-1911)+"."+(月+1)+"."+日);
                    } else {
                        yearmonthday.setText((年-1911)+"."+(月+1)+".0"+日);
                    }
                } else {
                    if (日 > 9) {
                        yearmonthday.setText((年-1911)+".0"+(月+1)+"."+日);
                    } else {
                        yearmonthday.setText((年-1911)+".0"+(月+1)+".0"+日);
                    }
                }
            } else {
                if (月 >= 9) {
                    if (日 > 9) {
                        yearmonthday.setText(年+"."+(月+1)+"."+日);
                    } else {
                        yearmonthday.setText(年+"."+(月+1)+".0"+日);
                    }
                } else {
                    if (日 > 9) {
                        yearmonthday.setText(年+".0"+(月+1)+"."+日);
                    } else {
                        yearmonthday.setText(年+".0"+(月+1)+".0"+日);
                    }
                }
            }
            if (CheckInWorkMenu.上班打卡TF == true) {
                sp.edit().putBoolean("上班打卡TF", true).commit();
            } else {
                sp.edit().putBoolean("上班打卡TF", false).commit();
            }
            if (sp.getBoolean("上班打卡TF", true) == true) {
                上班打卡.setVisibility(View.VISIBLE);
            } else {
                上班打卡.setVisibility(View.INVISIBLE);
            }
            if (CheckInWorkMenu.下班打卡TF == true) {
                sp.edit().putBoolean("下班打卡TF", true).commit();
            } else {
                sp.edit().putBoolean("下班打卡TF", false).commit();
            }
            if (sp.getBoolean("下班打卡TF", true) == true) {
                下班打卡.setVisibility(View.VISIBLE);
            } else {
                下班打卡.setVisibility(View.INVISIBLE);
            }
            if (CheckInWorkMenu.休息時間1起TF == true) {
                sp.edit().putBoolean("休息時間1起TF", true).commit();
            } else {
                sp.edit().putBoolean("休息時間1起TF", false).commit();
            }
            if (sp.getBoolean("休息時間1起TF", true) == true) {
                休息時間1起.setVisibility(View.VISIBLE);
            } else {
                休息時間1起.setVisibility(View.INVISIBLE);
            }
            if (CheckInWorkMenu.休息時間1終TF == true) {
                sp.edit().putBoolean("休息時間1終TF", true).commit();
            } else {
                sp.edit().putBoolean("休息時間1終TF", false).commit();
            }
            if (sp.getBoolean("休息時間1終TF", true) == true) {
                休息時間1終.setVisibility(View.VISIBLE);
            } else {
                休息時間1終.setVisibility(View.INVISIBLE);
            }

            if (CheckInWorkMenu.加班上班TF == true) {
                sp.edit().putBoolean("加班上班TF", true).commit();
            } else {
                sp.edit().putBoolean("加班上班TF", false).commit();
            }
            if (sp.getBoolean("加班上班TF", true) == true) {
                加班上班.setVisibility(View.VISIBLE);
            } else {
                加班上班.setVisibility(View.INVISIBLE);
            }
            if (CheckInWorkMenu.加班下班TF == true) {
                sp.edit().putBoolean("加班下班TF", true).commit();
            } else {
                sp.edit().putBoolean("加班下班TF", false).commit();
            }
            if (sp.getBoolean("加班下班TF", true) == true) {
                加班下班.setVisibility(View.VISIBLE);
            } else {
                加班下班.setVisibility(View.INVISIBLE);
            }
        }
        Log.d("heyCheckInWorkMeun1", "sp民國TF:" + sp.getBoolean("民國TF", true));
        Log.d("heyCheckInWorkMeun1", "sp上班打卡TF:" + sp.getBoolean("上班打卡TF", true));
        Log.d("heyCheckInWorkMeun1", "sp下班打卡TF:" + sp.getBoolean("下班打卡TF", true));
        Log.d("heyCheckInWorkMeun1", "sp休息時間1起TF:" + sp.getBoolean("休息時間1起TF", true));
        Log.d("heyCheckInWorkMeun1", "sp休息時間1終TF:" + sp.getBoolean("休息時間1終TF", true));
        Log.d("heyCheckInWorkMeun1", "sp外出打卡TF:" + sp.getBoolean("外出打卡TF", true));
        Log.d("heyCheckInWorkMeun1", "sp歸來打卡TF:" + sp.getBoolean("歸來打卡TF", true));
        Log.d("heyCheckInWorkMeun1", "sp加班上班TF:" + sp.getBoolean("加班上班TF", true));
        Log.d("heyCheckInWorkMeun1", "sp加班下班TF:" + sp.getBoolean("加班下班TF", true));
    }

    void findviews() {
        yearmonthday = (TextView)findViewById(R.id.yearmonthday);
        timeset = (TextClock)findViewById(R.id.timeset);
        Locate = (Button) findViewById(R.id.Locate);
        LocateLinearLayout = (LinearLayout)findViewById(R.id.LocateLinearLayout);
        one = (LinearLayout)findViewById(R.id.one);
        two = (LinearLayout)findViewById(R.id.two);
        three = (LinearLayout)findViewById(R.id.three);
        four = (LinearLayout)findViewById(R.id.four);
        上班打卡 = (Button) findViewById(R.id.上班打卡);
        下班打卡 = (Button) findViewById(R.id.下班打卡);
        休息時間1起 = (Button) findViewById(R.id.休息時間1起);
        休息時間1終 = (Button) findViewById(R.id.休息時間1終);
        加班上班 = (Button) findViewById(R.id.加班上班);
        加班下班 = (Button) findViewById(R.id.加班下班);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        employeid_list = bundle.getStringArrayList("employeid_list");
        employename_list = bundle.getStringArrayList("employename_list");
        no_list = bundle.getStringArrayList("no_list");
        dakaphoto_list = bundle.getStringArrayList("dakaphoto_list");
        dakalnglat_list = bundle.getStringArrayList("dakalnglat_list");
        callsp = bundle.getBoolean("callsp");
        Log.d("heyCheckInWorkMeun", "employeid_list:" + employeid_list);
        Log.d("heyCheckInWorkMeun", "employename_list:" + employename_list);
        Log.d("heyCheckInWorkMeun", "no_list:" + no_list);
        Log.d("heyCheckInWorkMeun", "dakaphoto_list:" + dakaphoto_list);
        Log.d("heyCheckInWorkMeun", "dakalnglat_list:" + dakalnglat_list);

        if (dakalnglat_list.get(0).equals("False")){
            Locate.setVisibility(View.INVISIBLE);
            LocateLinearLayout.setVisibility(View.INVISIBLE);
        }

        Calendar mCalendar = new GregorianCalendar();
        年 = mCalendar.get(Calendar.YEAR);
        月 = mCalendar.get(Calendar.MONTH);
        日 = mCalendar.get(Calendar.DAY_OF_MONTH);

        if (月 >= 9) {
            if (日 > 9) {
                yearmonthday.setText(年+"."+(月+1)+"."+日);
            } else {
                yearmonthday.setText(年+"."+(月+1)+".0"+日);
            }
        } else {
            if (日 > 9) {
                yearmonthday.setText(年+".0"+(月+1)+"."+日);
            } else {
                yearmonthday.setText(年+".0"+(月+1)+".0"+日);
            }
        }
        timeset.getFormat24Hour();
        timeset.is24HourModeEnabled();
        timeset.setFormat24Hour("HH:mm:ss");
    }

    public void onLocate(View view) {
        requestUserLocation();
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.gpssign)
                .setTitle("現在位置")
                .setMessage("<"+lat+","+lng+">")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
        return;
    }

    public boolean requestUserLocation() {
        boolean no = true;
        final LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判斷當前是否已經獲得了定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CheckInWorkMenu.this,("無法存取位置權限"), Toast.LENGTH_SHORT).show();
//            如果是6.0以上的去需求權限
            no = false;
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            return false;

        final List<String> permissionsList = new ArrayList<>();
        if(this.checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CAMERA);
        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            this.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]) , 0x00);
        Log.d("size",permissionsList.size() + "");
        if(permissionsList.size()>=1){
            goToAppSetting();
            return false;
        }

        String key = "";

        if(Common.needLocation){
            Location location = mLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            key = LocationManager.GPS_PROVIDER;
            if(location == null){
                location = mLocation.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                key = LocationManager.NETWORK_PROVIDER;
            }
            if(location != null){
                try{
                    lat = "" + Double.valueOf(location.getLatitude()).toString();
                    lng = "" + Double.valueOf(location.getLongitude()).toString();
                }catch(NumberFormatException e){
                    Log.d("error",e.getMessage());
                    lat = "";
                    lng = "";
                }
                if(lat.length() > 0 && lng.length() > 0 && Common.latitude.length() > 0 && Common.longitude.length() > 0){

                    float results[] = new float[1];

                    try{

                        Double latitude,longtitude,comLatitude,comLongtitude;
                        latitude = Double.valueOf(lat);
                        longtitude = Double.valueOf(lng);
                        comLatitude = Double.valueOf(Common.latitude);
                        comLongtitude = Double.valueOf(Common.longitude);
                        Location.distanceBetween(comLatitude, comLongtitude, latitude, longtitude, results);
                        distance = NumberFormat.getInstance().format(results[0]);
                        ((Button) findViewById(R.id.Locate)).setText(("距離公司還有")+distance+"m");
                    }catch(NumberFormatException e){
                        Log.d("error",e.getMessage());
                        distance = "";
                    }

                }
            }else{
                lat = "";
                lng = "";
            }

            mLocation.requestLocationUpdates(key,1,1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try{
                        lat = "" + Double.valueOf(location.getLatitude()).toString();
                        lng = "" + Double.valueOf(location.getLongitude()).toString();
                    }catch(NumberFormatException e){
                        Log.d("error",e.getMessage());
                        lat = "";
                        lng = "";
                    }
                    if(lat.length() > 0 && lng.length() > 0 && Common.latitude.length() > 0 && Common.longitude.length() > 0){

                        float results[] = new float[1];

                        try{

                            Double latitude,longtitude,comLatitude,comLongtitude;
                            latitude = Double.valueOf(lat);
                            longtitude = Double.valueOf(lng);
                            comLatitude = Double.valueOf(Common.latitude);
                            comLongtitude = Double.valueOf(Common.longitude);
                            Location.distanceBetween(comLatitude, comLongtitude, latitude, longtitude, results);
                            distance = NumberFormat.getInstance().format(results[0]);
                            ((Button) findViewById(R.id.Locate)).setText(("距離公司還有")+distance+"m");
                        }catch(NumberFormatException e){
                            Log.d("error",e.getMessage());
                            distance = "";
                        }
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d("enable","enable");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d("enable","disable");

                }
            }, CheckInWorkMenu.this.getMainLooper());
        }



        if(Common.needLocation && Common.needPhoto){

            if(lat.equals("") || lng.equals("")){
                ToastError(("請開啟GPS定位"));
                return false;
            }

            return no;
        }else if(Common.needLocation){

            if(lat.equals("") || lng.equals("")){
                ToastError("請開啟GPS定位");
                return false;
            }

            return no;
        }else if(Common.needPhoto){
            return true;
        }else{
            return true;
        }

    }

    private void goToAppSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", this.getPackageName(), null));
        startActivityForResult(intent , 0x00);
    }

    boolean searchattend() {
        String searchattendstr = "select  employeid  from  EmployeeAttend  where  employeid=@account and ndDate=@date";
        String ndDate;
        boolean TF;
        SearchAttenddbSearch.PutParameter("account", employeid_list.get(0));
        if (月 >= 9) {
            if (日 > 9) {
                ndDate = String.valueOf((年-1911))+(月+1)+日+"";
            } else {
                ndDate = String.valueOf((年-1911))+(月+1)+"0"+日;
            }
        } else {
            if (日 > 9) {
                ndDate = String.valueOf((年-1911))+"0"+(月+1)+日;
            } else {
                ndDate = String.valueOf((年-1911))+"0"+(月+1)+"0"+日;
            }
        }
        SearchAttenddbSearch.PutParameter("date", ndDate);

        Log.d("年",String.valueOf(年));
        Log.d("月",String.valueOf(月));
        Log.d("日",String.valueOf(日));
        Log.d("message",SearchAttenddbSearch.returnParameter().toString());

        if(SearchAttenddbSearch.JPSearchForGet(searchattendstr) == DBSearch.Result.fund) {
            TF = true;
        } else {
            TF = false;
        }
        Log.d("heyCheckInWorkMeun", "searchattendTF:" + TF);
        return TF;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null){

            try{
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                //43670
                int oldwidth = photo.getWidth();
                int oldheight = photo.getHeight();
                double scaleWidth = 1.1;
                double scaleHeight = 1.1;
                do{
                    scaleWidth -= 0.1;
                    scaleHeight -= 0.1;
                    Matrix matrix = new Matrix();
                    matrix.postScale((float)scaleWidth, (float)scaleHeight);

                    Log.d("doint","create the new Bitmap object");
                    // create the new Bitmap object
                    resizedBitmap = Bitmap.createBitmap(photo, 0, 0, oldwidth,
                            oldheight, matrix, true);
                }while(bitmapToHex(resizedBitmap).length > 43670);

                insertgpsinfo(String.copyValueOf(LeaveAudit.bitmapToHex(resizedBitmap)));

            }catch(Exception e){
                Log.d("error",e.getMessage());
                ToastError("打卡失敗");
            }

        }
    }

    void uploadPic(){

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);


    }

    void  insertgpsinfo(String bitmap){

        final String insertgpsinfostr = "INSERT INTO [GpsInfo]([ndDate],[ndDate2],[dakatime],[employeid],[employeno],[employename],[discern],[longitude],[latitude],[checkrange],[photo])" +
                "VALUES(@ndDate,@ndDate2,@dakatime,@employeid,@employeno,@employename,@discern,@longitude,@latitude,@checkrange,";
        boolean TF;
        if (月 >= 9) {
            if (日 > 9) {
                InsertGpsIfodbSearch.PutParameter("ndDate", String.valueOf((年-1911))+(月+1)+日);
                InsertGpsIfodbSearch.PutParameter("ndDate2", 年+(月+1)+日);
            } else {
                InsertGpsIfodbSearch.PutParameter("ndDate", String.valueOf((年-1911))+(月+1)+"0"+日);
                InsertGpsIfodbSearch.PutParameter("ndDate2", 年+(月+1)+"0"+日);
            }
        } else {
            if (日 > 9) {
                InsertGpsIfodbSearch.PutParameter("ndDate", String.valueOf((年-1911))+"0"+(月+1)+日);
                InsertGpsIfodbSearch.PutParameter("ndDate2", 年+"0"+(月+1)+日);
            } else {
                InsertGpsIfodbSearch.PutParameter("ndDate", String.valueOf((年-1911))+"0"+(月+1)+"0"+日);
                InsertGpsIfodbSearch.PutParameter("ndDate2", 年+"0"+(月+1)+"0"+日);
            }
        }
        InsertGpsIfodbSearch.PutParameter("dakatime", time);
        InsertGpsIfodbSearch.PutParameter("employeid", employeid_list.get(0));
        InsertGpsIfodbSearch.PutParameter("employeno", no_list.get(0));
        InsertGpsIfodbSearch.PutParameter("employename", employename_list.get(0));
        if (Common.needLocation) {
            InsertGpsIfodbSearch.PutParameter("longitude", lng);
            InsertGpsIfodbSearch.PutParameter("latitude", lat);
            if (Double.parseDouble(distance.replace(",","")) < Double.parseDouble(Common.checkrange)) {
                InsertGpsIfodbSearch.PutParameter("checkrange", "true");
            } else {
                InsertGpsIfodbSearch.PutParameter("checkrange", "false");
            }
        } else {
            InsertGpsIfodbSearch.PutParameter("longitude", "0.0");
            InsertGpsIfodbSearch.PutParameter("latitude", "0.0");
            InsertGpsIfodbSearch.PutParameter("checkrange", "false");
        }


        final ArrayList<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("photo","0x" + bitmap));

        new Thread(new Runnable() {
            @Override
            public void run() {
                InsertGpsIfodbSearch.PostToCheckin(insertgpsinfostr,list);

                updateattend();
            }
        }).start();

    }

    void updateattend() {
        final boolean TF;
        UpdateAttenddbSearch.PutParameter("dakatime", time);
        UpdateAttenddbSearch.PutParameter("account", employeid_list.get(0));
        if (月 >= 9) {
            if (日 > 9) {
                UpdateAttenddbSearch.PutParameter("date", String.valueOf((年-1911))+(月+1)+日);
            } else {
                UpdateAttenddbSearch.PutParameter("date", String.valueOf((年-1911))+(月+1)+"0"+日);
            }
        } else {
            if (日 > 9) {
                UpdateAttenddbSearch.PutParameter("date", String.valueOf((年-1911))+"0"+(月+1)+日);
            } else {
                UpdateAttenddbSearch.PutParameter("date", String.valueOf((年-1911))+"0"+(月+1)+"0"+日);
            }
        }
        if(UpdateAttenddbSearch.JPModifyData(updateattendstr) == DBSearch.Result.modified) {
            TF = true;
        } else {
            TF = false;
        }

        myHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!TF) {
                    Toast.makeText(getApplicationContext(),"查無出勤歷，打卡失敗", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"打卡成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean searchRecordCard() {
        String searchRecordCardstr = "select employeid from RecordCard where employeid=@account and ReDate=@date";
        String ndDate;
        boolean TF;
        SearchRecordCarddbSearch.PutParameter("account", employeid_list.get(0));
        if (月 >= 9) {
            if (日 > 9) {
                ndDate = String.valueOf((年-1911))+(月+1)+日+"";
            } else {
                ndDate = String.valueOf((年-1911))+(月+1)+"0"+日;
            }
        } else {
            if (日 > 9) {
                ndDate = String.valueOf((年-1911))+"0"+(月+1)+日;
            } else {
                ndDate = String.valueOf((年-1911))+"0"+(月+1)+"0"+日;
            }
        }
        SearchRecordCarddbSearch.PutParameter("date", ndDate);
        if(SearchRecordCarddbSearch.JPSearchForGet(searchRecordCardstr) == DBSearch.Result.fund) {
            TF = true;
        } else {
            TF = false;
        }
        Log.d("heyCheckInWorkMeun", "searchRecordCardTF:" + TF);
        return TF;
    }

    boolean insertRecordCard() {
        String insertRecordCardstr = "insert into RecordCard ([Employeid],[EmployeName],[ReDate],[ReDate1],[ImDate],[ImDate1],[goout],[goback]) " +
                "values (@employeid,@employename,@ndDate,@ndDate2,@ndDate,@ndDate2,@dakatime,@dakatime1)";
        boolean TF;
        InsertRecordCarddbSearch.PutParameter("employeid", employeid_list.get(0));
        InsertRecordCarddbSearch.PutParameter("employename", employename_list.get(0));
        if (月 >= 9) {
            if (日 > 9) {
                InsertRecordCarddbSearch.PutParameter("ndDate", String.valueOf((年-1911))+(月+1)+日);
                InsertRecordCarddbSearch.PutParameter("ndDate2", 年+(月+1)+日);
            } else {
                InsertRecordCarddbSearch.PutParameter("ndDate", String.valueOf((年-1911))+(月+1)+"0"+日);
                InsertRecordCarddbSearch.PutParameter("ndDate2", 年+(月+1)+"0"+日);
            }
        } else {
            if (日 > 9) {
                InsertRecordCarddbSearch.PutParameter("ndDate", String.valueOf((年-1911))+"0"+(月+1)+日);
                InsertRecordCarddbSearch.PutParameter("ndDate2", 年+"0"+(月+1)+日);
            } else {
                InsertRecordCarddbSearch.PutParameter("ndDate", String.valueOf((年-1911))+"0"+(月+1)+"0"+日);
                InsertRecordCarddbSearch.PutParameter("ndDate2", 年+"0"+(月+1)+"0"+日);
            }
        }
        if (InsertRecordCarddbSearch.JPInsertData(insertRecordCardstr) == DBSearch.Result.inserted) {
            TF = true;
        } else {
            TF = false;
        }
        Log.d("heyCheckInWorkMeun", "insertRecordCardTF:" + TF);
        return TF;
    }


    public void onOntimeD(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        requestUserLocation();
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set ontime=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.ontimeD);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }}
        }).start();
    }

    public void onOfftimeD(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        requestUserLocation();
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set offtime=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.offtimeD);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }

            }
        }).start();
    }

    public void onRestime1D(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        requestUserLocation();
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set restime1=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.restime1D);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    public void onRestime2D(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set restime2=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.restime2D);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }}
        }).start();
    }


    public void onAddontimeD(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        requestUserLocation();
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set addontime=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.addontimeD);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }}
        }).start();
    }

    public void onAddofftimeD(View view) {
        Calendar mCalendar = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        time = df.format(mCalendar.getTime());
        requestUserLocation();
        if(!NetWork.CheckNetWorkState(this))return;

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "儲存打卡資訊中", "請稍候...");
        dakaOK = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //有抓到資料且轉換List成功
                updateattendstr = "update EmployeeAttend set addofftime=@dakatime  where  employeid=@account and ndDate=@date";
                if(searchattend()) {
                    InsertGpsIfodbSearch.PutParameter("discern", Common.addofftimeD);
                    if(Common.needPhoto){
                        uploadPic();
                    }else{
                        insertgpsinfo("0x");
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }}
        }).start();
    }

    public void onSetCheckIn(View view) {
        Intent intent = new Intent(this,SetCheckInButton.class);
        sp.edit().putString("first", "nofirst").commit();
        startActivity(intent);
    }

}