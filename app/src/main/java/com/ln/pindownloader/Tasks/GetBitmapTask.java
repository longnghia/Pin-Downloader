package com.ln.pindownloader.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.DefaultUtils;
import com.ln.pindownloader.Utils.FileUtils;
import com.ln.pindownloader.Utils.HttpUtils;
import com.ln.pindownloader.Utils.ImageUtils;

import java.net.HttpURLConnection;

public class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {
    String TAG = "GetBitmapTask";
    Bitmap bitmap = null;
    ImageView imageView = null;
    ProgressBar progressBar = null;
    Button buttonDownload = null;
    androidx.appcompat.app.AlertDialog dialog;

    int fileSize;

    public GetBitmapTask(ImageView imageView, ProgressBar progressBar, Button buttonDownload, androidx.appcompat.app.AlertDialog dialog) {
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.buttonDownload = buttonDownload;
        this.dialog = dialog;
        ConstraintLayout constraintLayout = dialog.findViewById(R.id.dialog_constrain_layout);
    }

    public GetBitmapTask(androidx.appcompat.app.AlertDialog dialog) {
    }

    public GetBitmapTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        String link = strings[0];
        HttpURLConnection httpURLConnection;
        httpURLConnection = HttpUtils.request(link);
        if (httpURLConnection == null) {
            return null;
        }
        fileSize = httpURLConnection.getContentLength();

        Log.d(TAG, "Length of file: " + fileSize);

        bitmap = HttpUtils.parseBitmapRequest(httpURLConnection);

        if (bitmap != null) {
            httpURLConnection.disconnect();
            return bitmap;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            if (imageView != null) {
                Log.d(TAG, "onPostExecute: setImageBitmap");
                imageView.setImageBitmap(bitmap);
                if (DefaultUtils.autoDownload) {
                    String filename = FileUtils.getFileDir(DefaultUtils.appDir, "temp.png");
                    if (ImageUtils.bitmap2File(bitmap, filename)) {
                        Log.d(TAG, "onPostExecute: image downloaded :" + filename);
                    }
                }
            } else {
                Log.d(TAG, "onPostExecute: Imageview NULL");
            }
        } else {
            Log.e(TAG, "onPostExecute: bitmap null");
        }
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (buttonDownload != null) {
            buttonDownload.setEnabled(true);
        }
        if (dialog != null) {
            dialog.setMessage("Save this Pin?");
        }
    }
}
