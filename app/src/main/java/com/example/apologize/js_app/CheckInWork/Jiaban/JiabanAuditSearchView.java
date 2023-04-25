package com.example.apologize.js_app.CheckInWork.Jiaban;

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
import com.example.apologize.js_app.CheckInWork.Leave.LeaveAudit;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leo on 2017/11/14.
 */

public class JiabanAuditSearchView extends BaseActivity {

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

        ArrayList<String> Employename,Jid,JbDate,addtime1,addtime2,result;

        Employename = getIntent().getExtras().getStringArrayList("Employename");
        Jid = getIntent().getExtras().getStringArrayList("Jid");
        JbDate = getIntent().getExtras().getStringArrayList("JbDate");
        addtime1 = getIntent().getExtras().getStringArrayList("addtime1");
        addtime2 = getIntent().getExtras().getStringArrayList("addtime2");
        result = getIntent().getExtras().getStringArrayList("result");

        totalData = new ArrayList<>();
        for(int i = 0;i<Employename.size();i++){
            HashMap<String,String> map = new HashMap<>();
            map.put("Employename",Employename.get(i).toString());
            map.put("Jid",Jid.get(i).toString());
            map.put("JbDate",JbDate.get(i).toString());
            map.put("addtime1",addtime1.get(i).toString());
            map.put("addtime2",addtime2.get(i).toString());
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

                ((TextView)v.findViewById(R.id.line1)).setText(Common.toLanguage("員工姓名") + ": " + maps.get(position).get("Employename").toString());
                String JbDate = dateToText(maps.get(position).get("JbDate").toString());
                String addtime1 = timeToText(maps.get(position).get("addtime1").toString());
                String addtime2 = timeToText(maps.get(position).get("addtime2").toString());
                ((TextView)v.findViewById(R.id.line2)).setText(Common.toLanguage("加班日期") + ": " + JbDate);
                ((TextView)v.findViewById(R.id.line3)).setText(Common.toLanguage("時間起") + ": " + addtime1);
                ((TextView)v.findViewById(R.id.line4)).setText(Common.toLanguage("時間迄") + ": " + addtime2);
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
                        b.putString("Jid",maps.get(position).get("Jid").toString());
                        i.putExtras(b);
                        i.setClass(JiabanAuditSearchView.this, JiabanAudit.class);
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
