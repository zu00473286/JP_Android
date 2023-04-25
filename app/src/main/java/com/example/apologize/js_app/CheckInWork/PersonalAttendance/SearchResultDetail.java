package com.example.apologize.js_app.CheckInWork.PersonalAttendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
//import com.example.apologize.js_app.R;
import com.example.namespace.R;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by leo on 2017/11/8.
 */

public class SearchResultDetail extends BaseActivity {

    TextView plus,mins;
    int zoom = 17;
    LinearLayout piclayout;
    ProgressDialog dialog;
    Handler myHandler = new Handler();
    boolean connectOK;
    DBSearch dbSearch;
    WebView map;
    TextView l1,l2,l3,l4,l5,l6,l8,t1,t2,t3,t4,t5,t6,l9;
    ImageView t8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_result_detail);
        findView();
    }

    void findView(){

        l1 = (TextView)findViewById(R.id.l1);
        l2 = (TextView)findViewById(R.id.l2);
        l3 = (TextView)findViewById(R.id.l3);
        l4 = (TextView)findViewById(R.id.l4);
        l5 = (TextView)findViewById(R.id.l5);
        l6 = (TextView)findViewById(R.id.l6);
        l8 = (TextView)findViewById(R.id.l8);
        l9 = (TextView)findViewById(R.id.l9);
        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);
        t4 = (TextView)findViewById(R.id.t4);
        t5 = (TextView)findViewById(R.id.t5);
        t6 = (TextView)findViewById(R.id.t6);
        map = (WebView) findViewById(R.id.map);
        plus = (TextView)findViewById(R.id.plus);
        mins = (TextView)findViewById(R.id.mins);

        piclayout = (LinearLayout)findViewById(R.id.piclayout);

        t8 = (ImageView)findViewById(R.id.t8);

        plus.setOnClickListener(plusZoom);
        mins.setOnClickListener(minsZoom);

        didSetLanguage();
    }

    View.OnClickListener plusZoom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(zoom + 1 <= 20){
                zoom += 1;
                reloadMap();
            }
        }
    };

    View.OnClickListener minsZoom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(zoom - 1 >= 13){
                zoom -= 1;
                reloadMap();
            }
        }
    };

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        LinearLayout l = (LinearLayout) findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);
        

        l1.setText(Common.toLanguage("員工編號") + ":");
        l2.setText(Common.toLanguage("出勤日期") + ":");
        l3.setText(Common.toLanguage("出勤時間") + ":");
        l4.setText(Common.toLanguage("打卡類型") + ":");
        l5.setText(Common.toLanguage("經度") + ":");
        l6.setText(Common.toLanguage("緯度") + ":");
        l8.setText(Common.toLanguage("照片") + ":");
        l9.setText(Common.toLanguage("位置") + ":");


        getData();
    }

    void getData(){

        dbSearch = new DBSearch();
        dbSearch.PutParameter("bid",getIntent().getExtras().get("bid").toString());

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPSearchForGet("select employeid,longitude,latitude,nddate as date,dakatime as time,discern as type,checkrange,bid from gpsinfo where bid=@bid") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connectOK){

                            try{
                                if(dbSearch.dateArray.length() > 0){
                                    JSONObject object = dbSearch.dateArray.getJSONObject(0);
                                    t1.setText(object.get("employeid").toString());
                                    t2.setText(object.get("date").toString());
                                    t3.setText(object.get("time").toString());
                                    if(object.get("type").toString().equals(Common.ontimeD)){
                                        t4.setText(Common.toLanguage("上班打卡"));
                                    }else if(object.get("type").toString().equals(Common.offtimeD)){
                                        t4.setText(Common.toLanguage("下班打卡"));
                                    }else if(object.get("type").toString().equals(Common.restime1D)){
                                        t4.setText(Common.toLanguage("休息時間1起"));
                                    }else if(object.get("type").toString().equals(Common.restime2D)){
                                        t4.setText(Common.toLanguage("休息時間1終"));
                                    }else if(object.get("type").toString().equals(Common.addontimeD)){
                                        t4.setText(Common.toLanguage("加班上班"));
                                    }else if(object.get("type").toString().equals(Common.addofftimeD)){
                                        t4.setText(Common.toLanguage("加班下班"));
                                    }
                                    t5.setText(object.get("longitude").toString());
                                    t6.setText(object.get("latitude").toString());

                                    reloadMap();
                                }
                            }catch(JSONException e){
                                Log.d("error",e.getMessage());
                            }
                            getPic();

                        }
                    }
                });

            }
        }).start();
    }

    public float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    void reloadMap(){

        map.setLayoutParams(new LinearLayout.LayoutParams(map.getWidth(),map.getWidth()));

        String w = String.format("%.0f",map.getWidth() / getDensity(SearchResultDetail.this));

        String urlString = "https://maps.googleapis.com/maps/api/staticmap?center={" + t6.getText().toString() + "," + t5.getText().toString() + "}&zoom=" + zoom + "&size=" + w + "x" + w + "&markers=color:red%7Clabel:S%7C" + t6.getText().toString() + ","+ t5.getText().toString() + "&key=" + Common.APIKEY;
        String data = "<html><head></head><body><img src=" + urlString + " width='" + w + "' height='" + w + "'></body></html>";

        Log.d("data",data);

        map.getSettings().setJavaScriptEnabled(true);
        map.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
    }

    void getPic(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if(dbSearch.JPGetPic("select photo from gpsinfo where bid=@bid") == DBSearch.Result.fund){
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(connectOK){

                            if (dbSearch.dateArray != null) {
                                try {
                                    String str = dbSearch.dateArray.getJSONObject(0).getString("pic");
                                    char[] hex = str.toCharArray();
                                    int length = hex.length / 2;
                                    byte[] rawData = new byte[length];
                                    for (int i = 0; i < length; i++) {
                                        //先將hex資料轉10進位數值
                                        int high = Character.digit(hex[i * 2], 16);
                                        int low = Character.digit(hex[i * 2 + 1], 16);
                                        //將第一個值的二進位值左平移4位,ex: 00001000 => 10000000 (8=>128)
                                        //然後與第二個值的二進位值作聯集ex: 10000000 | 00001100 => 10001100 (137)
                                        int value = (high << 4) | low;
                                        //與FFFFFFFF作補集
                                        if (value > 127)
                                            value -= 256;
                                        //最後轉回byte就OK
                                        rawData[i] = (byte) value;
                                    }
                                    //取得圖片，但圖片可能太小，以下程式依piclayout(android:layout_width="match_parent)在螢幕的寬來調整
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
                                    int w = bitmap.getWidth();//取得圖片寬
                                    int h = bitmap.getHeight();//取得圖片高
                                    int layoutWidth = piclayout.getWidth();//取得piclayout寬
                                    float fw = (float) layoutWidth / w;//算出放大縮小比例給寬
                                    float fh = fw;//比例給高
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(fw, fh);
                                    Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);//0,0 = 起始座標
                                    t8.setImageBitmap(newbitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("Error", "pic" + e.toString());
                                }

                            }

                        }
                    }
                });

            }
        }).start();
    }
}
