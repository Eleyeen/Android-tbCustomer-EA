package com.toolsbox.customer.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.HistoryInfo;
import com.toolsbox.customer.common.model.api.JobHistoryData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.view.activity.login.EnterActivity;
import com.toolsbox.customer.view.activity.main.home.PostJobActivity;
import com.toolsbox.customer.view.fragment.Jobs.CompleteJobsFragment;
import com.toolsbox.customer.view.fragment.Jobs.PendingJobsFragment;
import com.toolsbox.customer.view.fragment.Jobs.ProcessJobsFragment;
import com.toolsbox.customer.view.fragment.Jobs.ScheduledJobsFragment;

import java.util.ArrayList;
import java.util.List;

import static com.toolsbox.customer.common.Constant.FRAG_HOME;
import static com.toolsbox.customer.common.Constant.FRAG_MARKET;


public class JobsFragment extends Fragment implements View.OnClickListener{
    private static String TAG = "JobsFragment";

    private int CURRENT_PAGE = 1;
    public boolean isLoading = false;
    private boolean isLastPage = false;

    private static List<HistoryInfo> arrHistory = new ArrayList<>();

    public JobsFragment() { }

    public static List<HistoryInfo> getArrHistory(){
        return arrHistory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        refreshJobs();
        initUI(view);
        return view;
    }

    public void refreshJobs(){
        CURRENT_PAGE = 1;
        arrHistory.clear();
        fetchJobs();
    }

    void initUI(View view){
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add(R.string.pending, PendingJobsFragment.class)
                .add(R.string.scheduled, ScheduledJobsFragment.class)
                .add(R.string.in_progress, ProcessJobsFragment.class)
                .add(R.string.complete, CompleteJobsFragment.class)
                .create());
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = view.findViewById(R.id.tab_viewpager);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void fetchJobs(){
        if (!PreferenceHelper.isLoginIn()) {
            MessageUtils.showCustomAlertDialogNoCancel(getActivity(), getResources().getString(R.string.note), getResources().getString(R.string.please_signup_to_view_job), new Notify() {
                @Override
                public void onSuccess(Object object) {
                    Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                    intentBroadcast.putExtra("Section", FRAG_HOME);
                    getActivity().sendBroadcast(intentBroadcast);
                    Intent intent = new Intent(getActivity(), EnterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFail() {

                }
            });
            return;
        }
        isLoading = true;
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.fetchAllJobHistory(getActivity(), CURRENT_PAGE, Constant.PER_PAGE, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                JobHistoryData data = (JobHistoryData) object;
                if (data != null){
                    if (data.status == 0){
                        int totalJobs = data.total_number;
                        arrHistory.addAll(data.info);
                        // Check if need more loading
                        int totalPage = (totalJobs + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (CURRENT_PAGE < totalPage)
                            fetchNextPage();
                        else
                            isLoading = false;
                        // Notify Child Job Fragments
                        Intent intentBroadcast = new Intent(Constant.ACTION_REFRESH_JOBS);
                        getActivity().sendBroadcast(intentBroadcast);
                    } else {
                        isLoading = false;
                        Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading = false;
                    Toast.makeText(getActivity(), R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                isLoading = false;
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fetchNextPage(){
        CURRENT_PAGE ++;
        fetchJobs();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
