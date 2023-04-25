package com.example.apologize.js_app.CheckInWork.Jiaban;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.CheckInWork.Attendance.EmployeAttendance_Search;
import com.example.apologize.js_app.CheckInWork.Leave.Leave_Search_View;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 2017/11/13.
 */

public class JiabanSearchCondition extends BaseActivity {

    StringBuilder sb;
    String sqlcmd,sqlcmd2,sqlcmd3;
    int test1 = 0;
    DBSearch dbSearch;
    boolean connectOK;
    Handler myHandler = new Handler();
    ProgressDialog dialog;
    Button b1,b2;
    TextView l1,l2,l3,l4,l5,l6;
    EditText t1,t3;
    Spinner t4;
    ArrayList<String> selectEmploye,selectDepart;
    ArrayList<String> selectEmployeID,selectDepartID;
    int myYear,myMonth,myDay;
    ArrayList<String> employename,JbDate,addtime1,addtime2,jid,result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiaban_search_condition);
        findView();
    }

    void findView(){

        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l3 = (TextView)findViewById(R.id.l3);
        l4 = (TextView)findViewById(R.id.l4);
        l5 = (TextView)findViewById(R.id.l5);
        l6 = (TextView)findViewById(R.id.l6);
        t1 = (EditText) findViewById(R.id.t1);
        t3 = (EditText) findViewById(R.id.t3);

        b1 = (Button)findViewById(R.id.search);
        b2 = (Button)findViewById(R.id.clear);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        if(Common.user_date == 1){
            t1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
            t3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
        }else{
            t1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
            t3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        }

        if(true){
            l5.setOnClickListener(departPick);
            l6.setOnClickListener(employePick);
            l5.setText(Common.toLanguage("點擊多選部門編號"));
        }else{
            l6.setVisibility(View.GONE);
            l5.setText(Common.toLanguage("員工編號") + ": " + Common.jpuser_id);
        }

        t4 = (Spinner)findViewById(R.id.t4);

        String[]items = new String[]{Common.toLanguage("審核中"),Common.toLanguage("審核失敗"),Common.toLanguage("審核通過")};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JiabanSearchCondition.this,R.layout.spinnerview,items);
        adapter.setDropDownViewResource(R.layout.spinnerview);
        t4.setAdapter(adapter);
        test1 = t4.getSelectedItemPosition();
        selectEmployeID = new ArrayList<>();
        selectEmploye = new ArrayList<>();
        selectDepartID = new ArrayList<>();
        selectDepart = new ArrayList<>();

        didSetLanguage();
    }

    public void didSetLanguage(){

        ((ScrollView)findViewById(R.id.scrollview)).setOnTouchListener(endedit);

        ImageView title = (ImageView)findViewById(R.id.title);
        LinearLayout l = (LinearLayout)findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);


        l1.setText(Common.toLanguage("加班時間") + ":");
        ((TextView)findViewById(R.id.title1)).setText(Common.toLanguage("搜尋條件"));
        l2.setText(Common.toLanguage("加班時間") + ":");
        l3.setText(Common.toLanguage("加班時間") + ":");
        l4.setText(Common.toLanguage("處理狀況") + ":");
        l6.setText(Common.toLanguage("點擊多選員工編號"));

        b1.setText(Common.toLanguage("搜尋"));
        b2.setText(Common.toLanguage("清除"));

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
            }
        });

        onClear();

        if(getIntent().getExtras().get("personal").toString().equals("0")){
            ForPersonal();
        }else{
            Log.d("false",getIntent().getExtras().get("personal").toString());
        }
    }

    void ForPersonal(){
        l5.setEnabled(false);
        l6.setEnabled(false);
        selectEmployeID = new ArrayList<>();
        selectEmployeID.add(Common.jpuser_empid);

        l6.setText("員工編號 : " + Common.jpuser_empid);

        dbSearch = new DBSearch();

        dbSearch.PutParameter("id",Common.jpuser_empid);

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPSearchForGet("select partname,partno from employee where employeid=@id") == DBSearch.Result.fund ) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectOK) {

                            if(dbSearch.dateArray.length() > 0){

                                try{
                                    JSONObject o = dbSearch.dateArray.getJSONObject(0);
                                    l5.setText("部門名稱 : " + o.getString("partname"));
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
                });

            }
        }).start();
    }

    void onSearch(){

        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");

        try{
            if(f.parse(t1.getText().toString()).after(f.parse(t3.getText().toString()))){
                ToastError(Common.toLanguage("起始日期不可大於終止日期"));
                return;
            }
        }catch(Exception e){
            Log.d("error",e.getMessage());
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();
        StringBuilder ss = new StringBuilder();
        if(true){
            for(int i = 0;i<selectEmployeID.size();i++){
                if(ss.toString().length() <= 0){
                    ss.append(" ( employeid='" + selectEmployeID.get(i).toString() + "'");
                }else{
                    ss.append(" or employeid='" + selectEmployeID.get(i).toString() + "'");
                }
            }
        }else{
            dbSearch.PutParameter("id",Common.jpuser_empid);
            ss.append("(employeid=@id");
        }

        if(ss.toString().length() > 0){
            ss.append(") and");
        }

        dbSearch.PutParameter("result",String.valueOf(t4.getSelectedItemPosition()));

        //sb.append("select qjno,qjdate2,result,udate2,yndate2,uontime,ynofftime,employename from qingjia where " + ss.toString() + " ((udate2<=@date1 and yndate2>=@date2) or (udate2 between @date1 and @date2) or (yndate2 between @date1 and @date2)) and result=@result order by qjdate desc");
        sb.append("select * from Jiaban where " + ss.toString() + " JbDate>=@date1 and JbDate<=@date2 and result=@result order by JbDate desc");

        dbSearch.PutParameter("date1", Common.DateToTWD(t1.getText().toString()));
        dbSearch.PutParameter("date2", Common.DateToTWD(t3.getText().toString()));

        dbSearch.PutParameter("id", Common.jpuser_empid);

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund && getSearchData()) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (connectOK) {

                            Intent i = new Intent();
                            Bundle b = new Bundle();
                            b.putStringArrayList("Employename",employename);
                            b.putStringArrayList("JbDate",JbDate);
                            b.putStringArrayList("addtime1",addtime1);
                            b.putStringArrayList("addtime2",addtime2);
                            b.putStringArrayList("result",result);
                            b.putStringArrayList("Jid",jid);
                            i.putExtras(b);
                            i.setClass(JiabanSearchCondition.this, Jiaban_Search_View.class);
                            startActivity(i);

                        } else {
                            new AlertDialog.Builder(JiabanSearchCondition.this)
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
        jid = new ArrayList<>();
        employename = new ArrayList<>();
        JbDate = new ArrayList<>();
        addtime1 = new ArrayList<>();
        addtime2 = new ArrayList<>();
        result = new ArrayList<>();
        try{
            for(int i = 0;i<dbSearch.dateArray.length();i++){
                object = dbSearch.dateArray.getJSONObject(i);
                jid.add(object.get("Jid").toString());
                employename.add(object.get("Employename").toString());
                JbDate.add(object.get("JbDate").toString());
                addtime1.add(object.get("addtime1").toString());
                addtime2.add(object.get("addtime2").toString());
                result.add(object.get("result").toString());
            }
        }catch(JSONException e){
            Log.d("error",e.getMessage());
            return false;
        }
        return true;
    }

    void onClear(){

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        if(Common.user_date == 1){
            t1.setText(String.valueOf(Integer.parseInt(f.format(date)) - 19110000));
        }else{
            t1.setText(f.format(date));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH,1);
        Date date1 = cal.getTime();
        if(Common.user_date == 1){
            t3.setText(String.valueOf(Integer.parseInt(f.format(date1).substring(0,6)) - 191100) + "01");
        }else{
            t3.setText(f.format(date1).substring(0,8) + "01");
        }
        t4.setSelection(0);
        if(true){
            l5.setText(Common.toLanguage("點擊多選部門編號"));
            l6.setText(Common.toLanguage("點擊多選員工編號"));
        }

        selectDepart.clear();
        selectDepartID.clear();
        selectEmploye.clear();
        selectEmployeID.clear();


    }

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
        l6.setText(con.toString());

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
        l5.setText(con.toString());

        if(selectDepartID.size() > 0){
            getEmploye();
        }else{
            selectEmployeID = new ArrayList<>();
            selectEmploye = new ArrayList<>();
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
                if(dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund){
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

    View.OnClickListener employePick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(JiabanSearchCondition.this,EmployeAttendance_Search.class);
            Bundle b = new Bundle();
            sqlcmd = "select employeid,employename,0 as [select] from employee where 0=0";
            sqlcmd2 = sqlcmd + "and employeid like + '%' + @keyword + '%'";
            sqlcmd3 = sqlcmd2;
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

            Intent i = new Intent(JiabanSearchCondition.this,EmployeAttendance_Search.class);
            Bundle b = new Bundle();
            sqlcmd = "select partno,partname,0 as [select] from part where 0=0";
            sqlcmd2 = sqlcmd + " and partno like + '%' + @keyword + '%'";
            sqlcmd3 = sqlcmd2;
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

}
