package com.comics.lounge.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.webkit.WebSettings;

import com.comics.lounge.R;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.databinding.ActivityTermConditionBinding;
import com.comics.lounge.utils.AppUtil;

public class TermCondition extends AppCompatActivity {
    ActivityTermConditionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermConditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
        binding.tvTermDes7.setText(Html.fromHtml(getString(R.string.term_des_7), Html.FROM_HTML_MODE_COMPACT));
        binding.tvTermDes14.setText(Html.fromHtml(getString(R.string.term_des_14), Html.FROM_HTML_MODE_COMPACT));
        binding.tvTermDes15.setText(Html.fromHtml(getString(R.string.term_des_15), Html.FROM_HTML_MODE_COMPACT));
    }
}