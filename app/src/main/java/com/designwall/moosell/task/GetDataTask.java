package com.designwall.moosell.task;

import android.os.AsyncTask;
import android.util.Log;

import com.designwall.moosell.task.woocomerce.OAuthInterceptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

import static com.designwall.moosell.config.Constant.*;

/**
 * Created by SCIT on 3/9/2017.
 */

public class GetDataTask extends AsyncTask<String, Integer, String[]> {
    public static final String METHOD_POST = "post";
    public static final String METHOD_GET = "get";
    public static final String METHOD_PUT = "put";
    public static final String METHOD_DELETE = "delete";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mClient;
    private String mMethod;
    private String mRequestBody;
    private OAuthInterceptor oAuth1;

    public GetDataTask(String method) {
        mMethod = method;
    }

    public GetDataTask(String method, String requestBody) {
        mMethod = method;
        mRequestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        oAuth1 = new OAuthInterceptor.Builder()
                .consumerKey(CONSUMER_KEY)
                .consumerSecret(CONSUMER_SECRET)
                .build();

        mClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(oAuth1)
                .build();
    }

    @Override
    protected String[] doInBackground(String... urls) {
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
//            Log.d("Test", "URL: " + urls[i]);
            Request request = null;
//            Log.v("Test", BASIC_AUTH);
            try {
                switch (mMethod){
                    case METHOD_POST:
                        request = new Request.Builder()
                                .url(urls[i])
                                .post( RequestBody.create(JSON, mRequestBody) )
//                                .header("Authorization", BASIC_AUTH)
                                .build();
                        break;
                    case METHOD_PUT:
                        request = new Request.Builder()
                                .url(urls[i])
                                .put( RequestBody.create(JSON, mRequestBody) )
//                                .header("Authorization", BASIC_AUTH)
                                .build();
                        break;
                    case METHOD_GET:
                        request = new Request.Builder()
                                .url(urls[i])
                                .get()
                                .addHeader("cache-control", "no-cache")
                                .build();
                        break;
                    case METHOD_DELETE:
                        //Log.d("Test", "Delete with: " + mMethod);
                        request = new Request.Builder()
                                .url(urls[i])
                                .delete()
                                .addHeader("cache-control", "no-cache")
                                .build();
                        break;
                    default:
                        request = new Request.Builder()
                                .url(urls[i])
//                                .addHeader("cache-control", "no-cache")
//                                .header("Authorization", BASIC_AUTH)
                                .build();
                }

                Response response = mClient.newCall(request).execute();
                result[i] = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //TODO: Update Progress here
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        //TODO: Executing data here
        //Log.v("Test", result.toString());
    }

/*    public static Response doGet(String url) throws IOException {
//        Random notRandom = new Random() {
//            @Override public void nextBytes(byte[] bytes) {
//                if (bytes.length != 32) throw new AssertionError();
//                ByteString hex = ByteString.decodeBase64("kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4c+g");
//                byte[] nonce = hex.toByteArray();
//                System.arraycopy(nonce, 0, bytes, 0, nonce.length);
//            }
//        };

        OAuthInterceptor oAuth1 = new OAuthInterceptor.Builder()
                .consumerKey(CONSUMER_KEY)
                .consumerSecret(CONSUMER_SECRET)
                // new TimestampServiceImpl().getNonce()
                //.accessToken("370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb")
                //.accessSecret("LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")
                .clock(new Oauth1SigningInterceptor.Clock())
                //.random(notRandom)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(oAuth1)
                .build();
        //client.networkInterceptors().add(oauth1);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("cache-control", "no-cache")
                .build();
        return client.newCall( request ).execute();
    }*/


/*
    // Usage with Legacy API:
        new AsyncTask<String, Void, Void>(){

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String params = "oauth_consumer_key=ck_b00ed47c23037e0a4472fc22b42c197585cad4f7&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1550161059&oauth_nonce=yQV6tn&oauth_version=1.0&oauth_signature=Ze99pkll0lf6LUY72OZERIqpKSE%3D";
            okhttp3.Response response = GetDataTask.doGet(params);
            Log.dialog("Test", response.toString());
            Log.dialog("Test", new JSONObject( response.body().string()).toString() );
            } catch (Exception e ){
                Log.e("Test", e.getClass().getSimpleName()+": " + e.getMessage());
            }
            return null;
        }

    }.execute();

    public static Response doGet(String params) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.4/woocommerce/wc-api/v3/products/categories?"+params)
                .get()
                .addHeader("cache-control", "no-cache")
                .build();

        return client.newCall(request).execute();
    }*/

}
