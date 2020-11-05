package com.app.theshineindia.forgot_password;

import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPresenter extends BasePresenter {
    private ForgotActivity activity;

    ForgotPresenter(ForgotActivity activity) {
        super(activity);
        this.activity = activity;
    }

    void requestForgotPassword() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().show();

            String url = WebServices.forget_password;

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("email", activity.et_email.getText().toString().trim());

            getJfns().makeHttpRequest(url, "POST", hashMap, false, WebServices.request_url_no_1);

        } else {
            Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getpDialog().isShowing()) {
            getpDialog().dismiss();
        }
        if (url_no == WebServices.request_url_no_1) {
            if (SharedMethods.isSuccess(result, activity))
                responseForgotPassword(result);
        }
    }

    private void responseForgotPassword(String response) {
        try {
            Toast.makeText(activity, new JSONObject(response).getString(WebServices.message), Toast.LENGTH_LONG).show();
            activity.finish();
        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
