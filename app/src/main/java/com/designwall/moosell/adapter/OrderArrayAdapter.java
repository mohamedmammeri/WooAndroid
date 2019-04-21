package com.designwall.moosell.adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.designwall.moosell.R;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.model.Order.OrderNote;
import com.designwall.moosell.model.Order.OrderStatus;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.util.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class OrderArrayAdapter extends ArrayAdapter<Order>
        implements View.OnClickListener, View.OnLongClickListener {

    public interface OnOrderClick {
        void showOrderDetail(int orderId);
        boolean deleteOrderRecord(int orderId);
    }

    private Context mContext;
    private List<Order> mOrders;
    private Gson mGson;

    private static class ViewHolder {
        TextView tvOrder;
        TextView tvOrderTotal;
        ImageView ivInfo;
    }

    public OrderArrayAdapter(@NonNull Context context, @NonNull List<Order> orders){
        super(context, R.layout.order_row, orders);
        this.mOrders = orders;
        this.mContext = context;
        mGson = new Gson();
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Order order = getItem(position);
        OrderArrayAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvOrder = (TextView) convertView.findViewById(R.id.tvOrder);
            viewHolder.tvOrderTotal= (TextView) convertView.findViewById(R.id.tvOrderTotal);
            viewHolder.ivInfo = (ImageView) convertView.findViewById(R.id.ivInfo);

            // This should be done dynamically because Orders here are saved in the DB
//            LinearLayout layoutOrder = (LinearLayout) convertView.findViewById(R.id.layoutOrder);
//            if (order.getStatus().equals(OrderStatus.Processing.toString()))
//                layoutOrder.setBackground(convertView.getResources().getDrawable(R.drawable.buttons_success));
//            else
//                layoutOrder.setBackground(convertView.getResources().getDrawable(R.drawable.buttons_danger));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvOrder.setText(String.format("N°:%d (%s)", order.getId(),
                Helper.formatDate(order.getCreated_at())));
        viewHolder.tvOrder.setOnClickListener(this);
        viewHolder.tvOrder.setOnLongClickListener(this);
        viewHolder.tvOrder.setTag(position);
        viewHolder.tvOrderTotal.setText(order.getTotal());
        viewHolder.ivInfo.setOnClickListener(this);
        viewHolder.ivInfo.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        final int position = (Integer) v.getTag();
        final Order order = getItem(position);
        switch (v.getId()){
            case R.id.tvOrder:
                showOrderNotes(order.getId());
                break;
            case R.id.ivInfo:
                ((OnOrderClick) mContext).showOrderDetail(order.getId());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final int position = (Integer) v.getTag();
        final Order order = getItem(position);
        if (v.getId() == R.id.tvOrder){
            Helper.showDialog(mContext, mContext.getString(R.string.delete_order_history_title),
                    mContext.getString(R.string.delete_order_history_prompt)+" (N°"+order.getId()+")",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (((OnOrderClick) mContext).deleteOrderRecord(order.getId())){
                                Log.d("Test", "Order N°"+order.getId()+ " deleted.");
                                remove(order);
//                                notifyDataSetChanged(); // true by default
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        return false;
    }

    private void showOrderNotes(final int orderId){
        new GetDataTask(GetDataTask.METHOD_GET) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    JsonElement jsonOrderNotes = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_ORDER_NOTES);
                    if (jsonOrderNotes != null) {
                        List<OrderNote> orderNotes = mGson.fromJson(jsonOrderNotes, new TypeToken<List<OrderNote>>(){}.getType());
                        if (orderNotes != null){
                            if (orderNotes.size() == 0){
                                Helper.showDialog(mContext, mContext.getString(R.string.order_num) + orderId,
                                        mContext.getString(R.string.order_no_notes));
                            } else {
                                StringBuilder notes = new StringBuilder();
                                for (OrderNote orderNote : orderNotes) {
                                    // Show Only Customer Note
                                    if (orderNote.isCustomer_note())
                                        notes.append(Helper.formatDate(orderNote.getCreated_at()) + ": " +
                                                orderNote.getNote().trim() + "\n");
                                }
                                if (notes.toString().trim().isEmpty())
                                    notes.append(mContext.getString(R.string.order_no_notes));
                                Helper.showDialog(mContext, mContext.getString(R.string.order_num) + orderId,
                                        mContext.getString(R.string.order_notes) + "\n" + notes.toString());
                            }
                        } else {
                            Log.d("Test", "OrderNotes is null");
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderNote(orderId));

        /*new GetDataTask(GetDataTask.METHOD_GET) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_ORDER);
                    if (jsonOrder != null) {
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        if (order != null){
                            Helper.showDialog(mContext, mContext.getString(R.string.order_info),
                                    mContext.getString(R.string.order_num) +" "+ order.getId()+"\n"+
                                    mContext.getString(R.string.order_status) +" "+ order.getStatus()+"\n"+
                                            mContext.getString(R.string.total) +" "+ order.getTotal().trim()+"\n"+
                                            mContext.getString(R.string.order_notes) +" "+ order.getNote().trim()

                            );
                            Log.d("Test", order.toString());
                        } else {
                            Log.d("Test", "Order is null");
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderId(orderId));*/
    }


}