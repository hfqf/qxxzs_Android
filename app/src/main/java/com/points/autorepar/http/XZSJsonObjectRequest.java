package com.points.autorepar.http;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class XZSJsonObjectRequest extends JsonObjectRequest {

    public XZSJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String s = new String(response.data);
        Log.d("HttpManager",s);

        return super.parseNetworkResponse(response);
    }

}
