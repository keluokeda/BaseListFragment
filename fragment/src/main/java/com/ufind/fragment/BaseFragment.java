package com.ufind.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseFragment extends Fragment {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mFrameLayout;
    private ProgressDialog mProgressDialog;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beforeCreateView(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(isRefreshEnable());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseFragment.this.onRefresh();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(getColorSchemeColors());
        mFrameLayout = findViewById(R.id.fl_content);
        addViewToContent(getContentView(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        afterCreateView(savedInstanceState);
        return mSwipeRefreshLayout;
    }

    protected int[] getColorSchemeColors() {
        return new int[]{Color.RED};
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.unsubscribe();
    }

    public final void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T findViewById(@IdRes int id) {
        return (T) mSwipeRefreshLayout.findViewById(id);
    }

    /**
     * 获取内容视图
     */
    protected abstract View getContentView();

    protected View getRootView() {
        return mSwipeRefreshLayout;
    }


    protected void addViewToContent(View view, FrameLayout.LayoutParams layoutParams) {
        mFrameLayout.addView(view, layoutParams);
    }

    /**
     * 创建 view  之前
     */
    protected void beforeCreateView(Bundle bundle) {

    }

    /**
     * 创建 view 之后
     */
    protected void afterCreateView(Bundle bundle) {

    }


    /**
     * 开始刷新视图内容
     */
    public void beginRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }


    /**
     * 刷新时回调的方法
     */
    protected abstract void onRefresh();

    /**
     * 是否可以下拉刷新
     */
    public abstract boolean isRefreshEnable();


    public void setRefreshEnable(boolean refreshEnable) {
        mSwipeRefreshLayout.setEnabled(refreshEnable);
    }

    protected final void endRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    protected final void showProgressDialog(CharSequence title) {
        if (mProgressDialog == null) {
            initProgressDialog();
        }
        mProgressDialog.setMessage(title);
        mProgressDialog.show();
    }

    protected final void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
    }

    protected final void showSnackBar(View view, CharSequence content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }

    public final void showSnackBar(String contentText, String actionText, final Runnable runnable) {
        Snackbar mSnackbar = Snackbar.make(getActivity().getWindow().getDecorView(), contentText, Snackbar.LENGTH_SHORT);
        mSnackbar.setAction(actionText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runnable.run();
            }
        });
        mSnackbar.setActionTextColor(Color.RED);
        //设置文字颜色为白色
        ((TextView) mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    protected final void hideSoftKeyBoard() {
        if (getActivity() != null) {
            InputMethodManager manager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);//隐藏软键盘
        }
    }
}
