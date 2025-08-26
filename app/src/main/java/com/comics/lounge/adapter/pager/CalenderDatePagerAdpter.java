package com.comics.lounge.adapter.pager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.comics.lounge.R;
import com.comics.lounge.adapter.CalenderDateListItemAdpter;
import com.comics.lounge.fragments.CalenderFragment;
import com.comics.lounge.modals.Event;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CalenderDatePagerAdpter extends PagerAdapter {
    private final SimpleDateFormat inputFormate;
    private final SimpleDateFormat outputFormate;
    private final SimpleDateFormat convertOrignalDateFormate;
    List<Event> filterEventsList = null;
    private LinearLayoutManager linearLayoutManager;
    private CalenderFragment calenderFragment;
    private Context context;
    private Map<Date, List<Event>> caDateListMap = null;
    private LayoutInflater layoutInflater;
    private CalenderDateListItemAdpter calenderDateListItemAdpter = null;
    private AppCompatImageView dataNotFoundIcon;

    public CalenderDatePagerAdpter(Context context, Map<Date, List<Event>> calMapList, CalenderFragment calenderFragment,
                                   SimpleDateFormat inputFormate, SimpleDateFormat outputFormate, SimpleDateFormat convertOrignalDateFormate) {
        caDateListMap = calMapList;
        this.context = context;
        this.calenderFragment = calenderFragment;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inputFormate = inputFormate;
        this.outputFormate = outputFormate;
        this.convertOrignalDateFormate = convertOrignalDateFormate;
    }

    @Override
    public int getCount() {
        return caDateListMap.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.calender_item_cell_list, container, false);
        assert view != null;
        AppCompatTextView dateTxt = view.findViewById(R.id.date_txt);

        String dateStr = convertOrignalDateFormate.format(Objects.requireNonNull(caDateListMap.keySet().toArray())[position]);
        dateTxt.setText(dateStr);
        ViewSwitcher viewSwitcher = view.findViewById(R.id.view_switcher);
        dataNotFoundIcon = view.findViewById(R.id.data_not_found_icon);
        dataNotFoundIcon.setImageResource(R.drawable.ic_calendar);

        RecyclerView eventRV = view.findViewById(R.id.calender_view_rv);

        linearLayoutManager = new LinearLayoutManager(context);
        eventRV.setLayoutManager(linearLayoutManager);

        if (filterEventsList != null) {
            if (filterEventsList.size() > 0) {
                viewSwitcher.setDisplayedChild(0);
            } else {
                viewSwitcher.setDisplayedChild(1);
            }
            ArrayList<Event> newList = new ArrayList<Event>();
            for (int i =0;i<filterEventsList.size();i++){
                long dayDifference = getCountOfDays(filterEventsList.get(i).getStartDate(),filterEventsList.get(i).getEndDate());
                Log.d("KRUTI", "getcountofdays:: " +dayDifference );
                filterEventsList.get(i).setDifferenceBtwnStartEnd(dayDifference);

            }
            for (int i =0;i<filterEventsList.size();i++){
                if(filterEventsList.get(i).getDifferenceBtwnStartEnd()>0){
                    ArrayList<Event> tempListForDifferentDate = new ArrayList<Event>();
                    for (int j =0;j<=filterEventsList.get(i).getDifferenceBtwnStartEnd() ;j++){

                        try {
                            Date  splitStartDate = inputFormate.parse(filterEventsList.get(i).getStartDate());
                            Calendar c = Calendar.getInstance();
                            c.setTime(splitStartDate);
                            c.add(Calendar.DAY_OF_MONTH,  +j);
                            Event e = new Event();
                            e.setDifferenceBtwnStartEnd(filterEventsList.get(i).getDifferenceBtwnStartEnd());
                            e.setDateList(filterEventsList.get(i).getDateList());
                            e.setDateStr(filterEventsList.get(i).getDateStr());
                            e.setDinnerList(filterEventsList.get(i).getDinnerList());
                            e.setDinnerTime(filterEventsList.get(i).getDinnerTime());
                            e.setDisablestatus(filterEventsList.get(i).isDisablestatus());
                            e.setEndDate(filterEventsList.get(i).getEndDate());
                            e.setFree(filterEventsList.get(i).getFree());
                            e.setId(filterEventsList.get(i).getId());
                            e.setImage(filterEventsList.get(i).getImage());
                            e.setLink(filterEventsList.get(i).getLink());
                            e.setMaxOrder(filterEventsList.get(i).getMaxOrder());
                            e.setOpenList(filterEventsList.get(i).getOpenList());
                            e.setOpenTime(filterEventsList.get(i).getOpenTime());
                            e.setProductDesc(filterEventsList.get(i).getProductDesc());
                            e.setProductName(filterEventsList.get(i).getProductName());
                            e.setStartDate(filterEventsList.get(i).getStartDate());
                            e.setTotalFreeEventLeft(filterEventsList.get(i).getTotalFreeEventLeft());
                            e.setSplitedEventStartDate(inputFormate.format(c.getTime()));
                            e.setShowTime(filterEventsList.get(i).getShowTime());

                            tempListForDifferentDate.add(e);
                            //        Log.w("AAAA==>", "splitted date:newlist data "+ tempListForDifferentDate.get(i).getSplitedEventStartDate() );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    newList.addAll(tempListForDifferentDate);

                }else {
                    newList.add(filterEventsList.get(i));
                }
            }

          calenderDateListItemAdpter = new CalenderDateListItemAdpter(context, newList,
                    this, inputFormate, outputFormate);
            eventRV.setAdapter(calenderDateListItemAdpter);
            calenderDateListItemAdpter.notifyDataSetChanged();

        } else {
            List<Event> calenderModals = caDateListMap.get(caDateListMap.keySet().toArray()[position]);
            if (calenderModals.size() > 0) {
                viewSwitcher.setDisplayedChild(0);
            } else {
                viewSwitcher.setDisplayedChild(1);
            }
            ArrayList<Event> newList = new ArrayList<Event>();
            for (int i =0;i<calenderModals.size();i++){
                long dayDifference = getCountOfDays(calenderModals.get(i).getStartDate(),calenderModals.get(i).getEndDate());
                calenderModals.get(i).setDifferenceBtwnStartEnd(dayDifference);

            }
            for (int i =0;i<calenderModals.size();i++){
                if(calenderModals.get(i).getDifferenceBtwnStartEnd()>0){
                    ArrayList<Event> tempListForDifferentDate = new ArrayList<Event>();
                    for (int j =0;j<=calenderModals.get(i).getDifferenceBtwnStartEnd() ;j++){

                        try {
                            Date  splitStartDate = inputFormate.parse(calenderModals.get(i).getStartDate());
                            Calendar c = Calendar.getInstance();
                            c.setTime(splitStartDate);
                            c.add(Calendar.DAY_OF_MONTH,  +j);
                            Event e = new Event();
                            e.setDifferenceBtwnStartEnd(calenderModals.get(i).getDifferenceBtwnStartEnd());
                            e.setDateList(calenderModals.get(i).getDateList());
                            e.setDateStr(calenderModals.get(i).getDateStr());
                            e.setDinnerList(calenderModals.get(i).getDinnerList());
                            e.setDinnerTime(calenderModals.get(i).getDinnerTime());
                            e.setDisablestatus(calenderModals.get(i).isDisablestatus());
                            e.setEndDate(calenderModals.get(i).getEndDate());
                            e.setFree(calenderModals.get(i).getFree());
                            e.setId(calenderModals.get(i).getId());
                            e.setImage(calenderModals.get(i).getImage());
                            e.setLink(calenderModals.get(i).getLink());
                            e.setMaxOrder(calenderModals.get(i).getMaxOrder());
                            e.setOpenList(calenderModals.get(i).getOpenList());
                            e.setOpenTime(calenderModals.get(i).getOpenTime());
                            e.setProductDesc(calenderModals.get(i).getProductDesc());
                            e.setProductName(calenderModals.get(i).getProductName());
                            e.setStartDate(calenderModals.get(i).getStartDate());
                            e.setTotalFreeEventLeft(calenderModals.get(i).getTotalFreeEventLeft());
                            e.setSplitedEventStartDate(inputFormate.format(c.getTime()));
                            e.setShowTime(calenderModals.get(i).getShowTime());

                            tempListForDifferentDate.add(e);
                    //        Log.w("AAAA==>", "splitted date:newlist data "+ tempListForDifferentDate.get(i).getSplitedEventStartDate() );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    newList.addAll(tempListForDifferentDate);

                }else {
                    newList.add(calenderModals.get(i));
                }
            }
            calenderDateListItemAdpter = new CalenderDateListItemAdpter(context, newList,
                    this, inputFormate, outputFormate);
            eventRV.setAdapter(calenderDateListItemAdpter);
            calenderDateListItemAdpter.notifyDataSetChanged();
        }

        container.addView(view);
        return view;
    }
    public  long getCountOfDays(String date1, String date2) {

        Date Date1 = null, Date2 = null;
        try {
            Date1 = inputFormate.parse(date1);
            Date2 = inputFormate.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Date2.getTime() - Date1.getTime()) / (24 * 60 * 60 * 1000);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void clickDateView(Event event) {
        calenderFragment.switchToEventDetail(event);
    }

    public void addOnChangeFIlter(String filterSearch, int currentItem) {
        List<Event> events = new LinkedList<Event>();
        for (Event event : caDateListMap.get(caDateListMap.keySet().toArray()[currentItem])) {
            if (event.getProductName().toLowerCase().contains(filterSearch.toLowerCase())) {
                events.add(event);
            }
        }
        calenderDateListItemAdpter.addListData(events);
        calenderDateListItemAdpter.notifyDataSetChanged();
    }

    public void newFilter(List<Event> events) {
        filterEventsList = events;
        notifyDataSetChanged();
    }

    public void attachMap(TreeMap<Date, List<Event>> treeMap) {
        caDateListMap = treeMap;
        notifyDataSetChanged();
    }
}
