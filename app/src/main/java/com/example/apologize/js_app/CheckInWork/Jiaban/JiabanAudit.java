package com.example.apologize.js_app.CheckInWork.Jiaban;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.apologize.js_app.Base.BaseActivity;
import com.example.apologize.js_app.Base.Common;
import com.example.apologize.js_app.Base.DBSearch;
import com.example.apologize.js_app.Base.DrawBoard;
import com.example.apologize.js_app.Base.NetWork;
import com.example.namespace.R;
//import com.example.apologize.js_app.R;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by leo on 2017/11/15.
 */

public class JiabanAudit extends BaseActivity {

    StringBuilder sb;
    ProgressDialog dialog;
    Handler myHandler = new Handler();
    boolean connectOK;
    DBSearch dbSearch;
    TextView l1, l2, l3, l4, l5, l6, l7, t1, t2, t3, t4, t5, t6, t7;
    TextView b1;
    String Jid = "";
    String bendays = "";
    String leaveYear = "";
    String leaveYear2 = "";
    String ljtxday = "";
    String ljtxday2 = "";
    Toolbar bar;
    Bitmap resizedBitmap;
    BigDecimal Totbny;
    BigDecimal standHours = new BigDecimal(Common.standardhour);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiaban_audit);
        findView();
    }

    void findView() {

        l1 = (TextView) findViewById(R.id.l1);
        l2 = (TextView) findViewById(R.id.l2);
        l3 = (TextView) findViewById(R.id.l3);
        l4 = (TextView) findViewById(R.id.l4);
        l5 = (TextView) findViewById(R.id.l5);
        l6 = (TextView) findViewById(R.id.l6);
        l7 = (TextView) findViewById(R.id.l7);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);

        bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        registerForContextMenu(bar);

        b1 = (TextView) findViewById(R.id.b1);
        Jid = getIntent().getExtras().getString("Jid");

        didSetLanguage();
    }

    public void didSetLanguage() {

        ImageView title = (ImageView) findViewById(R.id.title);
        LinearLayout l = (LinearLayout) findViewById(R.id.pageLabel);
        title.setImageResource(R.drawable.checkintitle);
        l.setBackgroundResource(R.drawable.page);

        l1.setText(Common.toLanguage("員工編號") + ":");
        l2.setText(Common.toLanguage("員工姓名") + ":");
        l3.setText(Common.toLanguage("加班類別") + ":");
        l4.setText(Common.toLanguage("加班日期") + ":");
        l5.setText(Common.toLanguage("時間起") + ":");
        l6.setText(Common.toLanguage("時間迄") + ":");
        l7.setText(Common.toLanguage("加班事由") + ":");
        b1.setText(Common.toLanguage("審核"));

        getData();
    }

    void getData() {

        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        sb.append("select * from Jiaban where Jid=@Jid");

        dbSearch.PutParameter("Jid", Jid);

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, "", "");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
                if (dbSearch.JPSearchForGet(sb.toString()) == DBSearch.Result.fund && setData()) {
                    connectOK = true;
                }

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        try {
                            if (dbSearch.dateArray != null) {
                                for (int i = 0; i < dbSearch.dateArray.length(); i++) {
                                    JSONObject object = dbSearch.dateArray.getJSONObject(i);
                                    t1.setText(object.get("Employeid").toString());
                                    t2.setText(object.get("Employename").toString());
                                    t3.setText(object.getString("Jbtype"));
                                    t4.setText(object.get("JbDate").toString());
                                    t5.setText(object.get("addtime1").toString());
                                    t6.setText(object.get("addtime2").toString());
                                    t7.setText(object.get("Memo").toString());
                                }
                            }else {
                                // handle null case
                            }

                        } catch (JSONException e) {
                            Log.d("error", e.getMessage());
                        }
                        if (connectOK) {


                        } else {
                            new AlertDialog.Builder(JiabanAudit.this)
                                    .setIcon(R.drawable.error)
                                    .setTitle("")
                                    .setMessage(Common.toLanguage("查無資料"))
                                    .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
//        menu.findItem(R.id.item1).setTitle(Common.toLanguage("上傳圖片"));
//        menu.findItem(R.id.item2).setTitle(Common.toLanguage("手繪簽名"));
        menu.findItem(R.id.item3).setTitle(Common.toLanguage("點擊審核"));
        menu.findItem(R.id.item4).setTitle(Common.toLanguage("不允許"));

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                //uploadpic選擇相片上傳
                Log.d("message", "getDat");
                Uri extras = data.getData();

                Log.d("message", "getdData");

                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), extras);

                    //43670
                    int oldwidth = photo.getWidth();
                    int oldheight = photo.getHeight();
                    double scaleWidth = 1.1;
                    double scaleHeight = 1.1;
                    do {
                        scaleWidth -= 0.1;
                        scaleHeight -= 0.1;
                        Matrix matrix = new Matrix();
                        matrix.postScale((float) scaleWidth, (float) scaleHeight);

                        Log.d("doint", "create the new Bitmap object");
                        // create the new Bitmap object
                        resizedBitmap = Bitmap.createBitmap(photo, 0, 0, oldwidth,
                                oldheight, matrix, true);
                    } while (bitmapToHex(resizedBitmap).length > 43670);

                    uploadData(bitmapToHex(resizedBitmap));

                } catch (Exception e) {
                    Log.d("error", e.getMessage());
                    ToastError("");
                }

            } else if (requestCode == 2) {

                Bundle b = data.getExtras();
                char[] bit = b.getCharArray("photo");
                uploadData(bit);

            }
        }
    }

    void uploadData(char[] charArray) {
        final char[] chars = charArray;
        dbSearch = new DBSearch();
        dbSearch.PutParameter("Jid", Jid);

        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, Common.toLanguage("更新中"), Common.toLanguage("請稍候") + "...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;

                ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("photo", "0x" + String.copyValueOf(chars)));

                if (dbSearch.PostToModify("update Jiaban set result='2'", list) == DBSearch.Result.modified) {
                    connectOK = true;
                }

                Log.d("message", dbSearch.data);
                if (connectOK) {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            new AlertDialog.Builder(JiabanAudit.this)
                                    .setIcon(R.drawable.success)
                                    .setTitle("")
                                    .setMessage(Common.toLanguage("更新成功"))
                                    .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    });
                } else {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            new AlertDialog.Builder(JiabanAudit.this)
                                    .setIcon(R.drawable.success)
                                    .setTitle("")
                                    .setMessage(Common.toLanguage("更新失敗"))
                                    .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    });
                }
            }
        }).start();
    }

    public static char[] bitmapToHex(Bitmap bitmap) {

        char[] result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).toCharArray();


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    void uploadPic() {

        Intent i = new Intent(Intent.ACTION_PICK, null);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(i, 1);

    }

    void sign() {

        Intent i = new Intent();
        i.setClass(JiabanAudit.this, DrawBoard.class);
        startActivityForResult(i, 2);
    }

    void clickToCheck() {
        if (!NetWork.CheckNetWorkState(this)) {
            ToastError(Common.toLanguage("無法連線"));
            return;
        }

        sb = new StringBuilder();
        dbSearch = new DBSearch();

        sb.append("update Jiaban set result='2' where Jid=@Jid");

        dbSearch.PutParameter("Jid", Jid);
        dbSearch.PutParameter("account", "BM");
        dialog = new ProgressDialog(this);
        dialog = ProgressDialog.show(this, Common.toLanguage("更新中"), Common.toLanguage("請稍候") + "...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                connectOK = false;
               // 先抓使用者參數設定的特休設定，
                //String str = "select JiabanSet,NianduBegin from scrit where scname=@account";
                //if (dbSearch.JPSearchForGet(str) == DBSearch.Result.fund && saveLeaveSet(dbSearch)) {
                    //更新假單&計算完特休
                    if (dbSearch.JPModifyData(sb.toString()) == DBSearch.Result.modified) {
                        connectOK = true;
                    }
                //}

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (connectOK) {
                            new AlertDialog.Builder(JiabanAudit.this)
                                    .setIcon(R.drawable.success)
                                    .setTitle("")
                                    .setMessage(Common.toLanguage("更新成功"))
                                    .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            if (t3.getText().toString().trim().equals("特休")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String str = "update Jiaban set result='0' where Jid=@Jid";
                                        dbSearch.ParameterClear();
                                        dbSearch.PutParameter("Jid", Jid);
                                        if (dbSearch.JPModifyData(str) == DBSearch.Result.modified) {
//                                            因特休失敗，假單更新回原本審核中
                                            Log.d("check", "connectOK");
                                        }
                                    }
                                }).start();
                                if(ljtxday.equals("")){
                                    new AlertDialog.Builder(JiabanAudit.this)
                                            .setIcon(R.drawable.error)
                                            .setTitle("")
                                            .setMessage(Common.toLanguage("更新失敗\n" +
                                                    "此員工特休未設定!"))
                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            }).show();
                                }else if(Double.parseDouble(Common.standardhour) == 0){
                                    new AlertDialog.Builder(JiabanAudit.this)
                                            .setIcon(R.drawable.error)
                                            .setTitle("")
                                            .setMessage(Common.toLanguage("更新失敗\n" +
                                                    "請確認該員工基本時數設定大於0，才能計算特休!"))
                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            }).show();
                                }else{
                                    new AlertDialog.Builder(JiabanAudit.this)
                                            .setIcon(R.drawable.error)
                                            .setTitle("")
                                            .setMessage(Common.toLanguage("更新失敗"))
                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            }).show();
                                }
                            } else {
                                new AlertDialog.Builder(JiabanAudit.this)
                                        .setIcon(R.drawable.error)
                                        .setTitle("")
                                        .setMessage(Common.toLanguage("更新失敗"))
                                        .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).show();
                            }
                        }
                    }
                });

            }
        }).start();
                            }

//    boolean saveLeaveSet(DBSearch dbSearch) {
//        JSONObject object;
//        try {
//            for (int i = 0; i < dbSearch.dateArray.length(); i++) {
//                object = dbSearch.dateArray.getJSONObject(i);
//                Common.leaveSet = object.get("leaveSet").toString();
//                Common.NianduBegin = object.get("NianduBegin").toString();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

//    String loadljtxday(DBSearch dbSearch) {
//        JSONObject object;
//        String tempLjtxday = "";
//        try {
//            for (int i = 0; i < dbSearch.dateArray.length(); i++) {
//                object = dbSearch.dateArray.getJSONObject(i);
//                tempLjtxday = object.get("ljtxday").toString();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return tempLjtxday;
//    }

                            void denied () {
                                if (!NetWork.CheckNetWorkState(this)) {
                                    ToastError(Common.toLanguage("無法連線"));
                                    return;
                                }

                                sb = new StringBuilder();
                                dbSearch = new DBSearch();

                                sb.append("update Jiaban set result='1' where Jid=@Jid");
                                dbSearch.PutParameter("Jid", Jid);

                                dialog = new ProgressDialog(this);
                                dialog = ProgressDialog.show(this, Common.toLanguage("更新中"), Common.toLanguage("請稍候") + "...");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        connectOK = false;
                                        if (dbSearch.JPModifyData(sb.toString()) == DBSearch.Result.modified) {
                                            connectOK = true;
                                        }

                                        myHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                if (connectOK) {

                                                    new AlertDialog.Builder(JiabanAudit.this)
                                                            .setIcon(R.drawable.success)
                                                            .setTitle("")
                                                            .setMessage(Common.toLanguage("更新成功"))
                                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    finish();
                                                                }
                                                            }).show();

                                                } else {
                                                    new AlertDialog.Builder(JiabanAudit.this)
                                                            .setIcon(R.drawable.error)
                                                            .setTitle("")
                                                            .setMessage(Common.toLanguage("更新失敗"))
                                                            .setPositiveButton(Common.toLanguage("確定"), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    finish();
                                                                }
                                                            }).show();
                                                }
                                            }
                                        });

                                    }
                                }).start();
                            }

                            @Override
                            public boolean onOptionsItemSelected (MenuItem item){
                                switch (item.getItemId()) {
//                                    case R.id.item1:
//                                        uploadPic();
//                                        break;
//                                    case R.id.item2:
//                                        sign();
//                                        break;
                                    case R.id.item3:
                                        clickToCheck();
                                        break;
                                    case R.id.item4:
                                        denied();
                                        break;
                                    default:
                                        break;
                                }
                                return super.onOptionsItemSelected(item);
                            }

                            boolean setData () {

                                return true;
                            }

//    boolean calcuLeave() {
//
//        String tLastYearDay = "";
//        String tNowLeaveYear = "";
//        leaveYear = t4.getText().toString().trim().substring(0, 3);
//        leaveYear2 = String.valueOf((Integer.parseInt(t4.getText().toString().trim().substring(0, 3)) + 1));
//        dbSearch = new DBSearch();
//        dbSearch.PutParameter("employeid", t1.getText().toString().trim());
//        dbSearch.PutParameter("absenceno", Common.absenceno);
//        dbSearch.PutParameter("udate", t4.getText().toString().trim().substring(0, 7));
//        dbSearch.PutParameter("leaveYear", leaveYear);
//        dbSearch.PutParameter("leaveYear2", leaveYear2);
//        String cmd = "";
//        try {
//                            region 抓取當年度&下一年度特休天數
//            if ((t4.getText().toString().trim().substring(0, 3) + Common.workday.substring(3, 7)).compareTo(t4.getText().toString().trim().substring(0, 7)) <= 0) {
//                cmd = "Select bendays from leave where employeid=@employeid and leaveYear=@leaveYear2";
//                if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
//                    JSONObject object;
//                    try {
//                        for (int i = 0; i < dbSearch.dateArray.length(); i++) {
//                            object = dbSearch.dateArray.getJSONObject(i);
//                            bendays = object.get("bendays").toString();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                }
//            } else {
//                cmd = "Select bendays from Jiaban where employeid=@employeid and leaveYear=@leaveYear";
//                if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
//                    JSONObject object;
//                    try {
//                        for (int i = 0; i < dbSearch.dateArray.length(); i++) {
//                            object = dbSearch.dateArray.getJSONObject(i);
//                            bendays = object.get("bendays").toString();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                }
//            }//endregion
//            region 判斷特休天數落在哪個年度區間
//            if (bendays.equals("3天")) {
//
//                DateTime workday = new DateTime(Integer.parseInt(Common.workday.substring(0, 3))
//                        , Integer.parseInt(Common.workday.substring(3, 5)), Integer.parseInt(Common.workday.substring(5, 7)), 0, 0);
//                DateTime lastYearDay = workday.plusMonths(6);
//                DateTime nowYearDay = workday.plusYears(1).plusDays(-1);
//                tLastYearDay = String.format("%03d", lastYearDay.getYear()) + String.format("%02d", lastYearDay.getMonthOfYear()) + String.format("%02d", lastYearDay.getDayOfMonth());
//                tNowLeaveYear = String.format("%03d", nowYearDay.getYear()) + String.format("%02d", nowYearDay.getMonthOfYear()) + String.format("%02d", nowYearDay.getDayOfMonth());
//                dbSearch.PutParameter("lastYearDay", tLastYearDay);
//                dbSearch.PutParameter("nowYearDay", tNowLeaveYear);
//            } else if (bendays.equals("7天")) {
//                tLastYearDay = String.format("%03d", Integer.parseInt(Common.workday.substring(0, 3)) + 1) + Common.workday.substring(3, 7);
//                tNowLeaveYear = String.format("%03d", Integer.parseInt(Common.workday.substring(0, 3)) + 2) + Common.workday.substring(3, 7);
//                dbSearch.PutParameter("lastYearDay", tLastYearDay);
//                dbSearch.PutParameter("nowYearDay", tNowLeaveYear);
//            } else {
//                if (t4.getText().toString().trim().substring(0, 7).compareTo(t4.getText().toString().trim().substring(0, 3) + Common.workday.substring(3, 7)) > 0) {
//                    tLastYearDay = t4.getText().toString().trim().substring(0, 3) + Common.workday.substring(3, 7);
//                    tNowLeaveYear = String.format("%03d", Integer.parseInt(t4.getText().toString().trim().substring(0, 3)) + 1) + Common.workday.substring(3, 7);
//                    dbSearch.PutParameter("lastYearDay", tLastYearDay);
//                    dbSearch.PutParameter("nowYearDay", tNowLeaveYear);
//                } else {
//                    tLastYearDay = String.format("%03d", Integer.parseInt(t4.getText().toString().trim().substring(0, 3)) - 1) + Common.workday.substring(3, 7);
//                    tNowLeaveYear = t4.getText().toString().trim().substring(0, 3) + Common.workday.substring(3, 7);
//                    dbSearch.PutParameter("lastYearDay", tLastYearDay);
//                    dbSearch.PutParameter("nowYearDay", tNowLeaveYear);
//                }
//            }//endregion
//            //region 會計年度制時，判斷請假時間屬於哪個年度
//            if (Common.leaveSet.equals("3") && Common.NianduBegin.length() == 4) {
//                String tNianduStart = "";
//                String tnianduEnd = "";
//                //請假時間在當年度起始以前，判定為上一個年度
//                if ((leaveYear + Common.NianduBegin).compareTo(t4.getText().toString().trim().substring(0, 7)) > 0) {
//                    tNianduStart = String.format("%03d", Integer.parseInt(leaveYear) - 1) + Common.NianduBegin;
//                    tnianduEnd = leaveYear + Common.NianduBegin;
//                    dbSearch.PutParameter("nianduStart", tNianduStart);
//                    dbSearch.PutParameter("nianduEnd", tnianduEnd);
//                }
//            }//endregion
//            //region 計算特休
//            if (t3.getText().toString().trim().equals("特休")) {
//                if (Double.parseDouble(Common.standardhour) == 0) {
//                    return false;
//                }
//                //會計年度抓取的日期範圍不同
//                if (Common.leaveSet.equals("3") && Common.NianduBegin.length() == 4) {
//                    //判斷請假日期的年度
//                    if ((leaveYear + Common.NianduBegin).compareTo(t4.getText().toString().trim().substring(0, 7)) > 0) {
//                        leaveYear = String.format("%03d", Integer.parseInt(leaveYear) - 1);
//                    }
//                    cmd = "Select ISNULL(Sum(QjCount),0) as QjCount from Qingjia where absenceno =@absenceno " +
//                            "and udate >= @nianduStart and yndate < @nianduEnd and employeid=@employeid and result = '2' ";
//                }
//                if (Common.leaveSet.equals("1")) {
//                    cmd = "Select ISNULL(Sum(QjCount),0) as QjCount from Qingjia Where absenceno = @absenceno and Substring(uDate,1,3)= substring(@udate,1,3)  and employeid =  @employeid and result = '2' ";
//                }
//                if (Common.leaveSet.equals("2")) {
//                    cmd = "Select ISNULL(Sum(QjCount),0) as QjCount from Qingjia Where absenceno = @absenceno and udate >= @lastYearDay " +
//                            " and udate <= @nowYearDay and employeid = @employeid and result = '2' ";
//                }
//                //取得目前年度所有特休通過的請假總時數
//                if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
//                    JSONObject object;
//                    try {
//                        for (int i = 0; i < dbSearch.dateArray.length(); i++) {
//                            object = dbSearch.dateArray.getJSONObject(i);
//                            Totbny = new BigDecimal(object.get("QjCount").toString());
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    Totbny = new BigDecimal("0");
//                }
//                cmd = "Select ljtxday from leave where employeid = @employeid and leaveYear = @leaveYear";
//                if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
//                    ljtxday = loadljtxday(dbSearch);
//                }
//                cmd = "Select ljtxday from leave where employeid = @employeid and leaveYear = @leaveYear2";
//                if (dbSearch.JPSearchForGet(cmd) == DBSearch.Result.fund) {
//                    ljtxday2 = loadljtxday(dbSearch);
//                }
//                //region 計算特休後更新
//                if (ljtxday.equals("")) {
//                    return false;
//                } else {
//                    BigDecimal tDay1 = new BigDecimal(ljtxday.substring(0, ljtxday.indexOf("天")));//抓特休天數
//                    BigDecimal tHours1 = tDay1.multiply(standHours);//天數換算小時
//                    BigDecimal tHours2 = new BigDecimal(ljtxday.substring(ljtxday.indexOf("天") + 1, ljtxday.indexOf("小")));//抓特休小時數
//                    BigDecimal tljtxday = tHours1.add(tHours2);//總特休時數
//                    tljtxday = tljtxday.subtract(Totbny);//總特休數扣掉已休天數
//                    String sol = String.format("%01d", (int) Math.floor(Totbny.doubleValue() / standHours.doubleValue())) + "天" + String.format("%01d", (int)(Totbny.doubleValue() % standHours.doubleValue())) + "小時";
//                    String jsol = String.format("%01d", (int) Math.floor(tljtxday.doubleValue() / standHours.doubleValue())) + "天" + String.format("%01d", (int)(tljtxday.doubleValue() % standHours.doubleValue())) + "小時";
//                    dbSearch.PutParameter("bnyxday", sol.trim());//本年度已休天數
//                    dbSearch.PutParameter("bnsyday", jsol.trim());//本年度剩餘特休天數
//
//                    cmd = "Update leave set bnyxday=@bnyxday,bnsyday=@bnsyday where employeid=@employeid and leaveYear=@leaveYear";
//                    if (dbSearch.JPModifyData(cmd) == DBSearch.Result.modified) {
//                        //如果有下年度的特休表則一起更新
//                        if (!"".equals(ljtxday2)) {
//                            cmd = "Update leave set bnyxday=@bnyxday,bnsyday=@bnsyday where employeid=@employeid and leaveYear=@leaveYear2";
//                            if (dbSearch.JPModifyData(cmd) == DBSearch.Result.modified) {
//                                return true;
//                            }
//                        }
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }//endregion
//
//            }//endregion
//            return true;//因為其他假別不需計算所以要通過
//        } catch (Exception e) {
//            return false;
                        }

