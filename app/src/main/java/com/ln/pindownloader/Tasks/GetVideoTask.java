package com.ln.pindownloader.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.HttpUtils;

import java.net.HttpURLConnection;

public class GetVideoTask extends AsyncTask<String, Void, Boolean> {

    String TAG = "DownloadImageTask";

    ProgressBar progressBar ;
    androidx.appcompat.app.AlertDialog dialog;
    Context context;

    ImageView imageView;
    EditText editText;
    Button positiveButton;
    Button negativeButton;

    public GetVideoTask() {
    }

    public GetVideoTask(Context context, androidx.appcompat.app.AlertDialog dialog) {
        this.context = context;
        this.dialog = dialog;
        imageView = dialog.findViewById(R.id.dialog_image);
        progressBar = dialog.findViewById(R.id.dialog_progress_bar);
        editText = dialog.findViewById(R.id.dialog_edit_text);
        positiveButton = dialog
                .getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        ConstraintLayout constraintLayout = dialog.findViewById(R.id.dialog_constrain_layout);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialog != null) {
            progressBar.setVisibility(View.VISIBLE);
            positiveButton.setEnabled(false);
//            editText.setFocusable(false);
            editText.setEnabled(false);


        }
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        String link = strings[0];
        String pathname = strings[1];

        HttpURLConnection httpURLConnection;
        httpURLConnection = HttpUtils.request(link);
        if (httpURLConnection == null) {
            return null;
        }
        int fileSize = httpURLConnection.getContentLength();

        Log.d(TAG, "Length of file: " + fileSize);

        if (HttpUtils.parseVideoRequest(httpURLConnection, pathname)) {
            return true;
        } else {
            Log.e(TAG, "doInBackground: parsevideorequest failed");
            return false;

        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (context != null) {
            if (aBoolean)
                Toast.makeText(context, "Video Saved!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Download Fail!", Toast.LENGTH_LONG).show();

        }else{
            Log.d(TAG, "onPostExecute: context NULL");
        }
        if (dialog != null) {
            progressBar.setVisibility(View.INVISIBLE);
//            editText.setFocusable(true);
//            negativeButton.setText("Close");
            positiveButton.setEnabled(true);
            editText.setEnabled(true);

//            positiveButton.setText();
//            dialog.dismiss();
        }
    }


}
