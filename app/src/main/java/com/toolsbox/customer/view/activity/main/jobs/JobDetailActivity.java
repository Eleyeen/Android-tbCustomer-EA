package com.toolsbox.customer.view.activity.main.jobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.AttachFileInfo;
import com.toolsbox.customer.common.model.AvailabilityDateInfo;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.model.api.JobDetailData;
import com.toolsbox.customer.common.model.api.JobDetailInfo;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.TimeHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.home.CalendarActivity;
import com.toolsbox.customer.view.activity.main.home.ImagePreviewActivity;
import com.toolsbox.customer.view.activity.main.home.PostJobActivity;
import com.toolsbox.customer.view.activity.main.home.SeeAvailabilityActivity;
import com.toolsbox.customer.view.activity.main.messages.ChatActivity;
import com.toolsbox.customer.view.adapter.JobAttachAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.toolsbox.customer.common.Constant.FROM_JOB_EDIT;

public class JobDetailActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "JobDetailActivity";

    private static final int REQUEST_CODE_AVAILABILITY_SELECT = 1;
    private Toolbar toolbar;
    private Button btnViewProposal, btnRemoveJob, btnChat;
    private TextView tvJobTitle, tvStatus, tvTotalHour, tvHourlyRate, tvSpecialistName, tvIndustry, tvPostDate, tvStartDate, tvDuration, tvValue,
    tvLocation, tvDetails, tvStartDateTitle;
    private Button btnAvailability;
    private LinearLayout llBackground, llTotalHour, llHourlyRate, llSpecialistName, llPostDate,  llStartDate, llDuration, llValue, llChat;
    private RecyclerView recyclerView;
    private JobAttachAdapter adapter;
    private JobDetailInfo jobDetailInfo;
    private static final int REQUEST_POST_JOB_ACTIVTY = 1;
    private static final int REQUEST_POST_JOB_Applied = 2;
    private int jobId;
    private ArrayList<AvailabilityDateInfo> arrSelectedDates = new ArrayList<>();
    private ArrayList<AttachFileInfo> dataAttach = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        initVariable();
        initUI();
    }

    void initVariable(){
        jobId =  getIntent().getIntExtra("jobId", 0);
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvStatus = findViewById(R.id.tv_status);
        tvTotalHour = findViewById(R.id.tv_total_hour);
        tvHourlyRate = findViewById(R.id.tv_hourly_rate);
        tvSpecialistName = findViewById(R.id.tv_specialist);
        tvIndustry = findViewById(R.id.tv_industry);
        tvPostDate = findViewById(R.id.tv_post_date);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvDuration = findViewById(R.id.tv_duration);
        tvValue = findViewById(R.id.tv_value);
        tvLocation = findViewById(R.id.tv_location);
        tvDetails = findViewById(R.id.tv_description);
        tvStartDateTitle = findViewById(R.id.tv_start_date_title);
        btnAvailability = findViewById(R.id.btn_availability);
        llTotalHour = findViewById(R.id.ll_total_hour);
        llHourlyRate = findViewById(R.id.ll_hourly_rate);
        llSpecialistName = findViewById(R.id.ll_specialist);
        llPostDate = findViewById(R.id.ll_post_date);
        llStartDate = findViewById(R.id.ll_start_date);
        llDuration = findViewById(R.id.ll_duration);
        llValue = findViewById(R.id.ll_value);
        llChat = findViewById(R.id.ll_chat);
        llBackground = findViewById(R.id.ll_background);
        btnViewProposal = findViewById(R.id.btn_view_proposal);
        btnRemoveJob = findViewById(R.id.btn_remove_job);
        btnChat = findViewById(R.id.btn_chat);
        btnViewProposal.setOnClickListener(this);
        btnRemoveJob.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnAvailability.setOnClickListener(this);
        llBackground.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobAttachAdapter(this, dataAttach);
        adapter.addListener(new JobAttachAdapter.OnItemClickListener() {

            @Override
            public void onItemOpen(AttachFileInfo item) {
                Intent intent = new Intent(JobDetailActivity.this, ImagePreviewActivity.class);
                intent.putExtra("file", item.url);
                intent.putExtra("isLocal", false);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        fetchJobDetails();
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.job_details);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_availability:
                Intent intent1 = new Intent(this, SeeAvailabilityActivity.class);
                if (jobDetailInfo.job.status >= Constant.SCHEDULED) {
                    intent1.putExtra("isEditable", false);
                    ArrayList<AvailabilityDateInfo> arrSelectedDates = GlobalUtils.convertAvailabilityDateModel(jobDetailInfo.job.availability_dates);
                    Gson gson = new Gson();
                    String strArrDate = gson.toJson(arrSelectedDates);
                    intent1.putExtra("arrDates", strArrDate);
                } else {
                    intent1.putExtra("isEditable", true);
                    Gson gson = new Gson();
                    String strArrDate = gson.toJson(arrSelectedDates);
                    intent1.putExtra("arrDates", strArrDate);
                }
                startActivityForResult(intent1, REQUEST_CODE_AVAILABILITY_SELECT);
                break;
            case R.id.btn_view_proposal:
                Intent intent = new Intent(JobDetailActivity.this, AppliedJobActivity.class);
                intent.putExtra("currentJob", jobDetailInfo);
                startActivity(intent);
                break;
            case R.id.btn_remove_job:
                MessageUtils.showCustomAlertDialog(JobDetailActivity.this, getResources().getString(R.string.remove_note), new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        removeJob();
                    }

                    @Override
                    public void onFail() {

                    }
                });
                break;
            case R.id.btn_chat:
//                showProgressDialog();
//                String endpointIdentity = GlobalUtils.getEndpointIdentity(currentHistoryInfo.contractor_info.id);
//                ChannelManager channelManager = ChannelManager.getInstance();
//                channelManager.joinOrCreateChannelWithCompletion(endpointIdentity, new CallbackListener<Channel>() {
//                    @Override
//                    public void onSuccess(Channel channel) {
//                        hideProgressDialog();
//                        goToChatActivity(channel.getSid());
//                    }
//
//                    public void onError(ErrorInfo errorInfo) {
//                        hideProgressDialog();
//                        Toast.makeText(JobDetailActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                break;
        }
    }

    void updateView(){
        llBackground.setVisibility(View.VISIBLE);
        // Button Layout
        if (jobDetailInfo.job.status < Constant.PENDING_APPROVAL) {
            hideChatButton();
            btnViewProposal.setText(R.string.view_proposal);
        } else {
            btnChat.setText(R.string.chat);
            btnViewProposal.setText(R.string.view_proposal);
        }

        if (jobDetailInfo.job.status < Constant.IN_PROGRESS) {
            btnRemoveJob.setText(R.string.cancel_job);
        } else {
            btnRemoveJob.setText(R.string.chat);
            hideChatButton();
        }

        tvJobTitle.setText(jobDetailInfo.job.name);
        tvDetails.setText(jobDetailInfo.job.description);
        tvIndustry.setText(Constant.gArrSpecialization[jobDetailInfo.job.industry - 1]);
        if (jobDetailInfo.job.status == Constant.IN_BIDDING_PROCESS || jobDetailInfo.job.status == Constant.PENDING_APPROVAL) {
            if (!StringHelper.isEmpty(jobDetailInfo.job.quoted_contractors) && Arrays.asList(jobDetailInfo.job.quoted_contractors.split(",")).size() > 0) {
                tvStatus.setText(R.string.quotes_available);
            } else {
                tvStatus.setText(Constant.gArrJobStatus[jobDetailInfo.job.status]);
            }
        } else {
            tvStatus.setText(Constant.gArrJobStatus[jobDetailInfo.job.status]);
        }
        tvPostDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_posted_date));
        tvLocation.setText(jobDetailInfo.job.area);
        if (!StringHelper.isEmpty(jobDetailInfo.job.job_started_date)) {
            tvStartDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_started_date));
        }
        tvValue.setText("$" + jobDetailInfo.job.accepted_budget);
        tvSpecialistName.setText(jobDetailInfo.job.contractor_name);
        llDuration.setVisibility(View.GONE);
        if (jobDetailInfo.job.status < Constant.SCHEDULED) {
            llSpecialistName.setVisibility(View.GONE);
            llValue.setVisibility(View.GONE);
            llTotalHour.setVisibility(View.GONE);
            llHourlyRate.setVisibility(View.GONE);
            llStartDate.setVisibility(View.GONE);
        } else {
            if (jobDetailInfo.job.status == Constant.SCHEDULED || jobDetailInfo.job.status == Constant.EN_ROUTE) {
                llStartDate.setVisibility(View.GONE);
            }
            llPostDate.setVisibility(View.GONE);
            if (jobDetailInfo.job.type == 1) {
                tvHourlyRate.setText("$" + jobDetailInfo.bid.hourly_rate);
                if (jobDetailInfo.job.status == Constant.FINISH) {
                    tvTotalHour.setText(String.format("%.2f", jobDetailInfo.record_hours.hours));
                } else {
                    tvTotalHour.setText("Pending Completion");
                    llValue.setVisibility(View.GONE);
                }
            } else {
                llTotalHour.setVisibility(View.GONE);
                llHourlyRate.setVisibility(View.GONE);
            }
        }

        if (jobDetailInfo.job.status == Constant.SCHEDULED) {
            tvStartDateTitle.setText("Scheduled Date");
            tvStartDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_scheduled_date));
        } else if (jobDetailInfo.job.status == Constant.FINISH) {
            tvStartDateTitle.setText("Completion Date");
            tvStartDate.setText(TimeHelper.convertFrindlyTime(jobDetailInfo.job.job_completed_date));
        }
    }

    void hideChatButton(){
        llChat.setVisibility(View.GONE);
    }

    void goToChatActivity(String  channelSid){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("sid", channelSid);
        startActivity(intent);
    }

    void fetchJobDetails(){
        showProgressDialog();
        ApiUtils.fetchJobDetails(this, jobId, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                JobDetailData data = (JobDetailData)object;
                if (data != null) {
                    if (data.status == 0){
                        jobDetailInfo = data.info;
                        arrSelectedDates = GlobalUtils.convertAvailabilityDateModel(jobDetailInfo.job.availability_dates);
                        if (!StringHelper.isEmpty(jobDetailInfo.job.attachment)) {
                            for (String item : Arrays.asList(jobDetailInfo.job.attachment.split(","))) {
                                dataAttach.add(new AttachFileInfo(GlobalUtils.fetchDownloadFileName(item), item, "", false));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                        updateView();
                    } else {
                        Toast.makeText(JobDetailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void removeJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.removeJob(this, jobDetailInfo.job.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        finish();
                    } else {
                        Toast.makeText(JobDetailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void completeJob(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.completeJob(this, 0, jobDetailInfo.job.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        finish();
                    } else {
                        Toast.makeText(JobDetailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(JobDetailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVTY:
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;

                case REQUEST_POST_JOB_Applied:
                    Intent intent1 = new Intent();
                    setResult(Activity.RESULT_OK, intent1);
                    finish();
                    break;

            }
        }
    }

}
