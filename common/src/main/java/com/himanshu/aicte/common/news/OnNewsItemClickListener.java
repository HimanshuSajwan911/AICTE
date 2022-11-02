package com.himanshu.aicte.common.news;

import android.view.View;

public interface OnNewsItemClickListener {
    int SAVED_NEWS = 1;
    int CARD = 2;
    int HEADLINE = 3;
    int BODY = 4;
    void onNewsItemClick(int position, int itemType, View view);

    void onNewsItemLongClick(int position, int itemType, View view);
}
