package com.designwall.moosell.activity.category;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.designwall.moosell.R;
import com.designwall.moosell.activity.card.CardActivity;
import com.designwall.moosell.activity.order.OrderActivity;
import com.designwall.moosell.util.Helper;

public class OnMenuButtonClickListener implements View.OnClickListener {

    Context mContext;

    public OnMenuButtonClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.cardBtn:
                int lastOrderId = Helper.loadInt(mContext, Helper.LAST_ORDER_ID, -1);
                if (lastOrderId > 0){
                    Log.d("Test", "Last Order ID: " + lastOrderId);
                    Intent intent = new Intent(mContext, CardActivity.class);
                    intent.putExtra(Helper.LAST_ORDER_ID, lastOrderId);
                    mContext.startActivity(intent);
                } else {
                    Helper.showDialog(mContext, mContext.getString(R.string.order_not_found),
                            mContext.getString(R.string.no_order_found));
                    Log.d("Test", "lastOrderId not found: "+ lastOrderId);
                }
                break;
            case R.id.menuBtn:
                mContext.startActivity(new Intent(mContext, OrderActivity.class));
                break;
        }

    }
}
