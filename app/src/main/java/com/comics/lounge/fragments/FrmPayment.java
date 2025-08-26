package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.StateAdapter;
import com.comics.lounge.databinding.FrmPaymentBinding;
import com.comics.lounge.modals.State;

import java.util.ArrayList;
import java.util.List;

public class FrmPayment extends Fragment {
    FrmPaymentBinding binding;
    NewMain activity;
    List<State> list;
    StateAdapter adapter;
    public FrmPayment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmPaymentBinding.inflate(getLayoutInflater());
        init();

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btCard.setOnClickListener(v -> activity.addFrmDetail(new FrmCard()));

        return binding.getRoot();
    }
    
    // init UI
    private void init(){
        list = new ArrayList<>();
        list.add(new State(69, "Australian Capital Territory"));
        list.add(new State(70, "New South Wales"));
        list.add(new State(71, "Northern Territory"));
        list.add(new State(72, "Queensland"));
        list.add(new State(73, "South Australia"));
        list.add(new State(74, "Tasmania"));
        list.add(new State(75, "Victoria"));
        list.add(new State(76, "Western Australia"));
        adapter = new StateAdapter(list);
        binding.spinner.setAdapter(adapter);
    }
}