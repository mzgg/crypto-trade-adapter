package com.crypto.adapter.out.binance;

import java.math.BigDecimal;

public class BinancePrecisionCalculator {

    public static int getDecimalPlaces(String stepSize) {
        BigDecimal step = new BigDecimal(stepSize);

        String plainString = step.stripTrailingZeros().toPlainString();

        int dotIndex = plainString.indexOf('.');
        if (dotIndex < 0) {
            return 0;
        }

        return plainString.length() - dotIndex - 1;
    }
}
