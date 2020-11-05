package com.app.theshineindia.baseclasses;

import android.content.Context;

import com.app.theshineindia.loaders.JSONFunctions;

public abstract class BasePresenter2 implements JSONFunctions.OnJSONResponseListener{
    private JSONFunctions jfns;

    public BasePresenter2(){
        jfns=new JSONFunctions(BasePresenter2.this);
    }
    public JSONFunctions getJfns() {
        return jfns;
    }

}
