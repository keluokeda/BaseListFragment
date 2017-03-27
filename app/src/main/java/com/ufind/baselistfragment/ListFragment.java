package com.ufind.baselistfragment;


import com.chad.library.adapter.base.BaseViewHolder;
import com.ufind.fragment.BaseListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;


public class ListFragment extends BaseListFragment<ParcelableString> {


    @Override
    public boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected void convertData(BaseViewHolder baseViewHolder, ParcelableString parcelableString) {
        baseViewHolder.setText(R.id.tv_content, parcelableString.getContent());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_content;
    }

    @Override
    protected Observable<List<ParcelableString>> getDataList(int currentIndex) {
        if (currentIndex == 5) {
            List<ParcelableString> list = new ArrayList<>(0);
            return Observable.just(list).delay(1000, TimeUnit.MILLISECONDS);
        }
        int start = (currentIndex - 1) * 20;
        int end = start + 20;
        List<ParcelableString> list = new ArrayList<>(20);
        for (int i = start; i < end; i++) {
            list.add(new ParcelableString(String.valueOf(i)));
        }
        return Observable.just(list).delay(2000, TimeUnit.MILLISECONDS);
    }
}
