package com.ln.pindownloader.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.DefaultUtils;
import com.ln.pindownloader.Utils.FileUtils;
import com.ln.pindownloader.Utils.HttpUtils;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    EditText inputText;
    Button buttonDownload;
    ImageView iconPinterest;
    ImageView iconFolder;
    private static final int REQUEST_WRITE_STORAGE = 142;

    boolean firstLaunch;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firstLaunch)
            return true;
        else {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_item_gear) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_item_info) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* test */
//        startActivity(new Intent(this,SettingsActivity.class));
//        finish();

        /* Load settings*/
//        SharedPreferences sharedPreferences = this.getSharedPreferences(DefaultUtils.appDir, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = DefaultUtils.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences1, key) -> {
//            String s = sharedPreferences1.getAll().toString();
//            Log.d(TAG, "onCreate: sharedPreferences key change\n"+key);
//            Log.d(TAG, "onCreate: sharedPreferences change\n="+s);
//        });

        boolean autoDownload = sharedPreferences.getBoolean("autoDownload", false);
        firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
        boolean darkMode = sharedPreferences.getBoolean("darkMode", false);
        boolean storageGranted = sharedPreferences.getBoolean("storageGranted", false);
        String defaultName = sharedPreferences.getString("defaultName", "By ID");

        if (firstLaunch) {
            setContentView(R.layout.welcome_layout);
            Button startBtn = findViewById(R.id.start_button);

            TextView textView =findViewById(R.id.policyTextView);
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
//            startBtn.setVisibility(View.INVISIBLE);

            new Handler().postDelayed(() -> {
                textView.setVisibility(View.VISIBLE);
                startBtn.setVisibility(View.VISIBLE);
            }, 1000);

            startBtn.setOnClickListener(v -> {
                editor.putBoolean("firstLaunch", false);
                editor.apply();
                finish();
//                startActivity(getIntent());
                startActivity(new Intent(this, HelpActivity.class));
            });
            return;
        }

        /* swipe to refesh */
        if (HttpUtils.checkInternetConnection(this)) {
            setContentView(R.layout.activity_main);

        } else {
            setContentView(R.layout.no_internet_layout);
            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
            pullToRefresh.setOnRefreshListener(() -> {
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                pullToRefresh.setRefreshing(false);

            });
            return;
        }

        this.inputText = findViewById(R.id.inputLink);
        this.buttonDownload = findViewById(R.id.buttonDownload);
        this.iconPinterest = findViewById(R.id.ic_pinterest);
        this.iconFolder = findViewById(R.id.ic_folder);


        /* Add storage permission */
        if (!haveStoragePermission()) {
            requestStoragePermission();
            Log.e(TAG, "onCreate: isgranted=" + false);
            Toast.makeText(this, "Storage permission required to download images!", Toast.LENGTH_LONG).show();
            editor.putBoolean("storageGranted", false);
            editor.apply();
            return;
//            finish();
        } else {
            editor.putBoolean("storageGranted", true);
            editor.apply();
            Log.d(TAG, "onCreate: isgranted=" + true);
            Log.d(TAG, "onCreate: path=" + Environment.getExternalStorageDirectory().getPath() + "/");
            File file = new File(FileUtils.getAppDir());
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    Toast.makeText(this, "Failed to create app folder!", Toast.LENGTH_LONG);
                } else {
                    Log.d(TAG, "onCreate: app path created");
                    iconFolder.setVisibility(View.VISIBLE);
                }
            } else {
                iconFolder.setVisibility(View.VISIBLE);
            }
        }

        this.buttonDownload.setOnClickListener(view -> {
            String input = inputText.getText().toString();
            if (validInput(input)) {

                Intent intent = new Intent(this, ShareReceiverActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, input);
                this.startActivity(intent);
                Log.d(TAG, "onCreate: intent sent!");
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
            }
        });

        this.iconPinterest.setOnClickListener(v -> {
            try {
                Log.d(TAG, "onCreate: startActivity Pinterest");
                String packageName = "com.pinterest";
                Intent intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent == null) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + packageName));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "onCreate: intent not found:\n");
                e.printStackTrace();
                Toast.makeText(this, "Please install Pinterest first", Toast.LENGTH_SHORT);
            }

        });

        /* change icfolder to app icon */
//        try {
//            Drawable icon = this.getPackageManager().getApplicationIcon("com.android.documentsui");
//            iconFolder.setImageDrawable(icon);
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.w(TAG, "onCreate: Can not set to target folder icon");
//            e.printStackTrace();
//        }

        this.iconFolder.setOnClickListener(v -> {
            Uri selectedUri = Uri.parse(FileUtils.getAppDir());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");

            if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                startActivity(intent);
            } else {
//                Toast.makeText(this, "No explorer installed!",Toast.LENGTH_SHORT);
                Log.e(TAG, "onCreate: no activity handle intent ACTION_VIEW: " + FileUtils.getAppDir());

                intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(FileUtils.getAppDir());
                intent.setDataAndType(uri, "resource/folder");
                startActivity(Intent.createChooser(intent, "Open folder"));
            }
        });

    }


    public boolean validInput(String input) {
        input = input.trim();
        return !input.isEmpty();
    }

    private boolean haveStoragePermission() {
        int permissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "User denied WRITE_EXTERNAL_STORAGE permission.");
                } else {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "User granted WRITE_EXTERNAL_STORAGE permission.");
                }
                startActivity(getIntent());
                finish();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: onpause");
    }
}
