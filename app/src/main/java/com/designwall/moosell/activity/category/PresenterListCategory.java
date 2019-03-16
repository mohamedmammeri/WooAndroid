package com.designwall.moosell.activity.category;

import android.util.Log;

import com.designwall.moosell.R;
import com.designwall.moosell.task.GetDataTask;
import com.designwall.moosell.model.Product.ProductCategory;
import com.designwall.moosell.util.Helper;
import com.designwall.moosell.util.Network;
import com.designwall.moosell.config.Url;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Project      : MooSell
 * Created by   : SCIT
 * On           : 3/10/2017.
 */

public class PresenterListCategory implements iPresenterListCategory {
    private Gson mGson;
    private List<ProductCategory> mCategories;
    private MainActivity mView;

    public PresenterListCategory(MainActivity view) {
        mGson        = new Gson();
        mCategories  = new ArrayList<>();
        mView        = view;
        mView.setupRecyclerView(mCategories);
    }

    @Override
    public void refreshData() {
        mCategories.clear();
        mView.notifyItemRangeRemoved(0, 0);
        loadData();
    }

    @Override
    public void chooseCategory(int position) {
        ProductCategory category = mCategories.get(position);
        mView.showProductsInCategory(category.getName(), category.getCount());
    }

    @Override
    public void loadData() {
        if (Network.isAvailable(mView)){
            Log.d("Test", "Loading data ...");
            new GetDataTask(GetDataTask.METHOD_GET){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.d("Test", "Pre-Executing...");
                    mView.showRecyclerViewShimmer();
                }
                @Override
                protected void onPostExecute(String[] result) {
                    super.onPostExecute(result);
//                    Log.dialog("Test", "Nbr of result: " + result.length);
                    if (result.length > 0) {
                        JsonElement jsonElement = new JsonParser().parse(result[0]).getAsJsonObject().get("errors");
                        if (jsonElement != null){
                            JsonArray jsonElements = jsonElement.getAsJsonArray();
                            for (int i = 0; i < jsonElements.size(); i++) {
                                JsonObject errorObject = jsonElements.get(i).getAsJsonObject();
                                Log.d("Test", errorObject.get("code").toString());
                                mView.setRefreshing(false);
                                mView.showDialog( mView.getString(R.string.error), errorObject.getAsJsonObject().get("message").toString());
                            }
                            return;
                        }
                        JsonElement jsonCategories = new JsonParser().parse(result[0]).getAsJsonObject().get(Url.OBJ_NAME_CATEGORIES);
                        mCategories.addAll((List<ProductCategory>) mGson.fromJson(jsonCategories, new TypeToken<List<ProductCategory>>() {
                        }.getType()));
                        mView.notifyItemRangeInserted(0, mCategories.size());
                        mView.setRefreshing(false);
                        mView.hideRecyclerViewShimmer();
                    } else {
                        Log.d("Test", "Result is empty.");
                    }
                }

            }.execute(Url.getProductCategories());

        } else{
            mView.setRefreshing(false);
            mView.showDialog(mView.getString(R.string.error), mView.getString(R.string.msg_no_connection));
        }
    }
}
