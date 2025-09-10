package com.comics.lounge.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.FullScreenImageViewActivity;
import com.comics.lounge.activity.MyBookingActivity;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.activity.NoInternetActivity;
import com.comics.lounge.adapter.EventDateListItemAdpter;
import com.comics.lounge.adapter.EventTimeListItemAdpter;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.BookTicketPojo;
import com.comics.lounge.modals.Date;
import com.comics.lounge.modals.Event;
import com.comics.lounge.modals.EventNew;
import com.comics.lounge.modals.EventPriceDates;
import com.comics.lounge.modals.Wallet;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.NumberUtils;
import com.comics.lounge.viewModel.EventVM;
import com.comics.lounge.webservice.EventDetailsService;
import com.comics.lounge.webservice.WalletService;
import com.comics.lounge.webservice.manager.EventDetailsServiceManager;
import com.comics.lounge.webservice.manager.WalletServiceManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmEventDetail extends Fragment implements ServiceCallback {
    SimpleDateFormat inputFormat = null;
    SimpleDateFormat outputFormat = null;
    int showOnlyMeal = 0;
    int withMeal = 0;
    double grandTotal = 0;
    public View bottomSheet;
    private MaterialButton getBookTiket;
    private EventDetailsServiceManager eventDetailsServiceManager = null;
    private String userId;
    private AppCompatImageView backgroundImage;
    private AppCompatTextView doorOpenTxt, dinerTxt, showTimeTxt, performanameTxt2, supportedName, description, showOnlyTotal, mealTotalPrice, grandTotalTxt, showOnlyMealCap, showWithMealCap;
    private EventDateListItemAdpter eventDateListItemAdpter = null;
    private RecyclerView dateRV;
    private String dateStr = null;
    private AppCompatImageView minusShowOnly;
    private AppCompatTextView showOnlyPlusMinus;
    private AppCompatImageView plusShowOnlyTxt, minusMeal;
    private AppCompatTextView mealPlusMinTxt;
    private AppCompatImageView plusMeal;
    private EventPriceDates showOnly = null, showWithOnly = null;
    private AppCompatImageView closeShetImg;
    private MaterialButton checkoutButon;
    private Event eventDetail = null;
    private RelativeLayout rlImg;
    private TextView tvTicketInfo;
    private ArrayList<EventPriceDates> eventPriceDatesList = null;
    private String eventId = "0";
    private CoordinatorLayout mainLayout;
    private WalletServiceManager walletServiceManager = null;
    private Wallet wallet;
    private SessionManager sessionManager;
    private ViewSwitcher vsEventdetail;
    private RecyclerView timeRV;
    private EventTimeListItemAdpter eventTimeListItemAdpter = null;
    private LinearLayout bottomTimeViewLayout;
    private String eventTime = "";
    private int eventTimePos = -1;
    private LinearLayout llTimeSelectLable;
    private TextView timeSelectValue;
    CheckBox cbFav;
    private int dateQty = 0;
    EventVM eventVM;
    NewMain activity;
    public boolean toProcessPay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        View view = inflater.inflate(R.layout.activity_event_detail_fragment, container, false);
        mainLayout = view.findViewById(R.id.main_layout);
        rlImg = view.findViewById(R.id.rl_img);
        backgroundImage = view.findViewById(R.id.background_image);
        doorOpenTxt = view.findViewById(R.id.door_open);
        dinerTxt = view.findViewById(R.id.dinner_txt);
        showTimeTxt = view.findViewById(R.id.show_time_txt);
        performanameTxt2 = view.findViewById(R.id.performer_title2);
        supportedName = view.findViewById(R.id.supported_name_txt);
        description = view.findViewById(R.id.descripton);
        showWithMealCap = view.findViewById(R.id.show_with_meal_caption);
        showOnlyMealCap = view.findViewById(R.id.show_only_caption);
        dateRV = view.findViewById(R.id.date_rv);
        vsEventdetail = view.findViewById(R.id.vs_event_detail);
        cbFav = view.findViewById(R.id.cb_like);
        rlImg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels / 2.135)));


        timeRV = view.findViewById(R.id.time_rv);
        LinearLayoutManager timelinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        timeRV.setLayoutManager(timelinearLayoutManager);

        sessionManager = new SessionManager(activity);

        getBookTiket = view.findViewById(R.id.get_ticket_btn);
        bottomSheet = view.findViewById(R.id.bottom_sheet);

        minusShowOnly = view.findViewById(R.id.minus_show_only);
        showOnlyPlusMinus = view.findViewById(R.id.plus_show_only_txt);
        plusShowOnlyTxt = view.findViewById(R.id.plus_show_only);
        showOnlyTotal = view.findViewById(R.id.total_show_only_txt);
        bottomTimeViewLayout = view.findViewById(R.id.bottom_time_view_layout);

        minusMeal = view.findViewById(R.id.minus_meal_txt);
        mealPlusMinTxt = view.findViewById(R.id.plus_meal_txt);
        plusMeal = view.findViewById(R.id.plus_meal_only);
        mealTotalPrice = view.findViewById(R.id.show_with_meal_total_price);

        grandTotalTxt = view.findViewById(R.id.grand_total);

        closeShetImg = view.findViewById(R.id.close_sheet_icon);
        checkoutButon = view.findViewById(R.id.check_out_tick);
        tvTicketInfo = view.findViewById(R.id.tvTicketInfo);

        llTimeSelectLable = view.findViewById(R.id.ll_event_time_label);
        timeSelectValue = view.findViewById(R.id.txt_event_time_display);

        if (getArguments().getString("id") != null) {
            userId = sessionManager.getCurrentUser().getUserId();
            eventId = getArguments().getString("id");
        }


        eventPriceDatesList = new ArrayList<EventPriceDates>();
        inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        outputFormat = new SimpleDateFormat("dd MMM");
        getBookTiket.setOnClickListener(v -> {
            if (eventDetail == null) return;
            if (eventDetail.isDisablestatus()) {
                openAlertDialogWindow();
            } else {
                if (!eventDetailsServiceManager.getDateList().isEmpty()) {
                    openBottomSheetMenu();
                } else {
                    Toast.makeText(activity, getString(R.string.bookig_date_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Date date = new Date();

        String url = UrlCollection.EVENT_DETAILS + "user_id=" + userId + "&event_id=" + eventId;
        eventDetailsServiceManager = new EventDetailsServiceManager(this, activity);
        eventDetailsServiceManager.generateAPI(url);
        eventDetailsServiceManager.prepareWebServiceJob();
        eventDetailsServiceManager.featchData();
        AppUtil.showLoading(activity);

        //   parentActivity.showLoader();


        callingWaletAPI();


        closeShetImg.setOnClickListener(v -> bottomSheet.setVisibility(View.GONE));


        showOnlyPlusMinus.setText(String.valueOf(showOnlyMeal));

        minusShowOnly.setOnClickListener(v -> {
            if (dateStr != null && !dateStr.equals("")) {
                if (showOnly != null) {
                    if (showOnlyMeal > 0) {
                        showOnlyMeal -= 1;
                        eventPriceDatesList.remove(showOnly);
                        calculateTotal(true, showOnlyMeal, showOnlyPlusMinus, showOnlyMealCap, showOnly, showOnlyTotal);
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.show_only_new) + " " + getResources().getString(R.string.ticket_not_avilble), Toast.LENGTH_SHORT).show();
                }

            } else {
                grandTotal = 0;
            }
        });

        plusShowOnlyTxt.setOnClickListener(v -> {
            if (dateStr != null && !dateStr.equals("")) {
                if (showOnly != null) {
                    showOnlyMeal += 1;
                    calculateTotal(true, showOnlyMeal, showOnlyPlusMinus, showOnlyMealCap, showOnly, showOnlyTotal);
                    eventPriceDatesList.add(showOnly);
                } else {
                    Toast.makeText(activity, getString(R.string.show_with_meal_qty) + " " + getResources().getString(R.string.ticket_not_avilble), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, getResources().getString(R.string.pls_select_date), Toast.LENGTH_SHORT).show();
            }
        });


        minusMeal.setOnClickListener(v -> {
            if (dateStr != null && !dateStr.equals("")) {
                if (showWithOnly != null) {
                    if (withMeal > 0) {
                        withMeal -= 1;
                        eventPriceDatesList.remove(showWithOnly);
                        calculateTotal(false, withMeal, mealPlusMinTxt, showWithMealCap, showWithOnly, mealTotalPrice);
                    }
                } else {
                    Toast.makeText(activity, Constant.SHOW_WITH_MEAL_STR + " " + getResources().getString(R.string.ticket_not_avilble), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(activity, getResources().getString(R.string.pls_select_date), Toast.LENGTH_SHORT).show();
            }

        });


        plusMeal.setOnClickListener(v -> {
            if (dateStr != null && !dateStr.equals("")) {
                if (showWithOnly != null) {
                    withMeal += 1;
                    grandTotal += 1;
                    calculateTotal(false, withMeal, mealPlusMinTxt, showWithMealCap, showWithOnly, mealTotalPrice);
                    eventPriceDatesList.add(showWithOnly);
                } else {
                    Toast.makeText(activity, Constant.SHOW_WITH_MEAL_STR + " " + getResources().getString(R.string.ticket_not_avilble), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(activity, getResources().getString(R.string.pls_select_date), Toast.LENGTH_SHORT).show();
            }
        });

        checkoutButon.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(activity)) {
                if (dateStr != null && !dateStr.equals("")) {
                    if (!eventDetail.getSessionList().isEmpty()) {
                        if (eventTimePos != -1) {
                            continueForCheckout();
                        } else {
                            Toast.makeText(activity, getResources().getString(R.string.pls_select_time), Toast.LENGTH_SHORT).show();;
                        }
                    } else {
                        int totalQty = showOnlyMeal + withMeal;
                        if (dateQty > 0 && totalQty <= dateQty) {
                            continueForCheckout();
                        } else {
                            Toast.makeText(activity, getResources().getString(R.string.free_ticket_over), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.pls_select_date), Toast.LENGTH_SHORT).show();
                }
            } else {
                switchTONoInternetActivity();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventVM = new ViewModelProvider(activity).get(EventVM.class);

        cbFav.setOnClickListener(v -> {
            EventNew en = new EventNew();
            en.setFav(!eventDetail.isFav());
            en.setId(eventId);
            eventVM.isChecked(en);
            HashMap<String, Object> map = new HashMap<>();
            map.put("user_id", userId);
            map.put("event_id", eventId);
            if (!eventDetail.isFav()) {
                addOrRemove("event/add_fav_event.php", map);
            } else {
                addOrRemove("event/remove_fav_event.php", map);
            }
            eventDetail.setFav(!eventDetail.isFav());
        });
    }

    // add or remove favorite gallery
    private void addOrRemove(String url, HashMap<String, Object> map) {
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.addOrRemoveGallery(url, map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        Toast.makeText(activity, jsonObject.optString("data"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void continueForCheckout() {
        if (withMeal > 0 || showOnlyMeal > 0) {
            String grandTotal = grandTotalTxt.getText().toString().trim();
            int totalCounter = withMeal + showOnlyMeal;

            BookTicketPojo bookTicketPojo = new BookTicketPojo();
            bookTicketPojo.setEventDetailId(eventDetail.getId());
            bookTicketPojo.setShowOnlyQty(showOnlyMeal);
            if (showOnly != null) {
                bookTicketPojo.setShowOnlyPrice(showOnly.getPrice());
            } else {
                bookTicketPojo.setShowOnlyPrice("$0");
            }

            if (showWithOnly != null) {
                bookTicketPojo.setWithMealPrice(showWithOnly.getPrice());
            } else {
                bookTicketPojo.setWithMealPrice("$0");
            }

            bookTicketPojo.setWithMealQty(withMeal);
            bookTicketPojo.setGrandTotal(grandTotal);
            bookTicketPojo.setTotalCounter(totalCounter);
            bookTicketPojo.setEventPriceDatesArrayList(eventPriceDatesList);
            bookTicketPojo.setWalletBalance(wallet != null ? wallet.balance : "0");
            bookTicketPojo.setWalletFreeTicket(wallet != null ? wallet.eventCountLeft : 0);
            bookTicketPojo.setFreeTicket(eventDetail.getFree());
            bookTicketPojo.setShowOnlyTotal(showOnlyTotal.getText().toString().trim());
            bookTicketPojo.setWithMeaalTotal(mealTotalPrice.getText().toString().trim());
            bookTicketPojo.setTotalFreeEventLeft(eventDetail.getTotalFreeEventLeft());
            activity.myBookingActivity = new MyBookingActivity();
            Bundle bundle = new Bundle();
            bundle.putSerializable("bookPojo", bookTicketPojo);
            bundle.putString("serverTime", eventDetail.getServerTime());
            bundle.putString("eventCloseTime", eventDetail.getEventCloseTime());
            bundle.putString("eventSelectTime", eventTime);
            bundle.putInt("eventSelectTimePOS", eventTimePos);
            bundle.putString("eventTitle", eventDetail.getProductName());
            bundle.putString("eventDateSelect", DatesUtils.AppDateFormat(dateStr, "dd/MM/yyyy"));
            bundle.putString("openTime", eventDetail.getOpenTime());
            activity.myBookingActivity.setArguments(bundle);
            activity.addFrmDetail(activity.myBookingActivity);
            toProcessPay = true;
            this.grandTotal = 0.0;
        } else {
            Toast.makeText(activity, getResources().getString(R.string.pls_add_ticket), Toast.LENGTH_SHORT).show();
        }
    }

    private void openAlertDialogWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setMessage(eventDetail.getDisableStatusMsg());
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //openBottomSheetMenu();
                    }
                });
        builder.show();
        builder.setCancelable(false);
    }

    private void switchTONoInternetActivity() {
        Intent intent = new Intent(activity, NoInternetActivity.class);
        startActivity(intent);
    }

    public void openBottomSheetMenu() {
        bottomSheet.setVisibility(View.VISIBLE);
    }

    private void callingWaletAPI() {
        walletServiceManager = new WalletServiceManager(this, activity);
        walletServiceManager.generateUrl(UrlCollection.WALLET + userId);
        walletServiceManager.prepareWebServiceJob();
        walletServiceManager.featchData();
    }

    private void calculateTotal(boolean isWithoutMeal, int counter, AppCompatTextView txtCounterView, AppCompatTextView txtView, EventPriceDates eventPriceDates, AppCompatTextView totalValue) {
        double grandTotal = 0.0;
        if (eventPriceDates != null) {
            if (isWithoutMeal) {
                txtCounterView.setText(String.valueOf(counter));
                txtView.setText(getString(R.string.show_only_new) +  NumberUtils.formatMoney(eventPriceDates.getPrice()) + " * " + counter);
                double totalPrice = NumberUtils.parseMoney(eventPriceDates.getPrice()) * counter;
                totalValue.setText(NumberUtils.formatMoney(totalPrice));

                eventPriceDates.setQty(String.valueOf(counter));
            } else {
                txtCounterView.setText(String.valueOf(counter));
                txtView.setText(getString(R.string.show_with_meal_qty) +  NumberUtils.formatMoney(eventPriceDates.getPrice()) + " * " + counter);
                double totalPrice = NumberUtils.parseMoney(eventPriceDates.getPrice()) * counter;
                totalValue.setText(NumberUtils.formatMoney(totalPrice));
                eventPriceDates.setQty(String.valueOf(counter));
            }
            try {
                //  String result = yourString.replaceAll("[-+.^:,]","");
                grandTotal += NumberUtils.parseMoney(mealTotalPrice)
                        + NumberUtils.parseMoney(showOnlyTotal);
                grandTotalTxt.setText(NumberUtils.formatMoney(grandTotal));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(EventDetailsService.SERVICE_NAME)) {
            // parentActivity.hideLoader();
            if (eventDetailsServiceManager.getServiceStatus() != null && eventDetailsServiceManager.getServiceStatus().equals("success")) {
                eventDetail = eventDetailsServiceManager.getEventObj();
                Picasso.get()
                        .load(eventDetail.getImage())
                        .placeholder(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher_foreground)
                        .into(backgroundImage);
                if (eventDetail.sessionsAllowed.toLowerCase().equals("yes")) {
                    StringBuilder sessions = new StringBuilder();
                    for (int i = 0; i < eventDetail.getSessionList().size(); i++) {
                        sessions.append(eventDetail.getSessionList().get(i)).append("\n");
                    }
                    showTimeTxt.setText(sessions.toString());


                    if (!eventDetail.getOpenList().isEmpty()) {
                        String openTime = "";
                        for (int i = 0; i < eventDetail.getOpenList().size(); i++) {
                            openTime += eventDetail.getOpenList().get(i) + "\n";
                        }
                        doorOpenTxt.setText(openTime);
                    } else {
                        doorOpenTxt.setText(eventDetail.getOpenTime());
                    }


                    if (!eventDetail.getDinnerList().isEmpty()) {
                        StringBuilder dinnerTime = new StringBuilder();
                        for (int i = 0; i < eventDetail.getDinnerList().size(); i++) {
                            dinnerTime.append(eventDetail.getDinnerList().get(i)).append("\n");
                        }
                        dinerTxt.setText(dinnerTime.toString());
                    } else {
                        dinerTxt.setText(eventDetail.getOpenTime());
                    }


                } else {
                    showTimeTxt.setText(eventDetail.getShowTime());
                    doorOpenTxt.setText(eventDetail.getOpenTime());
                    dinerTxt.setText(eventDetail.getDinnerTime());
                    llTimeSelectLable.setVisibility(View.GONE);
                }

                cbFav.setChecked(eventDetail.isFav());
                performanameTxt2.setText(eventDetail.getPerformerName().trim());
                supportedName.setText(eventDetail.getSupprterName());
                description.setText(eventDetail.getProductDesc());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                dateRV.setLayoutManager(linearLayoutManager);
                eventDateListItemAdpter = new EventDateListItemAdpter(activity, eventDetailsServiceManager.getDateList(), this);
                eventDateListItemAdpter.addDateFormate(inputFormat, outputFormat);
                dateRV.setAdapter(eventDateListItemAdpter);
                AppUtil.rcvNoAnimator(dateRV);

                if (!eventDetailsServiceManager.getEventObj().getSessionList().isEmpty()) {
                    bottomTimeViewLayout.setVisibility(View.VISIBLE);
                    eventTimeListItemAdpter = new EventTimeListItemAdpter(activity, eventDetailsServiceManager.getEventObj().getSessionList(), this);
                    timeRV.setAdapter(eventTimeListItemAdpter);
                } else {
                    bottomTimeViewLayout.setVisibility(View.GONE);
                }
//                Log.e("TAG", "serviceEnd: "+eventDetail.isMemberAccess() );
//                if (eventDetail.isMemberAccess()) {
//                    tvTicketInfo.setVisibility(View.VISIBLE);
//                } else {
//                    tvTicketInfo.setVisibility(View.GONE);
//                }
                mainLayout.setVisibility(View.VISIBLE);
            }
        } else if (serviceName.equals(WalletService.SERVICE_NAME)) {
            if (walletServiceManager.getServiceStatus() != null) {
                if (walletServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                    wallet = walletServiceManager.getWalletData();
                    sessionManager.storeFreeTicketCount(wallet.eventCountLeft);
                    sessionManager.freeEventRestored(wallet.freeEventRestored,
                            wallet.eventCountAllowed,
                            String.valueOf(wallet.eventCountLeft));
                }
            }

        }
        AppUtil.hideLoading();
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void clickDateView(String dateStr) {
        if (this.dateStr != null && !this.dateStr.equals(dateStr)) {
            withMeal = 0;
            showOnlyMeal = 0;
            grandTotal = 0;
            calculateTotal(false, withMeal, mealPlusMinTxt, showWithMealCap, showWithOnly, mealTotalPrice);
            calculateTotal(true, showOnlyMeal, showOnlyPlusMinus, showOnlyMealCap, showOnly, showOnlyTotal);
            eventPriceDatesList.clear();
        }

        this.dateStr = dateStr;


        for (Date date : eventDetailsServiceManager.getEventObj().getDateList()) {
            if (date.getDate().equals(dateStr)) {
                dateQty = date.getQty();
                break;
            }
        }


        for (EventPriceDates eventPriceDates : eventDetailsServiceManager.getEventObj().getEventPriceDatesList()) {
            if (dateStr.equals(eventPriceDates.getDate())) {
                if (eventPriceDates.getText().equals(Constant.SHOW_ONLY_STR)) {
                    showOnly = eventPriceDates;
                    showOnlyMealCap.setText(getString(R.string.show_only_new) + eventPriceDates.getPrice() + " * 0");
                }
                if (eventPriceDates.getText().equals(Constant.SHOW_WITH_MEAL_STR)) {
                    showWithOnly = eventPriceDates;
                    showWithMealCap.setText(getString(R.string.show_with_meal_qty) + eventPriceDates.getPrice() + " * 0");
                }
            }
        }
    }

    public void clickTimeForEvent(String dateStr, int position) {
        eventTime = dateStr;
        eventTimePos = position;
        llTimeSelectLable.setVisibility(View.VISIBLE);
        timeSelectValue.setText("OPEN TIME : " + eventTime + " DINNER TIME : " + eventDetail.getDinnerList().get(eventTimePos));
    }
}
