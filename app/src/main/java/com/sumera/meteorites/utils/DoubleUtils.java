package com.sumera.meteorites.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by martin on 08/09/16.
 */

public class DoubleUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
