package com.example.apologize.js_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.CheckInWork.Attendance.EmployeAttendanceCondition;
import com.example.apologize.js_app.CheckInWork.Attendance.EmployeAttendance_Search;
import com.example.apologize.js_app.CheckInWork.Authority.CheckinAuthorityLoginView;
import com.example.apologize.js_app.CheckInWork.CheckInWorkMenu;
import com.example.apologize.js_app.CheckInWork.Jiaban.JiabanMenu;
import com.example.apologize.js_app.CheckInWork.Leave.LeaveMenu;
import com.example.apologize.js_app.CheckInWork.PersonalAttendance.SearchCondition;
import com.example.namespace.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class APP_JPMenu extends BaseActivity {
    private TextView RunningTextView;
    Handler handler = new Handler();
    String RTV = "";
    ArrayList<String> employeid_list,employename_list,no_list,dakaphoto_list,dakalnglat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__jpmenu);
        RunningTextView = (TextView)findViewById(R.id.RunningTextView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlString = "http://www.bm888.com.tw/get_marquee.php";
                HttpURLConnection connection = null;

                try {
                    // 初始化 URL
                    URL url = new URL(urlString);
                    // 取得連線物件
                    connection = (HttpURLConnection) url.openConnection();
                    // 設定 request timeout
                    connection.setReadTimeout(1500);
                    connection.setConnectTimeout(1500);
                    // 模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                    // 設定開啟自動轉址
                    connection.setInstanceFollowRedirects(true);

                    // 若要求回傳 200 OK 表示成功取得網頁內容
                    if( connection.getResponseCode() == HttpsURLConnection.HTTP_OK ){
                        // 讀取網頁內容
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

                        String tempStr;
                        StringBuffer stringBuffer = new StringBuffer();

                        while((tempStr = bufferedReader.readLine()) != null ) {
                            stringBuffer.append( tempStr );
                        }

                        bufferedReader.close();
                        inputStream.close();

                        // 取得網頁內容類型
                        String  mime = connection.getContentType();
                        boolean isMediaStream = false;

                        // 判斷是否為串流檔案
                        if(mime.indexOf("audio") == 0 || mime.indexOf("video") == 0 ){
                            isMediaStream = true;
                        }

                        // 網頁內容字串
                        String responseString = stringBuffer.toString();
                        RTV = responseString;
                        //RunningTextView.setText(responseString);
                        Log.d("CustView", "Result:" + responseString);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                RunningTextView.setText(RTV);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        findviews();

    }

    void findviews(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        employeid_list = bundle.getStringArrayList("employeid_list");
        employename_list = bundle.getStringArrayList("employename_list");
        no_list = bundle.getStringArrayList("no_list");
        dakaphoto_list = bundle.getStringArrayList("dakaphoto_list");
        Log.d("", String.valueOf(Common.needPhoto));
        dakalnglat_list = bundle.getStringArrayList("dakalnglat_list");

        findViewById(R.id.Sub3).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(APP_JPMenu.this, CheckinAuthorityLoginView.class);
                startActivity(intent);
                return false;
            }
        });
    }

    public void onPersonalAttendance(View v){
        Intent i = new Intent(APP_JPMenu.this, SearchCondition.class);
        startActivity(i);
    }

    public void onCheckIn(View view){
        Log.d("", String.valueOf(Common.needPhoto));
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("employeid_list", employeid_list);
        bundle.putStringArrayList("employename_list", employename_list);
        bundle.putStringArrayList("no_list", no_list);
        bundle.putStringArrayList("dakaphoto_list", dakaphoto_list);
        bundle.putStringArrayList("dakalnglat_list", dakalnglat_list);
        bundle.putBoolean("callsp", true);
        intent.putExtras(bundle);
        intent.setClass(APP_JPMenu.this,CheckInWorkMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        Log.d("heyAPP_JPMenu", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
    }

    public void onLogOut(View view) {
        Intent intent = new Intent(APP_JPMenu.this, APP_JPServerLogIn.class);
        intent.putExtra("JPReLogIn",false);
        startActivity(intent);
        finish();
    }

    public void onInvoicing(View view) {
//        Intent intent = new Intent(APP_JPMenu.this, APP_Menu.class);
//        startActivity(intent);
//        finish();
        return;
    }

    public void onAttendance(View view) {
        final DBSearch dbSearch = new DBSearch();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbSearch.PutParameter("user",Common.jpuser_id);

                dbSearch.JPSearchForGet("select dakaquery from employee where webid=@user");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(dbSearch.dateArray.length() > 0){
                            try{
                                JSONObject o = dbSearch.dateArray.getJSONObject(0);
                                if(o.getString("dakaquery").toLowerCase().equals("true")){
                                   Intent i = new Intent();
                                   i.setClass(APP_JPMenu.this,EmployeAttendanceCondition.class);
                                   startActivity(i);
                                }else{
                                    DialogeError("","無權限查詢所有員工出缺勤");
                                }
                            }catch(JSONException e){
                                DialogeError("","無權限查詢所有員工出缺勤");
                                e.printStackTrace();
                            }
                        }else{
                            DialogeError("","無權限查詢所有員工出缺勤");
                        }
                    }
                });
            }
        }).start();

    }

    public void onLeave(View view) {
        Intent intent = new Intent(APP_JPMenu.this, LeaveMenu.class);
        startActivity(intent);
    }
    public void onJiaban(View view) {
        Intent intent = new Intent(APP_JPMenu.this, JiabanMenu.class);
        startActivity(intent);
    }
    public void onExit(View view) {
        finish();
    }
}
