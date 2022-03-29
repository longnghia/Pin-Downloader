package com.ln.pindownloader.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.ln.pindownloader.Utils.APIUtils;
import com.ln.pindownloader.Utils.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestTask extends AsyncTask<String, Void, JSONObject> {
    String TAG = "RequestTask";


    public RequestTask() {
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String link = strings[0];
//        link = "https://www.pinterest.com/pin/12033123994798527/";
//        link = "https://pin.it/6oYSu8D";
//        link = "https://pin.it/4X6NH0y";
//        link = "https://www.pinterest.com/pin/12033123994798527/"; //video
        if (link == null) return null;

        Log.d(TAG, "Requesting " + link + " ...");
        URL url;
        HttpURLConnection httpURLConnection , apiHttpURLConnection = null;
        httpURLConnection = HttpUtils.request(link);
        if (httpURLConnection == null) return null;
        try {
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                link = httpURLConnection.getHeaderField("location");
                Log.d(TAG, "redirected link: " + link);
                /* Todo : no request --> anr */
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: get response code failed");
            return null;
        }
//        httpURLConnection.disconnect();

        String id = parseID(link);
        Log.d(TAG, "doInBackground: id=" + id);
        if (id == null) return null;

        String param = "{\"options\":{\"field_set_key\":\"unauth_react_main_pin\",\"id\":\"" + id + "\"}}";
        String API = null;
        try {
            API = APIUtils.PinterestAPI+ URLEncoder.encode(param, "UTF-8");
            Log.d(TAG, "doInBackground: API=" + API);

            /* jsoup request
            String json = Jsoup.connect(API).ignoreContentType(true).execute().body();
            JSONObject data = new JSONObject(json).getJSONObject("resource_response").getJSONObject("data").getJSONObject("rich_metadata");
            Log.d(TAG, "doInBackground: rich_metadata: "+data.toString());
*/

            apiHttpURLConnection = HttpUtils.request(API);
            if (apiHttpURLConnection == null) return null;
            String body = HttpUtils.parseStringRequest(apiHttpURLConnection);

            if (body == null) return null;
            JSONObject data = new JSONObject(body).getJSONObject("resource_response").getJSONObject("data");

            JSONObject result = new JSONObject();
            result.put("id", id);

            result.put("title", data.getString("title").trim());
            result.put("description", data.getString("description").trim());
//                result.put("trackedLink", data.getString("title"));
//            result.put("username", data.getJSONObject("origin_pinner").getString("username"));

            if (data.has("images") || data.has("videos")) {

                Boolean isVideo = data.has("videos") && !data.isNull("videos");
                result.put("isVideo", isVideo);

                if (isVideo) {
                    JSONObject videoList = data.getJSONObject("videos").getJSONObject("video_list");
                    if (videoList.has("V_720P") && !videoList.isNull("V_720P")) {
                        String videoUrl = videoList.getJSONObject(("V_720P")).getString("url");
                        result.put("videoUrl", videoUrl);
                    } else {
                        Log.d(TAG, "doInBackground: not have V_720P: " + videoList);
                    }
                }

                String origUrl;
                JSONObject images = data.getJSONObject("images");
                JSONObject orig = images.getJSONObject("orig");
                origUrl = orig.getString("url");

                if (origUrl.isEmpty()) {
                    Log.e(TAG, "origin image url null");
                    return null;
                }
                result.put("url", origUrl);
                Log.d(TAG, "doInBackground: result=" + result);
                return result;
            } else {
                Log.e(TAG, "doInBackground: data NULL");
            }
        } catch (
                UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "API wrong: " + API);
        } catch (
                JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parse json failed");
        } finally {
            httpURLConnection.disconnect();
            apiHttpURLConnection.disconnect();
        }
        return null;
    }

    public String parseID(String url) {
        //https://www.pinterest.com/pin/793900240568783032/sent/?invite_code=abf6b4afa0844d3697df7e80ce9af000&sender=848295417220303476&sfo=1 --> 793900240568783032
        Pattern p = Pattern.compile("(?<=pinterest.com/pin/)\\d+"); //
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group();
        }
        return null;
    }


}

