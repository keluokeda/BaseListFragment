package com.ufind.baselistfragment;

import android.view.View;
import android.widget.TextView;

import com.ufind.fragment.BaseDataFragment;

import rx.Observable;



public class DataFragment extends BaseDataFragment<ParcelableString> {
    private TextView mTextView;
    @Override
    protected Observable<ParcelableString> getData() {
        return Observable.just(new ParcelableString("hello"));
    }

    @Override
    protected void bindData(ParcelableString parcelableString) {
        mTextView = findViewById(R.id.tv_content);
        mTextView.setText(parcelableString.getContent());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.layout_text;
    }

    @Override
    public boolean isRefreshEnable() {
        return false;
    }
}
