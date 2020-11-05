package com.app.theshineindia.profile;

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

public class ProfilePresenter extends BasePresenter {
    private ProfileActivity activity;

    ProfilePresenter(ProfileActivity activity) {
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
                try {
                    Toast.makeText(activity, new JSONObject(result).getString(WebServices.message), Toast.LENGTH_SHORT).show();
                    SP.setStringPreference(activity, SP.email, activity.et_email.getText().toString().trim());
                    IntentController.gotToActivityNoBack(activity, LoginActivity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void requestUpload() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().setMessage("Please wait...");
            getpDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));
            hashMap.put("email", activity.et_email.getText().toString().trim());
            hashMap.put("alternate_mobile", activity.et_wa_no.getText().toString().trim());

            getJfns().makeHttpRequest(WebServices.update_profile, "POST", hashMap, false, WebServices.request_url_no_1);
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
