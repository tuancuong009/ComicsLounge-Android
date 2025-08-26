package com.comics.lounge.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

import static com.comics.lounge.conf.Constant.CURRENCY_SYMBOL;

public class NumberUtils {

    private static String getFormattedNumber(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("##,##0.00");
        return decimalFormat.format(value);
    }

    public static Double parseMoney(String strAmount) {
        return Double.parseDouble(strAmount
                .replace(CURRENCY_SYMBOL, "")
                .replace(",", ""));
    }

    public static Double parseMoney(EditText edtAmount) {
        return parseMoney(edtAmount.getText().toString());
    }

    public static Double parseMoney(TextView tvAmount) {
        return parseMoney(tvAmount.getText().toString());
    }

    public static String formatMoney(double amount) {
        return CURRENCY_SYMBOL + getFormattedNumber(amount);
    }

    public static String formatMoney(float amount) {
        return CURRENCY_SYMBOL + getFormattedNumber(amount);
    }

    public static String formatMoney(String strAmount) {
        return CURRENCY_SYMBOL + getFormattedNumber(parseMoney(strAmount));
    }

    public static double parseStringToDouble(String amount) {
        return Double.parseDouble(amount.replace(CURRENCY_SYMBOL, ""));
    }

    public static String removeZeroIf(EditText edtNumber) {
        return removeZeroIf(edtNumber.getText().toString());
    }

    //TODO: Test of remove zero method
    /*String number = NumberUtils.removeZeroIf("095585030012");
    Log.e("Number", number);*/
    public static String removeZeroIf(String number) {
        if (number.trim().startsWith("0")) {
            return number.trim().substring(0, 1).replaceFirst("0", "") + number.trim().substring(1);
        } else return number;
    }

    public static float pxFromDp(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
