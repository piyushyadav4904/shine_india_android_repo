package com.app.theshineindia.registration;

import android.provider.Settings;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationPresenter extends BasePresenter {
    RegistrationActivity activity;

    public RegistrationPresenter(RegistrationActivity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getpDialog().isShowing()) {
            getpDialog().dismiss();
        }
        if (url_no == WebServices.request_url_no_1) {
            if (SharedMethods.isSuccess(result, activity)) {
                responseRegistration(result);
            }
        }
    }


    void requestRegistration() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().setMessage("Registration on process");
            getpDialog().show();

            String url = WebServices.registration;

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", activity.et_username.getText().toString().trim());
            hashMap.put("email", activity.et_email.getText().toString().trim());
            hashMap.put("mobile", activity.et_mobile.getText().toString().trim());
            hashMap.put("alternate_mobile", activity.et_wa_no.getText().toString().trim());
            hashMap.put("password", activity.et_password.getText().toString().trim());
            hashMap.put("plan_id", activity.plan_id);
            hashMap.put("secret_code", activity.et_secret_code.getText().toString().trim());
            hashMap.put("vendor_id", activity.et_vendor_id.getText().toString().trim());
            hashMap.put("imei_code", Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

            getJfns().makeHttpRequest(url, "POST", hashMap, false, WebServices.request_url_no_1);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }


    private void responseRegistration(String response) {
        try {
            JSONObject user_jo = new JSONObject(response).getJSONObject("data");
            SP.setStringPreference(activity, SP.user_data, user_jo.toString());

            SP.setStringPreference(activity, SP.user_id, user_jo.getString("user_id"));
            SP.setStringPreference(activity, SP.email, user_jo.getString("email"));
            SP.setStringPreference(activity, SP.mobile, user_jo.getString("mobile"));
            SP.setStringPreference(activity, SP.name, user_jo.getString("name"));

            SP.setStringPreference(activity, SP.password, activity.et_password.getText().toString().trim());

            IntentController.sendIntent(activity, HomeActivity.class);
            activity.finish();

        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
