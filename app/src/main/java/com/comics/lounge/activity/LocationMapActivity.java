package com.comics.lounge.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.utils.ToolbarUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMapActivity extends AbstractBaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout mainLayout;
    private AppCompatTextView toolbarNameTxt;
    //private AppCompatImageView leftArrowIcon;
    //private AppCompatImageView menuRight;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        toolbar = findViewById(R.id.toolbar);
        toolbarNameTxt = findViewById(R.id.toolbar_app_name_txt);
        //leftArrowIcon = findViewById(R.id.toolbar_app_logo);
        //menuRight = findViewById(R.id.menuRight);
        mainLayout = findViewById(R.id.llGoogleMap);

        toolbarNameTxt.setText(getResources().getString(R.string.we_locat_here));
        ToolbarUtils.showBackArrow(toolbar, this);
        /*findViewById(R.id.toolbar_logo_layout).setVisibility(View.GONE);
        findViewById(R.id.llRightMenu).setVisibility(View.GONE);
        findViewById(R.id.viewEmptySpace).setVisibility(View.VISIBLE);*/
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        /*setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        /*leftArrowIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        leftArrowIcon.setScaleX(ResourcesCompat.getFloat(getApplicationContext().getResources(), R.dimen.back_awwor_flot_size));
        leftArrowIcon.setScaleY(ResourcesCompat.getFloat(getApplicationContext().getResources(), R.dimen.back_awwor_flot_size));
        leftArrowIcon.setOnClickListener(v -> finish());*/

        //menuRight.setVisibility(View.INVISIBLE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Constant.LATITUDE, Constant.LONGITUDE);
        mMap.addMarker(new MarkerOptions().position(sydney).title(getString(R.string.app_name)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
