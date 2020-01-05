package com.example.user.chatroom.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {
    protected BaseActivity mBaseActivity;
    protected View mRootView;
    protected T mPresenter;
    Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutResourceId(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initData(getArguments());
        initView();
        setListener();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = getInstance();
        if (mPresenter != null){
            mPresenter.attachView(this);
        }
    }

    protected abstract int setLayoutResourceId();

    protected abstract T getInstance();

    /**
     *
     *
     * @param bundle
     */
    protected abstract void initData(Bundle bundle);

    protected abstract void initView();

    protected abstract void setListener();

    protected void showToast(String msg) {
        Toast.makeText(mBaseActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        if (mPresenter != null){
            mPresenter.detachView();
        }
        super.onDestroyView();
    }


    protected void startActivity(Class clazz) {
        Intent intent = new Intent(mBaseActivity, clazz);
        startActivity(intent);
    }
}
