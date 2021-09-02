package com.toolsbox.customer.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.model.ServiceItem;
import com.toolsbox.customer.common.utils.DeviceUtil;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.view.activity.main.home.IndustrySelectActivity;
import com.toolsbox.customer.view.activity.main.market.ContractorResultActivity;
import com.toolsbox.customer.view.adapter.PlaceArrayAdapter;
import com.toolsbox.customer.view.customUI.IconEditText;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MarketFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MarketFragment";
    private static final int REQUEST_CODE_INDUSTRY_SELECT = 1;
    private CardView cvSearch;
    private View fakeStatusBar;
    private IconEditText etIndustry;
    private AutoCompleteTextView etArea;
    private RelativeLayout rlIndustry;
    private IndicatorSeekBar isbRange;
    private int selectedIndustry;
    private LatLng selectedLocation;
    private PlaceArrayAdapter placeArrayAdapter;

    public MarketFragment() { }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable(){
        selectedIndustry = 0;
        selectedLocation = null;
    }

    void initUI(View view){
        cvSearch = view.findViewById(R.id.cv_search);
        cvSearch.setOnClickListener(this);
        etIndustry = view.findViewById(R.id.et_industry);
        etIndustry.setEnabled(false);
        rlIndustry = view.findViewById(R.id.rl_industry);
        rlIndustry.setOnClickListener(this);
        etArea = view.findViewById(R.id.et_area);
        etArea.setThreshold(3);
        etArea.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        etArea.setAdapter(placeArrayAdapter);
        isbRange = view.findViewById(R.id.isb_range);
        isbRange.setIndicatorTextFormat("${PROGRESS}km");
        isbRange.setProgress(200);
    }

    boolean requiredFieldNotEmpty(){
        return !(selectedIndustry == 0 || selectedLocation == null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_search:
                if (requiredFieldNotEmpty()){
                    Intent intent = new Intent(getActivity(), ContractorResultActivity.class);
                    intent.putExtra("Industry", selectedIndustry);
                    intent.putExtra("Lati", selectedLocation.latitude);
                    intent.putExtra("Longi", selectedLocation.longitude);
                    intent.putExtra("Radius", isbRange.getProgress());
                    startActivity(intent);
                } else {
                    if (selectedIndustry == 0){
                        Toast.makeText(getActivity(), R.string.no_industry_has_been_selected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedLocation == null){
                        Toast.makeText(getActivity(), R.string.please_specify_an_address, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            case R.id.rl_industry:
                Intent intent1 = new Intent(getActivity(), IndustrySelectActivity.class);
                intent1.putExtra("isMultiple", false);
                intent1.putExtra("from", Constant.FROM_MARKET);
                startActivityForResult(intent1, REQUEST_CODE_INDUSTRY_SELECT);
                break;
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(getActivity());
            placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    selectedLocation = place.getLatLng();
                    DeviceUtil.closeKeyboard(getActivity());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    selectedLocation = null;
                    if (exception instanceof ApiException) {
                        Toast.makeText(getActivity(), exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                    DeviceUtil.closeKeyboard(getActivity());
                }
            });
        }
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INDUSTRY_SELECT:
                    ServiceItem temp = (ServiceItem) data.getSerializableExtra("service");
                    selectedIndustry = temp.getId();
                    etIndustry.setText(Constant.gArrSpecialization[selectedIndustry - 1]);
                    break;

            }
        }
    }
}
