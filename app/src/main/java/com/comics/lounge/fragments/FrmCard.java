package com.comics.lounge.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmCardBinding;
import com.comics.lounge.utils.CustomSpinnerAdapter;
import com.comics.lounge.utils.FourDigitCardFormatWatcher;

public class FrmCard extends Fragment {
    FrmCardBinding binding;
    NewMain activity;
    public FrmCard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmCardBinding.inflate(getLayoutInflater());
        init();

        binding.edtNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
        binding.spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(Color.parseColor("#BFBFBF"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(Color.parseColor("#BFBFBF"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        CustomSpinnerAdapter monthAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, activity.getResources().getStringArray(R.array.month_array));
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMonth.setAdapter(monthAdapter);

        CustomSpinnerAdapter yearAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, activity.getResources().getStringArray(R.array.year_array));
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYear.setAdapter(yearAdapter);
    }
}