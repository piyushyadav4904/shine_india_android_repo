package com.app.theshineindia.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DualSimManager {

    public static final String KEY_FOR_SIM_1 = "KEY_FOR_SIM_1";
    public static final String KEY_FOR_SIM_2 = "KEY_FOR_SIM_2";
    private static final String TAG = "DualSimManager";

    // before calling this function please check runtime permission for phone_state
    public static Map<String, String> getSimSerialNumbersICCID(Context context){
        String sim1SerialNumber = null;
        String sim2SerialNumber = null;
        HashMap<String, String> stringStringMap = new HashMap<>();
        TelephonyManager telMngr = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        try{
            sim1SerialNumber = telMngr.getSimSerialNumber();
        }catch (Exception e){
            Log.d(TAG, "getSimSerialNumbersICCID_sim1: "+e.getMessage());
        }
        try{
            sim2SerialNumber = getDeviceIdBySlot(context, "getSimSerialNumber", 1);
        }catch (Exception e){
            Log.d(TAG, "getSimSerialNumbersICCID_sim2: "+e.getMessage());
        }
        if (sim1SerialNumber!=null){
            stringStringMap.put(KEY_FOR_SIM_1, sim1SerialNumber);
        }
        if (sim2SerialNumber!=null){
            stringStringMap.put(KEY_FOR_SIM_2, sim2SerialNumber);
        }
        return stringStringMap;
    }



    // before calling this function please check runtime permission for phone_state
    public static Map<String, String> getSimSimOperatorName(Context context){
        String sim1OperatorName = null;
        String sim2OperatorName = null;
        HashMap<String, String> stringStringMap = new HashMap<>();
        TelephonyManager telMngr = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        sim1OperatorName = telMngr.getSimOperatorName();
        try{
            sim2OperatorName = getDeviceIdBySlot(context, "getSimOperatorName", 1);
        }catch (Exception e){
            Log.d(TAG, "getSimSerialNumbersICCID_sim2: "+e.getMessage());
        }
        if (sim1OperatorName!=null){
            stringStringMap.put(KEY_FOR_SIM_1, sim1OperatorName);
        }
        if (sim2OperatorName!=null){
            stringStringMap.put(KEY_FOR_SIM_2, sim2OperatorName);
        }
        return stringStringMap;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws ITgerMethodNotFoundException {

        String imsi = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ITgerMethodNotFoundException(predictedMethodName);
        }

        return imsi;
    }
    private static class ITgerMethodNotFoundException extends Exception {
        private static final long serialVersionUID = -996812356902545308L;

        public ITgerMethodNotFoundException(String info) {
            super(info);
        }

    }
}
