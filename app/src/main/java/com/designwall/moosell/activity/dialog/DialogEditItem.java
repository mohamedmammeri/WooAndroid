package com.designwall.moosell.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.designwall.moosell.R;
import com.designwall.moosell.config.Constant;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.model.Order.subclass.LineItem;
import com.designwall.moosell.model.Product.Product;
import com.designwall.moosell.task.GetDataTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogEditItem extends Dialog implements
        View.OnClickListener {

    public interface OnEditItem {
        void loadLastOrder(int orderId);
    }

    private OnEditItem mActivity;
    private Product mProduct;
    private LineItem mLineItem;
    private int quantity;
    private int orderId;
    private Gson mGson;

    @BindView(R.id.btn_yes) Button yes;
    @BindView(R.id.btn_no) Button no;
    @BindView(R.id.btn_plus) Button btnPlus;
    @BindView(R.id.btn_minus) Button btnMinus;
    @BindView(R.id.etQte) EditText etQte;
    @BindView(R.id.ivProduct) ImageView ivProduct;
    @BindView(R.id.tvProduct) TextView tvProduct;
    @BindView(R.id.tvPrice) TextView tvPrice;
    @BindView(R.id.tvDescription) TextView tvDescription;
//    @BindView(R.id.tvRegularPrice) TextView tvRegularPrice;

    public DialogEditItem(Context context, Product product, LineItem lineItem, int orderId) {
        super(context);
        this.mActivity = (OnEditItem) context;
        this.mProduct = product;
        this.mLineItem = lineItem;
        this.quantity = lineItem.getQuantity();
        this.orderId = orderId;
        mGson = new Gson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_item);
        ButterKnife.bind(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        etQte.setText(String.valueOf(quantity));
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf( etQte.getText().toString().trim() );
                etQte.setText( String.valueOf( value+1 ) );
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf( etQte.getText().toString().trim() );
                if (value > 1)
                    etQte.setText( String.valueOf( value-1 ) );
            }
        });
        if (mProduct != null){
            if (mProduct.hasImage()) {
                Glide.with(getContext()).load(mProduct.getImageSrc())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivProduct);
            }
            tvProduct.setText( mProduct.getTitle() );
            tvPrice.setText( Constant.CURRENCY + mProduct.getPrice() );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(mProduct.getDescription().trim(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvDescription.setText(Html.fromHtml(mProduct.getDescription().trim()));
            }

                // Show RegularPrice & CurrentPrice
            if (!mProduct.getRegular_price().equals(mProduct.getPrice())) {
//                tvRegularPrice.setVisibility(View.VISIBLE);
//                tvRegularPrice.setText(Constant.CURRENCY + mProduct.getRegular_price() );
            } else {
//                tvRegularPrice.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                    updateOrder(orderId);
                    dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public void updateOrder(final int orderId){
        final String qte = etQte.getText().toString().trim();
        final String productId = String.valueOf(mLineItem.getProduct_id());
//        String price = String.valueOf(mLineItem.getPrice());
        String total = String.valueOf(Double.valueOf(mLineItem.getPrice())*Integer.valueOf(qte));
        String content = "{\n" +
                "  \"order\": {\n" +
                "    \"line_items\":[\n" +
                "      {\n" +
                "    \"id\":"+mLineItem.getId()+",\n" +
                "        \"product_id\":"+productId+",\n" +
/*                "        \"price\":"+price+",\n" +*/
                "        \"subtotal\":\""+total+"\",\n" +
                "        \"subtotal_tax\":\"0.00\",\n" +
                "        \"total_tax\":\"0.00\",\n" +
                "        \"total\":\""+total+"\",\n" +
                "        \"quantity\":"+qte+"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        new GetDataTask(GetDataTask.METHOD_PUT, content) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length > 0 && (!result[0].isEmpty())) {
                    JsonElement jsonElement = new JsonParser().parse(result[0]).getAsJsonObject()
                            .get(Url.OBJ_NAME_ORDER);
                    if (jsonElement != null) {
                        Order order = mGson.fromJson(jsonElement, new TypeToken<Order>(){}.getType());
                        if (order!= null){
                            Log.d("Test", "Order: " + order.toString());
                            ((OnEditItem) mActivity).loadLastOrder(orderId);
                        } else {
                            Log.d("Test", "Could not load order N°: " + orderId);
                        }
                    }
                } else {
                    Log.d("Test", "Result is empty");
                }
            }
        }.execute(Url.getOrderId(orderId));
    }

}
