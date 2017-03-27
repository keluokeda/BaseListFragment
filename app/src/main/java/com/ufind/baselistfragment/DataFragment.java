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
        mTextView.setText(parcelableString.getContent());
    }

    @Override
    protected View getContentView() {
        mTextView = new TextView(getActivity());
        return mTextView;
    }

    @Override
    public boolean isRefreshEnable() {
        return false;
    }
}
