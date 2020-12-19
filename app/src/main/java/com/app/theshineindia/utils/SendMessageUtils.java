package com.app.theshineindia.utils;

import android.telephony.SmsManager;
import android.util.Log;

public class SendMessageUtils {
    private static final String TAG = "SendMessageUtils";

    public static void SendMessage(String phone_number, String msg){
        Log.d(TAG, "SendMessage_phone: "+phone_number);
        Log.d(TAG, "SendMessage_msg: "+msg);
        //String tempMsg = "new sim insert";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_number, null, msg, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
