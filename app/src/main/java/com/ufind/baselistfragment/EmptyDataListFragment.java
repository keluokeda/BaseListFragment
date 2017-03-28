package com.ufind.baselistfragment;


import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.ufind.fragment.BaseListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class EmptyDataListFragment extends BaseListFragment<ParcelableString> {
    @Override
    protected void convertData(BaseViewHolder baseViewHolder, ParcelableString parcelableString) {
        Logger.d("convertData "+parcelableString.getContent());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_content;
    }

    @Override
    protected Observable<List<ParcelableString>> getDataList(int currentIndex) {
        List<ParcelableString> list = new ArrayList<>(0);
        return Observable.just(list).delay(1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected int getEmptyViewResId() {
        return R.layout.layout_empty_data;
    }
}
