package com.comics.lounge.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.adapter.ShowOnlyListItemAdpter;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.BookingHistory;
import com.comics.lounge.modals.ConfirmTicket;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.ConfirmTickertService;
import com.comics.lounge.webservice.DeleteTicketService;
import com.comics.lounge.webservice.manager.ConfirmTickertServiceManager;
import com.comics.lounge.webservice.manager.DeleteTicketServiceManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import static com.comics.lounge.conf.Constant.STATUS_CANCELLED;


public class ConfirmTicketDetailActivity extends AbstractBaseActivity implements ServiceCallback {

    int deletePos;
    String ticketType;
    private AppCompatTextView orderId;
    private LinearLayout downloadLayout;
    private AppCompatTextView perfornameTxt;
    private AppCompatTextView dateTxt;
    private AppCompatTextView toalPurchaseTxt;
    private RecyclerView showOnlyRV;
    private RecyclerView withMealRV;
    private String userId;
    private ConfirmTicket confirmTicket;
    private List<BookingHistory> showOnlyList = null;
    private List<BookingHistory> withMealList = null;
    private ShowOnlyListItemAdpter showOnlyListItemAdpter = null;
    private ShowOnlyListItemAdpter withMealListItemAdpter = null;
    private DeleteTicketServiceManager deleteTicketServiceManager = null;
    private ViewSwitcher viewSwitcherlayout;
    private LinearLayout mainLayout;
    private ConfirmTickertServiceManager confirmTickertServiceManager = null;
    private int tempOrderId = 0;
    private SessionManager sessionManager;
    private View llShowOnly, llShowWithMeal;
    private String orderDate;
    private String orderTime;
    ImageView btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ticket_detail);

        sessionManager = new SessionManager(getApplicationContext());
        mainLayout = findViewById(R.id.main_layout);
        viewSwitcherlayout = findViewById(R.id.view_switcher_layout);
        orderId = findViewById(R.id.order_id_txt);
        downloadLayout = findViewById(R.id.download_ticekt);
        perfornameTxt = findViewById(R.id.product_name_perforname);
        dateTxt = findViewById(R.id.date_time_txt);
        toalPurchaseTxt = findViewById(R.id.total_purchase_ticket_txt);
        showOnlyRV = findViewById(R.id.show_only_rv);
        llShowOnly = findViewById(R.id.llShowOnly);
        llShowWithMeal = findViewById(R.id.llShowWithMeal);
        btBack = findViewById(R.id.bt_back);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        showOnlyRV.setLayoutManager(linearLayoutManager1);

        /*menuRight.setVisibility(View.INVISIBLE);
        leftArrow.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        leftArrow.setScaleX(ResourcesCompat.getFloat(getApplicationContext().getResources(), R.dimen.back_awwor_flot_size));
        leftArrow.setScaleY(ResourcesCompat.getFloat(getApplicationContext().getResources(), R.dimen.back_awwor_flot_size));*/

        withMealRV = findViewById(R.id.with_neal_rv);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        withMealRV.setLayoutManager(linearLayoutManager2);

        Intent intent = getIntent();
        if (intent != null) {
            tempOrderId = intent.getIntExtra("orderID", 0);
            orderDate = intent.getStringExtra("orderDate");
            orderTime = intent.getStringExtra("orderTime");
        }
        userId = sessionManager.getCurrentUser().getUserId();

        showOnlyList = new LinkedList<>();
        withMealList = new LinkedList<>();

        deleteTicketServiceManager = new DeleteTicketServiceManager(this, this);

        downloadLayout.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(this)) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(confirmTicket.getDownloadLink()));
                startActivity(browserIntent);
            } else {
                switchToNoInternetActivity();
            }

        });
        btBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshActivity();
    }

    public void cancelTicket(int pos, String ticketType) {

        BookingHistory bookingHistory;
        if (ticketType.toLowerCase().equals((Constant.SHOW_ONLY_STR).toLowerCase())) {
            bookingHistory = showOnlyList.get(pos);
        } else {
            bookingHistory = withMealList.get(pos);
        }

        this.ticketType = ticketType;
        this.deletePos = pos;

        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmTicketDetailActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure want to cancel this ticket ?");

        // Set Alert Title
        builder.setTitle("Cancel Ticket !");

        builder.setCancelable(false);
        builder.setPositiveButton(
                "Yes",
                (dialog, which) -> {
                    deleteTicketServiceManager.prepareWebServiceJob();
                    JSONObject loginObj = new JSONObject();
                    try {
                        loginObj.put("order_id", confirmTicket.getOrderId());
                        loginObj.put("user_id", userId);
                        loginObj.put("order_event_id", bookingHistory.getOrderEventId());
                        deleteTicketServiceManager.feedParamsWithoutKey(loginObj.toString());
                        deleteTicketServiceManager.featchData();
                        viewSwitcherlayout.showNext();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        displaySnackbarMsg("Please try again");
                    }
                });
        builder.setNegativeButton(
                "No",
                (dialog, which) -> {
                    dialog.cancel();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(DeleteTicketService.SERVICE_NAME)) {
            viewSwitcherlayout.showPrevious();
            if (deleteTicketServiceManager.getServiceStatus() != null){
                if (deleteTicketServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                    displaySnackbarMsg(deleteTicketServiceManager.getResponseMsg());
                    refreshActivity();
                } else if (deleteTicketServiceManager.getServiceStatus().toLowerCase().equals("false")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert ! ");
                    builder.setCancelable(false);
                    builder.setMessage(deleteTicketServiceManager.getResponseMsg());

                    // add a button
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        refreshActivity();
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        } else if (serviceName.equals(ConfirmTickertService.SERVICE_NAME)) {

            viewSwitcherlayout.showPrevious();
            if (confirmTickertServiceManager.getConfirmTickets().size() > 0) {
                confirmTicket = confirmTickertServiceManager.getConfirmTickets().get(0);
                orderId.setText("Order ID : #" + confirmTicket.getOrderId());
                perfornameTxt.setText(confirmTicket.getBookingHistoryList().get(0).getProductName() + " - "
                        + confirmTicket.getBookingHistoryList().get(0).getPerformerName() + " - " +
                        confirmTicket.getBookingHistoryList().get(0).getSupporterName()
                );

                dateTxt.setText("Date & Time : " + DatesUtils.AppDateFormat(orderDate,"dd/MM/yyyy")  +" - "+orderTime );
                toalPurchaseTxt.setText("Total Purchase Tickets : " + confirmTicket.getBookingHistoryList().size() + " Tickets");
                showOnlyList.clear();
                withMealList.clear();
                boolean isAllTickerCancelled = true;

                for (BookingHistory bookingHistory : confirmTicket.getBookingHistoryList()) {
                    if (bookingHistory.getShowType().toLowerCase().equals((Constant.SHOW_ONLY_STR).toLowerCase())) {
                        showOnlyList.add(bookingHistory);
                    } else {
                        withMealList.add(bookingHistory);
                    }

                    if (!bookingHistory.getProductStatus().equals(STATUS_CANCELLED)) {
                        isAllTickerCancelled = false;
                    }
                }

                if (showOnlyList.isEmpty()) {
                    llShowOnly.setVisibility(View.GONE);
                } else {
                    showOnlyListItemAdpter = new ShowOnlyListItemAdpter(getApplicationContext(), showOnlyList, this);
                    showOnlyRV.setAdapter(showOnlyListItemAdpter);
                    showOnlyListItemAdpter.notifyDataSetChanged();
                }

                if (withMealList.isEmpty()) {
                    llShowWithMeal.setVisibility(View.GONE);
                } else {
                    withMealListItemAdpter = new ShowOnlyListItemAdpter(getApplicationContext(), withMealList, this);
                    withMealRV.setAdapter(withMealListItemAdpter);
                    withMealListItemAdpter.notifyDataSetChanged();
                }

                if (isAllTickerCancelled) {
                    downloadLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private void refreshActivity() {
        if (GlobalConf.checkInternetConnection(this)) {
            confirmTickertServiceManager = new ConfirmTickertServiceManager(this, this);
            confirmTickertServiceManager.generateUrl(UrlCollection.GET_SINGLE_ORDER + tempOrderId);
            confirmTickertServiceManager.prepareWebServiceJob();
            confirmTickertServiceManager.featchData();
            viewSwitcherlayout.showNext();
        } else {
            switchToNoInternetActivity();
        }

    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void displaySnackbarMsg(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void switchToNoInternetActivity() {
        Intent intent = new Intent(ConfirmTicketDetailActivity.this, NoInternetActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
