package com.example.apologize.js_app.CheckInWork.Attendance;

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

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by leo on 2017/11/9.
 */

public class EmployeAttendanceCondition extends BaseActivity {

    StringBuilder sb;
    Handler myHandler = new Handler();
    boolean connectOK;
    DBSearch dbSearch;
    ProgressDialog dialog;
    ArrayList<String> ontime,offtime,addontime,addofftime,resttime1,resttime2,employeid,date,employename;
    ArrayList<String> selectEmploye,selectDepart;
    ArrayList<String> selectEmployeID,selectDepartID;
    String sqlcmd,sqlcmd2,sqlcmd3;
    int myYear,myMonth,myDay;
    TextView l1,l2,l3,l4;
    EditText t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employe_attendance_condition);
        findView();
    }

    void findView(){

        findViewById(R.id.sview).setOnTouchListener(endedit);
        selectDepart = new ArrayList<>();
        selectEmploye = new ArrayList<>();
        selectDepartID = new ArrayList<>();
        selectEmployeID = new ArrayList<>();

        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l3 = (TextView)findViewById(R.id.l3);
        l4 = (TextView)findViewById(R.id.l4);

        t1 = (EditText)findViewById(R.id.t1);
        t2 = (EditText)findViewById(R.id.t2);

        if(Common.user_date == 1){
            t1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
            t2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
        }else{
            t1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
            t2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        }

        l3.setOnClickListener(departPick);
        l4.setOnClickListener(employePick);

        didSetLanguage();
    }

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        LinearLayout l = (LinearLayout) findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
        l.setBackgroundResource(R.drawable.page);

        ((Button)findViewById(R.id.search)).setText(Common.toLanguage("搜尋"));
        ((Button)findViewById(R.id.clear)).setText(Common.toLanguage("清除"));

        l1.setText(Common.toLanguage("查詢時間") + ":");
        l2.setText(Common.toLanguage("查詢時間") + ":");
        ((TextView)findViewById(R.id.l10)).setText(Common.toLanguage("查詢時間") + ":");
        l3.setText(Common.toLanguage("點擊多選部門編號"));
        l4.setText(Common.toLanguage("點擊多選員工編號"));

        t1.setText(GetMonthFirstDay(Common.user_date));
        t2.setText(GetDate(Common.user_date));

    }

    View.OnClickListener employePick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(EmployeAttendanceCondition.this,EmployeAttendance_Search.class);
            Bundle b = new Bundle();
            sqlcmd = "select employeid,employename,0 as [select] from employee where 0=0";
            sqlcmd2 = sqlcmd + "and employeid like + '%' + @keyword + '%'";
            sqlcmd3 = "select employeid,employename,0 as [select] from employee where employeid=@keyword";
            b.putString("line","2");
            b.putString("type","employe");
            b.putString("sqlcmd",sqlcmd);
            b.putString("sqlcmdwithcondition",sqlcmd2);
            b.putString("keyword","");
            b.putStringArrayList("didSelect",selectEmploye);
            b.putStringArrayList("didSelectID",selectEmployeID);
            i.putExtras(b);
            startActivityForResult(i,1);

        }
    };

    View.OnClickListener departPick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(EmployeAttendanceCondition.this,EmployeAttendance_Search.class);
            Bundle b = new Bundle();
            sqlcmd = "select partno,partname,0 as [select] from part where 0=0";
            sqlcmd2 = sqlcmd + " and partno like + '%' + @keyword + '%'";
            sqlcmd3 = "select partno,partname,0 as [select] from part where partno=@keyword";
            b.putString("line","2");
            b.putString("type","depart");
            b.putString("sqlcmd",sqlcmd);
            b.putString("sqlcmdwithcondition",sqlcmd2);
            b.putStringArrayList("didSelect",selectDepart);
            b.putStringArrayList("didSelectID",selectDepartID);
            b.putString("keyword","");
            i.putExtras(b);
            startActivityForResult(i,2);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){

                selectEmploye = data.getExtras().getStringArrayList("didSelect");
                selectEmployeID = data.getExtras().getStringArrayList("didSelectID");
                setEmployeList();

            }else if(requestCode == 2){

                selectDepart = data.getExtras().getStringArrayList("didSelect");
                selectDepartID = data.getExtras().getStringArrayList("didSelectID");
                setDepartList();

            }
        }
    }

    void setEmployeList(){
        StringBuilder con = new StringBuilder();
        con.append(Common.toLanguage("點擊多選員工編號"));

        if(selectEmploye.size() > 0){
            con.append(":\n");
        }

        for(int i = 0;i<selectEmploye.size();i++){
            if(i > 0){
                con.append("," + selectEmploye.get(i));
            }else{
                con.append(selectEmploye.get(i));
            }
        }
        l4.setText(con.toString());

    }

    void setDepartList(){
        StringBuilder con = new StringBuilder();
        con.append(Common.toLanguage("點擊多選部門編號"));

        if(selectDepart.size() > 0){
            con.append(":\n");
        }

        for(int i = 0;i<selectDepart.size();i++){
            if(i > 0){
                con.append("," + selectDepart.get(i));
            }else{
                con.append(selectDepart.get(i));
            }
        }
        l3.setText(con.toString());

        if(selectDepartID.size() > 0){
            getEmploye();
        }else{
            selectEmploye = new ArrayList<>();
            selectEmployeID = new ArrayList<>();
            setEmployeList();

        }
    }

    void getEmploye(){
        if(!NetWork.CheckNetWorkState(this))return;

        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        dbSearch = new DBSearch();


        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<selectDepartID.size();i++){
            if(i > 0){
                sb.append("or partno='" + selectDepartID.get(i).toString() + "'");
            }else{
                sb.append("partno='" + selectDepartID.get(i).toString() + "'");
            }
        }
        final String cmd = "select employename,employeid from employee where 0=0 and (" + sb.toString() + ")";

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPpostCommand(cmd) == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(connectOK){

                            try{
                                selectEmploye = new ArrayList<>();
                                selectEmployeID = new ArrayList<>();
                                for(int i = 0;i<dbSearch.dateArray.length();i+=1){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(i);
                                    if(!selectEmployeID.contains(object.get("employeid"))){
                                        selectEmployeID.add(object.get("employeid").toString());
                                        selectEmploye.add(object.get("employename").toString());
                                    }
                                }
                                setEmployeList();
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                        }
                    }
                });

            }
        }).start();
    }

    public void onSearch(View v){

        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }
        connectCancell = false;
        sb = new StringBuilder();
        dbSearch = new DBSearch();

        sb.append("select employename,employeid,nddate as date,ndweektype as week,employename,ontime,offtime,restime1 as resttime1,restime2 as resttime2,addontime,addofftime from employeeattend where nddate2 between @date1 and @date2");


        dbSearch.PutParameter("date1", Common.DateToUSD(t1.getText().toString()));
        dbSearch.PutParameter("date2", Common.DateToUSD(t2.getText().toString()));

        StringBuilder sb2 = new StringBuilder();

        if(selectEmployeID.size() > 0){
            for(int i = 0;i<selectEmployeID.size();i++){
                if(sb2.length() == 0){
                    sb2.append(" and ( employeid='" + selectEmployeID.get(i).toString() + "'");
                }else{
                    sb2.append(" or employeid='" + selectEmployeID.get(i).toString() + "'");
                }
            }
        }

        if(sb2.length() > 0){
            sb2.append(")");
        }

        sb.append(sb2.toString());
        sb.append(" order by employeid,nddate");

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                /*if (dbSearch.SearchForGet(sb.toString()) == DBSearch.Result.fund && getSearchData()) {
                    connectOK = true;
                }*/

                if (dbSearch.JPpostCommand(sb.toString()) == DBSearch.Result.fund && getSearchData()) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (connectOK) {

                            Intent i = new Intent();
                            Bundle b = new Bundle();
                            b.putStringArrayList("ontime",ontime);
                            b.putStringArrayList("offtime",offtime);
                            b.putStringArrayList("addontime",addontime);
                            b.putStringArrayList("addofftime",addofftime);
                            b.putStringArrayList("resttime1",resttime1);
                            b.putStringArrayList("resttime2",resttime2);
                            b.putStringArrayList("date",date);
                            b.putStringArrayList("employeid",employeid);
                            b.putStringArrayList("employename",employename);
                            i.putExtras(b);
                            i.setClass(EmployeAttendanceCondition.this,EmployeAttendanceResult.class);
                            startActivity(i);

                        } else {
                            new AlertDialog.Builder(EmployeAttendanceCondition.this)
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

    boolean getSearchData(){

        JSONObject object;
        ontime = new ArrayList<>();
        offtime = new ArrayList<>();
        addontime = new ArrayList<>();
        addofftime = new ArrayList<>();
        resttime1 = new ArrayList<>();
        resttime2 = new ArrayList<>();
        date = new ArrayList<>();
        employeid = new ArrayList<>();
        employename = new ArrayList<>();
        try{
            for(int i = 0;i<dbSearch.dateArray.length();i++){
                object = dbSearch.dateArray.getJSONObject(i);
                ontime.add(object.get("ontime").toString());
                offtime.add(object.get("offtime").toString());
                addontime.add(object.get("addontime").toString());
                addofftime.add(object.get("addofftime").toString());
                resttime1.add(object.get("resttime1").toString());
                resttime2.add(object.get("resttime2").toString());
                date.add(object.get("date").toString());
                employeid.add(object.get("employeid").toString());
                employename.add(object.get("employename").toString());
            }
        }catch(JSONException e){
            Log.d("error",e.getMessage());
            return false;
        }
        return true;
    }

    public void onClear(View v){

        l3.setText(Common.toLanguage("點擊多選部門編號"));
        l4.setText(Common.toLanguage("點擊多選員工編號"));
        t1.setText(GetMonthFirstDay(Common.user_date));
        t2.setText(GetDate(Common.user_date));

        selectEmploye.clear();
        selectEmployeID.clear();
        selectDepartID.clear();
        selectDepart.clear();
    }

}
