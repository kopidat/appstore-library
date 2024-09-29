package skimp.store.lib.net;

import android.content.Context;
import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import skimp.store.lib.R;

public class RetrofitClient {
    private static final String TAG = RetrofitClient.class.getSimpleName();

    private static RetrofitService service;

    // private construct
    private RetrofitClient() {
        Log.i(TAG, "RetrofitClient()");
    }

    private static class InnerInstanceClazz {
        private static final RetrofitClient instance = new RetrofitClient();
    }

    public static RetrofitService getService(Context context) {
        Log.i(TAG, "getService()");
        String base_url = context.getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitService.class);

        return InnerInstanceClazz.instance.service;
    }
}