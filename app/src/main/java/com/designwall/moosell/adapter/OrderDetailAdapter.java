package com.designwall.moosell.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.designwall.moosell.R;
import com.designwall.moosell.model.Order.subclass.LineItem;
import com.google.gson.Gson;

import java.util.List;

public class OrderDetailAdapter extends ArrayAdapter<LineItem> {

    private Context mContext;
    private List<LineItem> items;
    private Gson mGson;

    private static class ViewHolder {
        TextView tvProductName;
        TextView tvPrice;
        TextView tvQte;
    }

    public OrderDetailAdapter(@NonNull Context context, @NonNull List<LineItem> items){
        super(context, R.layout.item_detail_order, items);
        this.mContext = context;
        this.items = items;
        mGson = new Gson();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LineItem lineItem = getItem(position);
        OrderDetailAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_detail_order, parent, false);
            viewHolder = new OrderDetailAdapter.ViewHolder();
            viewHolder.tvProductName = (TextView) convertView.findViewById(R.id.tvProductName);
            viewHolder.tvPrice= (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.tvQte= (TextView) convertView.findViewById(R.id.tvQte);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.tvProductName.setText(lineItem.getName());
        viewHolder.tvPrice.setText(lineItem.getPrice());
        viewHolder.tvQte.setText(String.valueOf(lineItem.getQuantity()));
        return convertView;
    }

}
