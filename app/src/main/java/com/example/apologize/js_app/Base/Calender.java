package com.example.apologize.js_app.Base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
//import com.example.apologize.js_app.R;
import com.example.namespace.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Calender extends BaseActivity {

    private CalendarView calendar;
    private String selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);
        calendar = (CalendarView)findViewById(R.id.calendarView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(!bundle.getString("date").equals("")) {
            try {
                selectDate =intent.getStringExtra("date");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                java.util.Date d = sdf.parse(ToUSDdate(selectDate));
                calendar.setDate(d.getTime());
            } catch (ParseException e) {
                Log.d("Error", "item:" + e.toString());
            }
        }

        calendar.setOnDateChangeListener(listener);
    }

    CalendarView.OnDateChangeListener listener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            String y = ""+year;
            String m = ""+(month+1);
            String d = ""+dayOfMonth;
            if(Common.user_date ==1) y = ""+(year-1911);
            if(month+1 < 10) m = "0"+m;
            if(dayOfMonth < 10) d = "0"+d;
            selectDate = y + "/"+m+"/"+d;
        }
    };

    public void onConfirm(View view){
        Intent intent = new Intent();
        intent.putExtra("date", selectDate);
        setResult(RESULT_OK, intent);
        Calender.this.finish();
    }

    public void onReCall(View view){
        setResult(RESULT_CANCELED);
        Calender.this.finish();
    }

    public String ToUSDdate(String date){
        if(date.length() == 10)return  date;//因為有加2000/01/01
        String year = ""+(Integer.valueOf(date.substring(0, 3))+1911);
        Log.d("Error", date.substring(3));
        return year+date.substring(3);
    }
}
