package com.example.apologize.js_app.CheckInWork;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
//import com.example.apologize.js_app.R;
import com.example.namespace.R;
public class SetCheckInButton extends BaseActivity {
    Switch 民國Switch,上班打卡Switch,下班打卡Switch,休息時間1起Switch,休息時間1終Switch,外出打卡Switch,歸來打卡Switch,加班上班Switch,加班下班Switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_check_in_button);
        findview();
    }

    protected void onStart() {
        Log.d("heySetCheckInButton", "Common.openingActivity=" + Common.openingActivity);
        super.onStart();
        Log.d("heySetCheckInButton", "民國TF:" + CheckInWorkMenu.民國TF);
        Log.d("heySetCheckInButton", "上班打卡TF:" + CheckInWorkMenu.上班打卡TF);
        Log.d("heySetCheckInButton", "下班打卡TF:" + CheckInWorkMenu.下班打卡TF);
        Log.d("heySetCheckInButton", "休息時間1起TF:" + CheckInWorkMenu.休息時間1起TF);
        Log.d("heySetCheckInButton", "休息時間1終TF:" + CheckInWorkMenu.休息時間1終TF);
        Log.d("heySetCheckInButton", "加班上班TF:" + CheckInWorkMenu.加班上班TF);
        Log.d("heySetCheckInButton", "加班下班TF:" + CheckInWorkMenu.加班下班TF);
        if (CheckInWorkMenu.民國TF == true) {
            民國Switch.setChecked(true);
        } else {
            民國Switch.setChecked(false);
        }
        if (CheckInWorkMenu.上班打卡TF == true) {
            上班打卡Switch.setChecked(true);
        } else {
            上班打卡Switch.setChecked(false);
        }
        if (CheckInWorkMenu.下班打卡TF == true) {
            下班打卡Switch.setChecked(true);
        } else {
            下班打卡Switch.setChecked(false);
        }
        if (CheckInWorkMenu.休息時間1起TF == true) {
            休息時間1起Switch.setChecked(true);
        } else {
            休息時間1起Switch.setChecked(false);
        }
        if (CheckInWorkMenu.休息時間1終TF == true) {
            休息時間1終Switch.setChecked(true);
        } else {
            休息時間1終Switch.setChecked(false);
        }
        if (CheckInWorkMenu.加班上班TF == true) {
            加班上班Switch.setChecked(true);
        } else {
            加班上班Switch.setChecked(false);
        }
        if (CheckInWorkMenu.加班下班TF == true) {
            加班下班Switch.setChecked(true);
        } else {
            加班下班Switch.setChecked(false);
        }

        民國Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.民國TF = true;
                    Toast.makeText(getApplicationContext(),"民國年顯示開啟", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.民國TF = false;
                    Toast.makeText(getApplicationContext(),"民國年顯示關閉", Toast.LENGTH_SHORT).show();
                }
            }
        });

        上班打卡Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.上班打卡TF = true;
                    Toast.makeText(getApplicationContext(),"上班打卡按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.上班打卡TF = false;
                    Toast.makeText(getApplicationContext(),"上班打卡按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });

        下班打卡Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.下班打卡TF = true;
                    Toast.makeText(getApplicationContext(),"下班打卡按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.下班打卡TF = false;
                    Toast.makeText(getApplicationContext(),"下班打卡按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });

        休息時間1起Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.休息時間1起TF = true;
                    Toast.makeText(getApplicationContext(),"休息時間1起按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.休息時間1起TF = false;
                    Toast.makeText(getApplicationContext(),"休息時間1起按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });

        休息時間1終Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.休息時間1終TF = true;
                    Toast.makeText(getApplicationContext(),"休息時間1終按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.休息時間1終TF = false;
                    Toast.makeText(getApplicationContext(),"休息時間1終按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });

        加班上班Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.加班上班TF = true;
                    Toast.makeText(getApplicationContext(),"加班上班按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.加班上班TF = false;
                    Toast.makeText(getApplicationContext(),"加班上班按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });

        加班下班Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckInWorkMenu.加班下班TF = true;
                    Toast.makeText(getApplicationContext(),"加班下班按鈕顯示", Toast.LENGTH_SHORT).show();
                } else {
                    CheckInWorkMenu.加班下班TF = false;
                    Toast.makeText(getApplicationContext(),"加班下班按鈕隱藏", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void findview() {
        民國Switch = (Switch)findViewById(R.id.民國Switch);
        上班打卡Switch = (Switch)findViewById(R.id.上班打卡Switch);
        下班打卡Switch = (Switch)findViewById(R.id.下班打卡Switch);
        休息時間1起Switch = (Switch)findViewById(R.id.休息時間1起Switch);
        休息時間1終Switch = (Switch)findViewById(R.id.休息時間1終Switch);
        外出打卡Switch = (Switch)findViewById(R.id.外出打卡Switch);
        歸來打卡Switch = (Switch)findViewById(R.id.歸來打卡Switch);
        加班上班Switch = (Switch)findViewById(R.id.加班上班Switch);
        加班下班Switch = (Switch)findViewById(R.id.加班下班Switch);
    }

    public void onOK(View view) {
        CheckInWorkMenu.callsp = false;
        finish();
    }
}