package com.example.apologize.js_app.Base;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.apologize.js_app.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SendReport_OpenFooder extends BaseActivity {
    private ListView listView;
    private SimpleAdapter da;
    ArrayList<HashMap<String,String>>data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_open_fooder);
        findviews();
    }

    void findviews(){
        try{
            listView = (ListView)findViewById(R.id.listView);

            if(getIntent().getStringExtra("kind").equals("單行")){
                data.add(SendReport.Footer.get(0));
                data.add(SendReport.Footer.get(1));
                data.add(SendReport.Footer.get(2));
                data.add(SendReport.Footer.get(3));
                data.add(SendReport.Footer.get(4));
                data.add(SendReport.Footer.get(10));
            }else {
                data.add(SendReport.Footer.get(5));
                data.add(SendReport.Footer.get(6));
                data.add(SendReport.Footer.get(7));
                data.add(SendReport.Footer.get(8));
                data.add(SendReport.Footer.get(9));
                data.add(SendReport.Footer.get(10));
            }
            da = new SimpleAdapter(this,data, R.layout.open_fooder,new String[]{"taname","tamemo"},new int[]{R.id.textView,R.id.textView1});
            listView.setAdapter(da);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("data", (position + 1));
                    setResult(RESULT_OK, intent);
                    data.clear();
                    SendReport_OpenFooder.this.finish();
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
