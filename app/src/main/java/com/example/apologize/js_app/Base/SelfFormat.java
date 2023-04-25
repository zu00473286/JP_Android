package com.example.apologize.js_app.Base;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Apologize on 2016/8/29.
 */
public class SelfFormat extends DecimalFormat {
    public SelfFormat(String format){
        super(format);
        this.setRoundingMode(RoundingMode.HALF_UP);
    }
}
