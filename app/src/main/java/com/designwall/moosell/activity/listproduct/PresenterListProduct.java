package com.designwall.moosell.activity.listproduct;

import android.util.Log;

import com.designwall.moosell.R;
import com.designwall.moosell.config.Url;
import com.designwall.moosell.model.Product.Product;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.util.Helper;
import com.designwall.moosell.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project      : MooSell
 * Created by   : SCIT
 * On           : 3/15/2017.
 */

public class PresenterListProduct implements iPresenterListProduct {
    private Gson mGson;
    private List<Product> mProducts;
    private ListProductActivity mView;
    private String[] mPagesUrl;
    private int mCurrentPagePos;

    public PresenterListProduct(ListProductActivity view) {
        mGson = new Gson();
        mProducts = new ArrayList<>();
        mView = view;
        mCurrentPagePos = 0;
        mView.setupRecyclerView(mProducts);
    }

    @Override
    public void saveReceivedCategory(String categoryName, int categoryCount) {
        mPagesUrl = Helper.getPagesUrl(Url.getProductsInCategory(categoryName), categoryCount);
    }

    @Override
    public void loadPage(int pagePos) {
        final Type productsType = new TypeToken<List<Product>>(){}.getType();
        if (Network.isAvailable(mView)) {
            mCurrentPagePos = pagePos;
            if (mCurrentPagePos < mPagesUrl.length) {
                new GetDataTask(GetDataTask.METHOD_GET) {
                    @Override
                    protected void onPostExecute(String[] result) {
                        super.onPostExecute(result);

                        int lastPos = mProducts.size();
                        for (String aResult : result) {
//                            Log.v("Test", aResult);
                            JsonElement jsonProducts    = new JsonParser().parse(aResult).getAsJsonObject().get("products");
                            List<Product> newProducts = mGson.fromJson(jsonProducts, productsType);
                            mProducts.addAll(newProducts);
                        }

                        mView.notifyItemRangeInserted(lastPos, mProducts.size() - lastPos);

                        //Hide Loading View
                        mView.showLoadingView(false);
                    }
                }.execute(mPagesUrl[mCurrentPagePos]);
            }else{
                mView.showMessage(mView.getString(R.string.msg_no_more_data));
            }
        }else{
            mView.showMessage(mView.getString(R.string.msg_no_connection));
        }
    }

    @Override
    public void loadNextPage(){
        mCurrentPagePos++;
        loadPage(mCurrentPagePos);
    }

    @Override
    public void refreshData() {
        String[] requestUrls = Arrays.copyOfRange(mPagesUrl, 0, mCurrentPagePos+1);

        new GetDataTask(GetDataTask.METHOD_GET){
            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                try {
                    int oldSize = mProducts.size();
                    mProducts.clear();
                    mView.notifyItemRangeRemoved(0, oldSize);

                    for (String aResult : result) {
//                        JSONArray jsonArray = new JSONObject(aResult).getJSONArray("products");
                        JsonElement jsonProducts = new JsonParser().parse(aResult).getAsJsonObject().get("products");
                        mProducts.addAll((List<Product>) mGson.fromJson(jsonProducts, new TypeToken<List<Product>>() {}.getType()));
//                        Log.v("Test", "Nbr of Products: " + jsonArray.length());
//                        Log.v("Test", aResult);
                    }
                    mView.notifyItemRangeInserted(0, mProducts.size());
                } catch (/*JSON*/Exception e) {
                    Log.e("Test", "Error: " + e.getMessage());
                }
            }
        }.execute(requestUrls);
    }
}
