package com.designwall.moosell.activity.card;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class CardListAdapter extends ArrayAdapter<LineItem>
    implements View.OnClickListener {

    private Context mContext;
    private List<LineItem> lineItems;
    private Order mOrder;
    private Gson mGson;

    private static class ViewHolder {
        TextView tvName;
        TextView tvQte;
        TextView tvPrice;
        ImageView ivDelete;
    }

    public CardListAdapter(@NonNull Context context, @NonNull List<LineItem> lineItems, Order order) {
        super(context, R.layout.item_order, lineItems);
        this.lineItems = lineItems;
        this.mOrder = order;
        this.mContext = context;
        mGson = new Gson();
    }

    @Override
    public int getCount() {
        return lineItems.size();
    }

    @Override
    public void onClick(View v) {
        final int position = (Integer) v.getTag();
        final LineItem lineItem = (LineItem) getItem(position);
        switch (v.getId()){
            case R.id.name:
                editLineItem(lineItem);
                break;
            case R.id.ivDelete:
                Helper.showDialog(mContext, mContext.getString(R.string.delete_item), mContext.getString(R.string.delete_item_prompt),
                        new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItem(lineItem);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                break;
        }
    }

    private void editLineItem(final LineItem lineItem) {
        new GetDataTask(GetDataTask.METHOD_GET) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    Log.d("Test", "Result: " + result[0]);
                    JsonElement jsonProduct = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_PRODUCT);
                    if (jsonProduct != null) {
                        Product product = mGson.fromJson(jsonProduct, new TypeToken<Product>(){}.getType());
                        if (product != null){
                            new DialogEditItem(mContext, product, lineItem, mOrder.getId()).show();
//                            ((OnUpdateOrder) mContext).loadLastOrder(mOrder.getId());
//                            notifyDataSetChanged();
                        } else {
                            Log.d("Test", "Could not load product N°: " + lineItem.getProduct_id());
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getProductId(lineItem.getProduct_id()));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        LineItem lineItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
//        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_order, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.price);
            viewHolder.tvQte = (TextView) convertView.findViewById(R.id.qte);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
//            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
//            result = convertView;
        }
        viewHolder.tvName.setText(lineItem.getName());
        viewHolder.tvPrice.setText(lineItem.getPrice());
        viewHolder.tvQte.setText( String.valueOf(lineItem.getQuantity()) );
        viewHolder.tvName.setOnClickListener(this);
        viewHolder.tvName.setTag(position);
        viewHolder.ivDelete.setOnClickListener(this);
        viewHolder.ivDelete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    private void deleteItem(final LineItem lineItem){
        Log.d("Test", "OrderLineID: " + lineItem.getId() + ", productID: " + lineItem.getProduct_id());
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"line_items\": [\n" +
                "      {\n" +
                "    \"id\":"+lineItem.getId()+",\n" +
                "        \"product_id\":null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        new GetDataTask(GetDataTask.METHOD_PUT, content) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    Log.d("Test", "Result: " + result[0]);
                    JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_ORDER);
                    if (jsonOrder != null) {
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        if (order != null){
                            Log.d("Test", "OrderId: " + order.getId());
                            remove(lineItem);
                            notifyDataSetChanged();
                            ((OnUpdateOrder) mContext).updateOrderInfo(order);
                        } else {
                            Log.d("Test", "Could not delete lineItem N°: " + lineItem.getId());
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderId(mOrder.getId()));
    }

    public interface OnUpdateOrder {
        void updateOrderInfo(Order order);
    }
}
