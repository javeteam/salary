package com.aspect.salary.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonUtils {
    public static float round (float number, int decimalPlaces){
        BigDecimal bd = new BigDecimal(number).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        return bd.floatValue();
    }

}
