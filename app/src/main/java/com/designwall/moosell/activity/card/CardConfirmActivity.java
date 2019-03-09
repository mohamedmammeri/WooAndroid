package com.designwall.moosell.activity.card;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.designwall.moosell.R;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.db.DatabaseHelper;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.util.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardConfirmActivity extends AppCompatActivity {

    @BindView(R.id.etBillFirstName)
    EditText etBillFirstName;
    @BindView(R.id.etBillLastName)
    EditText etBillLastName;
    @BindView(R.id.etBillAddress1)
    EditText etBillAddress1;
    @BindView(R.id.etBillAddress2)
    EditText etBillAddress2;
    @BindView(R.id.etBillCompany)
    EditText etBillCompany;
    @BindView(R.id.etBillState)
    EditText etBillState;
    @BindView(R.id.etBillCity)
    EditText etBillCity;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etShipFirstName)
    EditText etShipFirstName;
    @BindView(R.id.etShipLastName)
    EditText etShipLastName;
    @BindView(R.id.etShipAddress1)
    EditText etShipAddress1;
    @BindView(R.id.etShipAddress2)
    EditText etShipAddress2;
    @BindView(R.id.etShipCompany)
    EditText etShipCompany;
    @BindView(R.id.etShipState)
    EditText etShipState;
    @BindView(R.id.etShipCity)
    EditText etShipCity;

    @BindView(R.id.btnValidate)
    Button btnValidate;

    @BindView(R.id.btnBack)
    Button btnBack;

    private Gson mGson;

    private DatabaseHelper dbHelper;
    private Dao<Order, Integer> orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_confirm);
        ButterKnife.bind(this);
        mGson = new Gson();

        dbHelper = new DatabaseHelper(this);
        try {
            orderDao = dbHelper.getDao();
        } catch (SQLException e) {
            Log.e("Test", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        final int orderId = getIntent().getIntExtra(Helper.LAST_ORDER_ID, 0);
        if (orderId == 0){
            Helper.showDialog(this, "Missing Order", "Could not found Order Number");
            finish();
            return;
        }

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( etBillFirstName.getText().toString().trim().isEmpty() ||
                        etBillLastName.getText().toString().trim().isEmpty() ||
                        etBillAddress1.getText().toString().trim().isEmpty() ){
                    Helper.showDialog(CardConfirmActivity.this, "Invalid Information",
                            "Please provide your First and Last Name and a billing address.");
                    return;
                }
                // Save Shipping & Billing info
                saveInfo();
                validateOrder(orderId);

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });

        // Load Shipping & Billing info
        loadInfo();


    }

    private void loadInfo() {
        // Billing Info
        etBillFirstName.setText( Helper.loadString(this, "etBillFirstName"));
        etBillLastName.setText( Helper.loadString(this, "etBillLastName"));
        etBillCompany.setText( Helper.loadString(this, "etBillCompany"));
        etBillAddress1.setText( Helper.loadString(this, "etBillAddress1"));
        etBillAddress2.setText( Helper.loadString(this, "etBillAddress2"));
        etBillCity.setText( Helper.loadString(this, "etBillCity"));
        etBillState.setText( Helper.loadString(this, "etBillState"));
        etEmail.setText( Helper.loadString(this, "etEmail"));
        etPhone.setText( Helper.loadString(this, "etPhone"));
        // Shipping Info
        etShipFirstName.setText( Helper.loadString(this, "etShipFirstName"));
        etShipLastName.setText( Helper.loadString(this, "etShipLastName"));
        etShipCompany.setText( Helper.loadString(this, "etShipCompany"));
        etShipAddress1.setText( Helper.loadString(this, "etShipAddress1"));
        etShipAddress2.setText( Helper.loadString(this, "etShipAddress2"));
        etShipCity.setText( Helper.loadString(this, "etShipCity"));
        etShipState.setText( Helper.loadString(this, "etShipState"));
    }

    private void saveInfo() {
        // Billing Info
        Helper.saveString(this, "etBillFirstName", etBillFirstName.getText().toString().trim());
        Helper.saveString(this, "etBillLastName", etBillLastName.getText().toString().trim());
        Helper.saveString(this, "etBillCompany", etBillCompany.getText().toString().trim());
        Helper.saveString(this, "etBillAddress1", etBillAddress1.getText().toString().trim());
        Helper.saveString(this, "etBillAddress2", etBillAddress2.getText().toString().trim());
        Helper.saveString(this, "etBillCity", etBillCity.getText().toString().trim());
        Helper.saveString(this, "etBillState", etBillState.getText().toString().trim());
        Helper.saveString(this, "etEmail", etEmail.getText().toString().trim());
        Helper.saveString(this, "etPhone", etPhone.getText().toString().trim());
        // Shipping Info
        Helper.saveString(this, "etShipFirstName", etShipFirstName.getText().toString().trim());
        Helper.saveString(this, "etShipLastName", etShipLastName.getText().toString().trim());
        Helper.saveString(this, "etShipCompany", etShipCompany.getText().toString().trim());
        Helper.saveString(this, "etShipAddress1", etShipAddress1.getText().toString().trim());
        Helper.saveString(this, "etShipAddress2", etShipAddress2.getText().toString().trim());
        Helper.saveString(this, "etShipCity", etShipCity.getText().toString().trim());
        Helper.saveString(this, "etShipState", etShipState.getText().toString().trim());
    }

    private void validateOrder(final int orderId) {
        String country = Helper.getCountryISOCode(getApplicationContext());
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"status\":\"processing\",\n" +
                "    \"payment_details\": {\n" +
                "      \"method_id\": \"cod\",\n" +
                "      \"method_title\": \"Cash on delivery\",\n" +
                "      \"paid\": false\n" + // if true set transaction_id as a string
                "    },\n" +
                "    \"billing_address\":{\n" +
                "      \"first_name\":\""+etBillFirstName.getText().toString().trim()+"\",\n" +
                "      \"last_name\":\""+etBillLastName.getText().toString().trim()+"\",\n" +
                "      \"company\":\""+etBillCompany.getText().toString().trim()+"\",\n" +
                "      \"address_1\":\""+etBillAddress1.getText().toString().trim()+"\",\n" +
                "      \"address_2\":\""+etBillAddress2.getText().toString().trim()+"\",\n" +
                "      \"city\":\""+etBillCity.getText().toString().trim()+"\",\n" +
                "      \"state\":\""+etBillState.getText().toString().trim()+"\",\n" +
                "      \"postcode\": \"\",\n" +
                "      \"country\": \""+country+"\",\n" +
                "      \"email\":\""+etEmail.getText().toString().trim()+"\",\n" +
                "      \"phone\":\""+etPhone.getText().toString().trim()+"\"\n" +
                "    },\n" +
                "    \"shipping_address\": {\n" +
                "      \"first_name\": \""+etShipFirstName.getText().toString().trim()+"\",\n" +
                "      \"last_name\": \""+etShipLastName.getText().toString().trim()+"\",\n" +
                "      \"company\": \""+etShipCompany.getText().toString().trim()+"\",\n" +
                "      \"address_1\": \""+etShipAddress1.getText().toString().trim()+"\",\n" +
                "      \"address_2\": \""+etShipAddress2.getText().toString().trim()+"\",\n" +
                "      \"city\": \""+etShipCity.getText().toString().trim()+"\",\n" +
                "      \"state\": \""+etShipState.getText().toString().trim()+"\",\n" +
                "      \"postcode\": \"\",\n" +
                "      \"country\": \""+country+"\"\n" +
                "    },\n" +
                "    \"shipping_lines\": [\n" +
                "      {\n" +
                "        \"method_id\": \"flat_rate\",\n" +
                "        \"method_title\": \"Flat Rate\",\n" +
                "        \"total\":0\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        Log.d("Test", "Content: " + content);
        new GetDataTask(GetDataTask.METHOD_PUT, content) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                    if (result.length > 0 && (!result[0].isEmpty())) {
                        Log.d("Test", "Result: " + result[0]);
                        JsonElement jsonResponse = new JsonParser().parse(result[0]);
                        JsonElement jsonOrder = jsonResponse.getAsJsonObject().get(Url.OBJ_NAME_ORDER);
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        if (order != null && order.getStatus() != null){
                            Log.d("Test", "Order Info: " + order.toString());
                            if (order.getStatus().equals("processing")){
                                if (Helper.saveInt(CardConfirmActivity.this, Helper.LAST_ORDER_ID, 0)){
                                    setResult(Activity.RESULT_OK, getIntent());
                                    if (orderDao != null){
                                        try {
                                            if (orderDao.create(order) > 0){
                                                Log.d("Test", "Order saved with ID: " + order.getId());
                                            }
                                        } catch (SQLException e) {
                                            Log.e("Test", "Error: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                    finish();
                                }
                            } else {
                                Log.d("Test", "Order ID: " + order.getId()+", Status: " + order.getStatus());
                            }
                        } else {
                            JsonArray error = jsonResponse.getAsJsonObject().getAsJsonArray("errors");
                            Helper.toastShort(CardConfirmActivity.this, "Error: "+ error.get(0).getAsJsonObject().get("message"));
                            Log.d("Test", "Order is null.");
                        }
                    } else {
                        Log.d("Test", "Result is empty");
                    }
            }
        }.execute(Url.getOrderId(orderId));
    }

}
