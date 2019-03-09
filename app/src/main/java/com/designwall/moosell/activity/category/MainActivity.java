package com.designwall.moosell.activity.category;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.designwall.moosell.activity.card.CardActivity;
import com.designwall.moosell.adapter.ListCategoryAdapter;
import com.designwall.moosell.config.Constant;
import com.designwall.moosell.db.DatabaseHelper;
import com.designwall.moosell.model.Product.ProductCategory;
import com.designwall.moosell.activity.listproduct.ListProductActivity;
import com.designwall.moosell.R;
import com.designwall.moosell.util.Helper;
import com.designwall.moosell.util.RecyclerViewItemClicked;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project      : MooSell
 * Created by   : SCIT
 * On           : 3/10/2017.
 */

public class MainActivity extends AppCompatActivity implements
        iViewListCategory,
        RecyclerViewItemClicked,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.categoriesRecyclerView)  ShimmerRecyclerView mCategoriesRecyclerView;
    @BindView(R.id.swipeRefreshLayout)      SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar)                 Toolbar mToolbar;
    @BindView(R.id.menuBtn)                 ImageButton menuBtn;
    @BindView(R.id.cardBtn)                 ImageButton cardBtn;
    @BindView(R.id.appTitle)                TextView appTitle;

    private ListCategoryAdapter mAdapter;
    private iPresenterListCategory mPresenter;

    private DatabaseHelper dbHelper;
    private Dao orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

//        dbHelper = new DatabaseHelper(this);
//        try {
//            orderDao = dbHelper.getDao();
//        } catch (SQLException e) {
//            Log.e("Test", e.getMessage());
//            e.printStackTrace();
//        }

        appTitle.setText(Constant.SHOP_NAME);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mPresenter = new PresenterListCategory(this);
        mPresenter.loadData();

        cardBtn.setOnClickListener(new OnMenuButtonClickListener(this));
        menuBtn.setOnClickListener(new OnMenuButtonClickListener(this));

    }

    /*@OnClick(R.id.cardBtn)
    public void menuButtonClick(View view){
    }*/

    @Override
    public void setupRecyclerView(List<ProductCategory> categories) {
        mAdapter = new ListCategoryAdapter(this, categories);
        mAdapter.setOnItemClickedListener(this);

        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCategoriesRecyclerView.setHasFixedSize(true);
        mCategoriesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void notifyItemRangeInserted(int start, int size) {
        mAdapter.notifyItemRangeInserted(start, size);
    }

    @Override
    public void notifyItemRangeRemoved(int start, int size) {
        mAdapter.notifyItemRangeRemoved(start, size);
    }

    @Override
    public void showRecyclerViewShimmer() {
        mCategoriesRecyclerView.showShimmerAdapter();
    }

    @Override
    public void hideRecyclerViewShimmer() {
        mCategoriesRecyclerView.hideShimmerAdapter();
    }

    @Override
    public void setRefreshing(boolean on) {
        mSwipeRefreshLayout.setRefreshing(on);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.refreshData();
    }

    @Override
    public void onRecyclerViewItemClicked(int position, View v) {
        mPresenter.chooseCategory(position);
    }

    @Override
    public void showProductsInCategory(String categoryName, int categoryCount) {
        Intent intent = new Intent(this, ListProductActivity.class);
        intent  .putExtra(Constant.INTENT_CATEGORY_NAME, categoryName)
                .putExtra(Constant.INTENT_CATEGORY_COUNT, categoryCount);
        startActivity(intent);
    }

    @Override
    public void showDialog(String title, String message) {
        Helper.showDialog(this, title, message);
    }
}
