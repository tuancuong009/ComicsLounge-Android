package com.comics.lounge.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.databinding.FrmMyBookingBinding;
import com.comics.lounge.fragments.CountryPickerBottomSheetDialog;
import com.comics.lounge.modals.BookTicketPojo;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.DialogUtils;
import com.comics.lounge.utils.JsonUtils;
import com.comics.lounge.utils.NumberUtils;
import com.comics.lounge.webservice.FinalOrdersService;
import com.comics.lounge.webservice.GetPaypalTokenService;
import com.comics.lounge.webservice.PayPalLogService;
import com.comics.lounge.webservice.ServerTimeService;
import com.comics.lounge.webservice.manager.FinalOrdersServiceManager;
import com.comics.lounge.webservice.manager.GetPaypalTokenServiceManager;
import com.comics.lounge.webservice.manager.PayPalLogServiceManager;
import com.comics.lounge.webservice.manager.ServerTimeServiceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.comics.lounge.conf.Constant.FREE_TICKETS;
import static com.comics.lounge.conf.Constant.FREE_TRANSACTION;
import static com.comics.lounge.conf.Constant.PAID_TICKETS;
import static com.comics.lounge.conf.Constant.PRINT_TICKETS;
import static com.comics.lounge.conf.Constant.REQUEST_PAY_WITH_PAYPAL;

public class MyBookingActivity extends Fragment implements ServiceCallback {
    FrmMyBookingBinding binding;
    double walletAppliedBalance = 0.0;
    private double totalPrice = 00.0;
    private double dcTickPrice = 00.0;
    private GetPaypalTokenServiceManager getPaypalTokenServiceManager;
    private FinalOrdersServiceManager finalOrdersServiceManager = null;
    private String userID;
    private boolean isFreeTicket = false;
    private boolean isWalletCheck = false;
    private double subTotalPrice = 0.0;
    private BookTicketPojo bookTicketPojo;
    private double finalPayableAmount = 0.0;
    private SessionManager sessionManager;
    private String closeEventTime;
    private ServerTimeServiceManager serverTimeServiceManager;
    private PayPalLogServiceManager payPalLogServiceManager;
    boolean isSubmitClicked = false;
    private String eventTimeStr;
    private int eventTimePOS;
    private String eventTitle;
    private String eventDateselect, eventOpenTime;
    private int stateId = 42;
    private String city = "";
    String day;
    NewMain activity;
    PopupWindow popupWindow;
    private int selectedNumber = 0;
    int totalTicket, freeTicket = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrmMyBookingBinding.inflate(getLayoutInflater());
        activity = (NewMain) getActivity();

        serverTimeServiceManager = new ServerTimeServiceManager(this, activity);
        payPalLogServiceManager = new PayPalLogServiceManager(this, activity);
        sessionManager = new SessionManager(activity);
        
        if (getArguments() != null) {
            bookTicketPojo = (BookTicketPojo) getArguments().getSerializable("bookPojo");
            closeEventTime = getArguments().getString("eventCloseTime");
            eventTimeStr = getArguments().getString("eventSelectTime");
            eventTimePOS = getArguments().getInt("eventSelectTimePOS", -1);
            eventTitle = getArguments().getString("eventTitle");
            eventDateselect = getArguments().getString("eventDateSelect");
            eventOpenTime = getArguments().getString("openTime");
        }
        userID = sessionManager.getCurrentlyLoggedUserId();
        
        binding.freeTicketCaption.setText(bookTicketPojo.getWalletFreeTicket() + " Tickets Available");
        walletDisabled(bookTicketPojo.getWalletBalance() == null || bookTicketPojo.getWalletBalance().equals("") || bookTicketPojo.getWalletBalance().equals("0"));

        if (sessionManager.getCurrentUser().getNoOfStrike() != null && sessionManager.getCurrentUser().getNoOfStrike() == 3) {
            binding.freeTicketHeading.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            binding.freeTicketCaption.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            binding.freeTicketCaption.setText("3 strikes(your membership is currently suspended)\n" +
                    "renewal date is " + "(" + sessionManager.getCurrentUser().getActivationDate() + ")");
            binding.freeTicektCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            binding.freeTicektCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            isViewHideOrShow(binding.freeTicektCheck, false);
        } else {
            if (bookTicketPojo.getFreeTicket() > 0 && bookTicketPojo.getWalletFreeTicket() > 0 && bookTicketPojo.getTotalFreeEventLeft() > 0) {
                enableMb();
            } else {
                disableMb();
            }
        }

        binding.eventTitleHeading.setText(eventTitle);

        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(eventDateselect);
            day = String.valueOf(android.text.format.DateFormat.format("EEEE", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (eventTimeStr != null && !eventTimeStr.equals("")) {
            binding.eventDatetimeHeading.setText(AppUtil.fmNewDate2(eventDateselect) + " - " + day + " " + eventTimeStr);
        } else {
            binding.eventDatetimeHeading.setText(AppUtil.fmNewDate2(eventDateselect) + " - " + day + " " + eventOpenTime);
        }

        binding.freeTicektCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (bookTicketPojo.getTotalCounter() >= 2) {
                    isFreeTicket = true;
                    totalPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) *
                            bookTicketPojo.getFreeTicket() * 2;
                    freeTicket = 2;
                } else {
                    isFreeTicket = true;
                    totalPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) *
                            bookTicketPojo.getFreeTicket() * 1;
                    freeTicket = 1;
                }
                if ((freeTicket + selectedNumber) > totalTicket){
                    if (selectedNumber > freeTicket) {
                        selectedNumber -= freeTicket;
                    }else {
                        selectedNumber = 0;
                    }
                    calculateUsePrintTick();
                }
                binding.discountTxt.setText("-"+NumberUtils.formatMoney(totalPrice));
            } else {
                totalPrice = 00.0;
                binding.discountTxt.setText("-"+NumberUtils.formatMoney(0));
                isFreeTicket = false;
                binding.subToatlTaxTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));
                freeTicket = 0;
            }
            calculateSubTotal();
            generateGrandTotal(isWalletCheck);
        });
        binding.walletCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            generateGrandTotal(isChecked);
            isWalletCheck = isChecked;
        });
        Log.d("PSDSDKS", bookTicketPojo.getShowOnlyPrice());

        binding.showOnlyCapTxt.setText(getString(R.string.show_only_new) + " " +
                NumberUtils.formatMoney(bookTicketPojo.getShowOnlyPrice()) + " * " + bookTicketPojo.getShowOnlyQty());
        binding.showOnlyTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getShowOnlyTotal()));
        binding.withMealCapTxt.setText(getString(R.string.show_with_meal_qty) + " " +
                NumberUtils.formatMoney(bookTicketPojo.getWithMealPrice()) + " * " + bookTicketPojo.getWithMealQty());
        binding.withMealTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getWithMeaalTotal()));
        totalTicket = Math.min(bookTicketPojo.getShowOnlyQty() + bookTicketPojo.getWithMealQty(), 10);
        binding.discountSubtotalTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));
        binding.discountTxt.setText("-"+NumberUtils.formatMoney(totalPrice));

        binding.subToatlTaxTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));


        calculateSubTotal();

        double grandTotalMain = getTotal();
        binding.grandTotalTxt.setText(NumberUtils.formatMoney(grandTotalMain));
        finalPayableAmount = grandTotalMain;

        binding.checkOutTick.setOnClickListener(v -> {
            if (isSubmitClicked) return;
            isSubmitClicked = true;
            openCountryDialog();

        });
        binding.isCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                AppUtil.enableBt(binding.checkOutTick);
            }else {
                AppUtil.disableBt(binding.checkOutTick);
            }
        });

        binding.freeTickeetIcon.setOnClickListener(v -> {
            if (sessionManager.getCurrentUser().getNoOfStrike() != null && sessionManager.getCurrentUser().getNoOfStrike() == 3) {
                if (sessionManager.getFreeEventRestoredCount().equals("0")) {
                    DialogUtils.showInfoAlert(activity, "",
                            "3 strikes(your membership is currently suspended)\n" +
                                    "renewal date is " + "(" + sessionManager.getCurrentUser().getActivationDate() + ")"
                                    + "\n\n" + "Event count allowed : "+sessionManager.getEventCountAllowed()+ "\n\n" + "Event count left : "+sessionManager.getEventCountLeft());
                } else {
                    DialogUtils.showInfoAlert(activity, "",
                            "3 strikes(your membership is currently suspended)\n" +
                                    "renewal date is " + "(" + sessionManager.getCurrentUser().getActivationDate() + ")" + "\n\n" + sessionManager.getFreeEventRestoredCount()
                                    + "\n\n" + "Event count allowed : "+sessionManager.getEventCountAllowed()+ "\n\n" + "Event count left : "+sessionManager.getEventCountLeft());
                }
            } else {
                if (sessionManager.getFreeEventRestoredCount().equals("0")) {
                    DialogUtils.showInfoAlert(activity, getString(R.string.free_ticket_title),
                            getString(R.string.free_ticket_info)
                                    + "\n\n" + "Event count allowed : "+sessionManager.getEventCountAllowed()+ "\n\n" + "Event count left : "+sessionManager.getEventCountLeft());
                } else {
                    DialogUtils.showInfoAlert(activity, getString(R.string.free_ticket_title),
                            getString(R.string.free_ticket_info) + "\n\n" + sessionManager.getFreeEventRestoredCount()
                                    + "\n\n" + "Event count allowed : "+sessionManager.getEventCountAllowed()+ "\n\n" + "Event count left : "+sessionManager.getEventCountLeft());
                }
            }

        });
        binding.llPrintTick.setOnClickListener(v -> showPopup());
        binding.tvPrintTick.setOnClickListener(v -> showPopup());

        return binding.getRoot();
    }

    // disable member
    private void disableMb(){
        binding.freeTicketHeading.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
        binding.freeTicketCaption.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
        binding.freeTickeetIcon.setColorFilter(activity.getColor(R.color.disable_input_color));

        binding.freeTickeetIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
        binding.freeTicektCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
        binding.freeTicektCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
        isViewHideOrShow(binding.freeTicektCheck, false);
        binding.freeTicektCheck.setChecked(false);
    }

    // enable member
    private void enableMb(){
        isViewHideOrShow(binding.freeTicektCheck, true);
        binding.freeTickeetIcon.setColorFilter(activity.getColor(R.color.colorAccent));
        binding.freeTicketHeading.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        binding.freeTicketCaption.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        binding.freeTicektCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
        binding.freeTicektCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
    }

    private void showPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        View popupView = inflater.inflate(R.layout.popup_ticket_list, null);
        LinearLayout container = popupView.findViewById(R.id.checkboxContainer);

        // Keep reference to all checkboxes to uncheck others
        List<CheckBox> checkBoxList = new ArrayList<>();

        for (int i = 1; i <= totalTicket - freeTicket; i++) {
            LinearLayout rowLayout = new LinearLayout(activity);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(6, 8, 6, 8);

            TextView numberView = new TextView(activity);
            numberView.setText(String.valueOf(i));
            numberView.setTextSize(16);
            numberView.setTextColor(getResources().getColor(android.R.color.black));
            numberView.setPadding(10, 0, 30, 0);
            numberView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            CheckBox checkBox = new CheckBox(activity);
            checkBox.setTag(i);
            if (i == selectedNumber) {
                checkBox.setChecked(true);
            }
            checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_splash)));
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            checkBoxList.add(checkBox);

            checkBox.setOnClickListener(view -> {
                int number = (int) view.getTag();
                if (selectedNumber == number){
                    selectedNumber = 0;
                    binding.llRead.setVisibility(View.GONE);
                    AppUtil.enableBt(binding.checkOutTick);
                }else {
                    selectedNumber = number;

                    for (CheckBox cb : checkBoxList) {
                        if (cb != view) {
                            cb.setChecked(false);
                        }
                    }

                    // Show result and dismiss popup
                    binding.cvPrintTick.setVisibility(View.VISIBLE);

                    binding.llRead.setVisibility(View.VISIBLE);
                    if (!binding.isCheckbox.isChecked()){
                        AppUtil.disableBt(binding.checkOutTick);
                    }else {
                        AppUtil.enableBt(binding.checkOutTick);
                    }
                }
                calculateUsePrintTick();
                popupWindow.dismiss();
            });

            rowLayout.addView(numberView);
            rowLayout.addView(checkBox);
            container.addView(rowLayout);
        }

        popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int xOffset = binding.llPrintTick.getWidth() - popupView.getMeasuredWidth();
        int yOffset = 0;

        if (totalTicket - freeTicket > 0){
            popupWindow.showAsDropDown(binding.llPrintTick, xOffset, yOffset);
        }
    }

    // calculate after use print ticket
    private void calculateUsePrintTick(){
        if (selectedNumber == 1) {
            binding.tvPrintTick.setText(selectedNumber + " credit");
        }else {
            binding.tvPrintTick.setText(selectedNumber + " credits");
        }
        if (selectedNumber > 0){
            binding.tvDcTickCap.setText(getString(R.string.discount_tickets)+" "+NumberUtils.formatMoney(NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()))+"*"+selectedNumber);
        }else {
            binding.tvDcTickCap.setText(getString(R.string.discount_tickets));
        }
        binding.dcPrintTick.setText(String.valueOf(selectedNumber));
        binding.dcTicket.setText("-"+NumberUtils.formatMoney(NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) * selectedNumber));
        dcTickPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) * selectedNumber;
        calculateSubTotal();
        generateGrandTotal(isWalletCheck);
    }

    private void openCountryDialog() {
        CountryPickerBottomSheetDialog bottomSheet = new CountryPickerBottomSheetDialog();
        bottomSheet.attachParam(this, eventDateselect);
        bottomSheet.show(getChildFragmentManager(),"CountryPickerBottomSheetDialog");

    }

    public void updateSubmit() {
        isSubmitClicked = false;
    }

    private void walletDisabled(boolean value) {
        if (value) {
            binding.walletCaption.setText("Wallet " + NumberUtils.formatMoney("0"));
            binding.walletCheck.setEnabled(false);
            isViewHideOrShow(binding.walletCheck, false);
            binding.walletCaption.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            binding.walletCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            binding.walletCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
        } else {
            binding.walletCaption.setText("Wallet " + NumberUtils.formatMoney(bookTicketPojo.getWalletBalance()));
            isViewHideOrShow(binding.walletCheck, true);
            binding.walletCheck.setEnabled(true);
            binding.walletCaption.setTextColor(ContextCompat.getColor(activity, R.color.black));
            binding.walletCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
            binding.walletCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
        }
    }

    /**
     * @param view
     * @param isHideOrShow this method use for enable or clickable view
     */
    public void isViewHideOrShow(View view, boolean isHideOrShow) {
        view.setEnabled(isHideOrShow);
        view.setClickable(isHideOrShow);
    }

    private void calculateSubTotal() {
        subTotalPrice = NumberUtils.parseMoney(bookTicketPojo.getGrandTotal()) - totalPrice - dcTickPrice;
        binding.subToatlTaxTxt.setText(NumberUtils.formatMoney(subTotalPrice));
        //  double gstPrice = (subTotalPrice / 100.0f) * Constant.GST;
        double bookingChargePrice = Constant.BOOKING_CHARGES;
        binding.taxTxt.setText(NumberUtils.formatMoney(bookingChargePrice));
    }


    private void generatePayPalToken() {
        if (GlobalConf.checkInternetConnection(activity)) {
            serverTimeServiceManager.prepareWebServiceJob();
            serverTimeServiceManager.featchData();
        } else {
            switchToNoInternetActivity();
        }
    }

    /**
     * @param viewSwitcher obj
     * @param displayIndex index  // 0- main view ,1- loading
     */
    public void displayViewSwitcher(ViewSwitcher viewSwitcher, int displayIndex) {
        viewSwitcher.setDisplayedChild(displayIndex);
    }

    private void switchToNoInternetActivity() {
        Intent intent = new Intent(activity, NoInternetActivity.class);
        startActivity(intent);
    }

    private void generateGrandTotal(boolean isWallet) {
        double grandTotalMain = getTotal();
        double total = 0.0;
        walletAppliedBalance = grandTotalMain;
        if (isWallet) {
            if (grandTotalMain > bookTicketPojo.getBalance()) {
                walletAppliedBalance = bookTicketPojo.getBalance();
                total = Math.abs(grandTotalMain - bookTicketPojo.getBalance());
            }
            binding.freeTicketTxt.setText("-"+NumberUtils.formatMoney(walletAppliedBalance));
            binding.freeTicGrandTotal.setText(NumberUtils.formatMoney(total));
            hideOrShowWalletView(View.VISIBLE);
        } else {
            hideOrShowWalletView(View.GONE);
        }

        if (subTotalPrice <= 0) {
            binding.grandTotalTxt.setText(NumberUtils.formatMoney(0));
            binding.freeTicketTxt.setText("-"+NumberUtils.formatMoney(walletAppliedBalance));
            binding.freeTicGrandTotal.setText(NumberUtils.formatMoney(total));
            binding.subToatlTaxTxt.setText(NumberUtils.formatMoney(0));
            binding.taxTxt.setText(NumberUtils.formatMoney(0));

            hideOrShowWalletView(View.GONE);
        } else {
            binding.grandTotalTxt.setText(NumberUtils.formatMoney(grandTotalMain));
        }
        finalPayableAmount = isWallet ? NumberUtils.parseMoney(binding.freeTicGrandTotal) : NumberUtils.parseMoney(binding.grandTotalTxt);
    }

    private void hideOrShowWalletView(int isStatus) {
        binding.walletTotalLayout.setVisibility(isStatus);
        binding.walletViewLayout.setVisibility(isStatus);
        binding.walletBalanceLayout.setVisibility(isStatus);
    }

    private double getTotal() {
        return NumberUtils.parseMoney(bookTicketPojo.getGrandTotal()) + NumberUtils.parseMoney(binding.taxTxt) - totalPrice - dcTickPrice;
    }

    public void callPayPalLog(String errorMsg) {
        payPalLogServiceManager.prepareWebServiceJob();
        payPalLogServiceManager.feedParamsWithoutKey(
                JsonUtils.INSTANCE.buildPayPalLogJson(activity, userID, errorMsg).replaceAll("\\\\", ""));
        payPalLogServiceManager.featchData();
    }

    public void addedNotchPaypal(String paymentNonce) {
        JsonArray freeJsonArray = new JsonArray();
        JsonArray paidJsonArray = new JsonArray();
        JsonArray printJsonArray = new JsonArray();
        JsonArray jsonTickets = new JsonArray();
        JsonObject jsPrint = new JsonObject();

        int freeTicket = bookTicketPojo.getFreeTicket() * 2;
        for (int j = 0; j < bookTicketPojo.getTotalCounter(); j++) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("product_id", bookTicketPojo.getEventDetailId() + "");
            jsonObj.addProperty("qty", "1");
            jsonObj.addProperty("custom_price", NumberUtils.parseMoney(bookTicketPojo.getEventPriceDatesArrayList().get(j).getPrice()).toString());

            JsonObject jsonObjectAttb = new JsonObject();
            JSONObject jsonObject;
            if (eventTimePOS != -1) {
                if (eventTimePOS == 0) {
                    jsonObject = bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeFirstJson();
                } else {
                    jsonObject = bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeSecondJson();
                }
            } else {
                jsonObject = bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeJson();
            }

            //JSONObject jsonObject = bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeJson();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();

                try {
                    if (jsonObject.get(key) instanceof JSONObject jsonObject1) {
                        JsonObject jsonObject2 = new JsonObject();
                        String firstKey = jsonObject1.keys().next();
                        jsonObject2.addProperty(firstKey, jsonObject1.getString(firstKey));
                        jsonObjectAttb.add(key, jsonObject2);
                    } else {
                        jsonObjectAttb.addProperty(key, jsonObject.getString(key));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jsonObj.add("attribute", jsonObjectAttb);

            if (eventTimePOS != -1) {
                if (eventTimePOS == 0) {
                    bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeFirstJson();
                } else {
                    bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeSecondJson();
                }
            } else {
                bookTicketPojo.getEventPriceDatesArrayList().get(j).getAttributeJson();
            }

            if (isFreeTicket && freeTicket > j) {
                jsonObj.addProperty("ticket_type", "free");
                freeJsonArray.add(jsonObj);
            } else {
                jsonObj.addProperty("ticket_type", "paid");
                paidJsonArray.add(jsonObj);
            }
            if (j == 0 && selectedNumber > 0){
                jsPrint.addProperty("product_id", bookTicketPojo.getEventDetailId() + "");
                jsPrint.addProperty("qty", selectedNumber);
                jsPrint.addProperty("custom_price", NumberUtils.parseMoney(bookTicketPojo.getEventPriceDatesArrayList().get(j).getPrice()).toString());
                jsPrint.add("attribute", jsonObjectAttb);
                jsPrint.addProperty("ticket_type", "printed");
                printJsonArray.add(jsPrint);
            }
        }

        finalOrdersServiceManager = new FinalOrdersServiceManager(this, activity);
        finalOrdersServiceManager.prepareWebServiceJob();
        try {
            JsonObject jsonObjectFree = new JsonObject();
            jsonObjectFree.add(FREE_TICKETS, freeJsonArray);
            JsonObject jsonObjectPaid = new JsonObject();
            jsonObjectPaid.add(PAID_TICKETS, paidJsonArray);
            JsonObject jsObjPrint = new JsonObject();
            jsObjPrint.add(PRINT_TICKETS, printJsonArray);
            jsonTickets.add(jsonObjectFree);
            jsonTickets.add(jsonObjectPaid);
            jsonTickets.add(jsObjPrint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject loginObj = new JsonObject();
        try {
            loginObj.addProperty("user_id", userID);
            loginObj.addProperty("virtuemart_state_id", stateId);
            loginObj.addProperty("city", city);
            loginObj.addProperty("paypal_response_invoice", finalPayableAmount == 0 ? FREE_TRANSACTION : paymentNonce);
            loginObj.addProperty("sales_price", String.format("%.2f", getTotal()));
            loginObj.addProperty("paypal_fullresponse", finalPayableAmount == 0 ? FREE_TRANSACTION : paymentNonce);
            loginObj.addProperty("wallet_adjutments", isWalletCheck ? walletAppliedBalance + "" : "0.0");
            loginObj.addProperty("paymentMethodNonce", paymentNonce != null && !paymentNonce.equals("") ? paymentNonce : "");


            loginObj.add("tickets", jsonTickets);

            Log.e("TagActivity", loginObj.toString());

            finalOrdersServiceManager.feedParamsWithoutKey(loginObj.toString().replaceAll("\\\\", ""));
            finalOrdersServiceManager.featchData();
            displayViewSwitcher(binding.viewSwitcherLayout, 1);
        } catch (Exception e) {
            e.printStackTrace();
            displaySnackbarMsg("Please try again");
        }
    }


    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        switch (serviceName) {
            case GetPaypalTokenService.SERVICE_NAME -> {
                new Handler().postDelayed(() -> displayViewSwitcher(binding.viewSwitcherLayout, 0), 4000);
                if (getPaypalTokenServiceManager.getPaypalToken() != null && !getPaypalTokenServiceManager.getPaypalToken().equals("")) {
                    Log.e("TAG", "serviceEnd: call paypal");
                    callingBraintreePaypal(getPaypalTokenServiceManager.getPaypalToken());
                }
            }
            case FinalOrdersService.SERVICE_NAME -> {
                finalOrdersServiceManager.getResponseMsg();
                //Log.e("FinalOrdersService: ", finalOrdersServiceManager.getResponseMsg());
                displayViewSwitcher(binding.viewSwitcherLayout, 1);
                finalOrdersServiceManager.getServiceStatus();
                if (finalOrdersServiceManager.getServiceStatus() != null &&
                        finalOrdersServiceManager.getServiceStatus().equalsIgnoreCase("success")) {
                    showPaymentSuccess("Congratulations, your booking is confirmed.");
                } else if (finalOrdersServiceManager.getServiceStatus() != null &&
                        finalOrdersServiceManager.getServiceStatus().equalsIgnoreCase("error")) {
                    // displaySnackbarMsg("Payment failed try again");
                    String message = finalOrdersServiceManager.getResponseMsg();
                    showPaymentSuccess(message);
                } else {
                    showPaymentSuccess("Congratulations, your booking is confirmed.");
                }
            }
            case ServerTimeService.SERVICE_NAME -> {
                Log.e("Server_time :", serverTimeServiceManager.gettingServerTime() + " = " + closeEventTime);
                if (!serverTimeServiceManager.gettingServerTime().equals("")) {
                    if (serverTimeServiceManager.gettingServerTime() != null && closeEventTime != null && serverTimeServiceManager.gettingServerTime() != "" && closeEventTime != "") {
                        // TODO: remove server time api and validation
                        boolean isValid = DatesUtils.validateEventClose(serverTimeServiceManager.gettingServerTime(), closeEventTime);
                        if (isValid) {
                            displayViewSwitcher(binding.viewSwitcherLayout, 1);
                            if (finalPayableAmount == 0) {
                                //Call Direct Final OrderAPI
                                addedNotchPaypal("");
                            } else {
                                getPaypalTokenServiceManager = new GetPaypalTokenServiceManager(this, activity);
                                getPaypalTokenServiceManager.prepareWebServiceJob();
                                getPaypalTokenServiceManager.featchData();
                                displayViewSwitcher(binding.viewSwitcherLayout, 1);
                            }
                        } else {
                            displaySnackbarMsg(getResources().getString(R.string.pls_select_before_close_date));
                        }
                    } else {
                        displaySnackbarMsg(getResources().getString(R.string.someting_wrong));
                    }
                }
            }
            case PayPalLogService.SERVICE_NAME ->
                    Log.e("PayPalLogService: ", payPalLogServiceManager.getResponseMsg());

            //TODO: pending
        }
    }

    private void showPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Payment");
        builder.setCancelable(false);
        builder.setMessage(s);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(activity, NewMain.class).putExtra("screen", "confirmed");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void callingBraintreePaypal(String paypalToken) {
        Intent intent = new Intent(activity, PaypalActivity.class);
        intent.putExtra(Constant.EXTRA_TITLE, getString(R.string.my_booking_str));
        intent.putExtra(Constant.EXTRA_PAYPAL_TOKEN, paypalToken);
        intent.putExtra(Constant.EXTRA_TOTAL, getTotal());
        Log.e("TAG", "callingBraintreePaypal: "+getTotal() );
        Log.e("TAG", "callingBraintreePaypal: "+paypalToken );

        startActivityForResult(intent, REQUEST_PAY_WITH_PAYPAL);
    }


    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void displaySnackbarMsg(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void saveState(int id, String city) {
        this.stateId = id;
        this.city = city;
        generatePayPalToken();
        displayViewSwitcher(binding.viewSwitcherLayout, 1);
    }
}
