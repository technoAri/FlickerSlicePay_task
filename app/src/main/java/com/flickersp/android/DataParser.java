package com.flickersp.android;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Rapidd08 on 1/13/2018.
 */

public class DataParser {
    private static final String MAIN_URL = "https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=" + Key.API_Key + "&per_page=" + Key.ItemsPerPage + "&page=1&format=json&nojsoncallback=1";
    public static final String TAG = "TAG";
    private static final String KEY_USER_ID = "user_id";
    private static Response response;

    public static JSONObject getDataFromWeb() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }
}
