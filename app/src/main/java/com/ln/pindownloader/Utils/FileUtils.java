package com.ln.pindownloader.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import java.io.File;

public class FileUtils {
    static public String getBaseDir() {
        String baseDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            baseDir = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" );
        } else {
            baseDir = (Environment.getExternalStorageDirectory() + "/" );
        }
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return baseDir;
    }

    static public String getAppDir(){
        return getBaseDir()+ DefaultUtils.appDir + "/";
    }

    static public String getFileDir(String appName, String fileName){
        return getAppDir()+fileName;
    }

    private boolean makeDir(Context context, String dir) {
        final File newFile;
        newFile = new File(dir);
        if (!newFile.exists()) {
            if (newFile.mkdirs()) {
                return true;
            } else {
                Toast.makeText(context, "Can not create download folder", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Log.e("makeDir", "makeDir: dir Existed!!");
            return true;
        }

    }

}
