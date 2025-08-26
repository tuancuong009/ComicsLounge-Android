package com.comics.lounge.modals;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Event {
    private int id;
    private String productName;
    private String productDesc;
    private String image;
    private String link;
    private String date;
    private String performerName;
    private String supprterName;
    private String showTime;
    private String dinnerTime;
    private String openTime;
    private int free;
    private int maxOrder;
    private String startDate;
    private String endDate;
    private String dateStr;
    private String serverTime;
    private String eventCloseTime;
    private boolean disablestatus, isFav;
    private String disableStatusMsg;
    public String sessionsAllowed;
    private int totalFreeEventLeft;
    private List<Date> dateList = null;
    private long DifferenceBtwnStartEnd = 0;
    private String splitedEventStartDate ;
    boolean memberAccess ;


    public List<Date> getDateList() {
        return dateList;
    }

    public void setDateList(List<Date> dateList) {
        this.dateList = dateList;
    }

    public int getTotalFreeEventLeft() {
        return totalFreeEventLeft;
    }

    public void setTotalFreeEventLeft(int totalFreeEventLeft) {
        this.totalFreeEventLeft = totalFreeEventLeft;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getEventCloseTime() {
        return eventCloseTime;
    }

    public void setEventCloseTime(String eventCloseTime) {
        this.eventCloseTime = eventCloseTime;
    }

    private List<EventPriceDates> eventPriceDatesList = null;
    private List<String> sessionList = null;
    private List<String> dinnerList = null;
    private List<String> openList = null;

    public Event() {
        eventPriceDatesList = new LinkedList<>();
        sessionList = new LinkedList<>();
        dinnerList = new LinkedList<>();
        openList = new LinkedList<>();
        dateList = new LinkedList<>();
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMemberAccess() {
        return memberAccess;
    }

    public void setMemberAccess(boolean memberAccess) {
        this.memberAccess = memberAccess;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getDinnerTime() {
        return dinnerTime;
    }

    public void setDinnerTime(String dinnerTime) {
        this.dinnerTime = dinnerTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(int maxOrder) {
        this.maxOrder = maxOrder;
    }

    public List<EventPriceDates> getEventPriceDatesList() {
        return eventPriceDatesList;
    }


    public void addEventPrice(EventPriceDates eventPriceDates) {
        eventPriceDatesList.add(eventPriceDates);
    }

    public void setSessionList(String sessionTime) {
        sessionList.add(sessionTime);
    }

    public void  addDateObj(Date date){
        dateList.add(date);
    }

    public List<String> getSessionList() {
        return sessionList;
    }

    public void addSessionOpen(String open) {
        openList.add(open);
    }

    public void addDinnerList(String dinner) {
        dinnerList.add(dinner);
    }

    public List<String> getDinnerList() {
        return dinnerList;
    }

    public void setDinnerList(List<String> dinnerList) {
        this.dinnerList = dinnerList;
    }

    public List<String> getOpenList() {
        return openList;
    }

    public void setOpenList(List<String> openList) {
        this.openList = openList;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getSupprterName() {
        return supprterName;
    }

    public void setSupprterName(String supprterName) {
        this.supprterName = supprterName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public boolean isDisablestatus() {
        return disablestatus;
    }

    public void setDisablestatus(boolean disablestatus) {
        this.disablestatus = disablestatus;
    }

    public String getDisableStatusMsg() {
        return disableStatusMsg;
    }

    public void setDisableStatusMsg(String disableStatusMsg) {
        this.disableStatusMsg = disableStatusMsg;
    }

    public long getDifferenceBtwnStartEnd() {
        return DifferenceBtwnStartEnd;
    }

    public void setDifferenceBtwnStartEnd(long differenceBtwnStartEnd) {
        DifferenceBtwnStartEnd = differenceBtwnStartEnd;
    }

    public String getSplitedEventStartDate() {
        return splitedEventStartDate;
    }

    public void setSplitedEventStartDate(String splitedEventStartDate) {
        this.splitedEventStartDate = splitedEventStartDate;
        Log.w("DDD==>", "setSplitedEventStartDate: "+this.splitedEventStartDate );
    }
}
