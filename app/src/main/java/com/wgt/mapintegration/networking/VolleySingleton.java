package com.wgt.mapintegration.networking;

/**
 * Created by debasish on 11-01-2018.
 */

import android.content.Context;



public class VolleySingleton {
    private static VolleySingleton instance;
    private static Context context;
    //private RequestQueue requestQueue;


    private VolleySingleton(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public static synchronized VolleySingleton getInstance(Context ctx) {
        if (instance == null) {
            instance = new VolleySingleton(ctx);
        }
        return instance;
    }

   /* public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }*/
}

