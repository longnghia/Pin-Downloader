package com.ln.pindownloader.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.ln.pindownloader.R;
import com.ln.pindownloader.Utils.DefaultUtils;

public class SettingsActivity extends AppCompatActivity {
    String TAG="SettingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sharedPreferences = DefaultUtils.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences1, key) -> {
            String s = sharedPreferences1.getAll().toString();
            Log.d(TAG, "onCreate: sharedPreferences key change\n"+key);
            Log.d(TAG, "onCreate: sharedPreferences change\n="+s);
        });

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        String TAG= "SettingsFragment";
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected: id="+id);
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}