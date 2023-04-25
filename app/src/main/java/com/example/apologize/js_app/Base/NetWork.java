package com.example.apologize.js_app.Base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.apologize.js_app.R;

/**
 * Created by Apologize on 2015/12/3.
 */
public class NetWork {
    public static boolean CheckNetWorkState(Context context)
    {
        /*所有可用網路連線資訊都儲存在ConnectivityManager實體中，程式中只要使用getSystemService(Context.CONNECTIVITY_SERVICE)方法即可取得ConnectivityManager實體。
          之後可使用getActiveNetworkInfo()方法取得目前作用中的網路資訊，當傳回值不為null表示有可用的網路，就可進一步檢查網路連線狀態。*/
        ConnectivityManager cm = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //isConnected=>網路是否連線 ；isAvailable=>網路是否可以使用
        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            ShowErrorDialog(context);
            return false;
        }
        else
            return true;
    }

    private static void ShowErrorDialog(Context context)
    {
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.error)
                .setTitle("錯誤")
                .setMessage("無網路服務")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
}
