package com.ufind.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseDataFragment<T extends Parcelable> extends BaseFragment {


    private boolean isLazyLoadDone = false;//懒加载是否完成
    private boolean isCreateViewDone = false;//view是否初始化完成


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //此方法 执行与 onCreateView之前
        super.setUserVisibleHint(isVisibleToUser);
        onLazyLoad();
    }

    @Override
    public boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected final View getContentView() {
        return View.inflate(getActivity(),getLayoutId(),null);
    }



    protected abstract int getLayoutId();

    /**
     * 当view 初始化完成 并且当前 fragment可见 并且懒加载没完成 执行此方法 加载数据
     */
    protected void onLazyLoad() {
        if (isCreateViewDone && getUserVisibleHint() && (!isLazyLoadDone)) {
            beginRefresh();
            isLazyLoadDone = true;
        }
    }

    @Override
    protected void afterCreateView(Bundle bundle) {
        super.afterCreateView(bundle);
        initViews(bundle);
        isCreateViewDone = true;
        onLazyLoad();
    }

    protected void initViews(Bundle bundle){

    }

    /**
     * 获取数据
     */
    protected abstract Observable<T> getData();

    /**
     * 填充数据
     */
    protected abstract void bindData(T t);

    protected void onError(Throwable throwable) {

    }

    @Override
    protected void onRefresh() {
        Subscription subscription = getData().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<T>() {
            @Override
            public void onCompleted() {
                endRefresh();
            }

            @Override
            public void onError(Throwable e) {
                BaseDataFragment.this.onError(e);
                endRefresh();
            }

            @Override
            public void onNext(T t) {
                bindData(t);
            }
        });
        addSubscription(subscription);
    }
}
