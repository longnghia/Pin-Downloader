package com.ln.pindownloader.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Tasks.PinterestTask;
import com.ln.pindownloader.Utils.DefaultUtils;
import com.ln.pindownloader.Utils.HttpUtils;
import org.json.JSONObject;

public class ShareReceiverActivity extends ComponentActivity {
    String TAG = "ShareReceiverActivity";
    View baseView;
    ImageView imageView;
    ProgressBar progressBar;
    Bitmap bitmap;
    EditText editText;
    ConstraintLayout constraintLayout;
    JSONObject info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Load settings*/
//        SharedPreferences sharedPreferences = this.getSharedPreferences(DefaultUtils.appDir, Context.MODE_PRIVATE);

        SharedPreferences sharedPreferences = DefaultUtils.getDefaultSharedPreferences(this);
        boolean autoDownload = sharedPreferences.getBoolean("autoDownload", false);
        boolean firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
        boolean darkMode = sharedPreferences.getBoolean("darkMode", false);
        boolean storageGranted = sharedPreferences.getBoolean("storageGranted", false);



        if (firstLaunch) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (!storageGranted) {
            Toast.makeText(this, "Need Storage permission to save images!", Toast.LENGTH_LONG);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (HttpUtils.checkInternetConnection(this)) {
            Log.d(TAG, "onCreate: setContentView share_background ");
            setContentView(R.layout.share_background);
        } else {
            setContentView(R.layout.no_internet_layout);
            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
            return;
        }

        baseView = findViewById(R.id.dialog_frame_layout);


        /* Inflate XML */
        LayoutInflater inflater = getLayoutInflater();
        constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.dialog_layout, null, false);
        editText = constraintLayout.findViewById(R.id.dialog_edit_text);

        info = new JSONObject();
        /* Intent Filter */
        Intent intent = getIntent();
        String type = intent.getType();
        String action = intent.getAction();
        String input;
        String url;

        if (action.equals(Intent.ACTION_SEND) && type != null) {
            if (type.equals("text/plain")) {
                Log.d(TAG, "onCreate: intent received!");

                new MaterialAlertDialogBuilder(this,R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setMessage("Loading data ...")
                        .setOnDismissListener(dialog1 -> {
                            this.finish();
                        })
                        .setPositiveButton(R.string.download, (dialog1, which) -> {
                        })
                        .setNegativeButton(R.string.close, (dialog1, which) -> {
                            return;
                        })
                        .setView(constraintLayout)
                        .show();
                if (true)return;
                /* show dialog */
                AlertDialog dialog =
                        new AlertDialog.Builder(this)
//                        .setTitle("Pin Downloader")

                        .setMessage("Loading data ...")
                        .setOnDismissListener(dialog1 -> {
                            this.finish();
                        })
                        .setPositiveButton(R.string.download, (dialog1, which) -> {
                        })
                        .setNegativeButton(R.string.close, (dialog1, which) -> {
                            return;
                        })
                        .setView(constraintLayout)
                        .show();

                Button positiveButton = dialog
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);
                Button negativeButton = dialog
                        .getButton(AlertDialog.BUTTON_NEGATIVE);

                /* background task */
                input = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (input != null) {
                    Log.d(TAG, "onCreate: input=" + input);
                    input = input.substring(input.indexOf("http"));

                    PinterestTask pinterestTask = new PinterestTask(this, sharedPreferences,dialog, info);
                    pinterestTask.execute(input);
                }
            }
        } else {
            Toast.makeText(this, "Error occurs", Toast.LENGTH_LONG).show();

            Log.e(TAG, "onCreate: intent error:" + type + " , " + action);
            this.finish();
        }
    }

    void showToast() {
//        Snackbar.make(baseView, "This is main activity", Snackbar.LENGTH_LONG)
//                .setAction("CLOSE", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                })
//                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
//                .show();

        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy: detroying...");
    }

}