package com.app.expensetracker.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.app.expensetracker.R;
import com.app.expensetracker.model.Months;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static List<Integer> listProd_inc = new ArrayList<>();
    public static List<Integer> listProd1_exp = new ArrayList<>();

    public static void ShowToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static List<Months> getAllMonths() {
        List<Months> months = new ArrayList<>();
        months.clear();
        months.add(new Months("Jan", "1"));
        months.add(new Months("Feb", "2"));
        months.add(new Months("Mar", "3"));
        months.add(new Months("Apr", "4"));
        months.add(new Months("May", "5"));
        months.add(new Months("Jun", "6"));
        months.add(new Months("Jul", "7"));
        months.add(new Months("Aug", "8"));
        months.add(new Months("Sep", "9"));
        months.add(new Months("Oct", "10"));
        months.add(new Months("Nov", "11"));
        months.add(new Months("Dec", "12"));
        return months;
    }

    public static String getCurrentMonthsIndex(String month) {
        String monthindex = "";
        switch (month) {
            case "Jan":
                return monthindex = "01";
            case "Feb":
                return monthindex = "02";
            case "Mar":
                return monthindex = "03";
            case "Apr":
                return monthindex = "04";
            case "May":
                return monthindex = "05";
            case "Jun":
                return monthindex = "06";
            case "Jul":
                return monthindex = "07";
            case "Aug":
                return monthindex = "08";
            case "Sep":
                return monthindex = "09";
            case "Oct":
                return monthindex = "10";
            case "Nov":
                return monthindex = "11";
            case "Dec":
                return monthindex = "12";
        }
        return monthindex;
    }


    public static String getCurrentMonths(String index) {
        String monthindex = "";
        switch (index) {
            case "01":
                return monthindex = "Jan";
            case "02":
                return monthindex = "Feb";
            case "03":
                return monthindex = "Mar";
            case "04":
                return monthindex = "Apr";
            case "05":
                return monthindex = "May";
            case "06":
                return monthindex = "Jun";
            case "07":
                return monthindex = "Jul";
            case "08":
                return monthindex = "Aug";
            case "09":
                return monthindex = "Sep";
            case "10":
                return monthindex = "Oct";
            case "11":
                return monthindex = "Nov";
            case "12":
                return monthindex = "Dec";
        }
        return monthindex;
    }


    public static String getCurrentMonths() {
        String months = "";
        Calendar cal = Calendar.getInstance();
        months = new SimpleDateFormat("MMM").format(cal.getTime());
        Log.d("CurrentMonth", months);
        // System.out.println(new SimpleDateFormat("MMM").format(cal.getTime()));
        return months;
    }

    public static String getCurrentYear() {
        String months = "";
        Calendar cal = Calendar.getInstance();
        months = new SimpleDateFormat("YYYY").format(cal.getTime());
        Log.d("CurrentYear", months);
        //System.out.println(new SimpleDateFormat("MMM").format(cal.getTime()));
        return months;
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getFormattedDate(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            //SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);  // 20120821
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }


    public static void openNavDrawer(int id, final Context mContext) {
        if (id == R.id.nav_import) {
            //Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean getIsExcelReadDone(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        boolean flag = preferences.getBoolean("msg", false);
        return flag;
    }

    public static void setIsExcelReadDone(Context mContext, boolean flag) {
        SharedPreferences preferences = mContext.getSharedPreferences("Expensetracker", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("msg", flag);
        editor.apply();

    }


    public static void shareAll(Context context, File file) {
        //String title = heading;
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent shareIntent = ShareCompat.IntentBuilder.from((Activity) context)
                .setType("*/*")
                .setStream(uri)
                .getIntent();
        context.startActivity(Intent.createChooser(shareIntent, "Share Using"));

    }

}
