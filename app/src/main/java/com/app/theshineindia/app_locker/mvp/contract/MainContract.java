package com.app.theshineindia.app_locker.mvp.contract;

import android.content.Context;

import com.app.theshineindia.app_locker.base.BasePresenter;
import com.app.theshineindia.app_locker.base.BaseView;
import com.app.theshineindia.app_locker.model.CommLockInfo;

import java.util.List;



public interface MainContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void onDestroy();
    }
}
