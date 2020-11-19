package com.app.theshineindia.home;

import android.content.Intent;
import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import org.json.JSONException;
import org.json.JSONObject;

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

        if (getSpotDialog().isShowing()) {
            getSpotDialog().dismiss();
        }

        if (url_no == WebServices.request_url_no_1) {
            SharedMethods.isSuccess(result, activity);
        } else if (url_no == WebServices.request_url_no_2) {
            getLogoutResponse(result);
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

    void logout(String userId) {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", userId);

            getJfns().makeHttpRequest(WebServices.logout, "POST", hashMap, false, WebServices.request_url_no_2);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }

    private void getLogoutResponse(String response) {
        try {
            if (new JSONObject(response).getString("message").equalsIgnoreCase("success")) {
                if (SP.logoutFunction(activity)) {
                    //stop all services

                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
