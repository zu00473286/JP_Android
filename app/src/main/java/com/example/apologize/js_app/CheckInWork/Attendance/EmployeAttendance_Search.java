package com.example.apologize.js_app.CheckInWork.Attendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leo on 2017/8/21.
 */

public class EmployeAttendance_Search extends BaseActivity{

    boolean isSelectAll = false;
    ArrayList<String> didSelectData;
    ArrayList<String> didSelectID;
    private ListView listView;
    private TextView tvCount,tvitem;
    private EditText editem;
    private SimpleAdapter da;
    ArrayList<HashMap<String,String>> data;
    int TotCount,TotPage,current = 0;
    StringBuilder sb = new StringBuilder();
    private String[]sql;
    Intent intent;
    Bundle bundle;
    ProgressDialog dialog;
    Handler myHandler = new Handler();
    DBSearch dbSearch = new DBSearch();
    boolean connetOK = false;
    String sqlcmd = "",keyword = "",sqlcmdwithcondition = "",line = "",type = "";
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view_with_selectall);
        findviews();
        intent = getIntent();
        bundle = intent.getExtras();
        sqlcmd = bundle.getString("sqlcmd");
        sqlcmdwithcondition = bundle.getString("sqlcmdwithcondition");
        keyword = bundle.getString("keyword");
        editem.setText(keyword);
        line = bundle.getString("line");
        type = bundle.getString("type");
        data = new ArrayList<>();
        switch (type){
            case "depart":

                tvitem.setText(Common.toLanguage("部門編號"));
                break;
            case "employe":

                tvitem.setText(Common.toLanguage("員工編號"));
                break;
            default:
                break;
        }
        Bundle b = getIntent().getExtras();
        didSelectData = b.getStringArrayList("didSelect");
        didSelectID = b.getStringArrayList("didSelectID");
        searchData();
    }

    void findviews() {
        listView =  (ListView)findViewById(R.id.rmNewConditionListView);
        tvCount = (TextView)findViewById(R.id.textView);
        tvitem = (TextView)findViewById(R.id.tvItem);
        editem = (EditText)findViewById(R.id.edItem);
        btn = (Button)findViewById(R.id.searchBtn);
        didSetLanguage();
    }

    public void didSetLanguage() {

        ((Button)findViewById(R.id.rmNewConditionBtnCancel)).setText(Common.toLanguage("取回"));
        ((Button)findViewById(R.id.searchBtn)).setText(Common.toLanguage("搜尋"));
        ((Button)findViewById(R.id.selectall)).setText(Common.toLanguage("全選"));
    }

    void searchData(){
        if(!NetWork.CheckNetWorkState(this))return;
        connetOK = false;
        tvCount.setText("");
        sb = new StringBuilder();
        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "","");

        if(keyword.length() > 0){
            dbSearch.PutParameter("keyword",keyword);
            sb.append(sqlcmdwithcondition);
        }else{
            sb.append(sqlcmd);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getList()) {
                    connetOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        current = 0;
                        setListViewAdapter(current);
                        if(data.size() == 0){
                            new AlertDialog.Builder(EmployeAttendance_Search.this)
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

    boolean getList() {
        JSONObject object;
        data = new ArrayList<>();
        try {

            if(dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund) {
                for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                    object = dbSearch.dateArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap();
                    switch (type) {
                        case "employe":

                            map.put("employeid",Common.toLanguage( "員工編號") + ": " + object.getString("employeid"));
                            map.put("employename", Common.toLanguage("員工姓名") + ": " + object.getString("employename"));
                            if(didSelectID.contains(object.getString("employeid"))){
                                map.put("select","1");
                            }else{
                                map.put("select","0");
                            }
                            data.add(map);

                            break;
                        case "depart":

                            map.put("partno", Common.toLanguage("部門編號") + ": " + object.getString("partno"));
                            map.put("partname", Common.toLanguage("部門名稱") + ": " + object.getString("partname"));
                            if(didSelectID.contains(object.getString("partno"))){
                                map.put("select","1");
                            }else{
                                map.put("select","0");
                            }
                            data.add(map);

                            break;
                        default:
                            break;
                    }
                }
            }

            TotCount = data.size();
            TotPage = TotCount/10;
            if((TotCount % 10) != 0)
            {
                TotPage++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void setListViewAdapter(int page) {

        setListView();

        sb.delete(0, sb.length());
        sb.append(page+1).append("/").append(TotPage).append(Common.toLanguage("頁"));
        tvCount.setText(sb.toString());
    }

    void setListView(){

        ArrayList<HashMap<String,String>> tempData = new ArrayList<>();

        for(int i = 0;i < 10;i++){
            if((i + current * 10) >= data.size()){
                break;
            }
            tempData.add(data.get(i + current * 10));

        }

        for(int i = 0;i<didSelectData.size();i++){
            if(tempData.contains(didSelectData.get(i))){
                for(int o = 0;o < tempData.size();o++){
                    if(tempData.get(o).equals(didSelectData.get(i))){
                        HashMap<String,String> map = tempData.get(o);
                        tempData.remove(o);
                        map.put("select","1");
                        tempData.add(o,map);
                        break;
                    }
                }
            }
        }

        String[] dataArray = {};

        switch (type){
            case "employe":
                dataArray = new String[] {"employeid","employename"};
                break;
            case "depart":
                dataArray = new String[] {"partno","partname"};
                break;
            default:
                break;
        }

        int layout = 0;
        int[] views = new int[] {};

        switch (line){
            case "2":
                layout = R.layout.list_cell_for_two_lines_with_select;
                views = new int[] {R.id.line1,R.id.line2};
                break;
            case "3":
                layout = R.layout.list_cell_for_three_lines;
                views = new int[] {R.id.line1,R.id.line2,R.id.line3};
                break;
            case "4":
                layout = R.layout.list_cell_for_four_lines;
                views = new int[] {R.id.line1,R.id.line2,R.id.line3,R.id.line4};
                break;
            case "5":
                layout = R.layout.list_cell_for_five_lines;
                views = new int[] {R.id.line1,R.id.line2,R.id.line3,R.id.line4,R.id.line5};
                break;
            default:
                break;
        }

        da = new SimpleAdapter(this,tempData,layout,dataArray,views) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(data.get(position + current * 10).get("select").equals("0")){
                    v.findViewById(R.id.select).setBackgroundResource(R.drawable.unselect_circle);
                }else{
                    v.findViewById(R.id.select).setBackgroundResource(R.drawable.select_circle);
                }
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //do something

                        if(data.get(position + current * 10).get("select").equals("1")){
                            data.get(position + current * 10).put("select","0");
                            (v.findViewById(R.id.select)).setBackgroundResource(R.drawable.unselect_circle);
                        }else{
                            data.get(position + current * 10).put("select","1");
                            (v.findViewById(R.id.select)).setBackgroundResource(R.drawable.select_circle);
                        }
                    }
                });
                return v;
            }
        };
        listView.setAdapter(da);
    }

    public void onSearch(View view){
        keyword = editem.getText().toString().trim();
        searchData();
    }

    public void onPageUp(View view){
        if(current -1 < 0)return;
        setListViewAdapter(--current);
    }

    public void onPageDown(View view){
        if(current +1 >= TotPage)return;
        setListViewAdapter(++current);
    }

    public void onBack(View view) {


        for(int i = 0;i<data.size();i++){
            if(data.get(i).get("select").equals("1")){
                String no,name;
                if(type.equals("employe")){
                    no = data.get(i).get("employeid").toString().split(" ")[1];
                    name = data.get(i).get("employename").toString().split(" ")[1];
                }else{
                    no = data.get(i).get("partno").toString().split(" ")[1];
                    name = data.get(i).get("partname").toString().split(" ")[1];
                }
                if(!didSelectID.contains(no)){
                    didSelectID.add(no);
                    didSelectData.add(name);
                }
            }else{
                String no = "",name = "";
                if(type.equals("employe")){
                    no = data.get(i).get("employeid").toString().split(" ")[1];
                    name = data.get(i).get("employename").toString().split(" ")[1];
                }else{
                    no = data.get(i).get("partno").toString().split(" ")[1];
                    name = data.get(i).get("partname").toString().split(" ")[1];
                }
                if(didSelectID.contains(no)){
                    int index = didSelectID.indexOf(no);
                    didSelectID.remove(no);
                    didSelectData.remove(index);
                }
            }
        }

        Log.d("data",didSelectData.toString());

        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putStringArrayList("didSelect",didSelectData);
        b.putStringArrayList("didSelectID",didSelectID);
        i.putExtras(b);
        setResult(RESULT_OK,i);
        finish();

    }

    public void onSelectAll(View v){

        isSelectAll = !isSelectAll;

        for(int i = 0;i<data.size();i++){
            HashMap<String,String> map = data.get(i);
            data.remove(i);
            if(isSelectAll){
                map.put("select","1");
            }else{
                map.put("select","0");
            }
            data.add(i,map);
        }

        setListView();
    }
}
