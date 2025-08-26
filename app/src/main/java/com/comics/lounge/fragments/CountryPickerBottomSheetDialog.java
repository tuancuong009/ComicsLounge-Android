package com.comics.lounge.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.comics.lounge.R;
import com.comics.lounge.activity.MyBookingActivity;
import com.comics.lounge.activity.NoInternetActivity;
import com.comics.lounge.adapter.StateAdapter;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.State;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.webservice.manager.ServerTimeServiceManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CountryPickerBottomSheetDialog extends BottomSheetDialogFragment implements ServiceCallback {

    private Spinner stateSPN;
    private EditText cityedt;
    private MaterialButton ppay;
    private List<State> stateList;
    private State state;
    private MyBookingActivity myBookingActivity;
    String serverTime, closeEventTime;
    private ServerTimeServiceManager serverTimeServiceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker_bottom_sheet, container, false);
        findViewByid(view);
        serverTimeServiceManager = new ServerTimeServiceManager(this, getActivity());
        if (GlobalConf.checkInternetConnection(getActivity())) {
            serverTimeServiceManager.prepareWebServiceJob();
            serverTimeServiceManager.featchData();
        } else {
            switchToNoInternetActivity();
        }
        return view;
    }

    private void switchToNoInternetActivity() {
        Intent intent = new Intent(getActivity(), NoInternetActivity.class);
        startActivity(intent);
    }

    private void findViewByid(View view) {
        stateSPN = (Spinner) view.findViewById(R.id.country_spn);
        cityedt = (EditText) view.findViewById(R.id.edt_city_name);
        ppay = (MaterialButton) view.findViewById(R.id.btnSubmit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stateList = new ArrayList<State>();
        stateList.add(new State(69, "Australian Capital Territory"));
        stateList.add(new State(70, "New South Wales"));
        stateList.add(new State(71, "Northern Territory"));
        stateList.add(new State(72, "Queensland"));
        stateList.add(new State(73, "South Australia"));
        stateList.add(new State(74, "Tasmania"));
        stateList.add(new State(75, "Victoria"));
        stateList.add(new State(76, "Western Australia"));

        StateAdapter stateAdapter = new StateAdapter(stateList);
        stateSPN.setAdapter(stateAdapter);

        stateSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ppay.setOnClickListener(v -> {
            if (TextUtils.isEmpty(serverTime)) {
                Toast.makeText(getContext(), R.string.updating_moment, Toast.LENGTH_LONG).show();
                return;
            }
            if (!AppUtil.isBlank(cityedt, getString(R.string.ccity_cannot_be_blank))) {
                String date = DatesUtils.DateFormat(closeEventTime);
                String currentDate = date + " 00:00:00pm";
                boolean isValid = DatesUtils.validateEventClose(serverTime, currentDate);
                if (isValid) {
                    myBookingActivity.saveState(state.getId(), cityedt.getText().toString());
                } else {
                    myBookingActivity.displaySnackbarMsg(getResources().getString(R.string.tickets_can_only));
                }

                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        myBookingActivity.updateSubmit();
    }

    public void attachParam(MyBookingActivity myBookingActivity,String closeEventTime) {
        this.myBookingActivity = myBookingActivity;
        this.closeEventTime = closeEventTime;
    }
    
    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        serverTime = serverTimeServiceManager.gettingServerTime();
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }
}
