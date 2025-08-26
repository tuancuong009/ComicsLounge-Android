package com.comics.lounge.utils;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatesUtils {

    public static String AppDateFormat(@Nullable String date, String pattern) {
        DateFormat apiDateFormate = new SimpleDateFormat(pattern);
        DateFormat appDateFormate = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            assert date != null;
            Date apiDate = apiDateFormate.parse(date);
            assert apiDate != null;
            return appDateFormate.format(apiDate);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String DateFormat(@Nullable String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = fmt.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
        return fmtOut.format(date);
    }

    public static List<Date> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.MONTH, 1);
        }
        dates.add(cal2.getTime());
        return dates;
    }

    public static boolean isBetweenCurrntMonth(String dateString1, String dateString2, Date currentDate) {
        boolean isTrueDate = false;
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat convertOrignalDateFormate = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat targetFormate = new SimpleDateFormat("MM-yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);

            String date1Str = convertOrignalDateFormate.format(date1);
            Date datemain1 = targetFormate.parse(date1Str);

            String date2Str = convertOrignalDateFormate.format(date2);
            Date datemain2 = targetFormate.parse(date2Str);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            String checkDateStr = convertOrignalDateFormate.format(currentDate);
            Date checkDate = targetFormate.parse(checkDateStr);

            if (checkDate.compareTo(datemain1) >= 0) {
                isTrueDate = checkDate.compareTo(datemain2) <= 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isTrueDate;
    }


    public static boolean validateEventClose(String dateString1, String dateString2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssaa");
        try {
            Date date1 = sdf.parse(dateString1);
            Date date2 = updateTimeTo6PM(sdf.parse(dateString2));
            if (date1.compareTo(date2) == 0) {
                return true;
            } else if (date1.compareTo(date2) < 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Date updateTimeTo6PM(Date endClose) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endClose);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    // convert string date to long date
    public static long strToLong(String strDate){
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            date = fm.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    // convert string to date
    public static Date strToDate(String strDate){
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            date = fm.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // format to new string date 'dd MMM\nEEE'
    public static String fmDate(String strDate){
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat fmNew = new SimpleDateFormat("dd MMM\nEEE", Locale.ENGLISH);
        try {
            date = fm.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fmNew.format(date);
    }

    // format to new string date 'dd MMMM yyyy - EEEE'
    public static String fmDate2(String strDate){
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat fmNew = new SimpleDateFormat("dd MMMM yyyy - EEEE", Locale.ENGLISH);
        try {
            date = fm.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fmNew.format(date);
    }
}
