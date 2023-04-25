package com.example.apologize.js_app.Base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.apologize.js_app.R;

import java.io.IOException;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 2017/11/16.
 */

public class DrawBoard extends BaseActivity {
    public static final int menuClear = 1;
    ProgressDialog dialog;
    Handler myHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_board);
        findviews();

    }
    SignDrawl signDrawl;
    Button clear,save;
    void  findviews(){
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        signDrawl = new SignDrawl(this);
        signDrawl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linear.addView(signDrawl);


        clear = (Button)findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signDrawl.clear();
            }
        });

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signDrawl.getTouched() == false){
                    ToastError("尚未簽名");
                    return;
                }

                save.setEnabled(false);
                clear.setEnabled(false);

                dialog = new ProgressDialog(DrawBoard.this);
                dialog = ProgressDialog.show(DrawBoard.this,"", "請稍候...");

                new Thread(new Runnable() {
                    Bitmap resizedBitmap;
                    Bitmap bitmap = null;
                    @Override
                    public void run() {

                        try {
                            bitmap = signDrawl.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //43670
                        int oldwidth = bitmap.getWidth();
                        int oldheight = bitmap.getHeight();
                        double scaleWidth = 1.1;
                        double scaleHeight = 1.1;
                        do{
                            scaleWidth -= 0.1;
                            scaleHeight -= 0.1;
                            Matrix matrix = new Matrix();
                            matrix.postScale((float)scaleWidth, (float)scaleHeight);

                            Log.d("doint","create the new Bitmap object");
                            // create the new Bitmap object
                            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, oldwidth,
                                    oldheight, matrix, true);
                        }while(bitmapToHex(resizedBitmap).length > 43670);

                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(dialog != null)
                                    dialog.dismiss();
                                Intent i = new Intent();
                                Bundle b = new Bundle();
                                b.putCharArray("photo",bitmapToHex(resizedBitmap));
                                i.putExtras(b);
                                setResult(RESULT_OK,i);
                                finish();
                            }
                        });

                    }
                }).start();

            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.add 參數: (int groupId, int itemId, int order, int titleRes)
        menu.add(0, menuClear, 0, "清除");
        return true;
    }

}