package com.app.theshineindia.app_presenter;

import android.content.Context;
import android.widget.Toast;

import com.app.theshineindia.baseclasses.BasePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MessagePresenter extends BasePresenter {
    private Context context;
    private String request_for;

    public MessagePresenter(Context context, String request_for) {
        super(context);
        this.context = context;
        this.request_for = request_for;
    }


    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (url_no == WebServices.request_url_no_1) {
            if (SharedMethods.isSuccess(result, context)) {
                responseSendSosMessage(result);
            }
        }
    }


    public void requestSendSosMessage(String request_for) {
        if (!JSONFunctions.isInternetOn(context)) return;

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", SP.getStringPreference(context, SP.user_id));
        hashMap.put("address", SP.getStringPreference(context, SP.last_address));
        hashMap.put("lat", SP.getStringPreference(context, SP.last_latitude));
        hashMap.put("long", SP.getStringPreference(context, SP.last_longitude));
        hashMap.put("flag", request_for);

        getJfns().makeHttpRequest(WebServices.send_sos_sms, "POST", hashMap, false, WebServices.request_url_no_1);
    }

    private void responseSendSosMessage(String response) {
        try {
            Toast.makeText(context, new JSONObject(response).getString("message"), Toast.LENGTH_SHORT).show();

            if (request_for != null && request_for.equals("sim_change")) {
                SP.setBooleanPreference(context, SP.is_sim_card_changed, false);
                SP.setBooleanPreference(context, SP.is_sim_tracker_on, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
