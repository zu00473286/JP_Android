package com.example.apologize.js_app.CheckInWork.Jiaban;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

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

public class JiabanSearchResult extends BaseActivity {

    boolean isCheck = true;
    int myYear,myMonth,myDay;
    boolean isModify = false;
    StringBuilder sb;
    boolean connectOK;
    DBSearch dbSearch;
    Handler myHandler = new Handler();
    String Jid = "";
    String leaveYear = "";
    String leaveYear2 = "";
    String ljtxday = "";
    String ljtxday2 = "";
    TextView l1,l2,l3,l4,l5,l6,l7,l8,l9;
    Spinner cJbtype;
    EditText tEmployeid,tEmployename,tJbDate,tAddtime1,tJbCount,tMemo,tAddtime2;
    TextView tResult;
    Button b1,b2;
    JSONObject ClassTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiaban_search_result);
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

        tEmployeid = (EditText)findViewById(R.id.tEmployeid);
        tEmployename = (EditText)findViewById(R.id.tEmployename);
        tJbDate = (EditText)findViewById(R.id.tJbDate);
        tAddtime1 = (EditText)findViewById(R.id.tAddtime1);
        tJbCount = (EditText)findViewById(R.id.tJbCount);
        tMemo = (EditText)findViewById(R.id.tMemo);
        tResult = (TextView) findViewById(R.id.tResult);
        tAddtime2 = (EditText)findViewById(R.id.tAddtime2);

        cJbtype = (Spinner)findViewById(R.id.cJbtype);

        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);

        cJbtype = (Spinner)findViewById(R.id.cJbtype);
        String[]comboitems = new String[]{Common.toLanguage("平日"),Common.toLanguage("假日")};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JiabanSearchResult.this,R.layout.spinnerview,comboitems);
        adapter.setDropDownViewResource(R.layout.spinnerview);
        cJbtype.setAdapter(adapter);

        tEmployeid.setEnabled(false);
        tEmployename.setEnabled(false);
        cJbtype.setEnabled(false);
        tJbDate.setEnabled(false);
        tAddtime1.setEnabled(false);
        tAddtime2.setEnabled(false);
        tJbCount.setEnabled(false);
        tMemo.setEnabled(false);
        tResult.setEnabled(false);

        tAddtime1.setOnClickListener(scanDateAndTime);
        tAddtime2.setOnClickListener(scanDateAndTime);

        tAddtime1.setFocusable(false);
        tAddtime2.setFocusable(false);

        b1.setOnClickListener(modify);
        b2.setOnClickListener(delete);

        didSetLanguage();
    }

    public  void  CountJiabanTime(){

        try{
            //SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            SimpleDateFormat time = new SimpleDateFormat("HHmm");

            Date StartDate = time.parse(tAddtime1.getText().toString());

            Date EndDate = time.parse(tAddtime2.getText().toString());

            float total = 0;

            //SimpleDateFormat ff = new SimpleDateFormat("EEEE");

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

            tJbCount.setText(String.format("%.0f",total * 60));

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
        l3.setText(Common.toLanguage("加班類別") + ": ");
        l4.setText(Common.toLanguage("加班日期") + ": ");
        l5.setText(Common.toLanguage("時間起") + ": ");
        l9.setText(Common.toLanguage("時間迄") + ": ");
        l6.setText(Common.toLanguage("加班分鐘") + ": ");
        l7.setText(Common.toLanguage("加班事由") + ": ");
        l8.setText(Common.toLanguage("申請狀態") + ": ");

        b1.setText(Common.toLanguage("修改"));
        b2.setText(Common.toLanguage("刪除"));

        Jid = getIntent().getExtras().get("Jid").toString();
        getJiaban();
    }

    View.OnClickListener scanDateAndTime = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView v = (TextView) view;
            String time = v.getText().toString().replace("/","").replace(":","");//.split(" ");

            final int hour = Integer.parseInt(time.substring(0,2));
            final int min = Integer.parseInt(time.substring(2,4));
            new TimePickerDialog(JiabanSearchResult.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    v.setText(String.format("%02d",hourOfDay)  + String.format("%02d",minute));
                    CountJiabanTime();

                }
            },hour,min,true).show();
        }
    };

    void setJiabanData(){

        if(dbSearch.dateArray.length() > 0){
            try{
                JSONObject object = dbSearch.dateArray.getJSONObject(0);
                tEmployeid.setText(object.get("Employeid").toString());
                tEmployename.setText(object.get("Employename").toString());
                cJbtype.setSelection(object.get("Jbtype").toString().contains("假") ? 1 : 0);
                tJbDate.setText(object.get("JbDate").toString());
                tAddtime1.setText(object.get("addtime1").toString());
                tJbCount.setText(object.get("JbCount").toString());
                tAddtime2.setText(object.get("addtime2").toString());
                tMemo.setText(dateToText(object.get("Memo").toString()));
                isCheck = true;
                if(object.get("result").toString().equals("0")){
                    isCheck = false;
                    tResult.setText(Common.toLanguage("審核中"));
                }else if(object.get("result").toString().equals("1")){
                    tResult.setText(Common.toLanguage("審核失敗"));
                }else if(object.get("result").toString().equals("2")){
                    tResult.setText(Common.toLanguage("審核通過"));
                }

            }catch(JSONException e){
                Log.d("error",e.getMessage());
                return ;
            }

        }else{
            return ;
        }

    }

    void getJiaban(){
        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        dbSearch.PutParameter("Jid",Jid);
        Log.d("Jid",Jid);
        sb.append("select * from Jiaban where Jid=@Jid");

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
                            setJiabanData();
                        }

                        dbSearch = new DBSearch();
                        dbSearch.PutParameter("employeid",tEmployeid.getText().toString());

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

        dbSearch.PutParameter("Jid",Jid);
        dbSearch.PutParameter("employeid",tEmployeid.getText().toString().trim());
        sb.append("delete Jiaban where Jid=@Jid");

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
            new AlertDialog.Builder(JiabanSearchResult.this)
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
            cJbtype.setEnabled(false);
            tJbDate.setEnabled(false);
            tAddtime1.setEnabled(false);
            tJbCount.setEnabled(false);
            tAddtime2.setEnabled(false);
            tMemo.setEnabled(false);
            b1.setText(Common.toLanguage("修改"));
            saveModify();
        }else{
            isModify = true;
            cJbtype.setEnabled(true);
            tJbDate.setEnabled(true);
            tAddtime1.setEnabled(true);
            tJbCount.setEnabled(true);
            tAddtime2.setEnabled(true);
            tMemo.setEnabled(true);
            b2.setEnabled(false);
            b1.setText(Common.toLanguage("完成"));
        }
    }

    void saveModify(){

        Log.d("message","saveModify");
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try{
            if(f.parse(tJbDate.getText().toString()).after(f.parse(tAddtime1.getText().toString()))){
                ToastError(Common.toLanguage("起始日期不可大於終止日期"));
                didClickModify();
                return;
            }
        }catch(Exception e){
            Log.d("error",e.getMessage());
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        dbSearch.PutParameter("Jid",Jid);
        dbSearch.PutParameter("JbDate",tJbDate.getText().toString());
        dbSearch.PutParameter("JbDate2",Common.DateToUSD(tJbDate.getText().toString()));
        dbSearch.PutParameter("Jbtype",cJbtype.getSelectedItemPosition() == 0 ? "平日" :"假日");
        dbSearch.PutParameter("addtime1",tAddtime1.getText().toString());
        dbSearch.PutParameter("addtime2",tAddtime2.getText().toString());
        dbSearch.PutParameter("JbCount",Double.parseDouble(tJbCount.getText().toString()));
        dbSearch.PutParameter("Memo",tMemo.getText().toString());


        sb.append("update Jiaban set JbCount=@JbCount,Jbtype=@Jbtype,JbDate=@JbDate,JbDate2=@JbDate2,addtime1=@addtime1,addtime2=@addtime2,Memo=@Memo where Jid=@Jid");

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


}
