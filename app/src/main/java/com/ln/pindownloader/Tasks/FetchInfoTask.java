package com.ln.pindownloader.Tasks;

import android.os.AsyncTask;
import android.util.Log;

public class FetchInfoTask extends AsyncTask<String, Void, String> {
    String TAG = "FetchInfoTask";

    @Override
    protected String doInBackground(String... strings) {
//        String link = strings[0];

        String link = "https://www.pinterest.com/pin/567172146830690218/";
        Log.d(TAG, "fetching info ...\n");
//        RequestTask requestTask = new RequestTask();

        return null;
    }
}
