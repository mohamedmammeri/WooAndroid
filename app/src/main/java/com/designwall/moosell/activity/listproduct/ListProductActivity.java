package com.designwall.moosell.activity.listproduct;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.designwall.moosell.R;
import com.designwall.moosell.activity.category.OnMenuButtonClickListener;
import com.designwall.moosell.activity.dialog.DialogAddItem;
import com.designwall.moosell.adapter.ListProductAdapter;
import com.designwall.moosell.config.Constant;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.model.Product.Product;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.util.Helper;
import com.designwall.moosell.util.ItemOffsetDecoration;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListProductActivity extends AppCompatActivity
        implements iViewListProduct,
        SwipeRefreshLayout.OnRefreshListener,
        ListProductAdapter.ItemClickListener,
        DialogAddItem.OnAddItem {

    @BindView(R.id.recyclerView) ShimmerRecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.loadingView) View mLoadingView;
    @BindView(R.id.menuBtn) ImageButton menuBtn;
    @BindView(R.id.cardBtn) ImageButton cardBtn;


    private iPresenterListProduct mPresenter;
    private ListProductAdapter mAdapter;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        ButterKnife.bind(this);

        mGson = new Gson();
        mPresenter = new PresenterListProduct(this);
        mPresenter.saveReceivedCategory(
                getIntent().getStringExtra(Constant.INTENT_CATEGORY_NAME),
                getIntent().getIntExtra(Constant.INTENT_CATEGORY_COUNT, 0)
        );

        //Load first products page
        mPresenter.loadPage(0);

        cardBtn.setOnClickListener(new OnMenuButtonClickListener(this));
        menuBtn.setOnClickListener(new OnMenuButtonClickListener(this));

    }

    @Override
    public void onRefresh() {
        mPresenter.refreshData();
    }

    @Override
    public void setupRecyclerView(List<Product> products) {
        int col = 4;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            col = 2;
        }

        mAdapter = new ListProductAdapter(this, products);
        mAdapter.setClickListener(this);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, col);

        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() -1 ) {
                        showLoadingView(true);
                        mPresenter.loadNextPage();
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void notifyItemRangeInserted(int start, int size) {
//        Log.v("Start", start+"");
//        Log.v("Size", size+"");
        mAdapter.notifyItemRangeInserted(start, size);
    }

    @Override
    public void notifyItemRangeRemoved(int start, int size) {
        mAdapter.notifyItemRangeRemoved(start, size);
    }

    @Override
    public void showLoadingView(boolean on) {
        mLoadingView.setVisibility( on? View.VISIBLE: View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
//        Log.d("Test", "Item: " + mAdapter.getProduct(position).toString());
        new DialogAddItem(this, mAdapter.getProduct(position)).show();
    }

    @Override
    public boolean addItem(Product product, int qte) {
        Log.d("Test", "Adding: " + product.toString()+", qte: " + qte);
        // We have to check Quantity first...
        if (product.getStock_quantity() > 0 && qte > product.getStock_quantity()){
            Helper.showDialog(this, "Qte", "Insufficient quantity in Stock.");
            return false;
        }
        String httpMethod = GetDataTask.METHOD_POST;
        String url = Url.getOrders();
        int lastOrderId = Helper.loadInt(this, Helper.LAST_ORDER_ID, -1);
        if (lastOrderId > 0){
            Log.d("Test", "Last Order ID: " + lastOrderId);
            httpMethod = GetDataTask.METHOD_PUT;
            url = Url.getOrderId(lastOrderId);
        }
        String requestBody = "{\n" +
                "\"order\": {" +
                /*"  \"customer_id\": 0," + */
                "  \"line_items\": [" +
                "    {" +
                "      \"product_id\":" + product.getId()+"," +
                "      \"quantity\":" + qte+
                "    }]" +
                "}}";
        final boolean[] resultReq = {false};
        // Send POST Request
        new GetDataTask(httpMethod, requestBody) {
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                if (result.length == 0){
                    Log.d("Test", "No data!");
                } else {
                    if (!result[0].isEmpty()) {
                        Log.d("Test", "Result: " + result[0]);
                        JsonElement jsonOrder = new JsonParser().parse(result[0]).getAsJsonObject().get(Url.OBJ_NAME_ORDER);
                        Order order = mGson.fromJson(jsonOrder, new TypeToken<Order>(){}.getType());
                        Toast.makeText(getApplicationContext(), "Order ID°" +order.getId(), Toast.LENGTH_SHORT).show();
                        Log.d("Test", order.toString());
                        if (Helper.saveInt(ListProductActivity.this, Helper.LAST_ORDER_ID, order.getId())){
                            Log.d("Test", "Last Order ID: " + order.getId()+" saved.");
                        }
                        resultReq[0] = true;
                    } else {
                        Log.d("Test", "Result is empty");
                    }
                }
                showLoadingView(false);
            }

        }.execute(url);
        return resultReq[0];
    }
}
