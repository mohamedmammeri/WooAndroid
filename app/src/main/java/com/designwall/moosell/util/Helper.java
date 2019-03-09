package com.designwall.moosell.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.designwall.moosell.activity.listproduct.ListProductActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by scit on 3/23/17.
 */

public class Helper {

    public static final String LAST_ORDER_ID = "LAST_ORDER_ID";
    public static final String HISTORY_ORDER_ID = "HISTORY_ORDER_ID";

    public static AlertDialog showDialog(Context context, String title, String message){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public static AlertDialog showDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .show();
    }

    public static AlertDialog showDialog(Context context, String title, String message,
                                  DialogInterface.OnClickListener onOkListener, DialogInterface.OnClickListener onCancelListener){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onOkListener)
                .setNegativeButton(android.R.string.cancel, onCancelListener)
                .show();
    }

    public static void toast(Context context, String msg, int duration){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void toastShort(Context context, String msg){
        toast(context, msg, Toast.LENGTH_SHORT);
    }
    public static void toastLong(Context context, String msg){
        toast(context, msg, Toast.LENGTH_LONG);
    }

    public static String[] getPagesUrl(String url, int count){
        String[] result;
        int page;
        if (count <= 10){
            page = 1;
        }else {
            if (count % 10 == 0){
                page = count / 10;
            }else {
                page = count / 10 + 1;
            }
        }
        result = new String[page];
        for (int i = 0; i < page; i++){
            result[i] = url + "&page="+(i+1);
        }
        return result;
    }

    /**
     * Save pairs key/value as string
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. username)
     * @param value the actual string value to save
     */
    public static boolean saveString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE); //or use getSharedPreferences instead
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * Save pairs key/value as an integer
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. userId)
     * @param value the actual integer value to save
     */
    public static boolean saveInt(Context context, String key, Integer value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * load pairs key/value as an integer (defaultValue cannot be null)
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. username)
     * @param defaultValue default value used if key is not found (can be <b>null</b>)
     * @return loaded integer value
     */
    public static Integer loadInt(Context context, String key, Integer defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

    /**
     * load pairs key/value as a string (defaultValue can be null)
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. username)
     * @param defaultValue default value used if key is not found (can be <b>null</b>)
     * @return loaded string value
     */
    public static String loadString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue == null?"":defaultValue);
    }

    /**
     * load pairs key/value as a string (defaultValue is empty)
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. username)
     * @return loaded string value
     */
    public static String loadString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
    /**
     * remove a key string parameter
     * @param context Context instance (usually refers to <b>this</b>)
     * @param key key of map parameters (ex. username)
     * @return true if the new values were successfully written to persistent storage
     */
    public static boolean removeString(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return preferences.edit().remove(key).commit();
    }

    public static String formatDate(String aDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String cunvertCurrentDate = aDate;
        Date date = null;
        try {
            date = df.parse(cunvertCurrentDate);
            return df.format(date);
        } catch (ParseException e) {}
        return "";
    }

    @NonNull
    public static String getCountryISOCode(Context context){
        String countryCode = Locale.getDefault().getCountry();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getNetworkCountryIso().isEmpty()? tm.getSimCountryIso(): tm.getNetworkCountryIso();
        } catch (Exception e) {}
        return countryCode.toUpperCase();
    }

}
