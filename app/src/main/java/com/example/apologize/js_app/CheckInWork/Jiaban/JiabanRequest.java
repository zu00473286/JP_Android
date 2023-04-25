package com.example.apologize.js_app.CheckInWork.Jiaban;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.CheckInWork.Leave.LeaveSearchCondition;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 2017/11/13.
 */

public class JiabanRequest extends BaseActivity {

    Handler myHandler = new Handler();
    boolean connectOK;
    ProgressDialog dialog;
    DBSearch dbSearch;
    StringBuilder sb;
    int myYear,myMonth,myDay;
    TextView time1,time2,l1,l2,l3,l4,l6,t4,l7;
    EditText date,memo,minutes;
    Spinner cJbtype;
    Button b1,b2;
    JSONObject ClassTime;
    String week = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiaban_request);
        findView();
    }

    void findView(){

        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l3 = (TextView)findViewById(R.id.l3);
        l4 = (TextView)findViewById(R.id.l4);
        l6 = (TextView)findViewById(R.id.l6);
        t4 = (TextView) findViewById(R.id.t4);
        l7 = (TextView) findViewById(R.id.l7);

        date = (EditText) findViewById(R.id.date);
        time1 = (TextView) findViewById(R.id.time1);
        minutes = (EditText) findViewById(R.id.minutes);
        time2 = (TextView) findViewById(R.id.time2);
        memo = (EditText) findViewById(R.id.memo);


        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);



        cJbtype = (Spinner)findViewById(R.id.cJbtype);
        String[]comboitems = new String[]{Common.toLanguage("平日"),Common.toLanguage("假日")};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JiabanRequest.this,R.layout.spinnerview,comboitems);
        adapter.setDropDownViewResource(R.layout.spinnerview);
        cJbtype.setAdapter(adapter);





        minutes.setText("0");

        String[]items = new String[Common.leave.size()];
        for(int i = 0;i<Common.leave.size();i++){
            items[i] = Common.toLanguage(Common.leave.get(i));
        }



        time1.setOnClickListener(scanDate);
        time2.setOnClickListener(scanDate);

        findViewById(R.id.sview).setOnTouchListener(endedit);

        didSetLanguage();
    }

    void didSetLanguage(){
        ImageView title = (ImageView)findViewById(R.id.title);
        LinearLayout l = (LinearLayout)findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);


        l1.setText(Common.toLanguage("加班時間") + ":");
        l2.setText(Common.toLanguage("加班時間") + ":");
        l3.setText(Common.toLanguage("加班時間") + ":");
        l4.setText(Common.toLanguage("員工編號") + ":");
        l6.setText(Common.toLanguage("加班事由") + ":");
        l7.setText(Common.toLanguage("加班時數") + ":");

        b1.setText(Common.toLanguage("儲存"));
        b2.setText(Common.toLanguage("取消"));

        t4.setText(Common.jpuser_empid);

        SimpleDateFormat f = new SimpleDateFormat("HHmm");
        SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
        date.setText(Common.DateToTWD(f1.format(new Date())));
        time1.setText(f.format(new Date()));
        time2.setText(f.format(new Date()));
        t4.setText(Common.jpuser_empid);
        memo.setText("");

        dbSearch = new DBSearch();
        dbSearch.PutParameter("employeid",Common.jpuser_empid);

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

    View.OnClickListener scanDate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView v = (TextView) view;
            String time = v.getText().toString().replace("/","").replace(":","");//.split(" ");
            //String date = ss[0];
            final int hour = Integer.parseInt(time.substring(0,2));
            final int min = Integer.parseInt(time.substring(2,4));
//            final Calendar c = Calendar.getInstance();
//            c.set(Integer.parseInt(Common.DateToUSD(time).substring(0,4)), Integer.parseInt(Common.DateToUSD(time).substring(4,6)), Integer.parseInt(Common.DateToUSD(time).substring(6,8)));
//
//            myYear = c.get(Calendar.YEAR);
//            myMonth = c.get(Calendar.MONTH) - 1;
//            myDay = c.get(Calendar.DAY_OF_MONTH);
            new TimePickerDialog(JiabanRequest.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    v.setText(String.format("%02d",hourOfDay)  + String.format("%02d",minute));
                    CountJiabanTime();

                }
            },hour,min,true).show();
        }
    };

    public  void  CountJiabanTime(){

        try{
            //SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            SimpleDateFormat time = new SimpleDateFormat("HHmm");

            Date StartDate = time.parse(time1.getText().toString());

            Date EndDate = time.parse(time2.getText().toString());

            float total = 0;

            //SimpleDateFormat ff = new SimpleDateFormat("EEEE");

            Calendar cal = Calendar.getInstance();

            cal.setTime(StartDate);
            Log.d("test",String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
            while(cal.getTime().compareTo(EndDate) != 1){

                Double StartTime,EndTime,Resttime1,Resttime2;



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

                    StartTime = (int) (StartTime / 100) + StartTime % 100 / 60;
                    EndTime = (int) (EndTime / 100) + EndTime % 100 / 60;

                    Resttime1 = (int) (Resttime1 / 100) + Resttime1 % 100 / 60;
                    Resttime2 = (int) (Resttime2 / 100) + Resttime2 % 100 / 60;

                    if (StartTime <= Resttime1 && EndTime >= Resttime2) {
                        total += EndTime - StartTime;
                        total -= Resttime2 - Resttime1;
                    } else if (StartTime < Resttime1 && EndTime < Resttime1) {
                        total += EndTime - StartTime;
                    } else if (StartTime > Resttime2 && EndTime > Resttime2) {
                        total += EndTime - StartTime;
                    } else if (StartTime > Resttime1 && EndTime > Resttime2) {
                        total += EndTime - Resttime2;
                    } else if (StartTime <= Resttime1 && EndTime <= Resttime2) {
                        total += Resttime1 - StartTime;
                    }

                cal.add(Calendar.DATE,1);

            }

            minutes.setText(String.format("%.0f",total * 60));

        }catch(ParseException e){
            Log.d("error",ClassTime.toString());
            e.printStackTrace();

        }catch (JSONException e){
            Log.d("error",ClassTime.toString());
            e.printStackTrace();
        }

    }

    public  void  onSave(View v){
        dbSearch = new DBSearch();
        dbSearch.PutParameter("employeid",Common.jpuser_empid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbSearch.JPSearchForGet("select employename from employee where employeid=@employeid");
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(dbSearch.dateArray.length() > 0){
                            try{
                                JSONObject o = dbSearch.dateArray.getJSONObject(0);
                                Common.jpuser_empname = o.getString("employename");
                                toSave();
                            }catch(JSONException e){
                                new AlertDialog.Builder(JiabanRequest.this)
                                        .setIcon(R.drawable.error)
                                        .setTitle("")
                                        .setMessage(Common.toLanguage("查無資料"))
                                        .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void toSave(){

        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try{
            if(f.parse(time1.getText().toString()).after(f.parse(time2.getText().toString()))){
                ToastError(Common.toLanguage("起始日期不可大於終止日期"));
                return;
            }
        }catch(Exception e){
            Log.d("error",e.getMessage());
        }


        dbSearch = new DBSearch();

        dbSearch.PutParameter("employeid",Common.jpuser_empid);
        dbSearch.PutParameter("employename",Common.jpuser_empname);
        f = new SimpleDateFormat("yyyyMMdd HHmmss");
        dbSearch.PutParameter("JbDate",date.getText().toString());
        dbSearch.PutParameter("JbDate2",Common.DateToUSD(date.getText().toString()));
        dbSearch.PutParameter("Jbtype",cJbtype.getSelectedItemPosition() == 0 ? "平日" :"假日");
        dbSearch.PutParameter("addtime1",time1.getText().toString());
        dbSearch.PutParameter("addtime2",time2.getText().toString());
        dbSearch.PutParameter("JbCount",Double.parseDouble(minutes.getText().toString()));
        dbSearch.PutParameter("Memo",memo.getText().toString());

        Log.d("parameter",dbSearch.returnParameter().toString());

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this,"","");

        new Thread(new Runnable() {
            @Override
            public void run() {

                //AppendQingjia預存程序在資料庫已建立
                connectOK = false;
                if (dbSearch.JPInsertData("Insert into Jiaban ([employeid],[employename],[JbDate],[JbDate2],[Jbtype],[addtime1],[addtime2],[JbCount],[Memo],[result]) VALUES (@employeid,@employename,@JbDate,@JbDate2,@Jbtype,@addtime1,@addtime2,@JbCount,@Memo,'0')") == DBSearch.Result.fund) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ToastError("儲存成功");
                        SimpleDateFormat f = new SimpleDateFormat("HHmm");
                        SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
                        date.setText(Common.DateToTWD(f1.format(new Date())));
                        time1.setText(f.format(new Date()));
                        time2.setText(f.format(new Date()));
                        t4.setText(Common.jpuser_empid);
                        memo.setText("");
                    }
                });

            }
        }).start();


    }

    public void onClear(View v){

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        time1.setText(Common.DateToTWD(f.format(new Date())));
        time2.setText(Common.DateToTWD(f.format(new Date())));
        t4.setText(Common.jpuser_empid);
        memo.setText("");
    }

}
