package com.designwall.moosell.activity.category;


import android.content.Context;

/**
 * Created by SCIT on 3/10/2017.
 */

public interface iPresenterListCategory {
    void refreshData();

    void chooseCategory(int position);

    void loadData();
}
