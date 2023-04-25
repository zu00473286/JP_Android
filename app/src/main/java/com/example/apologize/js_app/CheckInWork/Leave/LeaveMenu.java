package com.example.apologize.js_app.CheckInWork.Leave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.CheckInWork.Authority.CheckinAuthorityLoginView;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leo on 2017/11/6.
 */

public class LeaveMenu extends BaseActivity{

    Button sub1,sub2,sub3;
    Handler myHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_menu);
        findViews();
        didSetLanguage();
    }

    void findViews(){

        sub1 = (Button)findViewById(R.id.Sub1);
        sub2 = (Button)findViewById(R.id.Sub2);
        sub3 = (Button)findViewById(R.id.Sub3);

        sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(LeaveMenu.this,LeaveRequest.class);
                startActivity(i);
            }
        });

        sub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final DBSearch db = new DBSearch();
                        db.PutParameter("user",Common.jpuser_empid);
                        db.JPSearchForGet("select dakacheck from employee where employeid=@user");
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(db.dateArray.length() > 0){
                                    try{
                                        JSONObject o = db.dateArray.getJSONObject(0);
                                        Intent i = new Intent();
                                        Bundle b = new Bundle();
                                        i.setClass(LeaveMenu.this,LeaveSearchCondition.class);
                                        if(o.getString("dakacheck").equals("true")){
                                            b.putString("personal","1");
                                        }else{
                                            b.putString("personal","0");
                                        }
                                        i.putExtras(b);
                                        startActivity(i);
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });


        sub3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DBSearch db = new DBSearch();

                db.PutParameter("employeid",Common.jpuser_empid);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.JPSearchForGet("select dakacheck from employee where employeid=@employeid");
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(db.dateArray.length() > 0){
                                    try{
                                        JSONObject o = db.dateArray.getJSONObject(0);
                                        if(o.getString("dakacheck").equals("true")){
                                            Intent i = new Intent();
                                            i.setClass(LeaveMenu.this,LeaveAuditCondition.class);
                                            startActivity(i);
                                        }else{
                                            new AlertDialog.Builder(LeaveMenu.this)
                                                    .setIcon(R.drawable.error)
                                                    .setTitle("")
                                                    .setMessage(Common.toLanguage("沒有權限"))
                                                    .setPositiveButton(Common.toLanguage("確認"), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    }).show();
                                        }
                                    }catch(JSONException e){
                                        new AlertDialog.Builder(LeaveMenu.this)
                                                .setIcon(R.drawable.error)
                                                .setTitle("")
                                                .setMessage(Common.toLanguage("沒有權限"))
                                                .setPositiveButton(Common.toLanguage("確認"), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        sub3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent();
                i.setClass(LeaveMenu.this,CheckinAuthorityLoginView.class);
                startActivity(i);
                return false;
            }
        });

    }

    void didSetLanguage(){
        ImageView title = (ImageView)findViewById(R.id.title);
        LinearLayout l = (LinearLayout)findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
            l.setBackgroundResource(R.drawable.page);

        sub1.setText(Common.toLanguage("請假申請"));
        sub2.setText(Common.toLanguage("請假查詢"));
        sub3.setText(Common.toLanguage("請假審核"));

    }


}
