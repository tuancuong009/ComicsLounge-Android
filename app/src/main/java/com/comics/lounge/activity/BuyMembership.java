package com.comics.lounge.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.adapter.DesMbShipAdapter;
import com.comics.lounge.databinding.ActivityBuyMembershipBinding;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyMembership extends AppCompatActivity {
    ActivityBuyMembershipBinding binding;
    List<String> list;
    DesMbShipAdapter adapter;
    Membership membership;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyMembershipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
//        binding.cbTerm.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked){
//                binding.cbTerm.setBackgroundResource(R.drawable.cb_selector);
//                binding.tvLink.setTextColor(getColor(R.color.gray_2));
//            }
//        });
//        binding.btBuy.setOnClickListener(v -> {
//            if (binding.cbTerm.isChecked()){
//                Intent intent = new Intent(this, PromoCodeActivity.class);
//                intent.putExtra("membership", membership);
//                startActivity(intent);
//            }else {
//                Utils.INSTANCE.generateClickableLinkError(this, binding.tvLink);
//                binding.cbTerm.setBackgroundResource(R.drawable.cb_error_selector);
//                binding.tvLink.startAnimation(AppUtil.shakeError());
//                binding.tvLink.setTextColor(getColor(R.color.red));
//            }
//        });
    }

    // init UI
    private void init(){
//        Utils.INSTANCE.generateClickableLink(this, binding.tvLink);
//
//        AppUtil.showLoading(this);
//        list = new ArrayList<>();
//        binding.rcvDes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        adapter = new DesMbShipAdapter(list);
//        binding.rcvDes.setAdapter(adapter);
        binding.ivMember.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(this).widthPixels/2.45)));
        getMbShip();
    }

    // get membership description
    private void getMbShip(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getMbShipDes().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray jsArr = jsonObject.optJSONArray("memberships");
                            if (jsArr != null){
                                JSONObject jsMb = (JSONObject) jsArr.get(0);
                                membership = new Membership(Integer.parseInt(jsMb.optString("membership_id")), jsMb.optString("membershipname"), jsMb.optString("description"),
                                        jsMb.optString("price"), jsMb.optString("other_price"), jsMb.optString("image"), jsMb.optString("create_on"),
                                        jsMb.optString("status"), jsMb.optString("permonthallowed"));
                                binding.tvPrice.setText(Html.fromHtml("<font color=#EC027D>$"+jsMb.optString("price")+"</font> for 12 months", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvName.setText(jsMb.optString("membershipname"));
                                Glide.with(BuyMembership.this).load(jsMb.optString("image")).error(R.drawable.img_gold_mb).into(binding.ivMember);
                                JSONArray arrDes = jsMb.optJSONArray("description");
                                if (arrDes != null && arrDes.length() > 0){
//                                    binding.tvDes.setText(Html.fromHtml(arrDes.get(0).toString(), Html.FROM_HTML_MODE_COMPACT));
                                    for (int i = 0;i < arrDes.length();i++){
                                        String des = (String) arrDes.get(i);
                                        list.add(des);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                adapter.notifyDataSetChanged();
                binding.llContent.setVisibility(View.VISIBLE);
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppUtil.hideLoading();
                Toast.makeText(BuyMembership.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}