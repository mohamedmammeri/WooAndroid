package com.designwall.moosell.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.designwall.moosell.config.Constant;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SCIT on 3/9/2017.
 */

public class Network {

    public static boolean isAvailable(Activity activity) {
        boolean result = false;
        try {
            result = new NetworkChecker().execute().get();
        } catch (Exception e) {
            Log.d("Test", "Network unavailable: " + e.getMessage());
        }
        return result || isInternetAvailable(activity);
    }

    /**
     * This checks for internet
     * @param activity
     * @return
     */
    public static boolean isInternetAvailable(Activity activity) {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    static class NetworkChecker extends AsyncTask<Void, Void, Boolean>
    {

        protected void onPreExecute() {
            //display progress dialog.
        }
        protected Boolean doInBackground(Void... params) {
            int response = HttpURLConnection.HTTP_BAD_GATEWAY;
            try {
                URL url = new URL(Constant.HOST);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
//                String responseMsg = con.getResponseMessage();
                response = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response == HttpURLConnection.HTTP_OK;
        }


        protected void onPostExecute(Void result) {
            // dismiss progress dialog and update ui
        }
    }

}
