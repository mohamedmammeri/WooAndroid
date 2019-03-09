package com.designwall.moosell.task;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.lang.Override;import java.lang.String;import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext() );
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HurlStack(null, sslSocketFactoryProvider()));
        }
        return mRequestQueue;
    }


    /**
     * Provides a SSLSocketFactory that accepts Self Signed Certificates
     *
     * @return
     */
    private SSLSocketFactory sslSocketFactoryProvider(){

        final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
        }};


        final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {

                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, certs, new SecureRandom());
        } catch (final java.security.GeneralSecurityException ex) {
        }
        return  ctx.getSocketFactory();

    }




    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    // Simple Usage
        /*VolleySingleton.getInstance(mView).addToRequestQueue(
            new JsonObjectRequest(GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.dialog("Test", jsonObject.toString());

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Test", error.getClass().getSimpleName()+": " + error.getMessage());
                }
            }){

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    //map.put("Content-Type", "application/json");
                    //headers.put("Authorization", BASIC_AUTH);
                    headers.put("Authorization", "oauth_consumer_key=ck_b00ed47c23037e0a4472fc22b42c197585cad4f7&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1550164121&oauth_nonce=LsQlLu&oauth_version=1.0&oauth_signature=ZCo2tyxUmNgN6YpsXXnkEE7lUjE%3D");
                    //headers.put("oauth_consumer_key", CONSUMER_KEY);
                    //headers.put("oauth_consumer_secret", CONSUMER_SECRET);
                    //headers.put("oauth_signature_method","HMAC-SHA1");
                    //headers.put("oauth_timestamp","1550163912");
                    //headers.put("oauth_nonce", "bsU64Q");
                    //headers.put("oauth_version","1.0");
                    //headers.put("oauth_signature", "8ukayLRdg4WVnKD2f05bN99BG0U%3D");
                    return headers;
                }
            }
    );*/
}
