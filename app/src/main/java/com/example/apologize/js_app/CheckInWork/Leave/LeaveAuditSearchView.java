package com.example.apologize.js_app.CheckInWork.Leave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leo on 2017/11/14.
 */

public class LeaveAuditSearchView extends BaseActivity {

    SimpleAdapter da;
    int currentPage,totalPage;
    ListView listView;
    TextView pageLabel,backBtn;
    ImageButton preBtn,nextBtn;
    ArrayList<HashMap<String,String>> totalData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search_view_without_search);
        findView();
    }

    void findView(){

        listView = (ListView)findViewById(R.id.listView);
        pageLabel = (TextView)findViewById(R.id.pageLabel);
        backBtn = (TextView)findViewById(R.id.backBtn);
        preBtn = (ImageButton)findViewById(R.id.preBtn);
        nextBtn = (ImageButton)findViewById(R.id.nextBtn);

        ArrayList<String> employename,bid,date1,date2,time1,time2,requestdate,result;

        employename = getIntent().getExtras().getStringArrayList("employename");
        bid = getIntent().getExtras().getStringArrayList("bid");
        date1 = getIntent().getExtras().getStringArrayList("date1");
        date2 = getIntent().getExtras().getStringArrayList("date2");
        time1 = getIntent().getExtras().getStringArrayList("time1");
        time2 = getIntent().getExtras().getStringArrayList("time2");
        requestdate = getIntent().getExtras().getStringArrayList("requestdate");
        result = getIntent().getExtras().getStringArrayList("result");

        totalData = new ArrayList<>();
        for(int i = 0;i<employename.size();i++){
            HashMap<String,String> map = new HashMap<>();
            map.put("employename",employename.get(i).toString());
            map.put("bid",bid.get(i).toString());
            map.put("date1",date1.get(i).toString());
            map.put("date2",date2.get(i).toString());
            map.put("time1",time1.get(i).toString());
            map.put("time2",time2.get(i).toString());
            map.put("requestdate",requestdate.get(i).toString());
            map.put("result",result.get(i).toString());
            totalData.add(map);
        }

        totalPage = totalData.size() / 10 + totalData.size() % 10 == 0 ? 0 : 1;
        currentPage = 1;

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        didSetLanguage();
    }

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        title.setImageResource(R.drawable.checkintitle);
        backBtn.setText(Common.toLanguage("返回前一頁"));

        setListView();
    }

    void setListView(){

        pageLabel.setText(currentPage + "/" + totalPage + " " + Common.toLanguage("頁"));

        ArrayList<HashMap<String,String>> map = new ArrayList<>();

        int max = currentPage * 10;
        if(totalData.size() < currentPage * 10){
            max = totalData.size();
        }

        for(int i = (currentPage - 1) * 10;i<max ;i++){
            map.add(totalData.get(i));
        }

        final ArrayList<HashMap<String,String>> maps = map;

        da = new SimpleAdapter(this,maps,R.layout.list_cell_for_five_lines,new String[]{"","","","",""},new int[]{R.id.line1,R.id.line2,R.id.line3,R.id.line4,R.id.line5})  {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView)v.findViewById(R.id.line1)).setText(Common.toLanguage("員工姓名") + ": " + maps.get(position).get("employename").toString());
                String date1 = dateToText(maps.get(position).get("date1").toString());
                String time1 = timeToText(maps.get(position).get("time1").toString());
                String date2 = dateToText(maps.get(position).get("date2").toString());
                String time2 = timeToText(maps.get(position).get("time2").toString());
                String request1 = dateToText(maps.get(position).get("requestdate").toString().split(" ")[0]);
                String request2 = "";
                ((TextView)v.findViewById(R.id.line2)).setText(Common.toLanguage("請假日期") + ": " + date1 + " " +  time1);
                ((TextView)v.findViewById(R.id.line3)).setText(Common.toLanguage("到期日期") + ": " + date2 + " " + time2);
                ((TextView)v.findViewById(R.id.line4)).setText(Common.toLanguage("申請時間") + ": " + request1 + " " + request2);
                String result = "";
                if(maps.get(position).get("result").equals("0")){
                    result = Common.toLanguage("審核中");
                }else if(maps.get(position).get("result").equals("1")){
                    result = Common.toLanguage("審核失敗");
                }else if(maps.get(position).get("result").equals("2")){
                    result = Common.toLanguage("審核通過");
                }
                ((TextView)v.findViewById(R.id.line5)).setText(Common.toLanguage("處理狀況") + ": " + result);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        Bundle b = new Bundle();
                        b.putString("bid",maps.get(position).get("bid").toString());
                        i.putExtras(b);
                        i.setClass(LeaveAuditSearchView.this,LeaveAudit.class);
                        startActivityForResult(i,1);
                    }
                });

                return v;
            }
        };
        listView.setAdapter(da);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            if(requestCode == 1){
                finish();
            }
        }
    }

    void pre(){
        if((currentPage - 1) > 0){
            currentPage -= 1;
            setListView();
        }
    }

    void next(){
        if((currentPage + 1) <= totalPage){
            currentPage += 1;
            setListView();
        }
    }
}
