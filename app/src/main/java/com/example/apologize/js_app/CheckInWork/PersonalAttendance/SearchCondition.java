package com.example.apologize.js_app.CheckInWork.PersonalAttendance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apologize.js_app.APP_JPMenu;
import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.CheckInWork.Attendance.EmployeAttendanceResult;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by leo on 2017/9/15.
 */

public class SearchCondition extends BaseActivity {

    ArrayList<String> ontime,offtime,resttime1,resttime2,addontime,addofftime,date1,employeid,employename;
    boolean connectOK;
    EditText date,date2;
    TextView cuno,cuname;
    String sqlcmd,sqlcmd2,sqlcmd3;
    int myYear,myMonth,myDay;
    StringBuilder sb;
    Handler myHandler = new Handler();
    ProgressDialog dialog;
    DBSearch dbSearch;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_check_condition);
        findViews();
    }

    public void findViews(){
        cuno = (TextView) findViewById(R.id.cuno);
        cuname = (TextView)findViewById(R.id.cuname);

        date2 = (EditText)findViewById(R.id.date2);
        date = (EditText)findViewById(R.id.date1);

        if (Common.user_date == 1) {
            date.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
            date2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
        }else{
            date.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
            date2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        }

        date.setText(GetMonthFirstDay(Common.user_date));
        date2.setText(GetDate(Common.user_date));

        didSetLanguage();
    }

    public void didSetLanguage(){

        ImageView title = (ImageView)findViewById(R.id.title);
        LinearLayout l = (LinearLayout)findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);

        TextView id = (TextView)findViewById(R.id.no);
        id.setText(Common.toLanguage("員工編號") + ":");

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(Common.toLanguage("員工姓名") + ":");

        TextView date1 = (TextView)findViewById(R.id.l3);
        date1.setText(Common.toLanguage("查詢日期") + ":");
        TextView date3 = (TextView)findViewById(R.id.date3);
        date3.setText(Common.toLanguage("查詢日期") + ":");
        TextView date4 = (TextView)findViewById(R.id.date4);
        date4.setText(Common.toLanguage("查詢日期") + ":");

        date.setHint(Common.toLanguage("點擊選取"));
        date2.setHint(Common.toLanguage("點擊選取"));

        Button s = (Button)findViewById(R.id.btnSearch);
        s.setText(Common.toLanguage("搜尋"));

        Button c = (Button)findViewById(R.id.btnClear);
        c.setText(Common.toLanguage("清除"));

        getData();
    }

    void getData(){
        dbSearch = new DBSearch();
        dbSearch.PutParameter("id",Common.jpuser_id);
        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPSearchForGet("select employename from employee where webid=@id") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(connectOK){

                            cuno.setText(Common.jpuser_empid);
                            try{
                                for(int i = 0;i<dbSearch.dateArray.length();i+=1){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(i);
                                    cuname.setText(object.get("employename").toString());
                                }
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                        }else{
                            new AlertDialog.Builder(SearchCondition.this)
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
                });

            }
        }).start();
    }

        public void onClear(View v) {

            date.setText(GetMonthFirstDay(Common.user_date));
            date2.setText(GetDate(Common.user_date));

        }


        public void onSearch(View v) {

            if (!NetWork.CheckNetWorkState(this)) {
                ToastError(Common.toLanguage("無法連線"));
                return;
            }
            connectCancell = false;
            sb = new StringBuilder();
            dbSearch = new DBSearch();

            sb.append("select E.employename,E.employeid,E.nddate as date,E.ndweektype as week,E.employename,E.ontime,E.offtime,E.restime1 as resttime1,E.restime2 as resttime2,E.addontime,E.addofftime from employeeattend as E" +
                    " left join employee on E.employeid = employee.employeid where employee.employeid=@id and E.nddate2 between @date1 and @date2");

            if(Common.user_date == 1){
                dbSearch.PutParameter("date1", Common.DateToUSD(date.getText().toString()));
                dbSearch.PutParameter("date2", Common.DateToUSD(date2.getText().toString()));
            }else{
                dbSearch.PutParameter("date1", date.getText().toString());
                dbSearch.PutParameter("date2", date2.getText().toString());
            }

            dbSearch.PutParameter("id", cuno.getText().toString());

            Log.d("data",dbSearch.returnParameter().toString());

            dialog = new ProgressDialog(this);
            dialog = ProgressDialog.show(this, "","");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    connectOK = false;
                    if (dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund &&  getResult()) {
                        connectOK = true;
                    }

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (connectOK) {

                                Intent i = new Intent(SearchCondition.this, EmployeAttendanceResult.class);
                                Bundle b = new Bundle();
                                b.putStringArrayList("ontime",ontime);
                                b.putStringArrayList("offtime",offtime);
                                b.putStringArrayList("addontime",addontime);
                                b.putStringArrayList("addofftime",addofftime);
                                b.putStringArrayList("resttime1",resttime1);
                                b.putStringArrayList("resttime2",resttime2);
                                b.putStringArrayList("date",date1);
                                b.putStringArrayList("employeid",employeid);
                                b.putStringArrayList("employename",employename);
                                i.putExtras(b);
                                startActivity(i);

                            } else {
                                new AlertDialog.Builder(SearchCondition.this)
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
                    });

                }
            }).start();

        }

        boolean getResult(){

            ontime = new ArrayList<>();
            offtime = new ArrayList<>();
            resttime1 = new ArrayList<>();
            resttime2 = new ArrayList<>();
            addontime = new ArrayList<>();
            addofftime = new ArrayList<>();
            date1 = new ArrayList<>();
            employeid = new ArrayList<>();
            employename = new ArrayList<>();

            try{

                if (dbSearch.dateArray.length() > 0){
                    for(int i = 0;i<dbSearch.dateArray.length();i++){
                        JSONObject o = dbSearch.dateArray.getJSONObject(i);
                        ontime.add(o.get("ontime").toString());
                        offtime.add(o.get("offtime").toString());
                        addontime.add(o.get("addontime").toString());
                        addofftime.add(o.get("addofftime").toString());
                        resttime1.add(o.get("resttime1").toString());
                        resttime2.add(o.get("resttime2").toString());
                        employeid.add(o.get("employeid").toString());
                        employename.add(o.get("employename").toString());
                        date1.add(o.get("date").toString());
                    }
                }

            }catch(JSONException e){
                return false;
            }
            return true;
        }


    }

