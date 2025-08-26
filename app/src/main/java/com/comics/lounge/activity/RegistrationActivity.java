package com.comics.lounge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.comics.lounge.R;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.databinding.ActivityRegistrationBinding;
import com.comics.lounge.fragments.FrmTerm;
import com.comics.lounge.fragments.TermsAndConditionFragment;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import static com.comics.lounge.activity.MobileVerificationActivity.EXTRA_SCREEN_FLOW;
import static com.comics.lounge.conf.Constant.DEVICE_TYPE;


public class RegistrationActivity extends AbstractBaseActivity {
    ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isValid = AppUtil.isAlphabetAllowed(s.toString());
                if (!isValid && !s.toString().trim().isEmpty()) {
                    binding.nameEdit.setText("");
                    binding.ipFName.setError(getString(R.string.error_for_name));
                }else {
                    binding.ipFName.setError(null);
                    binding.nameEdit.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvFNameLabel.setTextColor(getColor(R.color.gray_1));
                }
            }
        });
        binding.lastNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isValid = AppUtil.isAlphabetAllowed(s.toString());
                if (!isValid && !s.toString().trim().isEmpty()){
                    binding.lastNameEdit.setText("");
                    binding.ipLName.setError(getString(R.string.error_for_name));
                }else {
                    binding.ipLName.setError(null);
                    binding.lastNameEdit.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvLNameLabel.setTextColor(getColor(R.color.gray_1));
                }
            }
        });
        binding.emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0){
                    binding.ipEmail.setError(null);
                    binding.emailEdit.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvEmailLabel.setTextColor(getColor(R.color.gray_1));
                }
            }
        });
        binding.passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0){
                    binding.ipPw.setError(null);
                    binding.passwordEdit.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvPwLabel.setTextColor(getColor(R.color.gray_1));
                }
            }
        });
        binding.isCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                binding.isCheckbox.setBackgroundResource(R.drawable.cb_selector);
                binding.tvLink.setTextColor(getColor(R.color.gray_2));
            }
        });


        binding.signUpBtn.setOnClickListener(v -> {
            AppUtil.hideKeyboard(binding.signUpBtn);
            if (!GlobalConf.checkInternetConnection(getApplicationContext())) {
                displaySnackBarMessage(getResources().getString(R.string.internet_not_found_str));
                return;
            }
            if (AppUtil.edtBlank(binding.nameEdit, binding.ipFName, getString(R.string.this_field_is_required))) {
                binding.tvFNameLabel.setTextColor(getColor(R.color.red));
            }else if (AppUtil.edtBlank(binding.lastNameEdit, binding.ipLName, getString(R.string.this_field_is_required))) {
                binding.tvLNameLabel.setTextColor(getColor(R.color.red));
            } else if (AppUtil.edtBlank(binding.emailEdit, binding.ipEmail, getString(R.string.this_field_is_required))) {
                binding.tvEmailLabel.setTextColor(getColor(R.color.red));
            } else if (!AppUtil.isValidEmail(binding.emailEdit.getText().toString().trim())) {
                binding.ipEmail.setError(getString(R.string.enter_valid_email));
                binding.tvEmailLabel.setTextColor(getColor(R.color.red));
            } else if (AppUtil.edtBlank(binding.passwordEdit, binding.ipPw, "Password cannot be blank")) {
                binding.tvPwLabel.setTextColor(getColor(R.color.red));
            } else if (!AppUtil.isValidPwd(binding.passwordEdit.getText().toString().trim())) {
                binding.ipPw.setError(getString(R.string.password_validation));
                binding.tvPwLabel.setTextColor(getColor(R.color.red));
            } else if (!binding.isCheckbox.isChecked()) {
                Utils.INSTANCE.generateClickableLinkError(this, binding.tvLink);
                binding.isCheckbox.setBackgroundResource(R.drawable.cb_error_selector);
                binding.tvLink.startAnimation(AppUtil.shakeError());
                binding.tvLink.setTextColor(getColor(R.color.red));
            } else {
                JSONObject registerObj = new JSONObject();
                try {
                    registerObj.put("email", binding.emailEdit.getText().toString().trim());
                    registerObj.put("password", binding.passwordEdit.getText().toString().trim());
                    registerObj.put("first_name", binding.nameEdit.getText().toString().trim());
                    registerObj.put("last_name", binding.lastNameEdit.getText().toString().trim());
                    registerObj.put("device_id", GlobalConf.getUniqueID());
                    registerObj.put("device_type", DEVICE_TYPE);
                    Intent intent = new Intent(getApplicationContext(), MobileVerificationActivity.class);
                    intent.putExtra(EXTRA_SCREEN_FLOW, MobileVerificationActivity.ScreeFlow.FROM_REGISTER);
                    intent.putExtra("regJson", registerObj.toString());
                    intent.putExtra("status", "2");
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.signInTxtId.setOnClickListener(v -> {
            if (getIntent().getStringExtra("auth") != null){
                finish();
            }else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("from", "auth");
                startActivity(intent);
            }
        });
        Utils.INSTANCE.generateClickableLink(this, binding.tvLink);
    }

    private void toggleVisibleViews(boolean showRegister) {
        if (showRegister) {
            binding.llContentView.setVisibility(View.VISIBLE);
            binding.llTerm.setVisibility(View.GONE);
        } else {
            binding.llContentView.setVisibility(View.GONE);
            binding.llTerm.setVisibility(View.VISIBLE);
        }
    }

    public void displaySnackBarMessage(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Fragment termsOfUse = getSupportFragmentManager().findFragmentByTag(FrmTerm.class.getSimpleName());
        if (termsOfUse != null) {
            if (!((TermsAndConditionFragment) termsOfUse).canGoBack()) {
                super.onBackPressed();
                toggleVisibleViews(true);
            }
            return;
        }
        super.onBackPressed();
        finish();
    }
}
