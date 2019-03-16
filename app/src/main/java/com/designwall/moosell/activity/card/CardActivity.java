package com.designwall.moosell.activity.card;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.designwall.moosell.R;
import com.designwall.moosell.activity.dialog.DialogEditItem;
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

public class CardActivity extends AppCompatActivity
        implements CardListAdapter.OnUpdateOrder,
        DialogEditItem.OnEditItem {

    private static final int CARD_CONFIRM_RESULT = 10;

    @BindView(R.id.lvItems) ListView lvItems;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.tvOrderNumber) TextView tvOrderNumber;
    @BindView(R.id.tvTotal) TextView tvTotal;
    @BindView(R.id.btnConfirm) Button btnConfirm;
    @BindView(R.id.btnDelete) Button btnDelete;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;

    private List<LineItem> items;
    private Order mOrder;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        mGson = new Gson();

        final int lastOrderId = getIntent().getIntExtra(Helper.LAST_ORDER_ID, 0);
        if (lastOrderId == 0){
            finish();
            return;
        }

        loadLastOrder(lastOrderId);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Helper.showDialog(CardActivity.this, getString(R.string.delete_order),
                     getString(R.string.delete_order_prompt),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder(mOrder.getId());
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvItems.getCount() == 0){
                    Helper.showDialog(CardActivity.this, getString(R.string.card_empty), getString(R.string.card_is_empty));
                } else {
                    startCardConfirmActivity(lastOrderId);
                }
            }
        });
    }

    private void startCardConfirmActivity(int orderId) {
        Intent intent = new Intent(this, CardConfirmActivity.class);
        intent.putExtra(Helper.LAST_ORDER_ID, orderId);
        startActivityForResult(intent, CARD_CONFIRM_RESULT);
    }

    private void deleteOrder(final int orderId) {
        new GetDataTask(GetDataTask.METHOD_DELETE) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    Log.d("Test", "Result: " + result[0]);
                    JsonElement jsonMessage = new JsonParser().parse(result[0]).getAsJsonObject().get(Url.OBJ_NAME_MESSAGE);
                    if (jsonMessage == null) {
                        Log.d("Test", "jsonMessage is NULL");
                    } else {
                        Log.d("Test", jsonMessage.getAsString() + ", ID: " + orderId);
                        if (jsonMessage.getAsString().equals("Deleted order") ||
                                jsonMessage.getAsString().equals("Permanently deleted order")){
                            if (Helper.saveInt(CardActivity.this, Helper.LAST_ORDER_ID, 0)){
//                                Log.d("Test", "lastOrderID saved: 0");
                                finish();
                            }
                        } else {
                            Log.d("Test", "Could not delete: " + jsonMessage.toString());
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderId(orderId));
    }

    @Override
    public void loadLastOrder(int lastOrderId) {
        new GetDataTask(GetDataTask.METHOD_GET) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingView(true);
            }

            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length == 0){
                    Log.d("Test", "No data!");
                } else {
                    if (!result[0].isEmpty()) {
                        Log.d("Test", "Result: " + result[0]);
                        JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject()
                                .get(Url.OBJ_NAME_ORDER);
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
//                        Log.d("Test", order.toString());
                        fillOrderInfo(order);
                    } else {
                        Log.d("Test", "Result is empty");
                    }
                }
                showLoadingView(false);
            }
        }.execute(Url.getOrderId(lastOrderId));
    }

    public void showLoadingView(boolean on) {
        pbLoading.setVisibility(on? View.VISIBLE: View.GONE);
        lvItems.setVisibility(on? View.GONE: View.VISIBLE);
        btnConfirm.setEnabled(!on);
        btnDelete.setEnabled(!on);
    }

    private void fillOrderInfo(Order order) {
        updateOrderInfo(order);
        mOrder = order;
        items = mOrder.getLine_items();
        if (items.size() > 0) {
            lvItems.setAdapter(new CardListAdapter(CardActivity.this, items, mOrder));
        } else {
            Log.d("Test", "Line items is empty");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CARD_CONFIRM_RESULT)
            switch (resultCode){
                case (RESULT_OK):
                    Helper.toastLong(this, getString(R.string.card_validated));
                    finish();
                    break;
                case RESULT_CANCELED:
//                    Helper.toastShort(this, "Card canceled.");
                    break;
            }
    }

    @Override
    public void updateOrderInfo(Order order) {
        tvOrderNumber.setText( String.valueOf( order.getOrder_number() ));
        tvCreatedAt.setText( Helper.formatDate(order.getCreated_at()) );
        tvTotal.setText( order.getTotal() );
//        tvStatus.setText( OrderStatus.getLocalized(order.getStatus(), this) );
    }

}
