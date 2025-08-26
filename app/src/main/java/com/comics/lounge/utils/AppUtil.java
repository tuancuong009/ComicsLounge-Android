package com.comics.lounge.utils;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.google.android.material.color.utilities.Contrast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GAURAV on 26-12-19
 */

public class AppUtil {
    static Dialog dialog;

    public static void disableBt(View view){
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.bg_bt_disable);
    }
    public static void enableBt(View view){
        view.setEnabled(true);
        view.setBackgroundResource(R.drawable.bg_button);
    }
    public static boolean isBlank(EditText editText, String message) {
        String content = editText.getText().toString();
        if (content.trim().equals("")) {
            //setSnackMessage(message);
            editText.setError(message);
            editText.requestFocus();
            return true;
        }
        return false;
    }
    public static void focusKeyboard(EditText edt, Context context) {
        edt.setFocusableInTouchMode(true);
        edt.requestFocus();
        WindowCompat.getInsetsController(((Activity) context).getWindow(), edt).show(WindowInsetsCompat.Type.ime());
        edt.setSelection(edt.getText().length());
    }
    public static boolean edtBlank(TextInputEditText editText, TextInputLayout ip, String message) {
        String content = editText.getText().toString();
        if (content.trim().equals("")) {
            //setSnackMessage(message);
            ip.setError(message);
            editText.setBackgroundResource(R.drawable.bg_edt_error);
            return true;
        }
        return false;
    }

    public static boolean isValidPwd(String pwd) {
        String EMAIL_PATTERN = "((?=.*[a-zA-Z])(?=.*?[0-9]).{6,20})";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isAlphabetAllowed(String str) {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("[a-zA-Z ]+")));
    }

    public static String getBuildVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            //return pInfo.versionCode;
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    // show keyboard
    public static void showSoftKeyboard(View view, Context context) {
        view.requestFocus();
        WindowCompat.getInsetsController(((Activity) context).getWindow(), view).show(WindowInsetsCompat.Type.ime());
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    // format to new datetime
    public static String fmNewDateTime(String dateTime) {
        String dayFm = "", dayOfWeekFm = "", monthFm = "";
        SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat fmDay = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat fmDayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat fmMonth = new SimpleDateFormat("MMM", Locale.ENGLISH);
        try {
            Date newDate = fm.parse(dateTime);
            dayFm = fmDay.format(newDate);
            dayOfWeekFm = fmDayOfWeek.format(newDate);
            monthFm = fmMonth.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfWeekFm+" "+appendDay(Integer.parseInt(dayFm))+" "+monthFm;
    }

    // format to new datetime
    public static String fmNewDate(String dateTime) {
        String dayFm = "";
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat fmDay = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            Date newDate = fm.parse(dateTime);
            dayFm = fmDay.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayFm;
    }

    // format to new datetime dd MMMM yyyy
    public static String fmNewDate2(String dateTime) {
        String dayFm = "";
        SimpleDateFormat fm = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        SimpleDateFormat fmDay = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            Date newDate = fm.parse(dateTime);
            dayFm = fmDay.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayFm;
    }

    // append st, nd, rd, th on day
    private static String appendDay(int day){
        String dayFm = "";
        switch (day){
            case 1:
                dayFm = day+"st";
                break;
            case 2:
                dayFm = day+"nd";
                break;
            case 3:
                dayFm = day+"rd";
                break;
            default:
                dayFm = day+"th";
                break;
        }
        return dayFm;
    }

    // web view client
    public static class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    // recycle view no blink when reload
    public static void rcvNoAnimator(RecyclerView rcv) {
        RecyclerView.ItemAnimator animator = rcv.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        rcv.getItemAnimator().setChangeDuration(0);
    }

    public static void showLoading(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pb_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public static void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    // setup dialog
    public static void setupDialog(Dialog dialog, int gravity, int height) {
        dialog.getWindow().setGravity(gravity);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // set and get password
    public static void setPw(Context context, String pw) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "password", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pw", pw);
        editor.apply();
    }

    public static String getPw(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "password", MODE_PRIVATE);
        return sharedPreferences.getString("pw", "");
    }

    // set and get reset entry submit time
    public static void setResetTime(Context context, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "reset_time", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("time", time).apply();
    }

    public static long getResetTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "reset_time", MODE_PRIVATE);
        return sharedPreferences.getLong("time", 0);
    }

    // set and get entry submitted
    public static void setEntrySubmitted(Context context, boolean b) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "entry_submitted", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_submitted", b).apply();
    }

    public static boolean getEntrySubmitted(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "entry_submitted", MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_submitted", false);
    }

    public static void saveImgToGallery(View v, Context context){
        Bitmap bm = loadBitmapFromView(v);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "comic", null);
    }

    public static Bitmap loadBitmapFromView(View capture_view) {
        if (capture_view == null) {
            return null;
        }

        capture_view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(capture_view.getWidth(), capture_view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = capture_view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        capture_view.draw(canvas);

        capture_view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // shake error
    public static TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(200);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    // download image
    public static void downloadImg(Context context, String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // animate slide down
    public static void slideDown(final View view) {
        view.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 1;
        view.setLayoutParams(layoutParams);

        view.measure(View.MeasureSpec.makeMeasureSpec(Resources.getSystem().getDisplayMetrics().widthPixels,
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED));

        final int height = view.getMeasuredHeight();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(1, height);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams1 = view.getLayoutParams();
            if (height > value) {
                layoutParams1.height = value;
            }else{
                layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            view.setLayoutParams(layoutParams1);
        });
        valueAnimator.start();
    }

    // animate slide up
    public static void slideUp(final View view) {
        view.post(() -> {
            final int height = view.getHeight();
            ValueAnimator valueAnimator = ObjectAnimator.ofInt(height, 0);
            valueAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                if (value > 0) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = value;
                    view.setLayoutParams(layoutParams);
                }else{
                    view.setVisibility(View.GONE);
                }
            });
            valueAnimator.start();
        });
    }
}
