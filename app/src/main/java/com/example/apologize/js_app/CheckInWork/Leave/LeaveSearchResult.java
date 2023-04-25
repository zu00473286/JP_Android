package com.example.apologize.js_app.CheckInWork.Leave;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.apologize.js_app.APP_JPMenu;
import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 2017/11/14.
 */

public class LeaveSearchResult extends BaseActivity {

    boolean isCheck = true;
    int myYear,myMonth,myDay;
    boolean isModify = false;
    StringBuilder sb;
    boolean connectOK;
    DBSearch dbSearch;
    Handler myHandler = new Handler();
    String bid = "";
    String leaveYear = "";
    String leaveYear2 = "";
    String ljtxday = "";
    String ljtxday2 = "";
    TextView l1,l2,l3,l4,l5,l6,l7,l8,l9;
    Spinner t3;
    EditText t1,t2,t4,t5,t6,t7,t8,t9;
    Button b1,b2;
    JSONObject ClassTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_search_result);
        findView();
    }

    void findView(){

        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l3 = (TextView)findViewById(R.id.l3);
        l4 = (TextView)findViewById(R.id.l4);
        l5 = (TextView)findViewById(R.id.l5);
        l6 = (TextView)findViewById(R.id.l6);
        l7 = (TextView)findViewById(R.id.l7);
        l8 = (TextView)findViewById(R.id.l8);
        l9 = (TextView)findViewById(R.id.l9);

        t1 = (EditText)findViewById(R.id.t1);
        t2 = (EditText)findViewById(R.id.t2);
        t4 = (EditText)findViewById(R.id.t4);
        t5 = (EditText)findViewById(R.id.t5);
        t6 = (EditText)findViewById(R.id.t6);
        t7 = (EditText)findViewById(R.id.t7);
        t8 = (EditText)findViewById(R.id.t8);
        t9 = (EditText)findViewById(R.id.t9);

        t3 = (Spinner)findViewById(R.id.t3);

        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);

        String[]items = new String[Common.leave.size()];
        for(int i = 0;i<Common.leave.size();i++){
            items[i] = Common.toLanguage(Common.leave.get(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LeaveSearchResult.this,R.layout.spinnerview,items);
        adapter.setDropDownViewResource(R.layout.spinnerview);
        t3.setAdapter(adapter);

        t1.setEnabled(false);
        t2.setEnabled(false);
        t3.setEnabled(false);
        t4.setEnabled(false);
        t5.setEnabled(false);
        t6.setEnabled(false);
        t7.setEnabled(false);
        t8.setEnabled(false);
        t9.setEnabled(false);

        t5.setOnClickListener(scanDateAndTime);
        t4.setOnClickListener(scanDateAndTime);

        t5.setFocusable(false);
        t4.setFocusable(false);

        b1.setOnClickListener(modify);
        b2.setOnClickListener(delete);

        didSetLanguage();
    }

    public  void  CountLeaveTime(){

        try{
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            SimpleDateFormat time = new SimpleDateFormat("HHmm");

            Date StartDate = f.parse(t4.getText().toString());

            Date EndDate = f.parse(t5.getText().toString());

            float total = 0;

            SimpleDateFormat ff = new SimpleDateFormat("EEEE");

            Calendar cal = Calendar.getInstance();

            cal.setTime(StartDate);
            Log.d("test",String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
            while(cal.getTime().compareTo(EndDate) != 1){

                Double StartTime,EndTime,Resttime1,Resttime2;

                String week = "";

                switch(cal.get(Calendar.DAY_OF_WEEK)){
                    case 1:
                        week = "sun";
                        break;
                    case 2:
                        week = "mon";
                        break;
                    case 3:
                        week = "tues";
                        break;
                    case 4:
                        week = "wed";
                        break;
                    case 5:
                        week = "thurs";
                        break;
                    case 6:
                        week = "fri";
                        break;
                    case 7:
                        week = "sat";
                        break;
                    default:
                        break;
                }
                
                if(cal.get(Calendar.DATE) == StartDate.getDate() && cal.get(Calendar.MONTH) == StartDate.getMonth()){
                    StartTime = Double.parseDouble(time.format(StartDate));
                }else{
                    StartTime = Double.parseDouble(ClassTime.getString(week + "ontime"));
                }

                if(cal.get(Calendar.DATE) == EndDate.getDate() && cal.get(Calendar.MONTH) == EndDate.getMonth()){
                    EndTime = Double.parseDouble(time.format(EndDate));
                }else{
                    EndTime = Double.parseDouble(ClassTime.getString(week + "offtime"));
                }

                Resttime1 = Double.parseDouble(ClassTime.getString(week + "resttime1"));
                Resttime2 = Double.parseDouble(ClassTime.getString(week + "resttime2"));

                StartTime = (int)(StartTime / 100) + StartTime % 100 / 60;
                EndTime = (int)(EndTime / 100) + EndTime % 100 / 60;
                Resttime1 = (int)(Resttime1 / 100) + Resttime1 % 100 / 60;
                Resttime2 = (int)(Resttime2 / 100) + Resttime2 % 100 / 60;

                if(StartTime <= Resttime1 && EndTime >= Resttime2){
                    total += EndTime - StartTime;
                    total -= Resttime2 - Resttime1;
                }else if(StartTime < Resttime1 && EndTime < Resttime1){
                    total += EndTime - StartTime;
                }else if(StartTime > Resttime2 && EndTime > Resttime2){
                    total += EndTime - StartTime;
                }else if(StartTime > Resttime1 && EndTime > Resttime2){
                    total += EndTime - Resttime2 ;
                }else if(StartTime <= Resttime1 && EndTime <= Resttime2){
                    total += Resttime1 - StartTime;
                }
                cal.add(Calendar.DATE,1);

            }

            t9.setText(String.format("%.2f",total));

        }catch(ParseException e){
            Log.d("error",ClassTime.toString());
            e.printStackTrace();

        }catch (JSONException e){
            Log.d("error",ClassTime.toString());
            e.printStackTrace();
        }

    }

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        LinearLayout l = (LinearLayout) findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
        l.setBackgroundResource(R.drawable.page);


        l5.setVisibility(View.INVISIBLE);

        l1.setText(Common.toLanguage("員工編號") + ": ");
        l2.setText(Common.toLanguage("員工姓名") + ": ");
        l3.setText(Common.toLanguage("請假類別") + ": ");
        l4.setText(Common.toLanguage("請假時間") + ": ");
        l5.setText(Common.toLanguage("請假時間") + ": ");
        l9.setText(Common.toLanguage("請假時數") + ": ");
        l6.setText(Common.toLanguage("請假事由") + ": ");
        l7.setText(Common.toLanguage("申請時間") + ": ");
        l8.setText(Common.toLanguage("申請狀態") + ": ");

        b1.setText(Common.toLanguage("修改"));
        b2.setText(Common.toLanguage("刪除"));

        bid = getIntent().getExtras().get("bid").toString();
        getLeave();
    }

    View.OnClickListener scanDateAndTime = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView v = (TextView) view;
            //String date = v.getText().toString().replace("/","");
            String[] ss = v.getText().toString().replace("/","").replace(":","").split(" ");
            String date = ss[0];
            final int hour = Integer.parseInt(ss[1].substring(0,2));
            final int min = Integer.parseInt(ss[1].substring(2,4));
            final Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(Common.DateToUSD(date).substring(0,4)), Integer.parseInt(Common.DateToUSD(date).substring(4,6)), Integer.parseInt(Common.DateToUSD(date).substring(6,8)));

            myYear = c.get(Calendar.YEAR);
            myMonth = c.get(Calendar.MONTH) - 1;
            myDay = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(LeaveSearchResult.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                    final String date2 = i + String.format("%02d",i1 + 1) + String.format("%02d",i2);
                    v.setText(Common.DateToTWD(date2) + " "+ String.format("%02d",hour) + ":" + String.format("%02d",min));

                    new TimePickerDialog(LeaveSearchResult.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            v.setText(Common.DateToTWD(date2) + " " + String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute));
                            CountLeaveTime();

                        }
                    },hour,min,true).show();
                }

            }, myYear, myMonth, myDay).show();
        }
    };

    void setLeaveData(){

        if(dbSearch.dateArray.length() > 0){
            try{
                JSONObject object = dbSearch.dateArray.getJSONObject(0);

                t1.setText(object.get("employeid").toString());
                t2.setText(object.get("employename").toString());
                t3.setSelection(Common.leave.indexOf(object.get("absencename").toString()));
                t4.setText(dateToText(object.get("udate").toString()) + " " + timeToText(object.get("uontime").toString()));
                t5.setText(dateToText(object.get("yndate").toString()) + " " + timeToText(object.get("ynofftime").toString()));
                t6.setText(object.get("reason").toString());
                t9.setText(object.get("qjcount").toString());
                t7.setText(dateToText(object.get("qjdate").toString()));
                isCheck = true;
                if(object.get("result").toString().equals("0")){
                    isCheck = false;
                    t8.setText(Common.toLanguage("審核中"));
                }else if(object.get("result").toString().equals("1")){
                    t8.setText(Common.toLanguage("審核失敗"));
                }else if(object.get("result").toString().equals("2")){
                    t8.setText(Common.toLanguage("審核通過"));
                }

            }catch(JSONException e){
                Log.d("error",e.getMessage());
                return ;
            }

        }else{
            return ;
        }

    }

    void getLeave(){
        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        dbSearch.PutParameter("bid",bid);

        sb.append("select qjcount,qjno,qjdate,qjdate2,year1,employeid,employename,absenceno,result,absencename,udate,yndate,uontime,ynofftime,qjmemo,udate2,yndate2,qjmemo as reason from qingjia where qjno=@bid");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectOK) {
                            setLeaveData();
                        }

                        dbSearch = new DBSearch();
                        dbSearch.PutParameter("employeid",t1.getText().toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(dbSearch.JPSearchForGet("select monontime,monofftime,monresttime1,monresttime2,tuesontime,tuesofftime,tuesresttime1,tuesresttime2,wedontime,wedofftime,wedresttime1,wedresttime2,thursontime,thursofftime,thursresttime1,thursresttime2,friontime,friofftime,friresttime1,friresttime2,satontime,satofftime,satresttime1,satresttime2,sunontime,sunofftime,sunresttime1,sunresttime2 from attendance left join employee on employee.presenttype=attendance.attendanceno where employee.employeid=@employeid") == DBSearch.Result.fund){
                                    myHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(dbSearch.dateArray.length() > 0){
                                                try{
                                                    ClassTime = dbSearch.dateArray.getJSONObject(0);

                                                }catch(JSONException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });

            }
        }).start();
    }

    void toDelete(){
        sb = new StringBuilder();
        dbSearch = new DBSearch();

        dbSearch.PutParameter("bid",bid);
        dbSearch.PutParameter("employeid",t1.getText().toString().trim());
        sb.append("delete qingjia where qjno=@bid");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPModifyData(sb.toString()) == DBSearch.Result.modified ) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectOK) {

                            ToastError(Common.toLanguage("刪除成功"));
                            Intent i = new Intent();
                            setResult(RESULT_CANCELED,i);
                            finish();

                        }else{
                            ToastError(Common.toLanguage("刪除失敗"));
                        }
                    }
                });

            }
        }).start();
    }

    View.OnClickListener delete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isCheck){
                ToastError(Common.toLanguage("申請已審核"));
                return;
            }
            new AlertDialog.Builder(LeaveSearchResult.this)
                    .setIcon(R.drawable.error)
                    .setTitle("")
                    .setMessage(Common.toLanguage("確定要刪除"))
                    .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toDelete();
                        }
                    }).setNegativeButton(Common.toLanguage("取消"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    };

    View.OnClickListener modify = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            didClickModify();
        }
    };

    void didClickModify(){
        if(isCheck){
            ToastError(Common.toLanguage("申請已審核"));
            return;
        }
        Log.d("message","didClickModify");
        Log.d("data", String.valueOf(isModify));
        if(isModify){
            isModify = false;
            b2.setEnabled(true);
            t3.setEnabled(false);
            t4.setEnabled(false);
            t5.setEnabled(false);
            t6.setEnabled(false);
            t9.setEnabled(false);
            b1.setText(Common.toLanguage("修改"));
            saveModify();
        }else{
            isModify = true;
            t3.setEnabled(true);
            t4.setEnabled(true);
            t5.setEnabled(true);
            t6.setEnabled(true);
            t9.setEnabled(true);
            b2.setEnabled(false);
            b1.setText(Common.toLanguage("完成"));
        }
    }

    void saveModify(){

        Log.d("message","saveModify");
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try{
            if(f.parse(t4.getText().toString()).after(f.parse(t5.getText().toString()))){
                ToastError(Common.toLanguage("起始日期不可大於終止日期"));
                didClickModify();
                return;
            }
        }catch(Exception e){
            Log.d("error",e.getMessage());
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        dbSearch.PutParameter("bid",bid);

        String date1 = t4.getText().toString().split(" ")[0].replace("/","");
        String time1 = t4.getText().toString().split(" ")[1].replace(":","");
        String date2 = t5.getText().toString().split(" ")[0].replace("/","");
        String time2 = t5.getText().toString().split(" ")[1].replace(":","");

        dbSearch.PutParameter("bid",bid);
        dbSearch.PutParameter("date1",date1);
        dbSearch.PutParameter("dates1",Common.DateToTWD(date1));
        dbSearch.PutParameter("dates2",Common.DateToTWD(date2));
        dbSearch.PutParameter("date2",date2);
        dbSearch.PutParameter("time1",time1);
        dbSearch.PutParameter("time2",time2);
        dbSearch.PutParameter("qjcount",t9.getText().toString());
        dbSearch.PutParameter("typeno",Common.leaveno.get(t3.getSelectedItemPosition()));
        dbSearch.PutParameter("typename",Common.leave.get(t3.getSelectedItemPosition()));
        dbSearch.PutParameter("reason",t6.getText().toString());

        f = new SimpleDateFormat("yyyyMMdd HHmm");
        dbSearch.PutParameter("date",f.format(new Date()));
        dbSearch.PutParameter("id",Common.jpuser_id);

        sb.append("update qingjia set qjcount=@qjcount,absenceno=@typeno,absencename=@typename,udate=@dates1,yndate=@dates2,udate2=@date1,yndate2=@date2,uontime=@time1,ynofftime=@time2,qjmemo=@reason where qjno=@bid");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPModifyData(sb.toString()) == DBSearch.Result.modified ) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectOK) {

                            ToastError(Common.toLanguage("儲存成功"));

                        }else{
                            ToastError(Common.toLanguage("儲存失敗"));
                        }
                    }
                });

            }
        }).start();
    }
    //刪除審核通過的假單時，計算特休(因為目前不開放刪除通過的，暫時用不到)
    void calcuLeave(){
        String cmd = "";
        String standardhour = "0";
        String bnyxday = "";
        cmd = "Select standardhour from employee where employeid = @employeid";
        if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
            JSONObject object;
            try {
                for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                    object = dbSearch.dateArray.getJSONObject(i);
                    standardhour = object.get("standardhour").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(t8.getText().toString().trim().equals("審核通過") && t3.getSelectedItem().toString().trim().equals("特休")
                && Double.parseDouble(standardhour) > 0) {
            leaveYear = t4.getText().toString().trim().substring(0, 3);
            leaveYear2 = String.valueOf((Integer.parseInt(t4.getText().toString().trim().substring(0, 3)) + 1));
            if((leaveYear + Common.NianduBegin).compareTo(t4.getText().toString().trim().substring(0, 7)) > 0
                && Common.leaveSet.equals("3") && Common.NianduBegin.length() == 4){
                leaveYear = String.valueOf(Integer.parseInt(leaveYear) - 1);
            }
            cmd = "Select bnyxday from leave where employeid = @employeid and leaveYear = @leaveYear";
            if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
                JSONObject object;
                try {
                    for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                        object = dbSearch.dateArray.getJSONObject(i);
                        bnyxday = object.get("bnyxday").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cmd = "Select ljtxday from leave where employeid = @employeid and leaveYear = @leaveYear";
            if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
                ljtxday = loadljtxday(dbSearch);
            }
            cmd = "Select ljtxday from leave where employeid = @employeid and leaveYear = @leaveYear2";
            if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
                ljtxday2 = loadljtxday(dbSearch);
            }
            if (bnyxday.length() > 0) {
                BigDecimal standHours = new BigDecimal(standardhour);
                BigDecimal bnDay1 = new BigDecimal(bnyxday.substring(0, bnyxday.indexOf("天")));//抓已休特休天數
                BigDecimal bnHours1 = bnDay1.multiply(standHours);//天數換算小時
                BigDecimal bnHours2 = new BigDecimal(bnyxday.substring(bnyxday.indexOf("天") + 1, bnyxday.indexOf("小")));//抓已休特休小時數
                BigDecimal qj = bnHours1.add(bnHours2);//已休特休總時數
                BigDecimal Qjcount = new BigDecimal(t9.getText().toString().trim());
                qj = qj.subtract(Qjcount);//已休時數扣掉目前假單時數

                BigDecimal tDay1 = new BigDecimal(ljtxday.substring(0, ljtxday.indexOf("天")));//抓特休天數
                BigDecimal tHours1 = tDay1.multiply(standHours);//天數換算小時
                BigDecimal tHours2 = new BigDecimal(ljtxday.substring(ljtxday.indexOf("天") + 1, ljtxday.indexOf("小")));//抓特休小時數
                BigDecimal tljtxday = tHours1.add(tHours2);//總特休時數
                tljtxday = tljtxday.subtract(qj);//總特休時數扣掉已休時數

                String sol = String.format("%01d", (int) Math.floor(qj.doubleValue() / standHours.doubleValue())) + "天" + String.format("%.1f", qj.doubleValue() % standHours.doubleValue()) + "小時";
                String jsol = String.format("%01d", (int) Math.floor(tljtxday.doubleValue() / standHours.doubleValue())) + "天" + String.format("%.1f", tljtxday.doubleValue() % standHours.doubleValue()) + "小時";
                dbSearch.PutParameter("bnyxday", sol.trim());//本年度已休天數
                dbSearch.PutParameter("bnsyday", jsol.trim());//本年度剩餘特休天數
                cmd = "Update leave set bnyxday=@bnyxday,bnsyday=@bnsyday where employeid=@employeid and leaveYear=@leaveYear";
                if (dbSearch.JPModifyData(cmd) == DBSearch.Result.modified) {
                    //如果有下年度的特休表則一起更新
                    if (!"".equals(ljtxday2)) {
                        cmd = "Update leave set bnyxday=@bnyxday,bnsyday=@bnsyday where employeid=@employeid and leaveYear=@leaveYear2";
                        if (dbSearch.JPModifyData(cmd) == DBSearch.Result.modified) {
                            Log.d("check","OK");
                        }
                    }

                }
            }
        }
    }

    String loadljtxday(DBSearch dbSearch) {
        JSONObject object;
        String tempLjtxday = "";
        try {
            for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                object = dbSearch.dateArray.getJSONObject(i);
                tempLjtxday = object.get("ljtxday").toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return tempLjtxday;
    }
}
