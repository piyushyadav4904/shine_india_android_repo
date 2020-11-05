package com.app.theshineindia.login;

import android.view.View;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginPresenter extends BasePresenter {
    private LoginActivity activity;

    LoginPresenter(LoginActivity activity) {
        super(activity);
        this.activity = activity;
    }

    void login(String email, String password) {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("email", email);
            hashMap.put("password", password);
            hashMap.put("imei_code", activity.device_imei);

            getJfns().makeHttpRequest(WebServices.login, "POST", hashMap, false, WebServices.request_url_no_1);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getSpotDialog().isShowing()) {
            getSpotDialog().dismiss();
        }

        if (url_no == WebServices.request_url_no_1) {
            if (SharedMethods.isSuccess(result, activity)) {
                getLoginResponse(result);

            } else if (activity.iv_bg.getVisibility() == View.VISIBLE) {
                activity.iv_bg.setVisibility(View.GONE);
            }
        }
    }

    private void getLoginResponse(String response) {
        try {
            JSONObject user_jo = new JSONObject(response).getJSONObject("data");
            SP.setStringPreference(activity, SP.user_data, user_jo.toString());

            SP.setStringPreference(activity, SP.user_id, user_jo.getString("user_id"));
            SP.setStringPreference(activity, SP.email, user_jo.getString("email"));
            SP.setStringPreference(activity, SP.mobile, user_jo.getString("mobile"));
            //SP.setStringPreference(activity, SP.name, user_jo.getString("name"));

            if (user_jo.getString("is_expired").equalsIgnoreCase("0")) { // 1, means expired
                IntentController.sendIntent(activity, HomeActivity.class);
                activity.finish();
            } else {
                Alert.showError(activity, "Your package have been expired. Please contact your admin");
            }
        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
