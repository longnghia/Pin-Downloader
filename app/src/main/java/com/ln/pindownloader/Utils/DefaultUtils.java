package com.ln.pindownloader.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public class DefaultUtils {
    public static int imageViewHeight = 400; // dp
    public static String appDir="PinDownloader";
    public static boolean autoDownload = false;

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static public SharedPreferences getDefaultSharedPreferences(Context context){
        String name =  context.getPackageName() + "_preferences";
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE );
        return sharedPreferences;
    }
}
