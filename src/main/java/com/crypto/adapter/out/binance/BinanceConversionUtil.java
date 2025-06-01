package com.crypto.adapter.out.binance;

public class BinanceConversionUtil {

    public static String convertSymbolFormat(String symbol) {
        return symbol.replace("_", "");
    }

    public static String removeCurrency(String symbol) {
        return symbol.replace("USDT", "").replace("_","");
    }

}
