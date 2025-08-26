package com.comics.lounge.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.adapter.NavMenuListItemAdpter;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.fragments.BookingHistoryFragment;
import com.comics.lounge.fragments.CalenderFragment;
import com.comics.lounge.fragments.ClaimMemberShipFragment;
import com.comics.lounge.fragments.ConfirmTicketFragment;
import com.comics.lounge.fragments.ContactUsFragment;
import com.comics.lounge.fragments.FeaturedVideoFragment;
import com.comics.lounge.fragments.FrmEventDetail;
import com.comics.lounge.fragments.HomeFragment;
import com.comics.lounge.fragments.MembershipFragment;
import com.comics.lounge.fragments.MyAccountFragment;
import com.comics.lounge.fragments.PrivacyPolicyFragment;
import com.comics.lounge.fragments.TermsAndConditionFragment;
import com.comics.lounge.modals.ConfirmTicket;
import com.comics.lounge.modals.Event;
import com.comics.lounge.modals.NavMenuItem;
import com.comics.lounge.modals.Wallet;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppVersionUtils;
import com.comics.lounge.utils.UserUtils;
import com.comics.lounge.webservice.PopUpService;
import com.comics.lounge.webservice.WalletService;
import com.comics.lounge.webservice.manager.PopUpServiceManager;
import com.comics.lounge.webservice.manager.ServerTimeServiceManager;
import com.comics.lounge.webservice.manager.WalletServiceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.comics.lounge.activity.EmailOTPVerifyActivity.EXTRA_SCREEN_FLOW;
import static com.comics.lounge.activity.EmailOTPVerifyActivity.EXTRA_USER_EMAIL;
import static com.comics.lounge.activity.EmailOTPVerifyActivity.EXTRA_USER_ID;
import static com.comics.lounge.activity.MobileVerificationActivity.ScreeFlow.FROM_HOME;
import static com.comics.lounge.conf.Constant.UNVERIFIED;

public class MainActivity extends AbstractBaseActivity
        implements ServiceCallback, NavigationView.OnNavigationItemSelectedListener {

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           finish();
        }
    };

    private View frameLayout;
    private HomeFragment homeFragment = null;
    private RecyclerView navMenuRV = null;
    private List<NavMenuItem> navMenuItemList = null;
    private NavMenuListItemAdpter navMenuListItemAdpter = null;
    private int[] directoryImg = {
            R.drawable.ic_home, R.drawable.ic_ticket, R.drawable.ic_calendar, R.drawable.ic_play_button,
            R.drawable.ic_membership, R.drawable.ic_membership, R.drawable.ic_menu_user, R.drawable.ic_notebook_of_contacts,
            R.drawable.ic_history, R.drawable.ic_accept,R.drawable.ic_privacy_policy, R.drawable.ic_logout};
    private DrawerLayout drawer;
    private SessionManager sessionManager;
    private ContactUsFragment contactUsFragment = null;
    private MyAccountFragment myAccountFragment = null;
    private Toolbar toolbar;
    private BookingHistoryFragment bookingHistoryFragment = null;
    private AppCompatTextView superuserTxt;
    private AppCompatImageView navIcon;
    private FeaturedVideoFragment featuredVideoFragment = null;
    private TermsAndConditionFragment termsAndConditionFragment = null;
    private PrivacyPolicyFragment privacyPolicyFragment = null;
    private MembershipFragment membershipFragment = null;
    private ConfirmTicketFragment confirmTicketFragment = null;
    private WalletServiceManager walletServiceManager = null;
    private Wallet wallet = null;
    private CalenderFragment calenderFragment = null;

    private User currentUser;
//    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private ServerTimeServiceManager serverTimeServiceManager = null;
    private PopUpServiceManager popUpServiceManager = null;
    private int isDialogShow = 0;
    private ClaimMemberShipFragment claimMemberShipFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBroadCast();
//        InternetAvailabilityChecker.init(this);

        retroApi = ComicsLoungeApp.getRetroApi();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navIcon = findViewById(R.id.menuRight);
        superuserTxt = findViewById(R.id.super_user_txt);
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);



        frameLayout = findViewById(R.id.container);
        drawer = findViewById(R.id.drawer_layout);
        navMenuItemList = new ArrayList<NavMenuItem>();

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();
//        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
//        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        navMenuRV = findViewById(R.id.nav_menu_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        navMenuRV.setLayoutManager(linearLayoutManager);


        navMenuListItemAdpter = new NavMenuListItemAdpter(getApplicationContext(), navMenuItemList, itemListClick);
        navMenuRV.setAdapter(navMenuListItemAdpter);
        generateNavMenu();

        Intent intent = getIntent();
        if (intent != null) {
            isDialogShow = intent.getIntExtra("isSplshScreen", 0);
        }

        //getting membership plan
        homeFragment = new HomeFragment();
        switchToCommonFragment(new HomeFragment());

        tvAppVersion.setText(getString(R.string.app_version, AppVersionUtils.INSTANCE.getBuildVersionName(this)));
        if (sessionManager.getCurrentUser() != null) {
            superuserTxt.setText(sessionManager.getCurrentUser().getName());
            Log.e("USerObj", sessionManager.getCurrentUser().toString());
        }

        superuserTxt.setOnClickListener(v -> {
            switchToCommonFragment(new MyAccountFragment());
        });

        navIcon.setOnClickListener(v -> openDrawer());

        //Check for update
        AppVersionUtils.INSTANCE.checkVersion(this);

        showPendingScreens();

        fetchServerDate();
        if (sessionManager.getShowOffer()) {
            fetchPopupdialogAPI();
            sessionManager.setShowOffer(false);
        }
    }


    private void fetchServerDate() {
        if (GlobalConf.checkInternetConnection(this)) {
            serverTimeServiceManager = new ServerTimeServiceManager(this, this);
            serverTimeServiceManager.prepareWebServiceJob();
            serverTimeServiceManager.featchData();
        }
    }

    //TODO: refactor this code, remove duplicate codes
    public void switchToCommonFragment(Fragment fragment) {
        if (GlobalConf.checkInternetConnection(this)) {
            swtitchToFragment(fragment, fragment.getClass().getSimpleName());
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            }
        } else {
            switchToNoInternetFoundActivity();
        }

    }


    private View.OnClickListener itemListClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavMenuItem navMenuItem = (NavMenuItem) v.getTag();
            if (navMenuItem.getId() == 0) {
                homeFragment = new HomeFragment();
                swtitchToFragment(homeFragment, homeFragment.getClass().getSimpleName());

            } else if (navMenuItem.getId() == 1) {
                confirmTicketFragment = new ConfirmTicketFragment();
                swtitchToFragment(confirmTicketFragment, confirmTicketFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 2) {
                calenderFragment = new CalenderFragment();
                swtitchToFragment(calenderFragment, calenderFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 3) {
               /* featuredVideoFragment = new FeaturedVideoFragment();
                swtitchToFragment(featuredVideoFragment, featuredVideoFragment.getClass().getSimpleName());
          */
                Intent intent = new Intent(MainActivity.this, VideoViewActivity.class);
                startActivity(intent);
            } else if (navMenuItem.getId() == 4) {
                membershipFragment = new MembershipFragment();
                swtitchToFragment(membershipFragment, membershipFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 5) {
                claimMemberShipFragment = new ClaimMemberShipFragment();
                swtitchToFragment(claimMemberShipFragment, claimMemberShipFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 6) {
                myAccountFragment = new MyAccountFragment();
                swtitchToFragment(myAccountFragment, myAccountFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 7) {
                contactUsFragment = new ContactUsFragment();
                swtitchToFragment(contactUsFragment, contactUsFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 8) {
                bookingHistoryFragment = new BookingHistoryFragment();
                swtitchToFragment(bookingHistoryFragment, bookingHistoryFragment.getClass().getSimpleName());
            } else if (navMenuItem.getId() == 9) {
                new Handler().postDelayed(() -> {
                    termsAndConditionFragment = new TermsAndConditionFragment();
                    termsAndConditionFragment.isCallingFromMemberActivity(false);
                    swtitchToFragment(termsAndConditionFragment, termsAndConditionFragment.getClass().getSimpleName());
                }, 500);
            }
            //KB added to add privacy policy drawer menu
            else if (navMenuItem.getId() == 10) {
                new Handler().postDelayed(() -> {
                    privacyPolicyFragment = new PrivacyPolicyFragment();
                    privacyPolicyFragment.isCallingFromMemberActivity(false);
                    swtitchToFragment(privacyPolicyFragment, privacyPolicyFragment.getClass().getSimpleName());
                }, 500);
            }else if (navMenuItem.getId() == 11) { // KB chnaged from 10 to 11 to add  privacy policy drawer menu
                openAlertDialog();
            }
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            }
        }
    };

    private void swtitchToFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        isCheckToAlreadyVisibleFragment(fragmentManager, confirmTicketFragment);
        fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).addToBackStack(tag).commit();
    }


    /**
     * @param fragmentManager
     * @param fragment
     */
    public void isCheckToAlreadyVisibleFragment(FragmentManager fragmentManager, Fragment fragment) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        if (fragment != null && fragment.isAdded()) {
            fragmentManager1.beginTransaction().remove(fragment).commit();
        }
    }

    private void generateNavMenu() {
        navMenuItemList.clear();
        navMenuItemList.addAll(getMenuItem());
        navMenuListItemAdpter.notifyDataSetChanged();
    }

    protected List<NavMenuItem> getMenuItem() {
        String[] menuContant = getMenuContent();
        List<NavMenuItem> navMenuItems = new ArrayList<NavMenuItem>();
        int i = 0;
        int[] targetIcons = directoryImg;
        for (String menuStr : menuContant) {
            navMenuItems.add(new NavMenuItem(i, menuStr, targetIcons[i]));
            i++;
        }
        return navMenuItems;
    }

    protected String[] getMenuContent() {
        return getResources().getStringArray(R.array.nav_menu_array);
    }

    public void switchToEventDetailFragment(Event event) {
        if (GlobalConf.checkInternetConnection(this)) {
            Intent intent = new Intent(this, FrmEventDetail.class);
            intent.putExtra("event_id", String.valueOf(event.getId()));
            intent.putExtra("walletObj", wallet);
            startActivity(intent);
        } else {
            switchToNoInternetFoundActivity();
        }
    }

    public void switchToEditProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void switchToConfirmTicketDetailFragment(ConfirmTicket confirmTicket) {
        Intent intent = new Intent(MainActivity.this, ConfirmTicketDetailActivity.class);
        intent.putExtra("orderID", confirmTicket.getOrderId());
        intent.putExtra("orderDate", confirmTicket.getBookingHistoryList().get(0).getEventDate());
        intent.putExtra("orderTime", confirmTicket.getBookingHistoryList().get(0).getShowTime());
        startActivity(intent);
    }

    public void switchToCalanderFragment() {
        if (GlobalConf.checkInternetConnection(this)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (calenderFragment != null && calenderFragment.isAdded()) {
                fragmentManager.beginTransaction().remove(calenderFragment).commit();
            }
            calenderFragment = new CalenderFragment();
            fragmentManager.beginTransaction().add(R.id.container, calenderFragment, "CalenderFragment").addToBackStack("CalenderFragment").commit();
        } else {
            switchToNoInternetFoundActivity();
        }

    }

    public void openAlertDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Are you sure want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionManager.logoutUser();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        /*
         * First close inner fragment thanafter close outer(Navigation) fragment
         */
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (membershipFragment != null && membershipFragment.isAdded()) {
                switchToCommonFragment(new HomeFragment());
            } else if (termsAndConditionFragment != null && termsAndConditionFragment.isAdded()) {
                termsAndConditionFragment.isBackPress();
            }else if (privacyPolicyFragment != null && privacyPolicyFragment.isAdded()) {
                privacyPolicyFragment.isBackPress();
            }
            else if (bookingHistoryFragment != null && bookingHistoryFragment.isAdded()) {
                switchToCommonFragment(new HomeFragment());
            } else if (fragment instanceof HomeFragment) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void displaySnackbarMsg(String message) {
        Snackbar.make(frameLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void hideKeyboardFrom() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(frameLayout.getWindowToken(), 0);
    }

    public void openDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    public void callingWaletAPI() {
        if (GlobalConf.checkInternetConnection(getApplicationContext())) {
            walletServiceManager = new WalletServiceManager(this, this);
            walletServiceManager.generateUrl(UrlCollection.WALLET + sessionManager.getCurrentlyLoggedUserId());
            walletServiceManager.prepareWebServiceJob();
            walletServiceManager.featchData();
        } else {
            switchToNoInternetFoundActivity();
        }
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(WalletService.SERVICE_NAME)) {
            //  Log.e("Event_status :",walletServiceManager.getServiceStatus());
            if (walletServiceManager.getServiceStatus() != null) {
                if (walletServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                    wallet = walletServiceManager.getWalletData();
                    sessionManager.storeFreeTicketCount(wallet.eventCountLeft);
//                    sessionManager.freeEventRestored(wallet.freeEventRestored,
//                            wallet.eventCountAllowed,
//                            wallet.freeEventRestored);
                    sessionManager.freeEventRestored(wallet.freeEventRestored,
                            wallet.eventCountAllowed,
                            String.valueOf(wallet.eventCountLeft));
                }
            }
        } else if (serviceName.equals(PopUpService.SERVICE_NAME)) {
            if (popUpServiceManager.getServiceStatus() != null &&
                    popUpServiceManager.getServiceStatus().equals("true")) {
                if (isDialogShow == 1 && !isFinishing()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    WebView wv = new WebView(this);
                    wv.loadUrl(UrlCollection.CUS_POP_UP_URL);
                    wv.setInitialScale(100);
                    wv.getSettings().setBuiltInZoomControls(true);
                    wv.getSettings().setDisplayZoomControls(false);
                    alert.setView(wv);
                    alert.setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
                    alert.show();
                }
            }
        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean Booking = getIntent().getBooleanExtra(Constant.BOOKING_CONFIRM,false);
        if (sessionManager.isLoggedIn()) {
            callingWaletAPI();
            updateLocalUser();
        }
        if (Booking){
            confirmTicketFragment = new ConfirmTicketFragment();
            swtitchToFragment(confirmTicketFragment, confirmTicketFragment.getClass().getSimpleName());

        }
    }



    RetroApi retroApi;

    private void updateLocalUser() {
        UserUtils.INSTANCE.fetchAndUpdateUser(
                retroApi,
                sessionManager,
                sessionManager.getCurrentlyLoggedUserId(),
                null,
                null
        );
    }

    private void showPendingScreens() {
        currentUser = sessionManager.getCurrentUser();
        Timber.e("User: %s", sessionManager.getCurrentUser().toString());

        if (currentUser.getOtpEmailStatus().equals(UNVERIFIED)) {
            Intent intent = new Intent(getApplicationContext(), EmailOTPVerifyActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, currentUser.getEmail());
            intent.putExtra(EXTRA_USER_ID, currentUser.getUserId());
            intent.putExtra(EXTRA_SCREEN_FLOW, EmailOTPVerifyActivity.ScreenFlow.HOME);
            startActivity(intent);
            finish();
        } else if ((currentUser.getMobile() != null && currentUser.getMobile().equals(UNVERIFIED)) ||
                (currentUser.getOtpVerifiedStatus().equals(UNVERIFIED))) {
            Intent intent = new Intent(getApplicationContext(), MobileVerificationActivity.class);
            intent.putExtra(MobileVerificationActivity.EXTRA_SCREEN_FLOW, FROM_HOME);
            startActivity(intent);
            finish();
        } else if ((currentUser.getMembership() == null || currentUser.getMembership()) &&
                (currentUser.getExpireStatus() == null || currentUser.getExpireStatus())) {
            Intent intent = new Intent(getApplicationContext(), MembershipActivity.class);
            startActivity(intent);
        }
        /*else if ((currentUser.getMembership() == null || !currentUser.getMembership()) &&
                (currentUser.getExpireStatus() == null || currentUser.getExpireStatus())) {
            Intent intent = new Intent(getApplicationContext(), MembershipActivity.class);
            startActivity(intent);
        }*/
    }

    private void setBroadCast() {
        IntentFilter intentFilter = new IntentFilter("close_home");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver);
//        mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    public void switchToNoInternetFoundActivity() {
        Intent intent = new Intent(MainActivity.this, NoInternetActivity.class);
        startActivity(intent);
    }

    public void switchToGoogleMapactivity() {
        Intent intent = new Intent(MainActivity.this, LocationMapActivity.class);
        startActivity(intent);
    }

    public void switchTotemsAndConditionFragment() {
        termsAndConditionFragment = new TermsAndConditionFragment();
        termsAndConditionFragment.isCallingFromMemberActivity(false);
        swtitchToFragment(termsAndConditionFragment, termsAndConditionFragment.getClass().getSimpleName());
    }

//    @Override
//    public void onInternetConnectivityChanged(boolean isConnected) {
//        if (!isConnected) {
//            Toast.makeText(getApplicationContext(), getText(R.string.no_internet_connection_str), Toast.LENGTH_SHORT).show();
//            switchToNoInternetFoundActivity();
//        }
//
//    }

    private void fetchPopupdialogAPI() {
        if (GlobalConf.checkInternetConnection(getApplicationContext())) {
            popUpServiceManager = new PopUpServiceManager(this, this);
            popUpServiceManager.prepareWebServiceJob();
            popUpServiceManager.featchData();
        } else {
            switchToNoInternetFoundActivity();
        }
    }

}
