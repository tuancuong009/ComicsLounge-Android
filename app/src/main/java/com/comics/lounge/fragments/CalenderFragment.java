package com.comics.lounge.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.comics.lounge.R;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.adapter.pager.CalenderDatePagerAdpter;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.customeview.OwnViewPager;
import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.CalenderService;
import com.comics.lounge.webservice.manager.CalenderServiceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class CalenderFragment extends Fragment implements ServiceCallback {

    SimpleDateFormat inputFormate = null;
    SimpleDateFormat outputFormate = null;
    private CalenderServiceManager calenderServiceManager = null;
    private OwnViewPager viewPager;
    private MainActivity parentActivity = null;
    private CalenderDatePagerAdpter calenderDatePagerAdpter = null;
    private SimpleDateFormat convertOrignalDateFormate;
    private AppCompatImageView menuImg;
    private AppCompatImageView leftArrow, rightArrow, leftArrowDisabled, rightArrowDisabled;
    private AppCompatEditText searchEdt;
    private TreeMap<Date, List<Event>> treeMap = null;
    private AppCompatTextView toolbarTxt;
    private LinearLayout toolbarLogoLayout;
    private ViewSwitcher viewSwitcher;
    private Toolbar toolbar;
    private LinearLayout llMainLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        viewSwitcher = view.findViewById(R.id.parent_view_switcher);
        toolbarLogoLayout = view.findViewById(R.id.toolbar_logo_layout);
        toolbarTxt = view.findViewById(R.id.toolbar_app_name_txt);
        viewPager = view.findViewById(R.id.view_pager);
        menuImg = view.findViewById(R.id.menuRight);
        leftArrow = view.findViewById(R.id.lefty_arrow);
        rightArrow = view.findViewById(R.id.right_arrow);
        leftArrowDisabled = view.findViewById(R.id.lefty_arrow_disabled);
        rightArrowDisabled = view.findViewById(R.id.right_arrow_disabled);
        searchEdt = view.findViewById(R.id.search_edt);
        llMainLayout = view.findViewById(R.id.llMainlayout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ivLogo = view.findViewById(R.id.toolbar_app_logo);
        ToolbarUtils.loanAppLogo(toolbar, ivLogo);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (GlobalConf.checkInternetConnection(getContext())) {
            calenderServiceManager = new CalenderServiceManager(this, getActivity());
            calenderServiceManager.prepareWebServiceJob();
            calenderServiceManager.featchData();
            viewSwitcher.showNext();
        } else {
            parentActivity.switchToNoInternetFoundActivity();
        }
        leftArrow.setVisibility(View.GONE);
        leftArrowDisabled.setVisibility(View.VISIBLE);
        inputFormate = new SimpleDateFormat("dd-MM-yyyy");
        outputFormate = new SimpleDateFormat("d");
        convertOrignalDateFormate = new SimpleDateFormat("MMMM  yyyy");

        // toolbarLogoLayout.setVisibility(View.INVISIBLE);
        toolbarTxt.setText(getText(R.string.calender_str));
        AppUtil.hideKeyboard(llMainLayout);
        leftArrow.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(getContext())) {
                searchEdt.setText("");
                int indexCounter = viewPager.getCurrentItem() - 1;
                if (viewPager.getCurrentItem() == 0) {
                    //refreshUI(indexCounter);
                    leftArrow.setVisibility(View.GONE);
                    leftArrowDisabled.setVisibility(View.VISIBLE);
                } else {
                    refreshUI(indexCounter);
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            } else {
                parentActivity.switchToNoInternetFoundActivity();
            }

        });

        searchEdt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                AppUtil.hideKeyboard(llMainLayout);
            }
        });

        rightArrow.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(getContext())) {
                searchEdt.setText("");
                int indexCounter = viewPager.getCurrentItem() + 1;
                if (indexCounter >= treeMap.size()) {

                } else {
                    refreshUI(indexCounter);
                }

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                parentActivity.switchToNoInternetFoundActivity();
            }

        });

        viewPager.setPagingEnabled(false);

        /*
         * Disable swipe in viewpager
         */
        // viewPager.beginFakeDrag();


        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.openDrawer();
            }
        });

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    updateData(s.toString());
                } else {
                    refreshUI(viewPager.getCurrentItem());
                }
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = viewPager.getCurrentItem();

                if (currentItem == (viewPager.getAdapter().getCount()-1)) {
                    rightArrow.setVisibility(View.GONE);
                    rightArrowDisabled.setVisibility(View.VISIBLE);
                } else {
                    rightArrow.setVisibility(View.VISIBLE);
                    rightArrowDisabled.setVisibility(View.GONE);
                }

                if (currentItem == 0) {
                    leftArrow.setVisibility(View.GONE);
                    leftArrowDisabled.setVisibility(View.VISIBLE);
                } else {
                    leftArrow.setVisibility(View.VISIBLE);
                    leftArrowDisabled.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

//    public void setupUI(View view) {
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
//                    AppUtil.hideKeyboard(llMainLayout);
//                    return false;
//                }
//            });
//        }
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }


    private void refreshUI(int isAdded) {
        int indexPager = isAdded;
        List<Event> eventList = treeMap.get(treeMap.keySet().toArray()[indexPager]);
        calenderDatePagerAdpter.newFilter(eventList);
        calenderDatePagerAdpter.notifyDataSetChanged();
    }

    private void updateData(String filterTxt) {
        List<Event> eventList = treeMap.get(treeMap.keySet().toArray()[viewPager.getCurrentItem()]);
        List<Event> events = new LinkedList<Event>();
        for (Event event : eventList) {
            if (event.getProductName().toLowerCase().contains(filterTxt.toLowerCase())) {
                events.add(event);
            }
        }
        calenderDatePagerAdpter.newFilter(events);
        calenderDatePagerAdpter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(CalenderService.SERVICE_NAME)) {
            viewSwitcher.showPrevious();
            if (calenderServiceManager.getServiceStatus().equals("success")) {
                calenderServiceManager.getCalMapList();
                treeMap = new TreeMap<Date, List<Event>>(calenderServiceManager.getCalMapList());

                //treeMap.putAll(calenderServiceManager.getCalMapList());
                calenderServiceManager.getCalMapList().clear();
                calenderDatePagerAdpter = new CalenderDatePagerAdpter(getContext(),
                        treeMap, this, inputFormate, outputFormate, convertOrignalDateFormate);
                viewPager.setAdapter(calenderDatePagerAdpter);
                calenderDatePagerAdpter.notifyDataSetChanged();
                viewPager.setOffscreenPageLimit(calenderServiceManager.getCalMapList().size());
            }

        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void switchToEventDetail(Event event) {
        parentActivity.switchToEventDetailFragment(event);
    }
}