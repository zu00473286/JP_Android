package com.example.apologize.js_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class APP_JPServerLogIn extends BaseActivity {
    private EditText edIP, edPort, edName, edUser, edPW;
    private Button btnOK;
    SharedPreferences sp;
    ProgressDialog dialog = null;
    boolean isConnet = false;
    boolean isCheckConnet = false;
    boolean updateOK = false;
    DBSearch dbUpdate = new DBSearch();
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__jpserver_log_in);
        findviews();
    }

    void findviews() {
        edIP = (EditText) findViewById(R.id.SRV_IP);
        edPort = (EditText) findViewById(R.id.SRV_Port);
        edName = (EditText) findViewById(R.id.SRV_Name);
        edUser = (EditText) findViewById(R.id.SRV_Uer);
        edPW = (EditText) findViewById(R.id.SRV_PW);
        btnOK = (Button) findViewById(R.id.SRV_OK);
    }

    @Override
    protected void onStart() {
        Log.d("heyAPP_JPServerLogIn", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
        sp = getSharedPreferences("JPSVRLogIn", MODE_PRIVATE);
        if (sp.getString("jpip", "").equals("")) //若為空代表第一次進入，帶入預設值
        {
            edIP.setText("61.218.134.132");
            edPort.setText("80");
            edName.setText("JPDEMO");
            edUser.setText("JBSDEMO");
            edPW.setText("JBSDEMO");
        } else {
            edIP.setText(sp.getString("jpip", "61.218.134.132"));
            edPort.setText(sp.getString("jpport", "80"));
            edName.setText(sp.getString("jpname", "JPDEMO"));
            edUser.setText(sp.getString("jpuser", "JBSDEMO"));
            edPW.setText(sp.getString("jppw", "JBSDEMO"));

            //判斷是否由App_Menu開啟
            //小心!!getIntent()，如果此頁面並不是由其他頁面intent啟動，取回值也不會是NULL
            //小心!!因為Intent沒有綁Bundle，所以getBoolean()會NullPointerException
            //若fromMenu=true，代表是由App_Menu開啟，不啟動自動連線
            Intent intent = getIntent();
            if (intent.getBooleanExtra("JPReLogIn", true))
                onOK(null);
            else
                return;
        }
    }

    public void onOK(View view) {
        if (!NetWork.CheckNetWorkState(this)) {
            return;
        }

        btnOK.setEnabled(false);

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "正在連線中", "請稍候...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //抓系統參數設定
                    String sql = "http://" + edIP.getText().toString() + ":" + edPort.getText() + "/JSON_WEB.aspx?"
                            + "&function=" + URLEncoder.encode("get", "UTF-8")
                            + "&user=" + URLEncoder.encode(edUser.getText().toString(), "UTF-8")
                            + "&pw=" + URLEncoder.encode(edPW.getText().toString(), "UTF-8")
                            + "&name=" + URLEncoder.encode(edName.getText().toString(), "UTF-8")
                            + "&parameter=" + URLEncoder.encode("[{}]", "UTF-8")
                            + "&sqlcmd=" + URLEncoder.encode("select * from systemset", "UTF-8");//

                    String sqlCheck = "http://" + edIP.getText().toString() + ":" + edPort.getText() + "/JSON_WEB.aspx?"
                            + "&function=" + URLEncoder.encode("get", "UTF-8")
                            + "&user=" + URLEncoder.encode(edUser.getText().toString(), "UTF-8")
                            + "&pw=" + URLEncoder.encode(edPW.getText().toString(), "UTF-8")
                            + "&name=" + URLEncoder.encode(edName.getText().toString(), "UTF-8")
                            + "&parameter=" + URLEncoder.encode("[{}]", "UTF-8")
                            + "&sqlcmd=" + URLEncoder.encode("select * from cardset", "UTF-8");//

                    final DBSearch dbSearch = new DBSearch();
                    final DBSearch dbCheckSearch = new DBSearch();
                    final DBSearch db = new DBSearch();
                    isConnet = false;
                    //使用者登入 檢查WEB版本
                    updateOK = false;

                    if (dbSearch.ServerLogIn(sql) == DBSearch.Result.fund && SaveCommondPreferences(dbSearch)) {
                        isConnet = true;
                        if (dbUpdate.JPUpdate() == DBSearch.Result.update) {

                            updateOK = true;
                        }
                    }
                    if (dbCheckSearch.ServerLogIn(sqlCheck) == DBSearch.Result.fund && SaveCommondPreferences2(dbCheckSearch)) {
                        isCheckConnet = true;
                    }
                    if (db.JPSearchForGet("select absenceno,absencename from absence") == DBSearch.Result.fund && SaveLeave(db)) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        btnOK.setEnabled(true);
                        if (updateOK == false) {
                            try {
                                DialogeError("APP版本跟Web版本不符，更新失敗!", dbUpdate.dateArray.getJSONObject(0).getString("message"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                DialogeError("APP版本跟Web版本不符，更新失敗!", "更新失敗");
                            }
                            return;
                        }
                        if (isConnet && isCheckConnet) {
                            Intent intent = new Intent(APP_JPServerLogIn.this, APP_CheckIn.class);
                            startActivity(intent);
                            finish();
                        } else {
                            btnOK.setEnabled(true);
                            new AlertDialog.Builder(APP_JPServerLogIn.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("錯誤")
                                    .setMessage("伺服器連線錯誤")
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

    boolean SaveLeave(DBSearch dbSearch) {
        Common.leave = new ArrayList<>();
        Common.leaveno = new ArrayList<>();
        try {
            for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                JSONObject o = dbSearch.dateArray.getJSONObject(i);
                Common.leave.add(o.getString("absencename"));
                Common.leaveno.add(o.getString("absenceno"));
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    boolean SaveCommondPreferences(DBSearch dbSearch) {
        //將連線成功的資訊存入SharedPreferences
        sp.edit().putString("jpip", edIP.getText().toString())
                .putString("jpport", edPort.getText().toString())
                .putString("jpname", edName.getText().toString())
                .putString("jpuser", edUser.getText().toString())
                .putString("jppw", edPW.getText().toString())
                .commit();

        //將連線成功的資訊存入Common
        Common.jpip = edIP.getText().toString();
        Common.jpport = edPort.getText().toString();
        Common.jpdb_name = edName.getText().toString();
        Common.jpdb_user = edUser.getText().toString();
        Common.jpdb_pw = edPW.getText().toString();
        Common.jpcn = "http://" + Common.jpip + ":" + Common.jpport + "/JSON_WEB.aspx?";
        Common.cnjp = "http://" + Common.jpip + ":" + Common.jpport + "/JP_WEB.aspx?";

        //撈出的系統參數存入Share
        try {
            Common.checkrange = dbSearch.dateArray.getJSONObject(0).get("checkrange").toString();
            Common.latitude = dbSearch.dateArray.getJSONObject(0).get("longitude").toString();
            Common.longitude = dbSearch.dateArray.getJSONObject(0).get("latitude").toString();

            Log.d("heyServerLogIn", "checkrange:" + Common.checkrange);
            Log.d("heyServerLogIn", "latitude:" + Common.latitude);
            Log.d("heyServerLogIn", "longitude:" + Common.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    boolean SaveCommondPreferences2(DBSearch dbSearch) {
        //撈出的系統參數存入Share
        try {
            Common.ontimeD = dbSearch.dateArray.getJSONObject(0).get("ontimeD").toString();
            Common.offtimeD = dbSearch.dateArray.getJSONObject(0).get("offtimeD").toString();
            Common.restime1D = dbSearch.dateArray.getJSONObject(0).get("restime1D").toString();
            Common.restime2D = dbSearch.dateArray.getJSONObject(0).get("restime2D").toString();
            Common.addontimeD = dbSearch.dateArray.getJSONObject(0).get("addontimeD").toString();
            Common.addofftimeD = dbSearch.dateArray.getJSONObject(0).get("addofftimeD").toString();
            Common.gooutD = dbSearch.dateArray.getJSONObject(0).get("gooutD").toString();
            Common.gobackD = dbSearch.dateArray.getJSONObject(0).get("gobackD").toString();

            Log.d("heyServerLogIn", "ontimeD:" + Common.ontimeD);
            Log.d("heyServerLogIn", "offtimeD:" + Common.offtimeD);
            Log.d("heyServerLogIn", "restime1D:" + Common.restime1D);
            Log.d("heyServerLogIn", "restime2D:" + Common.restime2D);
            Log.d("heyServerLogIn", "addontimeD:" + Common.addontimeD);
            Log.d("heyServerLogIn", "addofftimeD:" + Common.addofftimeD);
            Log.d("heyServerLogIn", "gooutD:" + Common.gooutD);
            Log.d("heyServerLogIn", "gobackD:" + Common.gobackD);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void onClear(View view) {
        edIP.setText("");
        edPort.setText("");
        edName.setText("");
        edUser.setText("");
        edPW.setText("");
    }
}
