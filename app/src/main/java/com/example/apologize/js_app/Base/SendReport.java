package com.example.apologize.js_app.Base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.apologize.js_app.R;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SendReport extends BaseActivity {
    EditText edMail,edHeader,edFooder;
    TextView emailTitle,tvPrintBom;
    Handler handler = new Handler();
    String peopleID,receiptID,receiptKind;
    String MaillSUBJECT;
    String SpNo,DateAc,DateAc1;
    String mail_cmdtxt;
    RadioButton rd1,rd2;
    public static ArrayList<HashMap<String,String>>Header = new ArrayList<>();
    public static ArrayList<HashMap<String,String>>Footer = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        findviews();
    }

    void findviews(){
        emailTitle = (TextView)findViewById(R.id.emailTitle);
        tvPrintBom = (TextView)findViewById(R.id.tvPrintBom);
        edMail = (EditText)findViewById(R.id.edMail);

        final Intent intent = getIntent();
        peopleID = intent.getStringExtra("peopleID");
        receiptID = intent.getStringExtra("receiptID");
        receiptKind = intent.getStringExtra("receiptKind");
        SpNo = intent.getStringExtra("spno");//客戶別應付帳款
        DateAc = intent.getStringExtra("dateac");//客戶別應付帳款
        DateAc1 = intent.getStringExtra("dateac1");//客戶別應付帳款

        rd1 = (RadioButton)findViewById(R.id.rd1);
        rd2 = (RadioButton)findViewById(R.id.rd2);
        edHeader = (EditText)findViewById(R.id.edHeader);
        edHeader.setKeyListener(null);
        edFooder = (EditText)findViewById(R.id.edFooter);
        edFooder.setKeyListener(null);

        edHeader.setText("第一組(長按開啟瀏覽)");
        edHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Header.size() == 0){
                    ToastError("查無資料");
                }
                Intent intent1 = new Intent(SendReport.this, SendReport_OpenHeader.class);
                startActivityForResult(intent1, 100);
                return true;
            }
        });
        edFooder.setText("第一組(長按開啟瀏覽)");
        edFooder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Footer.size() == 0){
                    ToastError("查無資料");
                }
                Intent intent1 = new Intent(SendReport.this, SendReport_OpenFooder.class);
                intent1.putExtra("kind","三行");
                startActivityForResult(intent1, 200);
                return true;
            }
        });

        switch (receiptKind){
            case "Quote":
                MaillSUBJECT = "報價單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                break;
            case "[Order]":
                MaillSUBJECT = "訂購單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                break;
            case "Sale":
                MaillSUBJECT = "銷貨單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                break;
            case "RSale":
                MaillSUBJECT = "退貨單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                break;
            case "FrmCust_Accb":
                MaillSUBJECT = "對帳單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                tvPrintBom.setText("列印明細");
                break;
            case "Finish":
                MaillSUBJECT = "完修單";
                mail_cmdtxt ="select cuemail as email from cust where cuno=@cuno";
                emailTitle.setText("客戶信箱：");
                tvPrintBom.setText("列印明細");
                break;


            case "FQuot":
                MaillSUBJECT = "詢價單";
                mail_cmdtxt ="select faemail as email from fact where fano=@cuno";
                emailTitle.setText("廠商信箱：");
                break;
            case "Ford":
                MaillSUBJECT = "採購單";
                mail_cmdtxt ="select faemail as email from fact where fano=@cuno";
                emailTitle.setText("廠商信箱：");
                break;
            case "BShop":
                MaillSUBJECT = "進貨單";
                mail_cmdtxt ="select faemail as email from fact where fano=@cuno";
                emailTitle.setText("廠商信箱：");
                rd2.setVisibility(View.INVISIBLE);
                break;
            case "RShop":
                MaillSUBJECT = "進出單";
                mail_cmdtxt ="select faemail as email from fact where fano=@cuno";
                emailTitle.setText("廠商信箱：");
                rd2.setVisibility(View.INVISIBLE);
                break;
            case "FrmFact_Accb":
                MaillSUBJECT = "對帳單";
                mail_cmdtxt ="select faemail as email from fact where fano=@cuno";
                emailTitle.setText("廠商信箱：");
                tvPrintBom.setText("列印明細");
                break;

        }

        SearchCustMail(peopleID);
    }
    boolean mial_getData = false;
    boolean usrcapt_getData = false;
    boolean rdFooter_getData = false;
    boolean rdHeader_getData = false;
    void SearchCustMail(String cuno) {
        if(!NetWork.CheckNetWorkState(this))return;
        final Dialog dialog = ProgressDialog.show(this, "資料抓取中", "請稍候");

        final DBSearch dbSearch_mail = new DBSearch();
        mial_getData = false;
        final DBSearch dbSearch_usrcapt = new DBSearch();
        usrcapt_getData = false;
        final DBSearch dbSearch_rdFooter = new DBSearch();
        rdFooter_getData = false;
        final DBSearch dbSearch_rdHeader = new DBSearch();
        rdHeader_getData = false;

        Header.clear();
        Footer.clear();

        dbSearch_mail.PutParameter("cuno",cuno);
        dbSearch_usrcapt.PutParameter("formname",receiptKind);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(dbSearch_mail.SearchForGet(mail_cmdtxt) == DBSearch.Result.fund)
                    mial_getData = true;
                if(dbSearch_usrcapt.SearchForGet("select * from usrcapt where formname =@formname") == DBSearch.Result.fund)
                    usrcapt_getData = true;
                if(dbSearch_rdHeader.SearchForGet("select * from pnthead") == DBSearch.Result.fund)
                    rdHeader_getData = true;
                if(dbSearch_rdFooter.SearchForGet("select * from tail") == DBSearch.Result.fund)
                    rdFooter_getData = true;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        try{
                            if(mial_getData) {
                                edMail.setText(dbSearch_mail.dateArray.getJSONObject(0).getString("email"));
                            }

                            if(rdHeader_getData){
                                for (int i=0;i<dbSearch_rdHeader.dateArray.length();i++){
                                    HashMap<String,String>item = new HashMap<String, String>();
                                    item.put("pnnote",dbSearch_rdHeader.dateArray.getJSONObject(i).getString("pnnote"));
                                    item.put("pnname",dbSearch_rdHeader.dateArray.getJSONObject(i).getString("pnname"));
                                    item.put("pntel",dbSearch_rdHeader.dateArray.getJSONObject(i).getString("pntel"));
                                    item.put("pnfax",dbSearch_rdHeader.dateArray.getJSONObject(i).getString("pnfax"));
                                    item.put("pnaddr",dbSearch_rdHeader.dateArray.getJSONObject(i).getString("pnaddr"));
                                    Header.add(item);
                                }
                                HashMap<String,String>item = new HashMap<String, String>();
                                item.put("pnnote","不列印");
                                item.put("pnname","");
                                item.put("pntel","");
                                item.put("pnfax","");
                                item.put("pnaddr","");
                                Header.add(item);
                            }

                            StringBuilder sb ;
                            if(rdFooter_getData){
                                for (int i=0;i<5;i++){
                                    HashMap<String,String>item = new HashMap<String, String>();
                                    item.put("taname",dbSearch_rdFooter.dateArray.getJSONObject(i).getString("taname"));
                                    item.put("tamemo",dbSearch_rdFooter.dateArray.getJSONObject(i).getString("tamemo"));
                                    Footer.add(item);
                                }
                                for (int i=5;i<dbSearch_rdFooter.dateArray.length();i=i+3){
                                    HashMap<String,String>item = new HashMap<String, String>();
                                    item.put("taname",dbSearch_rdFooter.dateArray.getJSONObject(i).getString("taname"));

                                    sb = new StringBuilder();
                                    sb.append(dbSearch_rdFooter.dateArray.getJSONObject(i).getString("tamemo")).append("\n");
                                    sb.append(dbSearch_rdFooter.dateArray.getJSONObject(i+1).getString("tamemo")).append("\n");
                                    sb.append(dbSearch_rdFooter.dateArray.getJSONObject(i + 2).getString("tamemo"));
                                    item.put("tamemo",sb.toString());
                                    Footer.add(item);
                                }
                                HashMap<String,String>item = new HashMap<String, String>();
                                item.put("taname","不列印");
                                item.put("tamemo","");
                                Footer.add(item);
                            }
                            String str;
                            if(usrcapt_getData){
                                for (int i=0;i<dbSearch_usrcapt.dateArray.length();i++){
                                    str = dbSearch_usrcapt.dateArray.getJSONObject(i).getString("usrvaluen");
                                    if(str.startsWith("rdHeader")){
                                        switch (str){
                                            case "rdHeader1":
                                                edHeader.setText("第一組(長按開啟瀏覽)");
                                                break;
                                            case "rdHeader2":
                                                edHeader.setText("第二組(長按開啟瀏覽)");
                                                break;
                                            case "rdHeader3":
                                                edHeader.setText("第三組(長按開啟瀏覽)");
                                                break;
                                            case "rdHeader4":
                                                edHeader.setText("第四組(長按開啟瀏覽)");
                                                break;
                                            case "rdHeader5":
                                                edHeader.setText("第五組(長按開啟瀏覽)");
                                                break;
                                            case "rdHeader6":
                                                edHeader.setText("不列印(長按開啟瀏覽)");
                                                break;
                                        }
                                    }

                                    if(str.startsWith("rdFooter")){
                                        switch (str){
                                            case "rdFooter1":
                                                edFooder.setText("第一組(長按開啟瀏覽)");
                                                break;
                                            case "rdFooter2":
                                                edFooder.setText("第二組(長按開啟瀏覽)");
                                                break;
                                            case "rdFooter3":
                                                edFooder.setText("第三組(長按開啟瀏覽)");
                                                break;
                                            case "rdFooter4":
                                                edFooder.setText("第四組(長按開啟瀏覽)");
                                                break;
                                            case "rdFooter5":
                                                edFooder.setText("第五組(長按開啟瀏覽)");
                                                break;
                                            case "rdFooter6":
                                                edFooder.setText("不列印(長按開啟瀏覽)");
                                                break;
                                        }
                                    }
                                }
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }

    String MakeLink() {
        //!!!! 因為訂單的列印預設(usrcapt)的KEY值是[Order]，但是[Order]無法直接當作網頁連結，所以要去除'['、']'
        receiptKind = receiptKind.replace("[","");
        receiptKind = receiptKind.replace("]","");
        //!!!! 由於應收付帳款撈資料複雜,所已另外
        String WebName = "Report";
        if(receiptKind.equals("FrmCust_Accb"))
            WebName = "CustAcc";
        if(receiptKind.equals("FrmFact_Accb"))
            WebName = "FactAcc";

        String bomprint = "fales";
        if(rd2.isChecked())
            bomprint = "true";

        try{
            DBSearch dbSearch = new DBSearch();
            dbSearch.PutParameter("peopleID",peopleID);
            dbSearch.PutParameter("receiptID",receiptID);
            dbSearch.PutParameter("SpNo",SpNo);
            dbSearch.PutParameter("DateAc",DateAc);
            dbSearch.PutParameter("DateAc1",DateAc1);
            String sqlCmdTxt = Common.cn.replaceAll("JSON_WEB",WebName)+"&function="+receiptKind+"&bomprint="+bomprint+"&name="+ Common.db_name+"&user="+Common.user_id
                    +"&header="+URLEncoder.encode(edHeader.getText().toString().replace("(長按開啟瀏覽)",""),"UTF-8")
                    +"&fooder="+URLEncoder.encode(edFooder.getText().toString().replace("(長按開啟瀏覽)", ""),"UTF-8");
            URL Url = new URL(sqlCmdTxt+"&parameter="+ URLEncoder.encode(dbSearch.ParameterToString(), "UTF-8")+ "&sqlcmd=");
            return Url.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public void ReportToMail(View view){
        if(edMail.getText().toString().trim().equals("")){
            Toast.makeText(SendReport.this,"客戶信箱不可為空",Toast.LENGTH_LONG).show();
        }

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + edMail.getText().toString().trim()));
            intent.putExtra(Intent.EXTRA_SUBJECT, new String(MaillSUBJECT.getBytes("UTF-8"), "UTF-8"));
            intent.putExtra(Intent.EXTRA_TEXT, MakeLink());
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void ReportToOthers(View view){
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, new String(MaillSUBJECT.getBytes("UTF-8"), "UTF-8"));
            intent.putExtra(Intent.EXTRA_TEXT, MakeLink());
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(Intent.createChooser(intent, "選用APP傳送"+MaillSUBJECT));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void ReportToPreview(View view){
        try {
            Uri uri = Uri.parse(MakeLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null) {
            switch (data.getIntExtra("data", 1)) {
                case 1:
                    edHeader.setText("第一組(長按開啟瀏覽)");
                    break;
                case 2:
                    edHeader.setText("第二組(長按開啟瀏覽)");
                    break;
                case 3:
                    edHeader.setText("第三組(長按開啟瀏覽)");
                    break;
                case 4:
                    edHeader.setText("第四組(長按開啟瀏覽)");
                    break;
                case 5:
                    edHeader.setText("第五組(長按開啟瀏覽)");
                    break;
                case 6:
                    edHeader.setText("不列印(長按開啟瀏覽)");
                    break;
            }
        }
        else if (requestCode == 200 && data != null){
            switch (data.getIntExtra("data", 1)) {
                case 1:
                    edFooder.setText("第一組(長按開啟瀏覽)");
                    break;
                case 2:
                    edFooder.setText("第二組(長按開啟瀏覽)");
                    break;
                case 3:
                    edFooder.setText("第三組(長按開啟瀏覽)");
                    break;
                case 4:
                    edFooder.setText("第四組(長按開啟瀏覽)");
                    break;
                case 5:
                    edFooder.setText("第五組(長按開啟瀏覽)");
                    break;
                case 6:
                    edFooder.setText("不列印(長按開啟瀏覽)");
                    break;
            }
        }
    }
}
