package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.DayAdapter;
import com.comics.lounge.adapter.EventAdapter;
import com.comics.lounge.databinding.FrmCalendarBinding;
import com.comics.lounge.modals.EventNew;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.servicecallback.ClickItemCalendar;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.viewModel.EventVM;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmCalendar extends Fragment implements ClickItemCalendar {
    FrmCalendarBinding binding;
    List<Integer> dayList;
    DayAdapter dayAdapter;
    EventAdapter adapter;
    NewMain activity;
    boolean isShow = true;
    Date d;
    Calendar c;
    int selectMonth, selectYear;
    String dayOfTheWeek, filterMonth;
    ArrayList<String> weeks = new ArrayList<>();
    List<EventNew> allEventlist, filterList;
    SimpleDateFormat sdf, fmCrMonth, fmFilterMonth;
    SessionManager sessionManager;
    EventVM eventVM;

    public FrmCalendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmCalendarBinding.inflate(getLayoutInflater());
        init();

        binding.btToggleCalendar.setOnClickListener(v -> {
            binding.btToggleCalendar.setImageResource(isShow ? R.mipmap.ic_down : R.mipmap.ic_up);
            binding.llCalendar.setVisibility(isShow ? View.GONE : View.VISIBLE);
            isShow = !isShow;
        });
        binding.btNext.setOnClickListener(v -> {
            if (selectMonth == 11) {
                selectMonth = 0;
                selectYear++;
            } else {
                selectMonth++;
            }
            d.setMonth(selectMonth);
            d.setYear(selectYear);
            c.set(Calendar.MONTH, selectMonth);
            c.set(Calendar.YEAR, selectYear);
            filterMonth = fmFilterMonth.format(c.getTime());
            binding.tvMonth.setText(fmCrMonth.format(c.getTime()));
            dayOfTheWeek = sdf.format(d).toUpperCase();
            int w = weeks.indexOf(dayOfTheWeek);
            if (w == 0) {
                dayAdapter = new DayAdapter(6, selectMonth, selectYear, activity, this);
            } else {
                dayAdapter = new DayAdapter(w - 1, selectMonth, selectYear, activity, this);
            }
            binding.rcvDay.setAdapter(dayAdapter);
            filterListByMonth();
            binding.btPrevious.setAlpha(1f);
            binding.btPrevious.setEnabled(true);
        });
        binding.btPrevious.setOnClickListener(v -> {
            if (selectMonth == 0) {
                selectMonth = 11;
                selectYear--;
            } else {
                selectMonth--;
            }
            d.setMonth(selectMonth);
            d.setYear(selectYear);
            c.set(Calendar.MONTH, selectMonth);
            c.set(Calendar.YEAR, selectYear);
            filterMonth = fmFilterMonth.format(c.getTime());
            if (selectMonth == Calendar.getInstance().get(Calendar.MONTH)){
                binding.btPrevious.setEnabled(false);
                binding.btPrevious.setAlpha(.5f);
            }
            binding.tvMonth.setText(fmCrMonth.format(c.getTime()));
            dayOfTheWeek = sdf.format(d).toUpperCase();
            int w = weeks.indexOf(dayOfTheWeek);
            if (w == 0) {
                dayAdapter = new DayAdapter(6, selectMonth, selectYear, activity, this);
            } else {
                dayAdapter = new DayAdapter(w - 1, selectMonth, selectYear, activity, this);
            }
            binding.rcvDay.setAdapter(dayAdapter);
            filterListByMonth();
            binding.btNext.setAlpha(1f);
            binding.btNext.setEnabled(true);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventVM = new ViewModelProvider(activity).get(EventVM.class);

        eventVM.getSelected().observe(getViewLifecycleOwner(), eventNew -> {
            Log.e("TAG", "livedatachanged: " );
            for (int i = 0;i < filterList.size();i++){
                EventNew en = filterList.get(i);
                if (en.getId().equals(eventNew.getId())){
                    en.setFav(eventNew.isFav());
                    adapter.notifyItemChanged(i);
                }
            }
        });
    }

    // init UI
    private void init() {
        sessionManager = new SessionManager(activity);
        binding.btPrevious.setEnabled(false);
        binding.btPrevious.setAlpha(.5f);
        // calendar
        weeks.add("SUN");
        weeks.add("MON");
        weeks.add("TUE");
        weeks.add("WED");
        weeks.add("THU");
        weeks.add("FRI");
        weeks.add("SAT");
        c = Calendar.getInstance();
        selectMonth = c.get(Calendar.MONTH);
        selectYear = c.get(Calendar.YEAR);
        sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        fmCrMonth = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        fmFilterMonth = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
        filterMonth = fmFilterMonth.format(c.getTime());
        binding.tvMonth.setText(fmCrMonth.format(c.getTime()));
        d = new Date();
        d.setDate(1);
        dayOfTheWeek = sdf.format(d).toUpperCase();
        dayList = new ArrayList<>();
        dayAdapter = new DayAdapter(weeks.indexOf(dayOfTheWeek), selectMonth, selectYear, activity, this);
        binding.rcvDay.setLayoutManager(new GridLayoutManager(activity, 7));
        AppUtil.rcvNoAnimator(binding.rcvDay);
        binding.rcvDay.setAdapter(dayAdapter);
        // event
        allEventlist = new ArrayList<>();
        filterList = new ArrayList<>();
        adapter = new EventAdapter(activity, filterList);
        binding.rcvEvent.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        AppUtil.rcvNoAnimator(binding.rcvEvent);
        binding.rcvEvent.setAdapter(adapter);
        AppUtil.showLoading(activity);
        getEvent();
    }

    // get event month
    private void getEvent(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getEvent(sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrPrd = jsonObject.optJSONArray("products");
                            if (arrPrd != null){
                                for (int i = 0;i < arrPrd.length();i++){
                                    JSONObject objPrd = (JSONObject) arrPrd.get(i);
                                    allEventlist.add(new EventNew(objPrd.optString("product_id"), objPrd.optString("product_name"),
                                            objPrd.optString("img"), objPrd.optString("start_date"), objPrd.optString("end_date"),
                                            objPrd.optBoolean("isFav"), objPrd.optBoolean("memberAccess")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                filterListByMonth();
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppUtil.hideLoading();
            }
        });
    }

    // filter list by Month
    private void filterListByMonth(){
        filterList.clear();
        for (EventNew en : allEventlist){
            if (en.getStDate().contains(filterMonth) || en.getEndDate().contains(filterMonth)){
                filterList.add(en);
            }
        }
        if (filterList.size() == 0){
//            binding.btNext.setAlpha(.5f);
//            binding.btNext.setEnabled(false);
            binding.tvNoData.setVisibility(View.VISIBLE);
        }else {
//            binding.btNext.setAlpha(1f);
//            binding.btNext.setEnabled(true);
            binding.tvNoData.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectedDay(String date) {
        filterList.clear();
        for (EventNew en : allEventlist){
            if (DatesUtils.strToLong(date) >= DatesUtils.strToLong(en.getStDate())
                    && DatesUtils.strToLong(date) <= DatesUtils.strToLong(en.getEndDate())){
                filterList.add(en);
            }
        }
        adapter.notifyDataSetChanged();
        binding.tvNoData.setVisibility(filterList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void unSelectDay() {
        filterListByMonth();
    }
}