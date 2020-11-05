package com.app.theshineindia.app_locker.mvp.contract;

import android.content.Context;

import com.app.theshineindia.app_locker.base.BasePresenter;
import com.app.theshineindia.app_locker.base.BaseView;
import com.app.theshineindia.app_locker.model.CommLockInfo;
import com.app.theshineindia.app_locker.mvp.p.LockMainPresenter;

import java.util.List;



public interface LockMainContract {
    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
