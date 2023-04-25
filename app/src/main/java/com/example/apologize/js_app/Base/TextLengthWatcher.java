package com.example.apologize.js_app.Base;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Apologize on 2015/6/24.
 * 使用此類別來限制EDITEXT輸入長度
 * 解按照資料庫規則，小寫1byte、大小2byte
 */
public class TextLengthWatcher implements TextWatcher {

    private  int maxLength;// 儲存最大的字串長度
    private  int currentEnd = 0;// 儲存目前字串改變的結束位置，例如：abcdefg變成abcd1234efg，變化的結束位置就在索引8

    public TextLengthWatcher(final int maxLength){
        if(maxLength >=0)
            this.maxLength = maxLength;
        else
            this.maxLength = 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        currentEnd = start + count;// 取得變化的結束位置
    }

    @Override
    public void afterTextChanged(Editable s) {
        while (checkLength(s) > maxLength){// 若變化後的長度超過最大長度
            // 刪除最後變化的字元
            currentEnd--;
            s.delete(currentEnd,currentEnd+1);
        }
    }

    /**
     * 計算字串的長度
     *
     * @param c
     *            傳入字串
     *
     * @return 傳回字串長度
     */
    protected int checkLength(final CharSequence c){
        int len = 0;
        final int l = c.length();
        for (int i=0;i<l;i++){
            final char temp = c.charAt(i);
            if(temp >=0x20 && temp<=0x7E){// 字元值 32~126 是 ASCII 半形字元的範圍
                len++;
            }else {
                len+=2;// 非半形字元
            }
        }
        return len;
    }
}
