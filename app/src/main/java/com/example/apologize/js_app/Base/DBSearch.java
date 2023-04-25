package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.apologize.js_app.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Apologize on 2015/6/18.
 */
public class  DBSearch {
    public enum Result{fund,notfund,tolong,modified,unmodified,inserted,uninsert,update,unupdate};
    public JSONArray dateArray = null;
    public String json = null;
    public Result getResult = Result.notfund;
    public String data = "";

    String sqlCmdTxt = "";

    private HashMap<String,Object>Parameter = new HashMap<>();
    public void PutParameter(String name,Object values){
        Parameter.put(name,values);
    }
    public void ParameterClear(){
        Parameter.clear();
    }
    String ParameterToString(){
        if(Parameter.size() == 0)return "";

        JSONObject js = new JSONObject(Parameter);
        return  "["+js.toString()+"]";
    }

    public Result SearchForGet(String sqltxt) {
        sqlCmdTxt = Common.cn+"&function=get&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(sqltxt))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public HashMap<String,Object> returnParameter(){
        return Parameter;
    }

    public Result ModifyData(String modifyStr) {
        sqlCmdTxt = Common.cn+"&function=modify&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        try {
            if(Connection(modifyStr) && dateArray.getJSONObject(0).getBoolean("result"))
                return this.getResult = Result.modified;
            else
                return this.getResult = Result.unmodified;
        } catch (JSONException e) {
            e.printStackTrace();
            return this.getResult = Result.unmodified;
        }
    }

    public Result PostToModify(String modifyStr,ArrayList<NameValuePair> params) {
        sqlCmdTxt = Common.cnjp+"&function=checkLeave&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        if(PostConnection(modifyStr,params) && data.length() == 0)
            return this.getResult = Result.modified;
        else
            return this.getResult = Result.unmodified;
    }

    public Result PostToCheckin(String modifyStr,ArrayList<NameValuePair> params) {
        sqlCmdTxt = Common.cnjp+"&function=checkin&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        if(PostConnection(modifyStr,params) && data.length() == 0)
            return this.getResult = Result.modified;
        else
            return this.getResult = Result.unmodified;
    }

    public Result JPpostCommand(String sqlCmd) {
        sqlCmdTxt = Common.cnjp+"&function=post&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cmd",sqlCmd));
        if(PostConnection("",params))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }

    public Result postCommand(String sqlCmd) {
        sqlCmdTxt = Common.cn+"&function=post&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cmd",sqlCmd));
        if(PostConnection("",params))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }

    public Result confirmPic(String type,String photo,String no,String date,String userno) {
        sqlCmdTxt = Common.cn+"&function=sign&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("type",type));
        params.add(new BasicNameValuePair("sign",photo));
        params.add(new BasicNameValuePair("no",no));
        params.add(new BasicNameValuePair("date",date));
        params.add(new BasicNameValuePair("userno",userno));
        if(PostConnection("",params))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }


    public Result InsertData(String modifyStr) {
        sqlCmdTxt = Common.cn+"&function=modify&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        Log.d("InsertInto",sqlCmdTxt);
        try {
            if(Connection(modifyStr) && dateArray.getJSONObject(0).getBoolean("result"))
                return this.getResult = Result.inserted;
            else
                return this.getResult = Result.uninsert;
        } catch (JSONException e) {
            e.printStackTrace();
            return this.getResult = Result.uninsert;
        }
    }

    public Result JPSearchForGet(String sqltxt) {
        sqlCmdTxt = Common.jpcn+"&function=get&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        if(!Connection(sqltxt))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result JPModifyData(String modifyStr) {
        sqlCmdTxt = Common.jpcn+"&function=modify&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        try {
            if(Connection(modifyStr) && dateArray.getJSONObject(0).getBoolean("result"))
                return this.getResult = Result.modified;
            else
                return this.getResult = Result.unmodified;
        } catch (JSONException e) {
            e.printStackTrace();
            return this.getResult = Result.unmodified;
        }
    }

    public Result JPInsertData(String modifyStr) {
        sqlCmdTxt = Common.cnjp+"&function=modify&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        try {
            if(Connection(modifyStr) && dateArray.getJSONObject(0).getBoolean("result"))
                return this.getResult = Result.inserted;
            else
                return this.getResult = Result.uninsert;
        } catch (JSONException e) {
            e.printStackTrace();
            return this.getResult = Result.uninsert;
        }
    }

    public String GetPKNo(String tableName,String pkNo,String date){
        sqlCmdTxt = Common.cn+"&function=get&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        String comtext = "";
        String searchDate = "";
        switch (Common.NoAdd)
        {
            case 1:
                searchDate = Common.DateToTWD(date);
                comtext = " Select top(1)" + pkNo + " from " + tableName + " where " + pkNo + " like '" + searchDate +"%' and Len(" + pkNo + ")=11 order by " + pkNo + " desc";
                break;
            case 2:
                searchDate =Common.DateToTWD(date).substring(5)+"00";
                comtext = " Select top(1)" + pkNo + " from " + tableName + " where " + pkNo + " like '" + searchDate +"%' and Len(" + pkNo + ")=11 order by " + pkNo + " desc";
                break;
            case 3:
                searchDate =Common.DateToUSD(date);
                comtext = " Select top(1)" + pkNo + " from " + tableName + " where " + pkNo + " like '" +searchDate +"%' and Len(" + pkNo + ")=12 order by " + pkNo + " desc";
                break;
            case 4:
                searchDate =Common.DateToUSD(date).substring(6)+"00";
                comtext = " Select top(1)" + pkNo + " from " + tableName + " where " + pkNo + " like '" + searchDate +"%' and Len(" + pkNo + ")=12 order by " + pkNo + " desc";
                break;
        }
        try{
            if(Connection(comtext) )
            {
                String pkmax = this.dateArray.getJSONObject(0).getString(pkNo);
                int BehindNum = Integer.parseInt(pkmax.substring(pkmax.length() - 4)) + 1 ;
                return pkmax.substring(0,pkmax.length()-4)+ String.format("%04d",BehindNum);
            }
            else
            {
                switch (Common.NoAdd){
                    case 1:
                        return searchDate + String.format("%04d", 1);
                    case 2:
                        return searchDate + String.format("%04d", 1);
                    case 3:
                        return searchDate + String.format("%04d", 1);
                    case 4:
                        return searchDate + String.format("%04d", 1);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
            return "";
        }
        return  "";
    }

    public Result GetPic(String modifyStr) {
        sqlCmdTxt = Common.cn+"&function=pic&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(modifyStr))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result JPGetPic(String modifyStr) {
        sqlCmdTxt = Common.cnjp+"&function=pic&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        if(!Connection(modifyStr))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result GetSalePrice(){
        sqlCmdTxt = Common.cn+"&function=getsaleprice&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(""))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result GetBShopPrice(){
        sqlCmdTxt = Common.cn+"&function=getbshopprice&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(""))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result GetCustChart(String kind,boolean hasSale,boolean hasRSale,boolean hasFinish){
        String getsale = "",getrsale="",getfinish="";
        if(hasSale)getsale="yes";
        if(hasRSale)getrsale="yes";
        if(hasFinish)getfinish="yes";
        sqlCmdTxt = Common.cn.replaceAll("JSON_WEB","custcost")+"&function="+kind+"&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw
                +"&getsale="+ getsale
                +"&getrsale="+ getrsale
                +"&getfinish="+ getfinish;
        if(Connection(""))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }

    public Result GetItemChart(String kind,boolean hasSale,boolean hasRSale,boolean hasFinish){
        String getsale = "",getrsale="",getfinish="";
        if(hasSale)getsale="yes";
        if(hasRSale)getrsale="yes";
        if(hasFinish)getfinish="yes";
        sqlCmdTxt = Common.cn.replaceAll("JSON_WEB","itemcost")+"&function="+kind+"&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw
                +"&getsale="+ getsale
                +"&getrsale="+ getrsale
                +"&getfinish="+ getfinish;
        if(Connection(""))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }

    public Result GetEmplChart(String kind,boolean hasSale,boolean hasRSale,boolean hasFinish){
        String getsale = "",getrsale="",getfinish="";
        if(hasSale)getsale="yes";
        if(hasRSale)getrsale="yes";
        if(hasFinish)getfinish="yes";
        sqlCmdTxt = Common.cn.replaceAll("JSON_WEB","emplcost")+"&function="+kind+"&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw
                +"&getsale="+ getsale
                +"&getrsale="+ getrsale
                +"&getfinish="+ getfinish;
        if(Connection(""))
            return this.getResult = Result.fund;
        else
            return this.getResult = Result.notfund;
    }

    public Result Update() {
        try{
            sqlCmdTxt = Common.cn.replaceAll("JSON_WEB","update")+"&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw+"&appvers="+ Common.Vers;
            if(Connection("") && this.dateArray.getJSONObject(0).getString("message").contains("成功"))
                 this.getResult = Result.update;
            else
                this.getResult = Result.unupdate;
        }catch (Exception ex){
            this.getResult = Result.unupdate;
        }finally {
            return this.getResult;
        }
    }
    public Result JPUpdate() {
        try{
            Log.d("","&appvers="+ Common.Vers);
            sqlCmdTxt = Common.jpcn.replaceAll("JSON_WEB","JPupdate")+"&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw+"&appvers="+ Common.Vers;
            if(Connection("") && this.dateArray.getJSONObject(0).getString("message").contains("成功"))
                this.getResult = Result.update;
            else
                this.getResult = Result.unupdate;
        }catch (Exception ex){
            this.getResult = Result.unupdate;
        }finally {
            return this.getResult;
        }
    }
    public Result ServerLogIn(String CN){
        try {
            URL Url = new URL(CN);
            Log.d("ServerLogin",Url.toString());
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.connect();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int lenght = 0;
            byte[]buff = new byte[1024];
            while ((lenght = in.read(buff)) != -1)
            {
                baos.write(buff,0,lenght);
            }
            byte[] bytes = baos.toByteArray();
            in.close();
            baos.close();

            if(bytes.length == 0)
                return Result.notfund;

            json = new String(bytes);
            dateArray = new JSONArray(json);
        }catch (JSONException e)
        {
            e.printStackTrace();
            return Result.notfund;
        }catch (Exception e) {
            e.printStackTrace();
            return Result.notfund;
        }
        return Result.fund;
    }

    public Result JPProcedureForResult(String ProcedureName){
        sqlCmdTxt = Common.jpcn+"&function=procedureforresult&name="+ Common.jpdb_name +"&user="+ Common.jpdb_user +"&pw="+ Common.jpdb_pw;
        if(!Connection(ProcedureName))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result ProcedureForResult(String ProcedureName){
        sqlCmdTxt = Common.cn+"&function=procedureforresult&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(ProcedureName))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result ProcedureForTable(String ProcedureName){
        sqlCmdTxt = Common.cn+"&function=procedurefortable&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!Connection(ProcedureName))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    public Result SearchForGetAll(String sqltxt) {
        sqlCmdTxt = Common.cn+"&function=get&name="+ Common.db_name +"&user="+ Common.db_user +"&pw="+ Common.db_pw;
        if(!ConnectionWithoutEncoderSqlcmd(sqltxt))
            return this.getResult = Result.notfund;
        else
            return this.getResult = Result.fund;
    }

    boolean ConnectionWithoutEncoderSqlcmd(String CN){
        try {//
            URL Url = new URL(sqlCmdTxt+"&parameter="+ URLEncoder.encode(ParameterToString(),"UTF-8")+ "&sqlcmd="+ URLEncoder.encode(CN, "UTF-8"));
            Log.d("heyconnectstr",Url.toString());
            Log.d("heyconnectstrlength",Url.toString().length()+"");
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            //連線網頁時間 單位毫秒
            conn.setConnectTimeout(10000);
            //網頁回傳時間 單位毫秒
            conn.setReadTimeout(10000);
            conn.connect();
            int I = conn.getResponseCode();
            //若setConnectTimeout & setReadTimeout發生，直接丟出錯誤，不執行以下程式碼
            //若用InputStream.read(Array[])，會導致回傳結果被截斷，因為返回數據流是持續的，所以不能一次性打包
            //所以用ByteArrayOutputStream，會在記憶體開一個虛擬SD，buff設定一次read()大小
            //baos.write()，buff由0->lenght寫入baos，避免寫入多餘byte
            InputStream in = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int lenght = 0;
            byte[]buff = new byte[1024];
            while ((lenght = in.read(buff)) != -1)
            {
                baos.write(buff,0,lenght);
            }
            byte[] bytes = baos.toByteArray();
            in.close();
            baos.close();

            if(bytes.length == 0)
                return false;

            json = new String(bytes);
            dateArray = new JSONArray(json);
        }catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    boolean Connection(String CN){
        try {
            URL Url = new URL(sqlCmdTxt+"&parameter="+ URLEncoder.encode(ParameterToString(),"UTF-8")+ "&sqlcmd="+ URLEncoder.encode(CN, "UTF-8"));
            Log.d("heyconnectstr",Url.toString());
            Log.d("heyconnectstrlength",Url.toString().length()+"");
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            //連線網頁時間 單位毫秒
            conn.setConnectTimeout(10000);
            //網頁回傳時間 單位毫秒
            conn.setReadTimeout(10000);
            conn.connect();
            int I = conn.getResponseCode();
            //若setConnectTimeout & setReadTimeout發生，直接丟出錯誤，不執行以下程式碼
            //若用InputStream.read(Array[])，會導致回傳結果被截斷，因為返回數據流是持續的，所以不能一次性打包
            //所以用ByteArrayOutputStream，會在記憶體開一個虛擬SD，buff設定一次read()大小
            //baos.write()，buff由0->lenght寫入baos，避免寫入多餘byte
            InputStream in = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int lenght = 0;
            byte[]buff = new byte[1024];
            while ((lenght = in.read(buff)) != -1)
            {
                baos.write(buff,0,lenght);
            }
            byte[] bytes = baos.toByteArray();
            in.close();
            baos.close();

            if(bytes.length == 0)
                return false;

            json = new String(bytes);
            dateArray = new JSONArray(json);

        }catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Result CheckItNo(String itno){
        DBSearch dbSearch = new DBSearch();
        dbSearch.PutParameter("itno",itno);
        return dbSearch.SearchForGet("select itno from item where itno=@itno");
    }

    public boolean PostConnection(String CN,ArrayList<NameValuePair> param){

        try{

            Log.d("connection post",sqlCmdTxt+"&parameter="+ URLEncoder.encode(ParameterToString(),"UTF-8")+ "&sqlcmd="+ URLEncoder.encode(CN, "UTF-8"));
            //建立HttpClient物件
            HttpClient httpClient = new DefaultHttpClient();
            //建立一個Post物件，並給予要連線的Url
            HttpPost httpPost = new HttpPost(sqlCmdTxt+"&parameter="+ URLEncoder.encode(ParameterToString(),"UTF-8")+ "&sqlcmd="+ URLEncoder.encode(CN, "UTF-8"));
            //建立一個ArrayList且需是NameValuePair，此ArrayList是用來傳送給Http server端的訊息
            List params = param;
            //發送Http Request，內容為params，且為UTF8格式
            httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            //接收Http Server的回應
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            //判斷Http Server是否回傳OK(200)
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                //將Post回傳的值轉為String，將轉回來的值轉為UTF8，否則若是中文會亂碼
                data = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                Log.d("result",data);
                dateArray = new JSONArray(data);
                return true;
            }else{
                data = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
                Log.d("result",data);
                dateArray = new JSONArray(data);
                return false;
            }

        }catch (IOException e) {
            // Log exception
            Log.d("cmd",param.toString());
            e.printStackTrace();
            return false;
        }catch(Exception e){
            Log.d("cmd",param.toString());
            e.printStackTrace();
            return false;
        }
    }
}
