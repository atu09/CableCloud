package com.mtaj.mtaj_08.cableplus_new;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by MTAJ-08 on 12/20/2016.
 */
public class CustomPriorityRequest extends JsonObjectRequest {

    // default value
    Priority mPriority = Priority.HIGH;

    public CustomPriorityRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public CustomPriorityRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority p){
        mPriority = p;
    }
}
