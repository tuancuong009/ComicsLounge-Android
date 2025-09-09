package com.comics.lounge.activity;

import static com.comics.lounge.conf.Constant.REFRESH_AFTER_PAY;
import static com.comics.lounge.conf.Constant.REQUEST_PAY_WITH_PAYPAL;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.databinding.ActivityNewMainBinding;
import com.comics.lounge.databinding.PopupInviteFrBinding;
import com.comics.lounge.fragments.ClaimMemberShipFragment;
import com.comics.lounge.fragments.FrmBecomeMb;
import com.comics.lounge.fragments.FrmBookingHistory;
import com.comics.lounge.fragments.FrmBuySuccess;
import com.comics.lounge.fragments.FrmCalendar;
import com.comics.lounge.fragments.FrmConcierge;
import com.comics.lounge.fragments.FrmConfirmedTicket;
import com.comics.lounge.fragments.FrmContact;
import com.comics.lounge.fragments.FrmEnterTable;
import com.comics.lounge.fragments.FrmEventDetail;
import com.comics.lounge.fragments.FrmGallery;
import com.comics.lounge.fragments.FrmMenu;
import com.comics.lounge.fragments.FrmMyAccount;
import com.comics.lounge.fragments.FrmOffers;
import com.comics.lounge.fragments.FrmSavedEvent;
import com.comics.lounge.fragments.FrmTerm;
import com.comics.lounge.fragments.MyAccountFragment;
import com.comics.lounge.sessionmanager.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class NewMain extends AbstractBaseActivity {
    public ActivityNewMainBinding binding;
    FragmentManager frmManager;
    FragmentTransaction frmTs;
    Fragment crFrm;
    FrmCalendar frmCalendar;
    public MyBookingActivity myBookingActivity;
    public FrmEventDetail frmEventDetail;
    FrmMyAccount frmMyAccount;
    FrmContact frmContact;
    FrmGallery frmGallery;
    FrmConcierge frmConcierge;
    FrmMenu frmMenu;
    List<Fragment> frmDetailList = new ArrayList<>();
    public boolean isChangePw = false;
    public SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (frmEventDetail != null && frmEventDetail.toProcessPay){
                    frmEventDetail.toProcessPay = false;
                    clickBackBt();
                }else if (frmEventDetail != null && frmEventDetail.bottomSheet.getVisibility() == View.VISIBLE){
                    frmEventDetail.bottomSheet.setVisibility(View.GONE);
                }else if (!frmDetailList.isEmpty()) {
                    clickBackBt();
                } else {
                    finish();
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        binding.topBar.btBack.setOnClickListener(v -> callback.handleOnBackPressed());
        binding.topBar.btMenu.setOnClickListener(v -> startActivityForResult(new Intent(this, Setting.class).putExtra("from", "main"), Constant.SETTING_CODE));
        binding.btCalendar.setOnClickListener(v -> {
            clickMn(binding.ivCalendar, binding.ivGallery, binding.ivFood, binding.ivConcierge, binding.ivContact, binding.ivAcc);
            if (frmCalendar != null){
                showFrm(frmCalendar);
            }else {
                frmCalendar = new FrmCalendar();
                loadFrm(frmCalendar);
            }
        });
        binding.btAcc.setOnClickListener(v -> {
            clickMn(binding.ivAcc, binding.ivGallery, binding.ivFood, binding.ivConcierge, binding.ivContact, binding.ivCalendar);
            if (frmMyAccount != null){
                showFrm(frmMyAccount);
                if (isChangePw){
                    frmMyAccount.loadPw();
                    isChangePw = false;
                }
            }else {
                frmMyAccount = new FrmMyAccount();
                loadFrm(frmMyAccount);
            }
        });
        binding.btContact.setOnClickListener(v -> {
            clickMn(binding.ivContact, binding.ivGallery, binding.ivFood, binding.ivConcierge, binding.ivAcc, binding.ivCalendar);
            if (frmContact != null){
                showFrm(frmContact);
            }else {
                frmContact = new FrmContact();
                loadFrm(frmContact);
            }
        });
        binding.btGallery.setOnClickListener(v -> {
            clickMn(binding.ivGallery, binding.ivContact, binding.ivFood, binding.ivConcierge, binding.ivAcc, binding.ivCalendar);
            if (frmGallery != null){
                showFrm(frmGallery);
            }else {
                frmGallery = new FrmGallery();
                loadFrm(frmGallery);
            }
        });
        binding.btFood.setOnClickListener(v -> {
            clickMn(binding.ivFood, binding.ivContact, binding.ivGallery, binding.ivConcierge, binding.ivAcc, binding.ivCalendar);
            if (frmMenu != null){
                showFrm(frmMenu);
            }else {
                frmMenu = new FrmMenu();
                loadFrm(frmMenu);
            }
        });
        binding.btConcierge.setOnClickListener(v -> {
            clickMn(binding.ivConcierge, binding.ivContact, binding.ivGallery, binding.ivFood, binding.ivAcc, binding.ivCalendar);
            if (frmConcierge != null){
                showFrm(frmConcierge);
            }else {
                frmConcierge = new FrmConcierge();
                loadFrm(frmConcierge);
            }
        });
        binding.btHome.setOnClickListener(v -> finish());
        String screen = getIntent().getStringExtra("screen");
        loadScreen(screen);
    }

    // init UI
    private void init(){
        frmManager = getSupportFragmentManager();
        sessionManager = new SessionManager(this);
    }

    // click back button
    public void clickBackBt(){
        if (crFrm == null && frmDetailList.size() == 1){
            finish();
        }else {
            removeFrm(frmDetailList.get(frmDetailList.size() - 1));
            frmDetailList.remove(frmDetailList.get(frmDetailList.size() - 1));
            if (isChangePw){
                frmMyAccount.loadPw();
                isChangePw = false;
            }
        }
    }

    // popup invite friend
    public void popupInviteFr(){
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        PopupInviteFrBinding inviteFrBinding = PopupInviteFrBinding.inflate(getLayoutInflater());
        dialog.setContentView(inviteFrBinding.getRoot());

        inviteFrBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        inviteFrBinding.btShare.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.comics.lounge");
            startActivity(Intent.createChooser(i, "Share App Link"));
            dialog.dismiss();
        });

        dialog.show();
    }

    // click menu
    public void clickMn(ImageView ivSelect, ImageView ivUnselect1, ImageView ivUnselect2, ImageView ivUnselect3, ImageView ivUnselect4, ImageView ivUnselect5){
        ivSelect.setColorFilter(getColor(R.color.bg_splash));
        ivUnselect1.setColorFilter(getColor(R.color.grey_2));
        ivUnselect2.setColorFilter(getColor(R.color.grey_2));
        ivUnselect3.setColorFilter(getColor(R.color.grey_2));
        ivUnselect4.setColorFilter(getColor(R.color.grey_2));
        ivUnselect5.setColorFilter(getColor(R.color.grey_2));
        clearFrmDetail();
    }

    // load screen
    private void loadScreen(String screen){
        switch (screen) {
            case "calendar":
                binding.btCalendar.performClick();
                break;
            case "account":
                binding.btAcc.performClick();
                break;
            case "term":
                addFrmDetail(new FrmTerm());
                break;
            case "contact":
                binding.btContact.performClick();
                break;
            case "claim_mb":
                addFrmDetail(new ClaimMemberShipFragment());
                break;
            case "saved":
                addFrmDetail(new FrmSavedEvent());
                break;
            case "confirmed":
                addFrmDetail(new FrmConfirmedTicket());
                break;
            case "history":
                addFrmDetail(new FrmBookingHistory());
                break;
            case "offers":
                addFrmDetail(new FrmOffers());
                break;
            case "gallery":
                binding.btGallery.performClick();
                break;
            case "menu":
                binding.btFood.performClick();
                break;
            case "concierge":
                binding.btConcierge.performClick();
                break;
            case "table":
                addFrmDetail(new FrmEnterTable());
                break;
            case "membership":
                addFrmDetail(new MyAccountFragment());
                break;
            case "become_membership":
                addFrmDetail(new FrmBecomeMb());
                break;
        }
    }

    // load fragment
    public void loadFrm(Fragment frag) {
        frmManager.beginTransaction().add(R.id.fl_main, frag).commit();
        crFrm = frag;
    }

    // show fragment
    public void showFrm(Fragment frag) {
        frmTs = frmManager.beginTransaction();
        frmTs.hide(crFrm).show(frag).commit();
        crFrm = frag;
    }

    // replace fragment
    public void replaceFrm(Fragment frag) {
        frmTs = frmManager.beginTransaction();
        frmTs.hide(crFrm).add(R.id.fl_main, frag).commit();
        crFrm = frag;
    }

    // add fragment detail
    public void addFrmDetail(Fragment frm) {
        frmTs = frmManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
        );
        frmTs.add(R.id.fl_detail, frm).commit();
        frmDetailList.add(frm);
    }

    // remove fragment
    public void removeFrm(Fragment frm) {
        frmTs = frmManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
        );
        frmTs.remove(frm).commit();
    }

    // clear fragment detail
    private void clearFrmDetail() {
        if (frmDetailList.size() > 0) {
            for (int i = 0; i < frmDetailList.size(); i++) {
                removeFrm(frmDetailList.get(i));
            }
            frmDetailList.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.SETTING_CODE){
            if (data != null){
                String screen = data.getStringExtra("screen");
                loadScreen(screen);
            }
        }
        if (resultCode == REQUEST_PAY_WITH_PAYPAL) {
            boolean success = data.getBooleanExtra(Constant.EXTRA_SUCCESS, false);
            String message = data.getStringExtra(Constant.EXTRA_MESSAGE);
            if (success) {
                myBookingActivity.addedNotchPaypal(message);
                String successMsg = getString(R.string.paypal_success_value);
                myBookingActivity.callPayPalLog(successMsg);
            } else {
                myBookingActivity.callPayPalLog(message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
        if (resultCode == REFRESH_AFTER_PAY){
            clearFrmDetail();
            FrmBuySuccess frm = new FrmBuySuccess();
            Bundle bundle = new Bundle();
            bundle.putString("name", data.getStringExtra("name"));
            frm.setArguments(bundle);
            addFrmDetail(frm);
        }
    }

}