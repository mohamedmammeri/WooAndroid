package com.designwall.moosell.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.designwall.moosell.R;
import com.designwall.moosell.adapter.OrderArrayAdapter;
import com.designwall.moosell.db.DatabaseHelper;
import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.util.Helper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity
        implements OrderArrayAdapter.OnOrderClick {

    @BindView(R.id.lvOrders) ListView lvOrders;
    private DatabaseHelper dbHelper;
    private Dao<Order, Integer> orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        dbHelper = new DatabaseHelper(this);
        List<Order> orders = new ArrayList<Order>();
        try {
            orderDao = dbHelper.getDao();
            orders.addAll( orderDao.queryForAll() );
            Log.d("Test", "Nbr of Orders: " + orders.size());
            for (Order o: orders){
                Log.d("Test", "Order: " + o.toString());
            }

        } catch (SQLException e) {
            Log.e("Test", "Error: " + e.getMessage());
        }

        if (orders.isEmpty()){
            Helper.showDialog(this, getString(R.string.no_order),
                    getString(R.string.order_history_empty));
            finish();
            return;
        }

        OrderArrayAdapter adapter = new OrderArrayAdapter(this, orders);
        lvOrders.setAdapter(adapter);

    }

    @Override
    public void showOrderDetail(int orderId) {
        Intent orderDetail = new Intent(this, OrderDetailActivity.class);
        orderDetail.putExtra(Helper.HISTORY_ORDER_ID, orderId);
        startActivity(orderDetail);
    }

/*
    // Delete Order Record from local database
    @Override
    public boolean deleteOrderRecord(int orderId) {
        try {
//            Order order = orderDao.queryForId(orderId);
            List<Order> orders = orderDao.queryForEq("id", orderId);
            if (orders != null) {
//                return orderDao.delete(order) > 0;
                return orderDao.delete(orders) > 0;
//                return orderDao.deleteById(orders) > 0;
            }
        } catch (SQLException e) {
            Helper.toastLong(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }*/

}
