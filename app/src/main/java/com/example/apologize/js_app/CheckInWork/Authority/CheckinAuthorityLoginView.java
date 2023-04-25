package com.example.apologize.js_app.CheckInWork.Authority;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apologize.js_app.APP_CheckIn;
import com.example.apologize.js_app.APP_JPMenu;
import com.example.apologize.js_app.APP_JPServerLogIn;
import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.NetWork;
import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by leo on 2018/2/25.
 */

public class CheckinAuthorityLoginView extends BaseActivity {
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
        setContentView(R.layout.activity_authority_check_in);
        findviews();
    }

    void findviews(){
        CHKIN_ID = (EditText)findViewById(R.id.CHKIN_ID);
        CHKIN_PW = (EditText)findViewById(R.id.CHKIN_PW);
        btnOK = (Button)findViewById(R.id.CHKIN_OK);
        btnCHKIN_Clear = (Button)findViewById(R.id.CHKIN_Clear);

        ((TextView)findViewById(R.id.l1)).setText("登入帳號:");
        ((TextView)findViewById(R.id.l2)).setText("登入密碼:");

        ((ImageView)findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CHKIN_ID.setText("BM");
        CHKIN_ID.setEnabled(false);
    }

    public void onOK(View view){
        if(CHKIN_ID.getText().toString().trim().equals("") || CHKIN_PW.getText().toString().trim().equals(""))
        {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.error)
                    .setTitle("錯誤")
                    .setMessage("請輸入管理者密碼")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return;
        }

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this,"打卡登入中", "請稍候...");//ff44570aca8241914870afbc310cdb85
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //抓使用者參數設定
                    String str = "select scno from scrit where scname=@account and scpass=@password";
                    DBSearch dbSearch = new DBSearch();
                    dbSearch.PutParameter("account", CHKIN_ID.getText().toString().trim());
                    dbSearch.PutParameter("password", CHKIN_PW.getText().toString().trim());
                    connetOK = false;
                    if(dbSearch.JPSearchForGet(str) == DBSearch.Result.fund)
                    {
                        connetOK = true;
                    }
                } catch (Exception  e) {
                    e.printStackTrace();
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnOK.setEnabled(true);
                        Log.d("hey", "connectOK:" + connetOK);
                        if(connetOK)
                        {
                            GetAuthority();
                        }
                        else
                        {
                            dialog.dismiss();
                            new AlertDialog.Builder(CheckinAuthorityLoginView.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("錯誤")
                                    .setMessage("輸入密碼錯誤")
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

    public  void  GetAuthority(){
        final DBSearch dbSearch = new DBSearch();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //抓使用者參數設定
                    String str = "select dakacheck,dakaquery from employee where employeid=@account";
                    dbSearch.PutParameter("account", Common.jpuser_empid);
                    connetOK = false;
                    if(dbSearch.JPSearchForGet(str) == DBSearch.Result.fund)
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
                        Log.d("hey", "connectOK:" + connetOK);
                        if(connetOK)
                        {
                            if(dbSearch.dateArray.length() > 0){
                                try{

                                    JSONObject o = dbSearch.dateArray.getJSONObject(0);
                                    Intent i = new Intent();
                                    i.setClass(CheckinAuthorityLoginView.this,CheckinAuthoritySetView.class);
                                    Bundle b = new Bundle();
                                    b.putString("check",o.getString("dakacheck"));
                                    b.putString("query",o.getString("dakaquery"));
                                    i.putExtras(b);
                                    startActivity(i);

                                }catch(JSONException e){
                                    e.printStackTrace();
                                    new AlertDialog.Builder(CheckinAuthorityLoginView.this)
                                            .setIcon(R.drawable.error)
                                            .setTitle("錯誤")
                                            .setMessage("資料格式錯誤")
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                }
                            }
                        }
                        else
                        {
                            new AlertDialog.Builder(CheckinAuthorityLoginView.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("錯誤")
                                    .setMessage("資料庫錯誤")
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

    public void  onClear(View view) {
        CHKIN_ID.setText("");
    }

}
