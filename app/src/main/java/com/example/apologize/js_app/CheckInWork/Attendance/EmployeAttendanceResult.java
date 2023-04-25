package com.example.apologize.js_app.CheckInWork.Attendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.CheckInWork.PersonalAttendance.SearchResultDetail;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by leo on 2017/9/18.
 */

public class EmployeAttendanceResult extends BaseActivity {

    int temp = 0;
    TextView t1,t2,t3,t4,t5,t6,t7,t8,t9;
    HorizontalScrollView rtScroll,rScroll;
    ScrollView lscroll,rscrollver;
    DBSearch dbSearch;
    StringBuilder sb;
    Handler myHandler = new Handler();
    ProgressDialog dialog;
    boolean connectOK;
    ListView rightList,leftList;
    ArrayList<HashMap<String,String>> data = new ArrayList<>();
    SimpleAdapter r;
    String year,month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_checkin_result);
        findVIew();
        didSetLanguage();
        //進到此畫面會直接抓資料所以直接卡畫面，避免誤按返回鍵造成閃退風險
        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","讀取中..");
    }

    public void findVIew(){

        t1 = (TextView)findViewById(R.id.text1);
        t2 = (TextView)findViewById(R.id.text2);
        t3 = (TextView)findViewById(R.id.text3);
        t4 = (TextView)findViewById(R.id.text4);
        t5 = (TextView)findViewById(R.id.text5);
        t6 = (TextView)findViewById(R.id.text6);
        t7 = (TextView)findViewById(R.id.text7);
        t8 = (TextView)findViewById(R.id.text8);
        t9 = (TextView)findViewById(R.id.text9);

        rtScroll = (HorizontalScrollView)findViewById(R.id.rtScroll);
        rScroll = (HorizontalScrollView)findViewById(R.id.rScroll);
        lscroll = (ScrollView)findViewById(R.id.lscroll);
        rscrollver = (ScrollView)findViewById(R.id.rscrollver);

        rightList = (ListView)findViewById(R.id.rightList);
        leftList = (ListView)findViewById(R.id.leftList);

        rightList.setEnabled(false);
        leftList.setEnabled(false);

        getText();

        rtScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollX = rtScroll.getScrollX(); //for horizontalScrollView
                int scrollY = rtScroll.getScrollY(); //for verticalScrollView
                rScroll.scrollTo(scrollX,scrollY);
            }
        });

        rScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollX = rScroll.getScrollX(); //for horizontalScrollView
                int scrollY = rScroll.getScrollY(); //for verticalScrollView
                rtScroll.scrollTo(scrollX,scrollY);
            }
        });

        lscroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollX = lscroll.getScrollX(); //for horizontalScrollView
                int scrollY = lscroll.getScrollY(); //for verticalScrollView
                rscrollver.scrollTo(scrollX,scrollY);
            }
        });

        rscrollver.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollX = rscrollver.getScrollX(); //for horizontalScrollView
                int scrollY = rscrollver.getScrollY(); //for verticalScrollView
                lscroll.scrollTo(scrollX,scrollY);
            }
        });

    }

    boolean loopSetData(){

        JSONObject object;
        try{

            for(int i = 0;i<dbSearch.dateArray.length();i++){
                object = dbSearch.dateArray.getJSONObject(i);
                HashMap<String,String> map = data.get(temp);
                data.remove(temp);

                if(object.get("type").equals(Common.ontimeD)){
                    map.put("sontime","1");
                }
                if(object.get("type").equals(Common.offtimeD)){
                    map.put("sofftime","1");
                }
                if(object.get("type").equals(Common.restime1D)){
                    map.put("sresttime1","1");
                }
                if(object.get("type").equals(Common.restime2D)){
                    map.put("sresttime2","1");
                }
                if(object.get("type").equals(Common.addontimeD)){
                    map.put("saddontime","1");
                }
                if(object.get("type").equals(Common.addofftimeD)){
                    map.put("saddofftime","1");
                }

                data.add(temp,map);
                Log.d("data",map.toString());
            }

        }catch(JSONException e){
            Log.d("error",e.getMessage());
            return false;
        }

        return true;
    }

    void loopGetData(){
        if(temp < data.size() && connectCancell == false){
            dbSearch = new DBSearch();
            dbSearch.PutParameter("id",data.get(temp).get("employeid").toString());
            dbSearch.PutParameter("date",data.get(temp).get("date").toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectOK = false;
                    if(dbSearch.JPSearchForGet("select employeid,nddate as date,discern as type from gpsinfo where employeid=@id and nddate=@date order by nddate ,dakatime asc") == DBSearch.Result.fund && loopSetData()){
                        connectOK = true;
                    }
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!connectOK){
                                HashMap<String,String> map = data.get(temp);
                                data.remove(temp);
                                map.put("sontime","0");
                                map.put("sofftime","0");
                                map.put("sresttime1","0");
                                map.put("sresttime2","0");
                                map.put("saddontime","0");
                                map.put("saddofftime","0");
                                
                                data.add(temp,map);
                            }
                            temp += 1;
                            loopGetData();
                        }
                    });
                }
            }).start();
        }else{
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    setListView();
                }
            });
        }
    }

    public void didSetLanguage(){

        ImageView title = (ImageView)findViewById(R.id.title);
        LinearLayout l = (LinearLayout)findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);

        t1.setText(Common.toLanguage("出勤日期"));
        t2.setText(Common.toLanguage("姓名"));
        t3.setText(Common.toLanguage("星期"));
        t4.setText(Common.toLanguage("上班時間"));
        t5.setText(Common.toLanguage("下班時間"));
        t6.setText(Common.toLanguage("休息時間1起"));
        t7.setText(Common.toLanguage("休息時間1終"));
        t8.setText(Common.toLanguage("加班上班"));
        t9.setText(Common.toLanguage("加班下班"));

        ((TextView)findViewById(R.id.hint)).setText(Common.toLanguage("長按開啟詳細資料"));

    }

    public void setListView(){

        final String[] stringnal = new String[]{"employename","week","ontime","offtime","resttime1","resttime2","addontime","addofftime"};

        int[] id = new int[]{R.id.text2,R.id.text3,R.id.text4,R.id.text5,R.id.text6,R.id.text7,R.id.text8,R.id.text9,R.id.text10,R.id.text11};

        r = new SimpleAdapter(this,data,R.layout.activity_search_checkin_result_cell,stringnal,id) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                final View v = super.getView(position,convertView,parent);


                if(data.get(position).get("sontime").equals("1")){
                    v.findViewById(R.id.text4).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text4).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.ontimeD,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                if(data.get(position).get("sofftime").equals("1")){
                    v.findViewById(R.id.text5).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text5).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.offtimeD,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                if(data.get(position).get("sresttime1").equals("1")){
                    v.findViewById(R.id.text6).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text6).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.restime1D,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                if(data.get(position).get("sresttime2").equals("1")){
                    v.findViewById(R.id.text7).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text7).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.restime2D,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                if(data.get(position).get("saddontime").equals("1")){
                    v.findViewById(R.id.text8).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text8).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.addontimeD,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                if(data.get(position).get("saddofftime").equals("1")){
                    v.findViewById(R.id.text9).setBackgroundResource(R.drawable.myborderwithcolor);
                    v.findViewById(R.id.text9).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            searchDetail(Common.addofftimeD,data.get(position).get("employeid").toString(),data.get(position).get("date").toString());
                            return false;
                        }
                    });

                }

                return v;
            }


        };
        rightList.setAdapter(r);

        final String[] stringna = new String[]{"date"};

        int[] id2 = new int[]{R.id.text1};

        r = new SimpleAdapter(this,data,R.layout.activity_search_checkin_result_date,stringna,id2) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position,convertView,parent);


                return v;
            }


        };
        leftList.setAdapter(r);

    }

    void searchDetail(String type, String employeid, String date){

        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        dbSearch = new DBSearch();
        dbSearch.PutParameter("type",type);
        dbSearch.PutParameter("id",employeid);
        dbSearch.PutParameter("date",date);

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPSearchForGet("select bid from gpsinfo where employeid=@id and nddate=@date and discern=@type order by dakatime desc") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(connectOK){

                            try{
                                if(dbSearch.dateArray.length() > 0){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(0);
                                    Intent i = new Intent();
                                    Bundle b = new Bundle();
                                    b.putString("bid",object.get("bid").toString());
                                    i.putExtras(b);
                                    i.setClass(EmployeAttendanceResult.this, SearchResultDetail.class);
                                    startActivity(i);
                                }else{
                                    new AlertDialog.Builder(EmployeAttendanceResult.this)
                                            .setIcon(R.drawable.error)
                                            .setTitle("")
                                            .setMessage(Common.toLanguage("查無資料"))
                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                }
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                        }else{
                            new AlertDialog.Builder(EmployeAttendanceResult.this)
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

    public void getText(){

        ArrayList<String> employeid,date,ontime,offtime,addontime,addofftime,resttime1,resttime2,employename;

        employeid = getIntent().getExtras().getStringArrayList("employeid");
        employename = getIntent().getExtras().getStringArrayList("employename");
        date = getIntent().getExtras().getStringArrayList("date");
        ontime = getIntent().getExtras().getStringArrayList("ontime");
        offtime = getIntent().getExtras().getStringArrayList("offtime");
        addontime = getIntent().getExtras().getStringArrayList("addontime");
        addofftime = getIntent().getExtras().getStringArrayList("addofftime");
        resttime1 = getIntent().getExtras().getStringArrayList("resttime1");
        resttime2 = getIntent().getExtras().getStringArrayList("resttime2");

        data = new ArrayList<>();

        HashMap<String,String> map;

        try{
            for(int i=0;i < ontime.size();i++){
                map = new HashMap<>();
                map.put("ontime",ontime.get(i).equals("null") ? "" : ontime.get(i));
                map.put("offtime",offtime.get(i).equals("null") ? "" : offtime.get(i));
                map.put("resttime1",resttime1.get(i).equals("null") ? "" : resttime1.get(i));
                map.put("resttime2",resttime2.get(i).equals("null") ? "" : resttime2.get(i));
                map.put("addontime",addontime.get(i).equals("null") ? "" : addontime.get(i));
                map.put("addofftime",addofftime.get(i).equals("null") ? "" : addofftime.get(i));
                map.put("sontime","0");
                map.put("sofftime","0");
                map.put("saddontime","0");
                map.put("saddofftime","0");
                map.put("sresttime1","0");
                map.put("sresttime2","0");
                map.put("date",date.get(i));
                map.put("employeid",employeid.get(i));
                map.put("employename",employename.get(i));

                SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(f.parse(date.get(i)));
                int week = cal.get(Calendar.DAY_OF_WEEK);
                switch (week){
                    case Calendar.SUNDAY:
                        map.put("week",Common.toLanguage("日"));
                        break;

                    case Calendar.MONDAY:
                        map.put("week",Common.toLanguage("一"));
                        break;

                    case Calendar.TUESDAY:
                        map.put("week",Common.toLanguage("二"));
                        break;

                    case Calendar.WEDNESDAY:
                        map.put("week",Common.toLanguage("三"));
                        break;

                    case Calendar.THURSDAY:
                        map.put("week",Common.toLanguage("四"));
                        break;

                    case Calendar.FRIDAY:
                        map.put("week",Common.toLanguage("五"));
                        break;

                    case Calendar.SATURDAY:
                        map.put("week",Common.toLanguage("六"));
                        break;
                    default:
                        map.put("week","日");
                        break;
                }

                data.add(map);
            }



            Resources r = getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,r.getDisplayMetrics());

            leftList.setLayoutParams(new LinearLayout.LayoutParams(140 * ((int)px + 1),40 * data.size() * ((int)px + 1)));
            rightList.setLayoutParams(new LinearLayout.LayoutParams(1470 * ((int)px + 1),40 * data.size() * ((int)px + 1)));

        }catch (Exception e){
            Log.d("error",e.toString());
            return ;
        }

        temp = -1;
        getPersonalData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectCancell = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectCancell = false;
    }

    void getPersonalData(){

        temp ++;

        if(temp < data.size() && connectCancell == false){
            loopGetPersonalData();
        }else{
//            dialog =  new ProgressDialog(this);
//            dialog = ProgressDialog.show(this, "","讀取中..");
            temp = 0;
            loopGetData();
        }

    }

    void loopGetPersonalData(){

        dbSearch = new DBSearch();
        dbSearch.PutParameter("id",data.get(temp).get("employeid").toString());

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPSearchForGet("select partname,employename from employee where employeid=@id") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connectOK){
                            try{
                                if(dbSearch.dateArray.length() > 0){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(0);
                                    HashMap<String,String> map = data.get(temp);
                                    map.put("employename",object.get("employename").toString() == "null" ? "" : object.get("employename").toString());
                                    map.put("partname",object.get("partname").toString() == "null" ? "" : object.get("partname").toString());
                                    data.remove(temp);
                                    data.add(temp,map);
                                }
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                        }
                        getPersonalData();
                    }
                });

            }
        }).start();
    }

}
