package com.ln.pindownloader.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class HttpUtils {


    static public String parseStringRequest(HttpURLConnection httpURLConnection) {
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = httpURLConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(br);
        }
        return null;
    }

    static public Bitmap parseBitmapRequest(HttpURLConnection httpURLConnection) {
        InputStream inputStream = null;

        try {
            inputStream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    static public Boolean parseVideoRequest(HttpURLConnection httpURLConnection, String pathname) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = httpURLConnection.getInputStream();
            File file = new File(pathname);
            if (file.exists()) {
                Log.e("HttpUtils", "parseVideoRequest: File existed");
//            return false;
            }
            outputStream = new FileOutputStream(file);
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                outputStream.write(data, 0, count);
            }
            Log.i("ParseRequest(Bitmap)", "parseVideoRequest: video downloaded!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ParseRequest", "Can't close readers");
                return false;
            }
        }
    }

    static public HttpURLConnection request(String link) {
        URL url;
        HttpURLConnection httpURLConnection;

        try {
            url = new URL(link);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(false); // true to auto redirect
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(7000);
            httpURLConnection.connect();

//            Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
//            Log.d(TAG, "header fields");
//            Log.d(TAG, headerFields.toString());
            int responseCode = httpURLConnection.getResponseCode();
            /*if (responseCode >= 300) { // setInstanceFollowRedirects(false)
                Log.d(TAG, "redirect to " + headerFields.get("location").get(0));
            }*/

            return httpURLConnection;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Request", "Error!, exit");
            return null;
        }
    }

    static public int getContentLength(String link) {
        HttpURLConnection httpURLConnection = HttpUtils.request(link);
        if (httpURLConnection != null) {
            int fileSize = httpURLConnection.getContentLength();
//            httpURLConnection.disconnect();
            return fileSize != -1 ? fileSize : 0;
        }
        return 0;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" B");
        }

        return hrSize;
    }

    static public boolean checkInternetConnection(Context context) {

        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(context, "No default network is currently active!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(context, "Network is not connected!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(context, "Network not available!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


}
