package com.ufind.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseListFragment<T extends Parcelable> extends BaseFragment {
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<T, BaseViewHolder> mBaseQuickAdapter;
    private int currentIndex = 1;

    private boolean isLazyLoadDone = false;//懒加载是否完成
    private boolean isCreateViewDone = false;//view是否初始化完成

    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //此方法 执行与 onCreateView之前
        super.setUserVisibleHint(isVisibleToUser);
        onLazyLoad();
    }

    @Override
    public boolean isRefreshEnable() {
        return true;
    }


    public boolean isLoadMoreEnable() {
        return true;
    }

    /**
     * 当view 初始化完成 并且当前 fragment可见 并且懒加载没完成 执行此方法 加载数据
     */
    protected void onLazyLoad() {
        if (isCreateViewDone && getUserVisibleHint() && (!isLazyLoadDone)) {
            beginRefresh();
            isLazyLoadDone = true;
        }
    }

    @CallSuper
    @Override
    protected void afterCreateView(Bundle bundle) {
        isCreateViewDone = true;
        init();
        initViews(bundle);
        onLazyLoad();
    }

    protected void initViews(Bundle bundle) {

    }

    @Override
    protected final View getContentView() {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(getActivity());
        }
        return mRecyclerView;
    }


    private void init() {
        initAdapter();

        mRecyclerView.setAdapter(mBaseQuickAdapter);

        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemClick(view, i, getEntity(i));
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemLongClick(view, i, getEntity(i));
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemChildClick(view, i, getEntity(i));
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                BaseListFragment.this.onItemChildLongClick(view, i, getEntity(i));
            }
        });
    }

    private void initAdapter() {
        mBaseQuickAdapter = new BaseQuickAdapter<T, BaseViewHolder>(getItemLayoutId(), new ArrayList<T>(0)) {
            @Override
            protected void convert(BaseViewHolder helper, T item) {
                convertData(helper, item);
            }
        };
        if (isLoadMoreEnable()) {
            mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadData();
                }
            }, mRecyclerView);
        }
        mBaseQuickAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        mBaseQuickAdapter.openLoadAnimation(getRecyclerViewLoadAnimation());
    }

    /**
     * item动画效果
     */
    protected int getRecyclerViewLoadAnimation() {
        return BaseQuickAdapter.SLIDEIN_BOTTOM;
    }

    @CallSuper
    @Override
    protected void onRefresh() {
        currentIndex = 1;
        mBaseQuickAdapter.setNewData(new ArrayList<T>(0));
        loadData();
    }

    private void loadData() {
        Subscription s = getDataList(currentIndex).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<List<T>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                loadDataFail(e);
            }

            @Override
            public void onNext(List<T> ts) {
                if (ts == null || ts.isEmpty()) {
                    if (mBaseQuickAdapter.getData().isEmpty()) {
                        //没有数据
                        if (getEmptyViewResId() != 0 && mBaseQuickAdapter.getEmptyView() == null) {
                            Logger.d("set empty view");
                            mBaseQuickAdapter.setEmptyView(getEmptyViewResId());
                        }
                    }
                    loadDataEnd();
                    return;
                }
                currentIndex++;
                addDataToRecyclerView(ts);
                loadDataComplete();
            }
        });
        addSubscription(s);


    }

    /**
     * 没有数据的时候展示的layout
     */
    protected int getEmptyViewResId() {
        return 0;
    }


    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected BaseQuickAdapter<T, BaseViewHolder> getTBaseQuickAdapter() {
        return mBaseQuickAdapter;
    }


    /**
     * 加载数据完成 并且还有数据
     */
    @CallSuper
    protected void loadDataComplete() {
        mBaseQuickAdapter.loadMoreComplete();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 加载数据完成 没有更多数据
     */
    @CallSuper
    protected void loadDataEnd() {
        mBaseQuickAdapter.loadMoreEnd();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 加载数据失败
     */
    @CallSuper
    protected void loadDataFail(Throwable throwable) {
        mBaseQuickAdapter.loadMoreFail();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 添加数据到 recycler view 中
     */
    protected void addDataToRecyclerView(List<T> list) {
        mBaseQuickAdapter.addData(list);
    }


    /**
     * 给item绑定数据
     */
    protected abstract void convertData(BaseViewHolder baseViewHolder, T t);

    /**
     * recycler view item id
     */
    protected abstract int getItemLayoutId();

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    //adapter 相关操作
    protected T getEntity(int position) {
        return mBaseQuickAdapter.getItem(position);
    }

    protected void notifyItemChanged(int position) {
        mBaseQuickAdapter.notifyItemChanged(position);
    }

    protected void removeItem(int position) {
        mBaseQuickAdapter.remove(position);
    }

    protected void addData(List<T> list) {
        mBaseQuickAdapter.addData(list);
    }

    protected void setNewData(List<T> list) {
        mBaseQuickAdapter.setNewData(list);
    }


    //item 事件

    protected void onItemClick(View view, int position, T entity) {

    }

    protected void onItemLongClick(View view, int position, T entity) {

    }

    protected void onItemChildClick(View view, int position, T entity) {

    }

    protected void onItemChildLongClick(View view, int position, T entity) {

    }


    /**
     * 获取列表数据
     */
    protected abstract Observable<List<T>> getDataList(int currentIndex);

}
