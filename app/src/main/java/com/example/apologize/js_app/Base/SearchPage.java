package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.apologize.js_app.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchPage extends BaseActivity {
    private ListView listView;
    private TextView tvCount,tvitem;
    private EditText editem;
    private SimpleAdapter da;
    ArrayList<HashMap<String,Object>> data;
    ArrayList<String> no_list,name_list,other_list;
    int TotCount,TotPage,current = 0;
    StringBuilder sb = new StringBuilder();
    private String[]sql;
    Intent intent;
    Bundle bundle;
    ProgressDialog dialog;
    Handler myHandler = new Handler();
    DBSearch dbSearch = new DBSearch();
    boolean connetOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        intent = getIntent();
        bundle = intent.getExtras();
        sql = bundle.getStringArray("sqlstr");
        findviews();
        searchData();
    }

    void findviews() {
        listView =  (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(click);
        tvCount = (TextView)findViewById(R.id.textView);
        tvitem = (TextView)findViewById(R.id.tvItem);
        editem = (EditText)findViewById(R.id.edItem);

        tvitem.setText(sql[2]);
        editem.setText(bundle.getString("values"));
    }

    void searchData(){
        if(!NetWork.CheckNetWorkState(this))return;
        connetOK = false;
        tvCount.setText("");
        sb.delete(0, sb.length());
        dialog =  new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "搜尋中", "請稍候...");

        if(editem.getText().equals(""))
            sb.append(sql[0]).append(" order by ").append(sql[1]);
        else
            sb.append(sql[0]).append(" where ").append(sql[1]).append(" like '%").append(editem.getText().toString().trim()).append("%'").append(" order by ").append(sql[1]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(dbSearch.SearchForGet(sb.toString())== DBSearch.Result.fund && getList()) {
                    connetOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if(connetOK){
                            current = 0;
                            setListViewAdapter(current);
                        }else{
                            new AlertDialog.Builder(SearchPage.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("錯誤")
                                    .setMessage("查無資料")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
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
        try {
            no_list = new ArrayList<>();
            name_list = new ArrayList<>();
            other_list = new ArrayList<>();
            for (int i=0;i < dbSearch.dateArray.length();i++)
            {
                object = dbSearch.dateArray.getJSONObject(i);
                no_list.add(object.getString("no"));
                name_list.add(object.getString("name"));
                other_list.add(object.getString("other"));
            }

            data = new ArrayList<>();
            TotCount = no_list.size();
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
        data.clear();

        for (int i=page*10;i< (page*10)+10;i++)
        {
            if(i >=TotCount )break;
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("no",no_list.get(i));
            hashMap.put("name",name_list.get(i));
            hashMap.put("other",other_list.get(i));
            data.add(hashMap);
        }

        da = new SimpleAdapter(this,data,R.layout.list_style,new String[]{"no","name","other"},new int[]{R.id.textView1,R.id.textView2,R.id.textView4})  {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (no_list.get(position+current*10).equals(""))
                    v.findViewById(R.id.lin1_1).setVisibility(View.GONE);
                else
                    v.findViewById(R.id.lin1_1).setVisibility(View.VISIBLE);
                if (name_list.get(position+current*10).equals(""))
                    v.findViewById(R.id.lin1_2).setVisibility(View.GONE);
                else
                    v.findViewById(R.id.lin1_2).setVisibility(View.VISIBLE);
                if (other_list.get(position+current*10).equals(""))
                    v.findViewById(R.id.lin1_3).setVisibility(View.GONE);
                else
                    v.findViewById(R.id.lin1_3).setVisibility(View.VISIBLE);
                return v;
            }
        };
        listView.setAdapter(da);

        sb.delete(0, sb.length());
        sb.append("共 ").append(TotCount).append(" 筆資料，目前 ").append(page+1).append("/").append(TotPage).append("頁");
        tvCount.setText(sb.toString());
    }

    public void onSearch(View view){
        if(editem.getText().toString().trim().equals("")){
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.error)
                    .setTitle("錯誤")
                    .setMessage("搜尋條件不可為空")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return;
        }
        searchData();
    }

    AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("data",((TextView)view.findViewById(R.id.textView1)).getText().toString().trim());
            intent.putExtra("data2",((TextView)view.findViewById(R.id.textView2)).getText().toString().trim());
            setResult(RESULT_OK,intent);
            SearchPage.this.finish();
        }
    };

    public void onPageUp(View view){
        if(current -1 < 0)return;
        setListViewAdapter(--current);
    }

    public void onPageDown(View view){
        if(current +1 >= TotPage)return;
        setListViewAdapter(++current);
    }

    public void onBack(View view) {
        finish();
    }
}
