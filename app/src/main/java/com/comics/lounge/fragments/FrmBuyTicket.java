package com.comics.lounge.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.DatesAdapter;
import com.comics.lounge.databinding.FrmBuyTicketBinding;
import com.comics.lounge.modals.PriceDates;
import com.comics.lounge.servicecallback.ClickItemCalendar;
import com.comics.lounge.servicecallback.ClickItemEventDate;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FrmBuyTicket extends Fragment implements ClickItemEventDate {
    FrmBuyTicketBinding binding;
    ArrayList<PriceDates> priceDatesList;
    DatesAdapter datesAdapter;
    NewMain activity;
    String dateStr = "", openTime = "";
    double showPrice = 0, mealPrice = 0;
    int showCount = 0, mealCount = 0;

    public FrmBuyTicket() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmBuyTicketBinding.inflate(getLayoutInflater());
        init();

        binding.btShowUp.setOnClickListener(v -> {
            if (!dateStr.equals("")) {
                binding.tvQtyShow.setText(String.valueOf(showCount + 1));
                showCount++;
                calculatePrice(binding.tvPriceShow, showPrice * showCount);
            } else {
                Toast.makeText(activity, R.string.please_select_date, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btShowDown.setOnClickListener(v -> {
            if (!dateStr.equals("")) {
                if (showCount > 0){
                    binding.tvQtyShow.setText(String.valueOf(showCount - 1));
                    showCount--;
                    calculatePrice(binding.tvPriceShow, showPrice * showCount);
                }
            } else {
                Toast.makeText(activity, R.string.please_select_date, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btMealUp.setOnClickListener(v -> {
            if (!dateStr.equals("")) {
                binding.tvQtyMeal.setText(String.valueOf(mealCount + 1));
                mealCount++;
                calculatePrice(binding.tvPriceMeal, mealPrice * mealCount);
            } else {
                Toast.makeText(activity, R.string.please_select_date, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btMealDown.setOnClickListener(v -> {
            if (!dateStr.equals("")) {
                if (mealCount > 0){
                    binding.tvQtyMeal.setText(String.valueOf(mealCount - 1));
                    mealCount--;
                    calculatePrice(binding.tvPriceMeal, mealPrice * mealCount);
                }
            } else {
                Toast.makeText(activity, R.string.please_select_date, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btCheckout.setOnClickListener(v -> {
            if (dateStr.equals("")){
                Toast.makeText(activity, R.string.please_select_date, Toast.LENGTH_SHORT).show();
            }else if (NumberUtils.parseMoney(binding.tvPriceTotal) == 0){
                Toast.makeText(activity, R.string.pls_add_ticket, Toast.LENGTH_SHORT).show();
            }else {
                Fragment frm = new FrmCheckout();
                Bundle bundle = new Bundle();
                bundle.putString("date", dateStr);
                bundle.putString("show", binding.tvPriceShow.getText().toString());
                bundle.putString("meal", binding.tvPriceMeal.getText().toString());
                bundle.putString("total", binding.tvPriceTotal.getText().toString());
                bundle.putString("open_time", openTime);
                frm.setArguments(bundle);
                activity.addFrmDetail(frm);
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init() {
        ArrayList<PriceDates> dateList = getArguments().getParcelableArrayList("dates");
        priceDatesList = new ArrayList<>();
        SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        for (PriceDates pd : dateList){
            if (!DatesUtils.strToDate(pd.getDate()).before(DatesUtils.strToDate(fm.format(new Date())))){
                priceDatesList.add(pd);
            }
        }
        datesAdapter = new DatesAdapter(priceDatesList, activity, this);
        binding.rcvDate.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.rcvDate.setAdapter(datesAdapter);
    }

    // calculate price
    private void calculatePrice(TextView tvPrice, double price) {
        tvPrice.setText(NumberUtils.formatMoney(price));
        double totalPrice = NumberUtils.parseMoney(binding.tvPriceMeal) + NumberUtils.parseMoney(binding.tvPriceShow);
        binding.tvPriceTotal.setText(NumberUtils.formatMoney(totalPrice));
    }

    @Override
    public void selectDate(PriceDates priceDates) {
        dateStr = priceDates.getDate();
        openTime = priceDates.getOpenTime();
        showPrice = priceDates.getShowPrice();
        mealPrice = priceDates.getMealPrice();
    }

    @Override
    public void unSelectDate() {
        dateStr = "";
    }
}