package com.app.theshineindia.home;

import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.loaders.JSONFunctions;

import java.util.HashMap;

public class HomePresenter extends BasePresenter {
    HomeActivity activity;

    public HomePresenter(HomeActivity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getpDialog().isShowing()) {
            getpDialog().dismiss();
        }
        if (url_no == WebServices.request_url_no_1) {
            SharedMethods.isSuccess(result, activity);
        }
    }

    public void requestContacts() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().setMessage("Getting your emergency contacts from the server");
            getpDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            //hashMap.put("user_id", getSsp().getUSERID());

            getJfns().makeHttpRequest(WebServices.locationcity, "POST", hashMap, false, WebServices.request_url_no_1);

        } else {
            Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
