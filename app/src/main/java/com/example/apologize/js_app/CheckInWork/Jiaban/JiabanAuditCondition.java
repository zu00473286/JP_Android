package com.example.apologize.js_app.CheckInWork.Jiaban;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.apologize.js_app.Base.JP_Search_View;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.CheckInWork.Jiaban.JiabanAuditSearchView;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 2017/11/13.
 */

public class JiabanAuditCondition extends BaseActivity {

    StringBuilder sb;
    ArrayList<String> employename,JbDate,addtime1,addtime2,result,Jid;
    Handler myHandler = new Handler();
    ProgressDialog dialog;
    DBSearch dbSearch;
    boolean connectOK;
    String sqlcmd,sqlcmd2,sqlcmd3;
    int myYear,myMonth,myDay;
    TextView t1,t2,title1,l1,l2,l3,l4,l5;
    EditText t3;
    Button b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiaban_audit_condition);
        findView();
    }

    void findView(){

        findViewById(R.id.sview).setOnTouchListener(endedit);

        title1 = (TextView)findViewById(R.id.title1);
        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l4 = (TextView)findViewById(R.id.l4);
        l3 = (TextView)findViewById(R.id.l3);
        l5 = (TextView)findViewById(R.id.l5);
        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);

        t3 = (EditText)findViewById(R.id.t3);

        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);

        t3.setOnLongClickListener(employeidLong);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        didSetLanguage();
    }

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        LinearLayout l = (LinearLayout) findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
        l.setBackgroundResource(R.drawable.page);

        l1.setText(Common.toLanguage("加班日期") + ":");
        l2.setText(Common.toLanguage("加班日期") + ":");
        l3.setText(Common.toLanguage("員工編號") + ":");
        title1.setText(Common.toLanguage("搜尋條件"));
        l5.setText(Common.toLanguage("加班日期") + ":");

        b1.setText(Common.toLanguage("搜尋"));
        b2.setText(Common.toLanguage("取消"));

        t3.setHint(Common.toLanguage("長按開啟搜尋"));

        t1.setOnClickListener(scanDateAndTime);
        t2.setOnClickListener(scanDateAndTime);

        onClear();
    }

    View.OnClickListener scanDateAndTime = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TextView v = (TextView) view;
            String date = v.getText().toString().replace("/","");
            final Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(Common.DateToUSD(date).substring(0,4)), Integer.parseInt(Common.DateToUSD(date).substring(4,6)), Integer.parseInt(Common.DateToUSD(date).substring(6,8)));

            myYear = c.get(Calendar.YEAR);
            myMonth = c.get(Calendar.MONTH) - 1;
            myDay = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(JiabanAuditCondition.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                    final String date2 = i + "/" + String.format("%02d",i1 + 1) + "/" + String.format("%02d",i2);

                    v.setText(Common.DateToTWD(date2));

                }

            }, myYear, myMonth, myDay).show();
        }
    };

    View.OnLongClickListener employeidLong = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(JiabanAuditCondition.this,JP_Search_View.class);
            Bundle b = new Bundle();
            sqlcmd = "select employeid,employename from employee where 0=0";
            sqlcmd2 = sqlcmd + " and employeid like + '%' + @keyword + '%'";
            sqlcmd3 = sqlcmd2;
            b.putString("line","2");
            b.putString("type","employeid");
            b.putString("sqlcmd",sqlcmd);
            b.putString("sqlcmdwithcondition",sqlcmd2);
            b.putString("keyword",t3.getText().toString());
            i.putExtras(b);
            startActivityForResult(i,1);
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Bundle b = data.getExtras();
                t3.setText(b.getString("employeid"));
                getEmployename();
            }
        }
    }

    void getEmployename(){
        if(!NetWork.CheckNetWorkState(this))return;

        dbSearch = new DBSearch();

        dbSearch.PutParameter("id",t3.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.SearchForGet("select employename from employee where employeid=@id") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connectOK){

                            try{
                                for(int i = 0;i<dbSearch.dateArray.length();i+=1){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(i);
                                    l4.setText(object.get("employename").toString());
                                }
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                        }else{
                            l4.setText("");
                        }
                    }
                });
            }
        }).start();
    }

    void onSearch(){

        if(!NetWork.CheckNetWorkState(this))return;

        dbSearch = new DBSearch();

        sb = new StringBuilder();
        sb.append("select * from Jiaban where ");

        if(t3.getText().toString().length() > 0){
            dbSearch.PutParameter("id",t3.getText().toString());
            sb.append("employeid=@id and ");
        }
        //sb.append(" ((udate2>=@date1 and yndate2<=@date2) or (udate2 between @date1 and @date2) or (yndate2 between @date1 and @date2)) and result='0' order by qjdate desc");
        sb.append(" (JbDate>=@date1 and JbDate<=@date2) and result='0' order by JbDate desc");
        String date = t1.getText().toString();
        dbSearch.PutParameter("date1",date.split(" ")[0].replace("/",""));

        date = t2.getText().toString();
        dbSearch.PutParameter("date2",date.split(" ")[0].replace("/",""));

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this,"","");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund && getSearchData()){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(connectOK){

                            Intent i = new Intent();
                            Bundle b = new Bundle();
                            b.putStringArrayList("Jid",Jid);
                            b.putStringArrayList("Employename",employename);
                            b.putStringArrayList("JbDate",JbDate);
                            b.putStringArrayList("addtime1",addtime1);
                            b.putStringArrayList("addtime2",addtime2);
                            b.putStringArrayList("result",result);
                            i.putExtras(b);
                            i.setClass(JiabanAuditCondition.this, JiabanAuditSearchView.class);
                            startActivity(i);

                        }else{
                            new AlertDialog.Builder(JiabanAuditCondition.this)
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
        Jid = new ArrayList<>();
        employename = new ArrayList<>();
        JbDate = new ArrayList<>();
        addtime1 = new ArrayList<>();
        addtime2 = new ArrayList<>();
        result = new ArrayList<>();
        try{

            for(int i = 0;i<dbSearch.dateArray.length();i++){
                object = dbSearch.dateArray.getJSONObject(i);
                Jid.add(object.get("Jid").toString());
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
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        t1.setText(Common.DateToTWD(f.format(date)));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH,1);
        Date date1 = cal.getTime();
        t2.setText(Common.DateToTWD(f.format(date1)));
        t3.setText("");
        l4.setText("");
    }

}
