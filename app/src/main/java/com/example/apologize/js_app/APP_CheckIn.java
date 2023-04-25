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
import android.widget.TextView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class APP_CheckIn extends BaseActivity {
    private EditText CHKIN_ID,CHKIN_PW;
    private Button btnOK,btnCHKIN_Clear;
    SharedPreferences sp;
    Handler myHandler = new Handler();
    ArrayList<String> employeid_list = new ArrayList<String>();
    ArrayList<String>employename_list = new ArrayList<String>();
    ArrayList<String>no_list = new ArrayList<String>();
    ArrayList<String>dakaphoto_list = new ArrayList<String>();
    ArrayList<String>dakalnglat_list = new ArrayList<String>();
    ProgressDialog dialog = null;
    boolean connetOK = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__check_in);
        findviews();
    }

    void findviews(){
        CHKIN_ID = (EditText)findViewById(R.id.CHKIN_ID);
        CHKIN_PW = (EditText)findViewById(R.id.CHKIN_PW);
        btnOK = (Button)findViewById(R.id.CHKIN_OK);
        btnCHKIN_Clear = (Button)findViewById(R.id.CHKIN_Clear);
        TextView AppVers = (TextView)findViewById(R.id.AppVers);
        AppVers.setText("APP版本 : "+Common.Vers);

        btnCHKIN_Clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(APP_CheckIn.this, APP_JPServerLogIn.class);
                intent.putExtra("JPReLogIn",false);
                startActivity(intent);
                finish();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d("heyAPP_CheckIn", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
        sp = getSharedPreferences("CheckIn",MODE_PRIVATE);
        if(!sp.getString("id","").equals("")) //若不為空代表已登入過，直接帶出帳號
        {
            CHKIN_ID.setText(sp.getString("id", ""));
        }
        if (!CHKIN_ID.getText().toString().trim().equals("")) {
            CHKIN_PW.requestFocus();
        }
    }

    public void onOK(View view){
        if(CHKIN_ID.getText().toString().trim().equals("") || CHKIN_PW.getText().toString().trim().equals(""))
        {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.error)
                    .setTitle("錯誤")
                    .setMessage("請輸入員工編號/密碼")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return;
        }

        if(!NetWork.CheckNetWorkState(APP_CheckIn.this)) {
            return;
        }

        btnOK.setEnabled(false);

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this,"打卡登入中", "請稍候...");//ff44570aca8241914870afbc310cdb85
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //抓使用者參數設定
                    String str = "select employeid,employename,no,dakaphoto,dakalnglat,workday,standardhour from employee where webid=@account and webpassword=@password";
                    DBSearch dbSearch = new DBSearch();
                    dbSearch.PutParameter("account", CHKIN_ID.getText().toString().trim());
                    dbSearch.PutParameter("password", CHKIN_PW.getText().toString().trim());
                    connetOK = false;
                    if(dbSearch.JPSearchForGet(str) == DBSearch.Result.fund && SaveSharedPreferences(dbSearch))
                    {
                        connetOK = true;
                    }

                } catch (Exception  e) {
                    e.printStackTrace();
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        btnOK.setEnabled(true);
                        Log.d("hey", "connectOK:" + connetOK);
                        if(connetOK)
                        {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("employeid_list", employeid_list);
                            bundle.putStringArrayList("employename_list", employename_list);
                            bundle.putStringArrayList("no_list", no_list);
                            bundle.putStringArrayList("dakaphoto_list", dakaphoto_list);
                            bundle.putStringArrayList("dakalnglat_list", dakalnglat_list);
                            intent.putExtras(bundle);
                            intent.setClass(APP_CheckIn.this,APP_JPMenu.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            btnOK.setEnabled(true);
                            new AlertDialog.Builder(APP_CheckIn.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("錯誤")
                                    .setMessage("輸入員工編號錯誤")
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

    boolean SaveSharedPreferences(DBSearch dbSearch) {
        sp.edit().putString("id", CHKIN_ID.getText().toString()).commit();

        Common.jpuser_id = CHKIN_ID.getText().toString().trim();

        employeid_list.clear();
        employename_list.clear();
        no_list.clear();
        dakaphoto_list.clear();
        dakalnglat_list.clear();
        JSONObject object;
        try {
            for (int i=0;i < dbSearch.dateArray.length();i++)
            {
                object = dbSearch.dateArray.getJSONObject(i);
                employeid_list.add(object.get("employeid").toString());
                employename_list.add(object.get("employename").toString());
                no_list.add(object.get("no").toString());
                dakaphoto_list.add(object.get("dakaphoto").toString());
                Common.jpuser_empid = employeid_list.get(0).toString();
                Common.jpuser_empname = employename_list.get(0).toString();
                Common.workday = object.get("workday").toString();
                Common.standardhour = object.get("standardhour").toString();
                if(object.get("dakaphoto").toString().toLowerCase().equals("true")){
                    Common.needPhoto = true;
                }else{
                    Common.needPhoto = false;
                }
                dakalnglat_list.add(object.get("dakalnglat").toString());
                if(object.get("dakalnglat").toString().toLowerCase().equals("true")){
                    Common.needLocation = true;
                }else{
                    Common.needLocation = false;
                }

            }
            Log.d("heyCheckIn", "employeid_list:" + employeid_list);
            Log.d("heyCheckIn", "employename_list:" + employename_list);
            Log.d("heyCheckIn", "no_list:" + no_list);
            Log.d("heyCheckIn", "dakaphoto_list:" + dakaphoto_list);
            Log.d("heyCheckIn", "dakalnglat_list:" + dakalnglat_list);
        } catch (JSONException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }

    public void  onClear(View view) {
        CHKIN_ID.setText("");
        CHKIN_PW.setText("");
    }
}
