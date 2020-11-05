package com.app.theshineindia.sos;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.home.HomeActivity;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SosPresenter extends BasePresenter {
    private SOSActivity activity;

    SosPresenter(SOSActivity activity) {
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
                responseContact(result);
            }

        } else if (url_no == WebServices.request_url_no_2) {
            if (SharedMethods.isSuccess(result, activity)) {
                responseAddContact(result);
            }

        } else if (url_no == WebServices.request_url_no_3) {
            if (SharedMethods.isSuccess(result, activity)) {
                responseChangeStatus(result);
            }

        } else if (url_no == WebServices.request_url_no_4) {
            if (SharedMethods.isSuccess(result, activity)) {
                responseDeleteContact(result);
            }
        }
    }


    void requestContacts() {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));

            getJfns().makeHttpRequest(WebServices.sos_list, "POST", hashMap, false, WebServices.request_url_no_1);
        } else {
            //Alert.showError(activity, activity.getString(R.string.no_internet));
            String sos_response = SP.getStringPreference(activity, SP.sos_response);
            if (sos_response != null) {
                responseContact(sos_response);
            }
        }
    }

    private void responseContact(String response) {
        try {
            SP.setStringPreference(activity, SP.sos_response, response);

            JSONArray data_ja = SharedMethods.getResponseArray(response, activity, WebServices.data, activity.mEmergencyRecyclerView);
            if (data_ja == null) {
                return;
            }

            activity.emergencyList.clear();
            activity.active_contact_list.clear();

            for (int i = 0; i < data_ja.length(); i++) {
                JSONObject contact_jo = data_ja.getJSONObject(i);

                Contact contact = new Contact(
                        contact_jo.getString("name"),
                        contact_jo.getString("mobile_no"),
                        contact_jo.getString("sos_id"),
                        contact_jo.getString("status")
                );
                activity.emergencyList.add(contact);

                if (contact_jo.getString("status").equalsIgnoreCase("active")) {
                    activity.active_contact_list.add(contact_jo.getString("mobile_no"));
                }
            }

            activity.setAdapter();
            SP.saveArrayList(activity, SP.active_sos_contact_list, activity.active_contact_list);

        } catch (JSONException ex) {
            Alert.showError(activity, ex.getMessage());
        }
    }


    void requestAddContacts(String name, String mobile_no) {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));
            hashMap.put("name", name);
            hashMap.put("mobile_no", mobile_no);

            getJfns().makeHttpRequest(WebServices.add_sos, "POST", hashMap, false, WebServices.request_url_no_2);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }

    private void responseAddContact(String response) {
        requestContacts();

        activity.et_name.getText().clear();
        activity.et_mobile.getText().clear();
        activity.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    void requestChangeStatus(String sos_id, String status) {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));
            hashMap.put("sos_id", sos_id);
            hashMap.put("status", status);

            getJfns().makeHttpRequest(WebServices.active_inactive_sos, "POST", hashMap, false, WebServices.request_url_no_3);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }

    private void responseChangeStatus(String response) {
        try {
            Alert.showMessage(activity, new JSONObject(response).getString("message"));
            requestContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void requestDeleteContact(String sos_id) {
        if (JSONFunctions.isInternetOn(activity)) {
            getSpotDialog().show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(activity, SP.user_id));
            hashMap.put("sos_id", sos_id);

            getJfns().makeHttpRequest(WebServices.delete_sos, "POST", hashMap, false, WebServices.request_url_no_4);
        } else {
            Alert.showError(activity, activity.getString(R.string.no_internet));
        }
    }

    private void responseDeleteContact(String response) {
        try {
            Alert.showMessage(activity, new JSONObject(response).getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
