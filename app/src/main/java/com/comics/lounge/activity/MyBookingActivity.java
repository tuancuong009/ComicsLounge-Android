package com.comics.lounge.activity;

import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

import android.app.Activity;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.BrowserSwitchResult;
import com.braintreepayments.api.PayPalAccountNonce;
import com.braintreepayments.api.PayPalBrowserSwitchResultCallback;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalFlowStartedCallback;
import com.braintreepayments.api.PayPalPaymentIntent;
import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.fragments.CountryPickerBottomSheetDialog;
import com.comics.lounge.modals.BookTicketPojo;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.DialogUtils;
import com.comics.lounge.utils.JsonUtils;
import com.comics.lounge.utils.NumberUtils;
import com.comics.lounge.utils.PayPalUtils;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.FinalOrdersService;
import com.comics.lounge.webservice.GetPaypalTokenService;
import com.comics.lounge.webservice.PayPalLogService;
import com.comics.lounge.webservice.ServerTimeService;
import com.comics.lounge.webservice.manager.FinalOrdersServiceManager;
import com.comics.lounge.webservice.manager.GetPaypalTokenServiceManager;
import com.comics.lounge.webservice.manager.PayPalLogServiceManager;
import com.comics.lounge.webservice.manager.ServerTimeServiceManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
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

import static com.comics.lounge.conf.Constant.BOOKING_CONFIRM;
import static com.comics.lounge.conf.Constant.FREE_TICKETS;
import static com.comics.lounge.conf.Constant.FREE_TRANSACTION;
import static com.comics.lounge.conf.Constant.PAID_TICKETS;
import static com.comics.lounge.conf.Constant.REQUEST_PAY_WITH_PAYPAL;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import timber.log.Timber;

public class MyBookingActivity extends Fragment implements ServiceCallback {

    double walletAppliedBalance = 0.0;
    private AppCompatTextView showOnlyCap;
    private AppCompatTextView showOnlyValue;
    private AppCompatTextView withMealCap;
    private AppCompatTextView withMealPriceTxt;
    private AppCompatTextView discountSubTotalTxt;
    private AppCompatTextView discountPriceTxt;
    private AppCompatTextView subTotalTaxTxt;
    private AppCompatTextView subToatlTaxPrice;
    private AppCompatTextView grandTotalTxt;
    private AppCompatTextView freeTicketCap;
    private AppCompatCheckBox freeTicketCheck;
    private AppCompatTextView walletCap;
    private AppCompatCheckBox walletCheck;
    private double totalPrice = 00.0;
    private MaterialButton checkOutBtn;
    private GetPaypalTokenServiceManager getPaypalTokenServiceManager;
    private FinalOrdersServiceManager finalOrdersServiceManager = null;
    private String userID;
    private boolean isFreeTicket = false;
    private boolean isWalletCheck = false;
    private AppCompatImageView freeTicketIcon;
    private AppCompatTextView freeTicketheading;
    private ViewSwitcher viewSwitcherLayout;
    private LinearLayout mainLayout;
    private double subTotalPrice = 0.0;
    private BookTicketPojo bookTicketPojo;
    private AppCompatTextView walGrandTotalTxt;
    private AppCompatTextView walletBalanceTxt;
    private LinearLayout walletTotalLayout, llRead;
    private LinearLayout walletBalanceLayout;
    private View walletViewLayout;
    private double finalPayableAmount = 0.0;
    private SessionManager sessionManager;
    private String closeEventTime;
    private ServerTimeServiceManager serverTimeServiceManager;
    private TextView gstTitleTxt;
    private PayPalLogServiceManager payPalLogServiceManager;
    boolean isSubmitClicked = false;
    private String eventTimeStr;
    private int eventTimePOS;
    private TextView titleTxt;
    private TextView dateOrTime;
    private String eventTitle;
    private String eventDateselect, eventOpenTime;
    private int stateId = 42;
    private String city = "";
    String day;
    NewMain activity;
    PopupWindow popupWindow;
    LinearLayoutCompat llPrintTick;
    CardView cvPrintTick;
    TextView tvPrintTick;
    CheckBox cbRead;
    private int selectedNumber = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        View view = inflater.inflate(R.layout.activity_my_booking, container, false);
        mainLayout = view.findViewById(R.id.main_layout);
        freeTicketCap = view.findViewById(R.id.free_ticket_caption);
        freeTicketCheck = view.findViewById(R.id.free_ticekt_check);
        viewSwitcherLayout = view.findViewById(R.id.view_switcher_layout);
        walletCap = view.findViewById(R.id.wallet_caption);
        walletCheck = view.findViewById(R.id.wallet_check);
        llPrintTick = view.findViewById(R.id.ll_print_tick);
        llRead = view.findViewById(R.id.ll_read);
        cvPrintTick = view.findViewById(R.id.cv_print_tick);
        tvPrintTick = view.findViewById(R.id.tv_print_tick);
        cbRead = view.findViewById(R.id.is_checkbox);

        titleTxt = view.findViewById(R.id.event_title_heading);
        dateOrTime = view.findViewById(R.id.event_datetime_heading);

        //ticket
        freeTicketheading = view.findViewById(R.id.free_ticket_heading);
        freeTicketIcon = view.findViewById(R.id.free_tickeet_icon);

        showOnlyCap = view.findViewById(R.id.show_only_cap_txt);
        showOnlyValue = view.findViewById(R.id.show_only_txt);

        withMealCap = view.findViewById(R.id.with_meal_cap_txt);
        withMealPriceTxt = view.findViewById(R.id.with_meal_txt);

        discountSubTotalTxt = view.findViewById(R.id.discount_subtotal_txt);
        discountPriceTxt = view.findViewById(R.id.discount_txt);

        subTotalTaxTxt = view.findViewById(R.id.sub_toatl_tax_txt);

        gstTitleTxt = view.findViewById(R.id.gst_title_txt);
        subToatlTaxPrice = view.findViewById(R.id.tax_txt);

        grandTotalTxt = view.findViewById(R.id.grand_total_txt);
        checkOutBtn = view.findViewById(R.id.check_out_tick);

        walletBalanceTxt = view.findViewById(R.id.free_ticket_txt);
        walGrandTotalTxt = view.findViewById(R.id.free_tic_grand_total);

        walletBalanceLayout = view.findViewById(R.id.wallet_balance_layout);
        walletViewLayout = view.findViewById(R.id.wallet_view_layout);
        walletTotalLayout = view.findViewById(R.id.wallet_total_layout);


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
            //Log.e("POJJOO", bookTicketPojo.getEventTitle());
        }
        userID = sessionManager.getCurrentlyLoggedUserId();
        
        freeTicketCap.setText(bookTicketPojo.getWalletFreeTicket() + " Tickets Available");
        // bookTicketPojo.setWalletBalance("0");
        if (bookTicketPojo.getWalletBalance() != null && !bookTicketPojo.getWalletBalance().equals("") && !bookTicketPojo.getWalletBalance().equals("0")) {
            walletDisabled(false);
        } else {
            walletDisabled(true);
        }

        if (sessionManager.getCurrentUser().getNoOfStrike() != null && sessionManager.getCurrentUser().getNoOfStrike() == 3) {
            freeTicketheading.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            freeTicketCap.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            freeTicketCap.setText("3 strikes(your membership is currently suspended)\n" +
                    "renewal date is " + "(" + sessionManager.getCurrentUser().getActivationDate() + ")");
            freeTicketCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            freeTicketCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            isViewHideOrShow(freeTicketCheck, false);
        } else {
            if (bookTicketPojo.getFreeTicket() > 0 && bookTicketPojo.getWalletFreeTicket() > 0 && bookTicketPojo.getTotalFreeEventLeft() > 0) {
                isViewHideOrShow(freeTicketCheck, true);
                freeTicketheading.setTextColor(ContextCompat.getColor(activity, R.color.black));
                freeTicketCap.setTextColor(ContextCompat.getColor(activity, R.color.black));
                freeTicketIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(activity, R.drawable.ic_question);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(activity, R.color.colorAccent));
                freeTicketIcon.setImageDrawable(wrappedDrawable);
                freeTicketCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
                freeTicketCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
            } else {
                freeTicketheading.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
                freeTicketCap.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(activity, R.drawable.ic_question);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(activity, R.color.disable_input_color));
                freeTicketIcon.setImageDrawable(wrappedDrawable);

                freeTicketIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
                freeTicketCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
                freeTicketCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
                isViewHideOrShow(freeTicketCheck, false);
            }
        }


        // gstTitleTxt.setText("GST (" + Constant.GST + "%)");

        titleTxt.setText(eventTitle);

        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = format.parse(eventDateselect);
            day = String.valueOf(android.text.format.DateFormat.format("EEEE", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (eventTimeStr != null && !eventTimeStr.equals("")) {
            dateOrTime.setText(AppUtil.fmNewDate2(eventDateselect) + " - " + day + " " + eventTimeStr);
        } else {
            dateOrTime.setText(AppUtil.fmNewDate2(eventDateselect) + " - " + day + " " + eventOpenTime);
        }
        AppUtil.disableBt(checkOutBtn);


        freeTicketCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //walletDisabled(true);
                // walletDisabled(false);
                if (bookTicketPojo.getTotalCounter() >= 2) {
                    isFreeTicket = true;
                    totalPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) *
                            bookTicketPojo.getFreeTicket() * 2;
                    /*totalPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) *
                            bookTicketPojo.getFreeTicket() * 2;*/
                    discountPriceTxt.setText(NumberUtils.formatMoney(totalPrice));
                } else {
                    isFreeTicket = true;
                    totalPrice = NumberUtils.parseMoney(bookTicketPojo.getShowOnlyPrice()) *
                            bookTicketPojo.getFreeTicket() * 1;
                    discountPriceTxt.setText(NumberUtils.formatMoney(totalPrice));
                }
            } else {
                //walletDisabled(false);
                //  walletDisabled(false);
                totalPrice = 00.0;
                discountPriceTxt.setText(NumberUtils.formatMoney(0));
                isFreeTicket = false;
                subTotalTaxTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));
            }
            calculateSubTotal();
            generateGrandTotal(isWalletCheck);
        });
        walletCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            generateGrandTotal(isChecked);
            isWalletCheck = isChecked;
        });
        Log.d("PSDSDKS", bookTicketPojo.getShowOnlyPrice());

        showOnlyCap.setText(getString(R.string.show_only_new) + " " +
                NumberUtils.formatMoney(bookTicketPojo.getShowOnlyPrice()) + " * " + bookTicketPojo.getShowOnlyQty());
        showOnlyValue.setText(NumberUtils.formatMoney(bookTicketPojo.getShowOnlyTotal()));
        withMealCap.setText(getString(R.string.show_with_meal_qty) + " " +
                NumberUtils.formatMoney(bookTicketPojo.getWithMealPrice()) + " * " + bookTicketPojo.getWithMealQty());
        withMealPriceTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getWithMeaalTotal()));

        discountSubTotalTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));
        discountPriceTxt.setText(NumberUtils.formatMoney(totalPrice));

        subTotalTaxTxt.setText(NumberUtils.formatMoney(bookTicketPojo.getGrandTotal()));
        // subToatlTaxPrice.setText("$4.95");


        calculateSubTotal();

        double grandTotalMain = getTotal();
        grandTotalTxt.setText(NumberUtils.formatMoney(grandTotalMain));
        finalPayableAmount = grandTotalMain;

        checkOutBtn.setOnClickListener(v -> {
            if (isSubmitClicked) return;
            isSubmitClicked = true;
            openCountryDialog();

        });
        cbRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppUtil.enableBt(checkOutBtn);
                }else {
                    AppUtil.disableBt(checkOutBtn);
                }
            }
        });

        freeTicketIcon.setOnClickListener(v -> {
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
        llPrintTick.setOnClickListener(v -> showPopup());
        cvPrintTick.setOnClickListener(v -> showPopup());

        return view;
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

        for (int i = 1; i <= 10; i++) {
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
                    selectedNumber = -1;
                    llRead.setVisibility(View.GONE);
                    cvPrintTick.setVisibility(View.GONE);
                }else {
                    selectedNumber = number;

                    // Uncheck all other checkboxes
                    for (CheckBox cb : checkBoxList) {
                        if (cb != view) {
                            cb.setChecked(false);
                        }
                    }

                    // Show result and dismiss popup
                    cvPrintTick.setVisibility(View.VISIBLE);
                    if (selectedNumber == 1) {
                        tvPrintTick.setText(selectedNumber + " ticket");
                    }else {
                        tvPrintTick.setText(selectedNumber + " tickets");
                    }
                    llRead.setVisibility(View.VISIBLE);
                }
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
        popupWindow.showAsDropDown(llPrintTick, 0, 10);
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
            walletCap.setText("Wallet " + NumberUtils.formatMoney("0"));
            walletCheck.setEnabled(false);
            isViewHideOrShow(walletCheck, false);
            walletCap.setTextColor(ContextCompat.getColor(activity, R.color.disable_input_color));
            walletCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
            walletCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.disable_input_color)));
        } else {
            walletCap.setText("Wallet " + NumberUtils.formatMoney(bookTicketPojo.getWalletBalance()));
            isViewHideOrShow(walletCheck, true);
            walletCheck.setEnabled(true);
            walletCap.setTextColor(ContextCompat.getColor(activity, R.color.black));
            walletCheck.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
            walletCheck.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorAccent)));
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
        subTotalPrice = NumberUtils.parseMoney(bookTicketPojo.getGrandTotal()) - totalPrice;
        subTotalTaxTxt.setText(NumberUtils.formatMoney(subTotalPrice));
        //  double gstPrice = (subTotalPrice / 100.0f) * Constant.GST;
        double bookingChargePrice = Constant.BOOKING_CHARGES;
        subToatlTaxPrice.setText(NumberUtils.formatMoney(bookingChargePrice));
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
            walletBalanceTxt.setText(NumberUtils.formatMoney(walletAppliedBalance));
            walGrandTotalTxt.setText(NumberUtils.formatMoney(total));
            hideOrShowWalletView(View.VISIBLE);
        } else {
            hideOrShowWalletView(View.GONE);
        }

        if (subTotalPrice <= 0) {
            grandTotalTxt.setText(NumberUtils.formatMoney(0));
            walletBalanceTxt.setText(NumberUtils.formatMoney(walletAppliedBalance));
            walGrandTotalTxt.setText(NumberUtils.formatMoney(total));
            subTotalTaxTxt.setText(NumberUtils.formatMoney(0));
            subToatlTaxPrice.setText(NumberUtils.formatMoney(0));

            hideOrShowWalletView(View.GONE);
        } else {
            grandTotalTxt.setText(NumberUtils.formatMoney(grandTotalMain));
        }
        finalPayableAmount = isWallet ? NumberUtils.parseMoney(walGrandTotalTxt) : NumberUtils.parseMoney(grandTotalTxt);
    }

    private void hideOrShowWalletView(int isStatus) {
        walletTotalLayout.setVisibility(isStatus);
        walletViewLayout.setVisibility(isStatus);
        walletBalanceLayout.setVisibility(isStatus);
    }

    private double getTotal() {
        return NumberUtils.parseMoney(bookTicketPojo.getGrandTotal()) + NumberUtils.parseMoney(subToatlTaxPrice) - totalPrice;
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
        JsonArray jsonTickets = new JsonArray();

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
                    if (jsonObject.get(key) instanceof JSONObject) {
                        JSONObject jsonObject1 = (JSONObject) jsonObject.get(key);
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
        }

        finalOrdersServiceManager = new FinalOrdersServiceManager(this, activity);
        finalOrdersServiceManager.prepareWebServiceJob();
        try {
            JsonObject jsonObjectFree = new JsonObject();
            jsonObjectFree.add(FREE_TICKETS, freeJsonArray);

            JsonObject jsonObjectPaid = new JsonObject();
            jsonObjectPaid.add(PAID_TICKETS, paidJsonArray);
            jsonTickets.add(jsonObjectFree);
            jsonTickets.add(jsonObjectPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject loginObj = new JsonObject();
        String walletBal = bookTicketPojo.getWalletBalance() != null && bookTicketPojo.getWalletBalance().equals("") ? bookTicketPojo.getWalletBalance() : "";
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
            displayViewSwitcher(viewSwitcherLayout, 1);
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
        if (serviceName.equals(GetPaypalTokenService.SERVICE_NAME)) {
            new Handler().postDelayed(() -> displayViewSwitcher(viewSwitcherLayout, 0), 4000);
            if (getPaypalTokenServiceManager.getPaypalToken() != null && !getPaypalTokenServiceManager.getPaypalToken().equals("")) {
                Log.e("TAG", "serviceEnd: call paypal" );
                callingBraintreePaypal(getPaypalTokenServiceManager.getPaypalToken());
            }
        } else if (serviceName.equals(FinalOrdersService.SERVICE_NAME)) {
            finalOrdersServiceManager.getResponseMsg();
            //Log.e("FinalOrdersService: ", finalOrdersServiceManager.getResponseMsg());
            displayViewSwitcher(viewSwitcherLayout, 1);
            finalOrdersServiceManager.getServiceStatus();
            if (finalOrdersServiceManager.getServiceStatus() != null &&
                    finalOrdersServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                showPaymentSuccess("Congratulations, your booking is confirmed.");
            } else if (finalOrdersServiceManager.getServiceStatus() != null &&
                    finalOrdersServiceManager.getServiceStatus().toLowerCase().equals("error")) {
                // displaySnackbarMsg("Payment failed try again");
                String message = finalOrdersServiceManager.getResponseMsg();
                showPaymentSuccess(message);
            } else {
                showPaymentSuccess("Congratulations, your booking is confirmed.");
            }

        } else if (serviceName.equals(ServerTimeService.SERVICE_NAME)) {
            Log.e("Server_time :", serverTimeServiceManager.gettingServerTime() + " = " + closeEventTime);
            if (!serverTimeServiceManager.gettingServerTime().equals("")) {
                if (serverTimeServiceManager.gettingServerTime() != null && closeEventTime != null && serverTimeServiceManager.gettingServerTime() != "" && closeEventTime != "") {
                    // TODO: remove server time api and validation
                    boolean isValid = DatesUtils.validateEventClose(serverTimeServiceManager.gettingServerTime(), closeEventTime);
                    if (isValid) {
                        displayViewSwitcher(viewSwitcherLayout, 1);
                        if (finalPayableAmount == 0) {
                            //Call Direct Final OrderAPI
                            addedNotchPaypal("");
                        } else {
                            getPaypalTokenServiceManager = new GetPaypalTokenServiceManager(this, activity);
                            getPaypalTokenServiceManager.prepareWebServiceJob();
                            getPaypalTokenServiceManager.featchData();
                            displayViewSwitcher(viewSwitcherLayout, 1);
                        }
                    } else {
                        displaySnackbarMsg(getResources().getString(R.string.pls_select_before_close_date));
                    }
                } else {
                    displaySnackbarMsg(getResources().getString(R.string.someting_wrong));
                }
            }
        } else if (serviceName.equals(PayPalLogService.SERVICE_NAME)) {
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

        startActivityForResult(intent, REQUEST_PAY_WITH_PAYPAL);
    }


    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void displaySnackbarMsg(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void saveState(int id, String city) {
        this.stateId = id;
        this.city = city;
        generatePayPalToken();
        displayViewSwitcher(viewSwitcherLayout, 1);
    }
}
