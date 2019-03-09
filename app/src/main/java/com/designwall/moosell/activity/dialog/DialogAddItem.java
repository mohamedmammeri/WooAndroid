package com.designwall.moosell.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import com.designwall.moosell.model.Product.Product;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogAddItem extends Dialog implements
        android.view.View.OnClickListener {

    public interface OnAddItem {
        boolean addItem(Product product, int qte);
    }

    private OnAddItem mActivity;
    private Product mProduct;


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

    public DialogAddItem(Context context, Product product) {
        super(context);
        this.mActivity = (OnAddItem) context;
        this.mProduct = product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_item);
        ButterKnife.bind(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
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
                if (mActivity.addItem(mProduct, Integer.valueOf( etQte.getText().toString() ))){
                    dismiss();
//                ((Activity) mActivity).finish();
                    return;
                }
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}
