package com.example.apologize.js_app.Base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.apologize.js_app.R;

public class SendReport_OpenHeader extends BaseActivity {
    private ListView listView;
    private SimpleAdapter da;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_open_header);
        findviews();
    }

   void findviews(){
       try{
           listView = (ListView)findViewById(R.id.listView);

           da = new SimpleAdapter(this,SendReport.Header, R.layout.open_hearder,new String[]{"pnnote","pnname","pntel","pnfax","pnaddr"},new int[]{R.id.textView,R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4});
           listView.setAdapter(da);
           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent intent = new Intent();
                   intent.putExtra("data",(position+1));
                   setResult(RESULT_OK, intent);
                   SendReport_OpenHeader.this.finish();
               }
           });
       }catch (Exception ex){
           ex.printStackTrace();
       }
   }
}
