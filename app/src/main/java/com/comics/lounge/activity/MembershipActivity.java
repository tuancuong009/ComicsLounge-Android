package com.comics.lounge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;

import com.comics.lounge.R;
import com.comics.lounge.fragments.MembershipFragment;
import com.comics.lounge.fragments.TermsAndConditionFragment;
import com.google.android.material.snackbar.Snackbar;

public class MembershipActivity extends AbstractBaseActivity {

    private MembershipFragment membershipFragment = null;
    private LinearLayout mainLayout;
    private AppCompatTextView skipTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        skipTxt = findViewById(R.id.skip_txt);
        mainLayout = findViewById(R.id.main_layout);

        switchToMemberShopFragment();

        skipTxt.setOnClickListener(v -> switchToDashboardFragment());
    }

    private void switchToMemberShopFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        membershipFragment = new MembershipFragment();
        fragmentManager.beginTransaction().replace(R.id.container, membershipFragment, "MembershipFragment").commit();
    }

    public void displaySnackbarMsg(String snackbarMessage) {
        Snackbar.make(mainLayout, snackbarMessage, Snackbar.LENGTH_LONG).show();
    }

    public void switchToDashboardFragment() {
        Intent intent = new Intent(MembershipActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
