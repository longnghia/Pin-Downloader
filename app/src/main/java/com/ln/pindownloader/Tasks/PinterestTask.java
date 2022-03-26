package com.ln.pindownloader.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.DefaultUtils;
import com.ln.pindownloader.Utils.FileUtils;
import com.ln.pindownloader.Utils.ImageUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class PinterestTask extends AsyncTask<String, Void, Void> {

    String TAG = "PinterestTask";
    String input;

    ImageView imageView;
    ProgressBar progressBar;
    Context context;
    ConstraintLayout constraintLayout;

    EditText editText;
    Button positiveButton;
    Button negativeButton;

    androidx.appcompat.app.AlertDialog dialog;
    JSONObject info;

    Boolean autoDownload;

    int fileSize;

    public PinterestTask(Context context, SharedPreferences sharedPreferences, androidx.appcompat.app.AlertDialog dialog, JSONObject info) {
        this.dialog = dialog;
        this.context = context;
        this.autoDownload = DefaultUtils.autoDownload;
        this.info = info;
        autoDownload = sharedPreferences.getBoolean("autoDownload", false);

        constraintLayout = dialog.findViewById(R.id.dialog_constrain_layout);
        imageView = constraintLayout.findViewById(R.id.dialog_image);
        progressBar = constraintLayout.findViewById(R.id.dialog_progress_bar);
        editText = constraintLayout.findViewById(R.id.dialog_edit_text);

        positiveButton = dialog
                .getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        negativeButton = dialog
                .getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        negativeButton.setEnabled(false);
    }

    @Override
    protected Void doInBackground(String... strings) {
        input = strings[0];
        return null;
    }


    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);

        RequestTask requestTask = new RequestTask();

        requestTask.execute(input);

        JSONObject result = null;
        try {
            result = requestTask.get();

            if (result == null) {
                Log.e(TAG, "onClick: result NULL");
                Toast.makeText(context, "invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            /* set info */
            editText.setVisibility(View.VISIBLE);
            String id;
            id = result.getString("id");
            Boolean isVideo =result.getBoolean("isVideo");
            String des = result.getString("description");
            editText.setText(id);
            positiveButton.setEnabled(true);

            String imageUrl = null;
            if (result.has("url") && !result.isNull("url")) {
                dialog.setMessage("Pin found!");
                imageUrl = result.getString("url");
                Log.d(TAG, "onClick: image URL=" + imageUrl);
                Bitmap bitmap = null;
                GetBitmapTask getBitmapTask = new GetBitmapTask();
                bitmap = getBitmapTask.execute(imageUrl).get();
                progressBar.setVisibility(View.INVISIBLE);
                if (bitmap != null) {
//                    int height = bitmap.getHeight();
//                    if (height < imageView.getHeight()) {
//                        imageView.getLayoutParams().height = height;
//                    } else {
//                        imageView.getLayoutParams().height = ImageUtils.dpToPx(DefaultUtils.imageViewHeight, context);
//                    }
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "onClick: bitmap NULL");
                    return;
                }

                if (autoDownload) {
                    editText.setFocusable(false);
                    positiveButton.setText(R.string.ok);
                } else {
                    positiveButton.setText(R.string.download);
                }

                String savepath = null;
                /* download video */
                if (isVideo) {
                    String videoUrl = result.getString("videoUrl");

                    if (autoDownload) {
                        savepath = FileUtils.getFileDir(DefaultUtils.appDir, id + ".mp4");
                        new GetVideoTask(context, dialog).
                                execute(videoUrl, savepath);

                    } else { // manually download
                        positiveButton.setOnClickListener(v -> {
//                            negativeButton.setEnabled(true);
//                            negativeButton.setText(R.string.background);

                            String fileName =editText.getText().toString();
                            fileName=fileName.isEmpty()?id:fileName;

                            String finalSavepath = FileUtils.getFileDir(DefaultUtils.appDir, fileName + ".mp4");
                            Log.d(TAG, "onPostExecute: user's savename=" + finalSavepath);
                            try {
                                new GetVideoTask(context, dialog).
                                        execute(videoUrl, finalSavepath);
                            }catch (IllegalStateException e){
                                Log.e(TAG, "onPostExecute: ERROR" );
                                e.printStackTrace();
                                Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT);
                                dialog.dismiss();
                            }
//                            Toast.makeText(context, "Downloading video in background...",Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        });
                    }
                    /* download image */
                } else {
                    if (autoDownload) {
                        savepath = FileUtils.getFileDir(DefaultUtils.appDir, id + ".png");
                        if (ImageUtils.bitmap2File(bitmap, savepath)) {
                            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else { // manually download
//                        negativeButton.setEnabled(true);
//                        negativeButton.setText(R.string.background);

                        Bitmap finalBitmap = bitmap;
                        positiveButton.setOnClickListener(v -> {
                            String fileName =editText.getText().toString();
                            fileName=fileName.isEmpty()?id:fileName;
                            String finalSavepath1 = FileUtils.getFileDir(DefaultUtils.appDir, fileName+ ".png");
                            Log.d(TAG, "onPostExecute: user's savename=" + finalSavepath1);
                            new GetImageTask(context, dialog, finalSavepath1).execute(finalBitmap);
//                            Toast.makeText(context, "Downloading image in background...",Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        });

                    }
                }

                Log.d(TAG, "onClick: save-path = " + savepath);
            }

            // TODO preview and download
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
