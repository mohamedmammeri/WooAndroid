package com.designwall.moosell.config;

import android.util.Base64;

/**
 * Created by SCIT on 3/9/2017.
 */

public class Constant {

    public static final String SHOP_NAME     = "HappyShop";

    public static final String CONSUMER_KEY     = "ck_b00ed47c23037e0a4472fc22b42c197585cad4f7";
    public static final String CONSUMER_SECRET  = "cs_2e447bc3f81ce490ff94284ff636fd5a268f7a34";
    public static final String HOST = "http://192.168.43.106/woocommerce";

    public static final String API_VER          = "/wc-api/v3/"; //working (old way the current used one)
//    public static final String API_VER          = "/wp-json/wp/v2/"; // working
//    public static final String API_VER          = "/wp-json/wc/v3/"; // working
//    public static final String BASIC_AUTH = "Basic " + Base64.encodeToString((CONSUMER_KEY + ":" + CONSUMER_SECRET).getBytes(), Base64.NO_WRAP);

    public static final String INTENT_CATEGORY_NAME = "com.designwall.moosell.Config.Constant.KEY_CATEGORY_NAME";
    public static final String INTENT_CATEGORY_COUNT = "com.designwall.moosell.Config.Constant.KEY_CATEGORY_COUNT";

    public static final String CURRENCY = "DZD ";
}
