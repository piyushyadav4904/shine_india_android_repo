package com.app.theshineindia.Plan;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.registration.RegistrationActivity;
import com.app.theshineindia.sos.Contact;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlanPresenter extends BasePresenter {
    SelectPlanActivity activity;

    public PlanPresenter(SelectPlanActivity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (getSpotDialog().isShowing()) {
            getSpotDialog().dismiss();
        }
        if (url_no == WebServices.request_url_no_1) {
            if (SharedMethods.isSuccess(result, activity)) {
                responsePlanList(result);
            }
        }
    }


    void requestPlanList() {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            String url = WebServices.plan_list;


            getJfns().makeHttpRequest(url, "POST", WebServices.request_url_no_1);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }


    private void responsePlanList(String response) {
        try {
            JSONArray data_ja = SharedMethods.getResponseArray(activity, response, "category");
            if (data_ja == null) {
                return;
            }

            activity.plan_list.clear();

            for (int i = 0; i < data_ja.length(); i++) {
                JSONObject contact_jo = data_ja.getJSONObject(i);

                Plan contact = new Plan(
                        contact_jo.getString("id"),
                        contact_jo.getString("title")
                );
                activity.plan_list.add(contact);
            }

            activity.setPlanList();

        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }
}
