package de.niklas_guertler.directcall;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<String> permissionListener = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    doCall();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            });

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);

        if (!sharedPreferences.contains("phone_number") || sharedPreferences.getString("phone_number", "").isEmpty()) {
            Intent settingIntent = new Intent(getApplicationContext (), SettingsActivity.class);
            startActivity(settingIntent);
            finish ();
        } else {
            if (sharedPreferences.getBoolean("call_app_start", false))
                doCall();

            Button btnCancel = findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(v -> finish ());

            Button btnCall = findViewById(R.id.btn_call);

            String number = sharedPreferences.getString("phone_number", null);
            if (number != null)
                btnCall.setText(String.format(getString(R.string.call_fmt), number));

            btnCall.setOnClickListener(view -> doCall());
        }
    }

    private void doCall() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            String number = sharedPreferences.getString("phone_number", null);

            if (number != null) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                callIntent.putExtra(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, sharedPreferences.getBoolean("call_speaker", false));

                startActivity(callIntent);
                finish();
            }
        } else {
            permissionListener.launch(Manifest.permission.CALL_PHONE);
        }
    }
}