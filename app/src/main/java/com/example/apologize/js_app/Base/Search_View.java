package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//import com.example.apologize.js_app.R;

import com.example.namespace.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leo on 2017/8/21.
 */

public class Search_View extends BaseActivity{

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
        setContentView(R.layout.activity_search_view);
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
            case "employeid":

                tvitem.setText(Common.toLanguage("員工編號"));
                break;
            case "cuname":

                tvitem.setText(Common.toLanguage("員工姓名"));
                break;
            case "departid":

                tvitem.setText(Common.toLanguage("部門編號"));
                break;
            case "shift":

                tvitem.setText(Common.toLanguage("班別編號"));
                break;
            default:
                break;
        }
        searchData();
    }

    void findviews() {
        listView =  (ListView)findViewById(R.id.rmNewConditionListView);
        tvCount = (TextView)findViewById(R.id.textView);
        tvitem = (TextView)findViewById(R.id.tvItem);
        editem = (EditText)findViewById(R.id.edItem);
        btn = (Button)findViewById(R.id.searchBtn);

        ((Button)findViewById(R.id.rmNewConditionBtnCancel)).setText(Common.toLanguage("返回前一頁"));
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
                            new AlertDialog.Builder(Search_View.this)
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

            if(dbSearch.SearchForGet(sb.toString()) == DBSearch.Result.fund) {
                for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                    object = dbSearch.dateArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap();
                    switch (type) {
                        case "employeid":

                            map.put("employeid",Common.toLanguage( "員工編號") + ": " + object.getString("employeid"));
                            map.put("employename", Common.toLanguage("員工姓名") + ": " + object.getString("employename"));
                            data.add(map);

                            break;
                        case "employename":

                            map.put("employeid", Common.toLanguage("員工編號") + ": " + object.getString("employeid"));
                            map.put("employename", Common.toLanguage("員工姓名") + ": " + object.getString("employename"));
                            data.add(map);

                            break;
                        case "departid":

                            map.put("departid", Common.toLanguage("部門編號") + ": " + object.getString("departid"));
                            map.put("departname", Common.toLanguage("部門名稱") + ": " + object.getString("departname"));
                            data.add(map);

                            break;
                        case "shift":

                            map.put("shiftid", Common.toLanguage("班別編號") + ": " + object.getString("shiftid"));
                            map.put("shiftname", Common.toLanguage("班別名稱") + ": " + object.getString("shiftname"));
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

    void setListViewAdapter(int page1) {

        final int page = page1;
        String[] dataArray = {};

        switch (type){
            case "employeid":
                dataArray = new String[] {"employeid","employename"};
                break;
            case "employename":
                dataArray = new String[] {"employeid","employename"};
                break;
            case "departid":
                dataArray = new String[] {"departid","departname"};
                break;
            case "shift":
                dataArray = new String[] {"shiftid","shiftname"};
                break;
            default:
                break;
        }

        int layout = 0;
        int[] views = new int[] {};

        switch (line){
            case "2":
                layout = R.layout.list_cell_for_two_lines;
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

        ArrayList<HashMap<String,String>> dic = new ArrayList<>();
        int max = page * 10 + 10;
        if(data.size() < max){
            max = data.size();
        }
        for(int i = page * 10 ;i < max; i++){
            dic.add(data.get(i));
        }

        final ArrayList<HashMap<String,String>> dd = dic;

        da = new SimpleAdapter(this,dd,layout,dataArray,views) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //do something
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        switch (type){
                            case "employeid":
                                if (data.get(position).get("employename").toString().split(" ").length > 1){
                                    b.putString("employename",data.get(position).get("employename").toString().split(" ")[1]);
                                }
                                if(data.get(position).get("employeid").toString().split(" ").length > 1){

                                    b.putString("employeid",data.get(position).get("employeid").toString().split(" ")[1]);
                                }

                                break;
                            case "employename":
                                if (data.get(position).get("employename").toString().split(" ").length > 1){
                                    b.putString("employename",data.get(position).get("employename").toString().split(" ")[1]);
                                }
                                if(data.get(position).get("employeid").toString().split(" ").length > 1){

                                b.putString("employeid",data.get(position).get("employeid").toString().split(" ")[1]);
                                }
                                break;
                            case "departid":
                                if (data.get(position).get("departid").toString().split(" ").length > 1){
                                    b.putString("departid",data.get(position).get("departid").toString().split(" ")[1]);
                                }
                                if(data.get(position).get("departname").toString().split(" ").length > 1){

                                    b.putString("departname",data.get(position).get("departname").toString().split(" ")[1]);
                                }
                                break;
                            case "shift":
                                if (data.get(position).get("shiftid").toString().split(" ").length > 1){
                                    b.putString("shiftid",data.get(position).get("shiftid").toString().split(" ")[1]);
                                }
                                if(data.get(position).get("shiftname").toString().split(" ").length > 1){

                                    b.putString("shiftname",data.get(position).get("shiftname").toString().split(" ")[1]);
                                }
                            break;
                            default:
                                break;
                        }
                        intent.putExtras(b);
                        setResult(RESULT_OK, intent);
                        Search_View.this.finish();
                    }
                });
                return v;
            }
        };
        listView.setAdapter(da);

        sb.delete(0, sb.length());
        sb.append(page+1).append("/").append(TotPage).append(Common.toLanguage("頁"));
        tvCount.setText(sb.toString());
    }

    public void onSearch(View view){
        keyword = editem.getText().toString().trim();
        searchData();
    }

    public void onPageUp(View view){
        if(current -1 < 0)return;
        current -= 1;
        setListViewAdapter(current);
    }

    public void onPageDown(View view){
        if(current +1 >= TotPage)return;
        current += 1;
        setListViewAdapter(current);
    }

    public void onBack(View view) {
        finish();
    }
}
