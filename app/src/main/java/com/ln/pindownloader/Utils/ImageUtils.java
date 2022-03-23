package com.ln.pindownloader.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.*;

public class ImageUtils {

    static public boolean bitmap2File(Bitmap bitmap, String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            Log.e("ImageUtils", "bitmap2File: File existed");
//            return false;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeQuietly(outputStream);
//            bitmap.recycle();
        }
    }
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}