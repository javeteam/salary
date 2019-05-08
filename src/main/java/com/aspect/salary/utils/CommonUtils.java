package com.aspect.salary.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class CommonUtils {

    public static float roundValue(float number, int decimalPlaces){
        BigDecimal bd = new BigDecimal(number).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        return bd.floatValue();
    }

    public enum Weight {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }

    public enum Position {
        TR,
        QA,
        Sales,
        IT,
        PM,
        CEO,
        HR,
        VM,
        Other
    }

    public static String currencyFormatter (int value){
        String moneyValue = new StringBuilder(Integer.toString(value)).reverse().toString();
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < moneyValue.length(); i++){
            char ch = moneyValue.charAt(i);
            if(i != 0 && i % 3 == 0){
                result.append(" ");
            }
            result.append(ch);
        }
        return result.reverse().toString();
    }

}
