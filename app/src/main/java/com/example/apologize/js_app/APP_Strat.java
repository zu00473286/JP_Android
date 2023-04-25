package com.example.apologize.js_app;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;


import com.example.namespace.R;


public class APP_Strat extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appstrat);
    }

    @Override
    protected void onStart() {
        Log.d("heyAPP_Strat", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
        if(this.isNetConnection)
        {
            new CountDownTimer(1000,1000){
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(APP_Strat.this,APP_JPServerLogIn.class);
                    startActivity(intent);
                    finish();
                }
            }.start();
        }
    }
}
