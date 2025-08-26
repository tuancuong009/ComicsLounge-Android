package com.comics.lounge.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.comics.lounge.R;
import com.comics.lounge.conf.GlobalConf;
import com.google.android.material.snackbar.Snackbar;

public class NoInternetActivity extends AbstractBaseActivity {

    private AppCompatTextView retry;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        mainLayout = findViewById(R.id.main_layout);
        retry = findViewById(R.id.retry_btn);


        retry.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(getApplicationContext())) {
                switchToMainActivity();
            } else {
                displaySnackbarMsg(getResources().getString(R.string.internet_not_found_str));
            }
        });
    }

    private void switchToMainActivity() {
        finish();
    }

    public void displaySnackbarMsg(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }


}
