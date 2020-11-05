package com.app.theshineindia.baseclasses;

import android.app.ProgressDialog;
import android.content.Context;
import com.app.theshineindia.loaders.JSONFunctions;
import dmax.dialog.SpotsDialog;

public abstract class BasePresenter implements JSONFunctions.OnJSONResponseListener{
    private JSONFunctions jfns;
    private ProgressDialog pDialog;
    private SpotsDialog spot_dialog;

    public BasePresenter(Context context){
        jfns=new JSONFunctions(BasePresenter.this);

        pDialog=new ProgressDialog(context);

        spot_dialog = new SpotsDialog(context);
    }

    public JSONFunctions getJfns() {
        return jfns;
    }

    public ProgressDialog getpDialog() {
        return pDialog;
    }

    public SpotsDialog getSpotDialog() {
        return spot_dialog;
    }

}
