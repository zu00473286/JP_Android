package com.example.apologize.js_app.CheckInWork.Authority;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.R;

/**
 * Created by leo on 2018/2/25.
 */

public class CheckinAuthoritySetView extends BaseActivity {

    Handler myHandler = new Handler();
    CheckBox box1,box2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_set);
        findView();
    }

    void  findView(){

        box1 = (CheckBox)findViewById(R.id.box1);
        box2 = (CheckBox)findViewById(R.id.box2);

        ((ImageView)findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().getExtras().getString("check").equals("true")){
            box1.setChecked(true);
        }else{
            box1.setChecked(false);
        }

        if(getIntent().getExtras().getString("query").equals("true")){
            box2.setChecked(true);
        }else{
            box2.setChecked(false);
        }
    }

    public void OnConfirm(View view){
        final DBSearch dbSearch = new DBSearch();
        new Thread(new Runnable() {
            @Override
            public void run() {

                dbSearch.PutParameter("user", Common.jpuser_id);

                if(box1.isChecked()){
                    dbSearch.PutParameter("check","true");
                }else{
                    dbSearch.PutParameter("check","false");
                }

                if(box2.isChecked()){
                    dbSearch.PutParameter("query","true");
                }else{
                    dbSearch.PutParameter("query","false");
                }

                dbSearch.JPModifyData("update employee set dakacheck=@check , dakaquery=@query where webid=@user");

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogeSuccess("","更新成功");
                    }
                });

            }
        }).start();
    }
}
