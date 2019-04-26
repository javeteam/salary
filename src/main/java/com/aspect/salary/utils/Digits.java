package com.aspect.salary.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Digits {
    public static float round (float number, int decimalPlaces){
        BigDecimal bd = new BigDecimal(number).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        return bd.floatValue();
    }
}
