package com.designwall.moosell;

import com.designwall.moosell.task.woocomerce.OAuthInterceptor;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.designwall.moosell.config.Constant.API_VER;
import static com.designwall.moosell.config.Constant.CONSUMER_KEY;
import static com.designwall.moosell.config.Constant.CONSUMER_SECRET;
import static com.designwall.moosell.config.Constant.HOST;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FeaturesTest {

    public static final String BODY = "{\"CONSUMER_KEY\":\""+CONSUMER_KEY+"\",\"CONSUMER_SECRET\":\""+CONSUMER_SECRET+"\"}";
    public static final String URL = HOST+API_VER;

    @Rule
    public MockWebServer server = new MockWebServer();

    private OAuthInterceptor oAuth1;
    private OkHttpClient mClient;

//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }

    // Mocking with annotations
//    @Mock
//    JSONObject jsonObject;
//    @Rule
//    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setup(){
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
        server.enqueue(new MockResponse().setBody(BODY));
    }

    @Test
    public void testRestApi() throws IOException {
        HttpUrl baseUrl = server.url(URL);
//        HttpUrl baseUrl = server.url(Url.getProducts());
//        server.start();
        String responseBody = sendGetRequest(baseUrl);

        assertTrue("Response starts with 'store':", responseBody.startsWith("{\"store\":"));

        /*/ Mocking JSONObject
        JsonFactory jsonFactory = new JsonFactory();
        JsonFactory jsonFactorySpy = Mockito.spy(jsonFactory);
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        doReturn(jsonObject).when(jsonFactorySpy).jsonFactoryMethod(responseBody);

        // Testing...
        JSONObject jsonResponse = jsonFactorySpy.constructJson(responseBody);
//        JSONArray jsonArray = jsonResponse.getJSONArray("products");
        assertEquals(jsonResponse, jsonObject);
//        assertEquals("", jsonFactorySpy.asString(responseBody));

        doReturn(responseBody)
                .when(jsonResponse).toString();

        doReturn(3)
                .when(jsonResponse).length();

        assertEquals(3, jsonResponse.length());
        assertEquals(responseBody, jsonResponse.toString());
*/
//         verify(jsonResponse).isNull("products"); //Actually, there were zero interactions with this mock.

    }

    @Test
    public void testGetProduct() throws IOException {
        HttpUrl baseUrl = server.url(URL+"products/23");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"product\""));
    }

    @Test
    public void testGetProductCount() throws IOException {
        HttpUrl baseUrl = server.url(URL+"products/count");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
        assertFalse(responseBody.equals("{\"count\":0}"));
    }

    @Test
    public void testGetOrdersCount() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/count");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
        assertFalse(responseBody.equals("{\"count\":0}"));
    }

    @Test
    public void testGetOrder() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/69");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
/*{"order":{"id":47,"order_number":"47","order_key":"wc_order_95Ywy16R1M1QS","created_at":"2019-02-28T22:32:51Z",
"updated_at":"2019-03-01T16:32:42Z","completed_at":"1970-01-01T00:00:00Z","status":"pending","currency":"DZD",
"total":"2560.00","subtotal":"2560.00","total_line_items_quantity":10,"total_tax":"0.00","total_shipping":"0.00",
"cart_tax":"0.00","shipping_tax":"0.00","total_discount":"0.00","shipping_methods":"",
"payment_details":{"method_id":"","method_title":"","paid":false},
"billing_address":{"first_name":"","last_name":"","company":"","address_1":"","address_2":"","city":"","state":"",
"postcode":"","country":"","email":"","phone":""},"shipping_address":{"first_name":"","last_name":"","company":"",
"address_1":"","address_2":"","city":"","state":"","postcode":"","country":""},"note":"","customer_ip":"192.168.1.5",
"customer_user_agent":"okhttp\/3.6.0","customer_id":0,
"view_order_url":"http:\/\/192.168.1.5\/woocommerce\/my-account\/view-order\/47\/",
"line_items":[{"id":19,"subtotal":"900.00","subtotal_tax":"0.00","total":"900.00","total_tax":"0.00",
"price":"180.00","quantity":5,"tax_class":"","name":"Complet Viande","product_id":18,"sku":"7","meta":[]},
{"id":20,"subtotal":"960.00","subtotal_tax":"0.00","total":"960.00","total_tax":"0.00","price":"320.00","quantity":3,
"tax_class":"","name":"Pizza Margerit","product_id":16,"sku":"5","meta":[]},{"id":21,"subtotal":"700.00",
"subtotal_tax":"0.00","total":"700.00","total_tax":"0.00","price":"350.00","quantity":2,"tax_class":"",
"name":"Pizza Poulet","product_id":23,"sku":"10","meta":[]}],"shipping_lines":[],"tax_lines":[],
"fee_lines":[],"coupon_lines":[],"customer":{"id":0,"email":"","first_name":"","last_name":"",
"billing_address":{"first_name":"","last_name":"","company":"","address_1":"","address_2":"","city":"","state":"",
"postcode":"","country":"","email":"","phone":""},"shipping_address":{"first_name":"","last_name":"","company":"",
"address_1":"","address_2":"","city":"","state":"","postcode":"","country":""}}}} */
        assertTrue(responseBody.startsWith("{\"order\":"));
    }

    @Test
    public void testGetAllOrders() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"orders\":[{\"id\":"));
    }

    @Test
    public void testPostOrder() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders");
        String content = "{\n" +
                "  \"order\": {\n" +/*
                "    \"payment_details\": {\n" +
                "      \"method_id\": \"bacs\",\n" +
                "      \"method_title\": \"Direct Bank Transfer\",\n" +
                "      \"paid\": false\n" +
                "    },\n" +
                "    \"billing_address\": {\n" +
                "      \"first_name\": \"John\",\n" +
                "      \"last_name\": \"Doe\",\n" +
                "      \"address_1\": \"969 Market\",\n" +
                "      \"address_2\": \"\",\n" +
                "      \"city\": \"San Francisco\",\n" +
                "      \"state\": \"CA\",\n" +
                "      \"postcode\": \"94103\",\n" +
                "      \"country\": \"US\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"phone\": \"(555) 555-5555\"\n" +
                "    },\n" +
                "    \"shipping_address\": {\n" +
                "      \"first_name\": \"John\",\n" +
                "      \"last_name\": \"Doe\",\n" +
                "      \"address_1\": \"969 Market\",\n" +
                "      \"address_2\": \"\",\n" +
                "      \"city\": \"San Francisco\",\n" +
                "      \"state\": \"CA\",\n" +
                "      \"postcode\": \"94103\",\n" +
                "      \"country\": \"US\"\n" +
                "    },\n" +*/
                "    \"customer_id\": 0,\n" +
                "    \"line_items\": [\n" +
                "      {\n" +
                "        \"product_id\": 10,\n" +
                "        \"quantity\": 6\n" +
                "      }\n" +
                "    ]\n" + /*
                "    ,\"shipping_lines\": [\n" +
                "      {\n" +
                "        \"method_id\": \"flat_rate\",\n" +
                "        \"method_title\": \"Flat Rate\",\n" +
                "        \"total\": 10\n" +
                "      }\n" +
                "    ]\n" +*/
                "  }\n" +
                "}";
        String responseBody = sendPostRequest(baseUrl, content);
        System.out.println(responseBody);
        /*
{"order":{"id":39,"order_number":"39","order_key":"wc_order_AeKvHg0PSHqq2","created_at":"2019-02-27T18:37:56Z",
"updated_at":"2019-02-27T18:37:56Z","completed_at":"1970-01-01T00:00:00Z","status":"pending","currency":"DZD",
"total":"1680.00","subtotal":"1680.00","total_line_items_quantity":6,"total_tax":"0.00","total_shipping":"0.00",
"cart_tax":"0.00","shipping_tax":"0.00","total_discount":"0.00","shipping_methods":"",
"payment_details":{"method_id":"","method_title":"","paid":false},
"billing_address":{"first_name":"","last_name":"","company":"","address_1":"","address_2":"","city":"","state":"","postcode":"","country":"","email":"","phone":""},
"shipping_address":{"first_name":"","last_name":"","company":"","address_1":"","address_2":"","city":"","state":"","postcode":"","country":""},
"note":"","customer_ip":"192.168.1.5","customer_user_agent":"okhttp\/3.6.0","customer_id":0,
"view_order_url":"http:\/\/192.168.1.5\/woocommerce\/my-account\/view-order\/39\/",
"line_items":[{"id":9,"subtotal":"1680.00","subtotal_tax":"0.00","total":"1680.00","total_tax":"0.00",
"price":"280.00","quantity":6,"tax_class":"","name":"Pizza Simple","product_id":10,"sku":"4","meta":[]}],
"shipping_lines":[],"tax_lines":[],"fee_lines":[],"coupon_lines":[],"customer":{"id":0,"email":"","first_name":"",
"last_name":"","billing_address":{"first_name":"","last_name":"","company":"","address_1":"","address_2":"","city":"",
"state":"","postcode":"","country":"","email":"","phone":""},"shipping_address":{"first_name":"","last_name":"",
"company":"","address_1":"","address_2":"","city":"","state":"","postcode":"","country":""}}}}
         */
        assertTrue(responseBody.startsWith("{\"order\""));
    }

    @Test
    public void testUpdateOrder() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/46");
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"line_items\": [\n" +
                "      {\n" +
                "        \"product_id\": 16,\n" +
                "        \"quantity\": 2\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        String responseBody = sendPutRequest(baseUrl, content);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"order\""));
    }

    @Test
    public void testUpdateQteOrder() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/47");
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"line_items\": [\n" +
                "      {\n" +
                "        \"id\":20,\n" +
                "        \"product_id\": 16,\n" +
                "        \"quantity\": 2\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        String responseBody = sendPutRequest(baseUrl, content);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"order\""));
    }

    @Test
    public void testDeleteOrder() throws IOException {
//        HttpUrl baseUrl = server.url(URL+"orders/38");
        HttpUrl baseUrl = server.url(URL+"orders/38/?force=true");
        String responseBody = sendDeleteRequest(baseUrl);
        System.out.println(responseBody);
//        assertTrue(responseBody.equals("{\"message\":\"Deleted order\"}")); // Delete order without "/?force=true" param
        assertTrue(responseBody.equals("{\"message\":\"Permanently deleted order\"}")); // Delete with "/?force=true"
    }

    @Test
    public void testDeleteProductFromOrder() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/47");
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"line_items\": [\n" +
                "      {\n" +
                "    \"id\":19,\n" +
                "        \"product_id\":null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        String responseBody = sendDeleteRequest(baseUrl, content);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"order\":"));
    }


    @Test
    public void testGetOrderNotes() throws IOException {
        HttpUrl baseUrl = server.url(URL+"orders/69/notes");
        String responseBody = sendGetRequest(baseUrl);
        System.out.println(responseBody);
        assertTrue(responseBody.startsWith("{\"order_notes\":[{\"id\":"));
        /*
{"order_notes":[{"id":"35","created_at":"2019-03-10T12:07:53Z","note":"This order will be delayed.",
"customer_note":true},{"id":"34","created_at":"2019-03-09T22:07:35Z",
"note":"Order status changed from Pending payment to Processing.","customer_note":false}]}
         */
    }


    private String sendGetRequest(HttpUrl baseUrl) throws IOException {
        Response response = mClient.newCall(
                new Request.Builder()
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .url(baseUrl)
                        .build()
        ).execute();
        return response.body().string();
    }

    private String sendPostRequest(HttpUrl baseUrl, String content) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .post(body)
                .url(baseUrl)
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private String sendPutRequest(HttpUrl baseUrl, String content) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .put(body)
                .url(baseUrl)
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private String sendDeleteRequest(HttpUrl baseUrl) throws IOException {
        Request request = new Request.Builder()
                .delete()
                .url(baseUrl)
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private String sendDeleteRequest(HttpUrl baseUrl, String content) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .put(body)
                .url(baseUrl)
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    @After
    public void cleanUp(){
        mClient = null;
        oAuth1 = null;
    }

    /*/ for JSONObject Mocking
    class JsonFactory {
        JSONObject jsonFactoryMethod(String input) throws JSONException {
            return new JSONObject(input);
        }
        public JSONObject constructJson(String input) throws JSONException {
            return jsonFactoryMethod(input);
        }
        public String asString(String input) throws JSONException {
            String result = jsonFactoryMethod(input).toString(4);
            return result;
        }
    }*/

    /*
    // not used (it won't parse)
    class MyJsonObject {
        JSONCreator creator;
        MyJsonObject(JSONCreator creator) {
            this.creator = creator;
        }
        public void doesSomethingWithJSON(String input) throws JSONException {
            JSONObject jsonObject = creator.createJSONObject(input);
//            return jsonObject;
            JSONArray jsonArray = jsonObject.getJSONArray("products");
            System.out.println(jsonArray.toString());
//            Assert.assertNotNull(jsonObject);
//            Assert.assertEquals(3, jsonArray.length());
        }
    }

    class JSONCreator {
        JSONObject createJSONObject(String input) throws JSONException {
            return new JSONObject(input);
        }
    }
*/

}




