package com.designwall.moosell.activity.order;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.designwall.moosell.R;
import com.designwall.moosell.activity.card.CardActivity;
import com.designwall.moosell.activity.card.CardListAdapter;
import com.designwall.moosell.activity.listproduct.ListProductActivity;
import com.designwall.moosell.adapter.OrderDetailAdapter;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.model.Order.subclass.LineItem;
import com.designwall.moosell.model.Product.Product;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.util.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvOrderDetailNumber)
    TextView tvOrderDetailNumber;

    @BindView(R.id.tvOrderCreatedAt)
    TextView tvOrderCreatedAt;

    @BindView(R.id.tvOrderStatus)
    TextView tvOrderStatus;

    @BindView(R.id.lvOrderItems)
    ListView lvOrderItems;

    @BindView(R.id.tvOrderTotal)
    TextView tvOrderTotal;

    @BindView(R.id.tvOrderNotes)
    TextView tvOrderNotes;

    @BindView(R.id.btnReOrder)
    Button btnReOrder;

    private Gson mGson;
    private Order mOrder;
    private List<LineItem> mItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        mGson = new Gson();

        final int orderId = getIntent().getIntExtra(Helper.HISTORY_ORDER_ID, 0);
        if (orderId <= 0){
            Helper.showDialog(this, "Error", "Order not found");
            finish();
        }

        final int lastOrderId = Helper.loadInt(this, Helper.LAST_ORDER_ID, 0);

        btnReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastOrderId > 0){
                    Helper.showDialog(OrderDetailActivity.this,
                            "Order Opened", "Order N°: " + lastOrderId+" is already open.");
                } else {
                    Helper.toastShort(getApplicationContext(), "Creating a new Order");
                    createOrderFrom();
                }
            }
        });

        loadOrder(orderId);

    }

    private void loadOrder(int orderId){
        new GetDataTask(GetDataTask.METHOD_GET) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_ORDER);
                    if (jsonOrder != null) {
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        if (order != null){
                            fillOrderInfo(order);
                            Log.d("Test", order.toString());
                        } else {
                            Log.d("Test", "Order is null");
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderId(orderId));
    }

    private void fillOrderInfo(Order order) {
        updateOrderInfo(order);
        mOrder = order;
        mItems = mOrder.getLine_items();
        if (mItems.size() > 0) {
            lvOrderItems.setAdapter(new OrderDetailAdapter(this, mItems));
        } else {
            Log.d("Test", "LineItems is empty");
        }
    }

    public void updateOrderInfo(Order order) {
        tvOrderDetailNumber.setText( String.valueOf( order.getOrder_number() ));
        tvOrderCreatedAt.setText( Helper.formatDate(order.getCreated_at()) );
        tvOrderStatus.setText( order.getStatus() );
        tvOrderTotal.setText( order.getTotal() );
        tvOrderNotes.setText( order.getNote() );
    }

    private void createOrderFrom() {
        StringBuilder products = new StringBuilder();
        for(LineItem item: mItems){
            Log.d("Test", "Adding ProductID: " + item.getProduct_id()+", qte: " + item.getQuantity());
            /*/ TODO: check product's stock
            Product product = getProductyById(item.getProduct_id());
            if (product.getStock_quantity() > 0 && item.getQuantity() > product.getStock_quantity()){
                Helper.toastShort(this, "Insufficient quantity in Stock for: "+product.getName());
            }*/
            products.append("{" +
                        "\"product_id\":" + item.getProduct_id()+"," +
                        "\"quantity\":" + item.getQuantity()+
                        "}");
            if (!item.equals(mItems.get(mItems.size()-1))){
                products.append(",");
            }
        }
        String requestBody = "{\n" +
                "\"order\": {" +
                /*"  \"customer_id\": 0," + */
                "  \"line_items\": [" + products +"]" +
                "}}";
        Log.d("Test", requestBody);
        new GetDataTask(GetDataTask.METHOD_POST, requestBody) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                    if (!result[0].isEmpty()) {
                        Log.d("Test", "Result: " + result[0]);
                        JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject()
                                .get(Url.OBJ_NAME_ORDER);
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        Toast.makeText(getApplicationContext(), "Order ID°" +order.getId(), Toast.LENGTH_SHORT).show();
                        Log.d("Test", order.toString());
                        if (Helper.saveInt(OrderDetailActivity.this, Helper.LAST_ORDER_ID, order.getId())){
                            Log.d("Test", "Last Order ID: " + order.getId()+" saved.");
                            Intent cardActivity = new Intent(getApplicationContext(), CardActivity.class);
                            cardActivity.putExtra(Helper.LAST_ORDER_ID, order.getId());
                            startActivity(cardActivity);
                        }
                    } else {
                        Log.d("Test", "Result is empty");
                    }
            }

        }.execute(Url.getOrders());

    }

//    private Product getProductyById(int product_id) {
//        return null;
//    }


}
