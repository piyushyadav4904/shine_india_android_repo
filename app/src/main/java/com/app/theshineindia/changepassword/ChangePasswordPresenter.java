package com.app.theshineindia.changepassword;

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

public class ChangePasswordPresenter extends BasePresenter {
    ChangePasswordActivity activity;

    public ChangePasswordPresenter(ChangePasswordActivity activity) {
        super(activity);
        this.activity = activity;
    }

    void requestCP() {
        if (JSONFunctions.isInternetOn(activity)) {
            getpDialog().setTitle("Please wait...");
            getpDialog().setMessage("While we are updating your password");
            getpDialog().show();

            String url = WebServices.change_password;

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));
            hashMap.put("old_password", activity.et_old_password.getText().toString().trim());
            hashMap.put("password", activity.et_new_password.getText().toString().trim());

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
        switch (url_no) {
            case WebServices.request_url_no_1:
                if (SharedMethods.isSuccess(result, activity)) {
                    responseCP(result);
                }
                break;
        }
    }

    private void responseCP(String response) {
        try {
            Toast.makeText(activity, new JSONObject(response).getString(WebServices.message), Toast.LENGTH_SHORT).show();
            activity.finish();

        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
