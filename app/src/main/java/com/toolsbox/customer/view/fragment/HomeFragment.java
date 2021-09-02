package com.toolsbox.customer.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.view.activity.main.home.IndustrySelectActivity;

import static android.app.Activity.RESULT_OK;
import static com.toolsbox.customer.common.Constant.FRAG_JOBS;
import static com.toolsbox.customer.common.Constant.FRAG_MARKET;


public class HomeFragment extends Fragment implements View.OnClickListener{
    private static String TAG = "HomeFragment";

    private CardView cvPostJob, cvExploreMarket;
    private static final int REQUEST_POST_JOB_ACTIVITY = 1;
    public HomeFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initVariable();
        initUI(view);
        return view;
    }

    void initVariable(){
    }

    void initUI(View view){
        cvPostJob = view.findViewById(R.id.cv_post_job);
        cvExploreMarket = view.findViewById(R.id.cv_explore_market);
        cvPostJob.setOnClickListener(this);
        cvExploreMarket.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_post_job:
                Intent intent = new Intent(getActivity(), IndustrySelectActivity.class);
                startActivityForResult(intent, REQUEST_POST_JOB_ACTIVITY);
                break;

            case R.id.cv_explore_market:
                Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                intentBroadcast.putExtra("Section", FRAG_MARKET);
                getActivity().sendBroadcast(intentBroadcast);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVITY:
                    Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                    intentBroadcast.putExtra("Section", FRAG_JOBS);
                    getActivity().sendBroadcast(intentBroadcast);
                    break;

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
