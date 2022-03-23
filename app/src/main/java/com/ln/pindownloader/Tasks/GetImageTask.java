package com.ln.pindownloader.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.*;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.ImageUtils;

public class GetImageTask extends AsyncTask<Bitmap, Void, Boolean> {
    Context context;
    AlertDialog dialog;
    String savepath;
    ImageView imageView;
    EditText editText;
    Button positiveButton;
    Button negativeButton;
    ProgressBar progressBar;

    public GetImageTask(Context context, AlertDialog dialog, String savepath) {
        this.context = context;
        this.dialog = dialog;
        this.savepath = savepath;
        this.imageView = dialog.findViewById(R.id.dialog_image);
        this.progressBar = dialog.findViewById(R.id.dialog_progress_bar);
        this.editText = dialog.findViewById(R.id.dialog_edit_text);
        this.positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        this.negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
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
    protected Boolean doInBackground(Bitmap... bitmaps) {
        Bitmap bitmap = bitmaps[0];
        return ImageUtils.bitmap2File(bitmap, savepath);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (context != null)
            if (aBoolean) {
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show();
            }
        if (dialog != null){
            progressBar.setVisibility(View.INVISIBLE);
//            editText.setFocusable(true);
//            negativeButton.setText("Close");
            editText.setEnabled(true);
            positiveButton.setEnabled(true);

//            dialog.dismiss();
        }
    }
}
