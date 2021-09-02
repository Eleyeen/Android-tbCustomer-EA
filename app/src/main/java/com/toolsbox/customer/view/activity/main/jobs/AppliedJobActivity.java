package com.toolsbox.customer.view.activity.main.jobs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.ProposalInfo;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.model.api.HistoryInfo;
import com.toolsbox.customer.common.model.api.JobDetailInfo;
import com.toolsbox.customer.common.model.api.JobProposalData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.home.ImagePreviewActivity;
import com.toolsbox.customer.view.activity.main.market.ReviewsActivity;
import com.toolsbox.customer.view.activity.main.messages.ChatActivity;
import com.toolsbox.customer.view.activity.main.profile.CreditCardActivity;
import com.toolsbox.customer.view.activity.main.profile.VerifyPhoneActivity;
import com.toolsbox.customer.view.adapter.JobAppliedAdapter;
import com.toolsbox.customer.view.customUI.ConfirmDialog;
import com.toolsbox.customer.view.customUI.PaymentHoldDialog;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import static com.toolsbox.customer.common.Constant.FROM_EDIT_PHONE;
import static com.toolsbox.customer.common.Constant.FROM_VERIFY_PHONE;

public class AppliedJobActivity extends BaseActivity implements JobAppliedAdapter.OnItemClickListener{
    private static final String TAG = "AppliedJobActivity";
    private static final int REQUEST_CODE_APPLIED_JOB = 1;

    private JobAppliedAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;
    private JobDetailInfo jobDetailInfo;
    private boolean isRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_job);
        initVariable();
        initUI();
        fetchProposal();
    }

    void initVariable(){
        jobDetailInfo = (JobDetailInfo) getIntent().getSerializableExtra("currentJob");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        rlNodata = findViewById(R.id.rl_no_data);
        rlLoadingData = findViewById(R.id.rl_loading_data);
        tvNoTitle = findViewById(R.id.tv_no_title);
        tvNoTitle.setText(R.string.no_quotation);
        recyclerView =  findViewById(R.id.recycler_proposals);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new JobAppliedAdapter(this, new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.proposal);
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

    void fetchProposal(){
        isRefresh = true;
        updateBackground();
        int type = jobDetailInfo.job.status > Constant.PENDING_APPROVAL? 1 : 0;
        int contractor_id = jobDetailInfo.job.status > Constant.PENDING_APPROVAL? jobDetailInfo.job.contractor_id : 0;
        ApiUtils.fetchProposal(this, jobDetailInfo.job.id, type, contractor_id, new Notify() {
            @Override
            public void onSuccess(Object object) {
                isRefresh = false;
                JobProposalData data = (JobProposalData) object;
                GlobalUtils.printObject(">Result>", data);
                if (data != null){
                    if (data.status == 0){
                        List<ProposalInfo> arrProposal = data.info;
                        adapter.addAll(arrProposal);
                    } else {
                        Toast.makeText(AppliedJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AppliedJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
                updateBackground();
            }

            @Override
            public void onFail() {
                isRefresh = false;
                Toast.makeText(AppliedJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                updateBackground();
            }
        });
    }

    void updateBackground(){
        if (adapter.getItemCount() == 0) {
            if (isRefresh) {
                rlNodata.setVisibility(View.GONE);
                rlLoadingData.setVisibility(View.VISIBLE);
            } else {
                rlNodata.setVisibility(View.VISIBLE);
                rlLoadingData.setVisibility(View.GONE);
            }
        } else {
            rlNodata.setVisibility(View.GONE);
            rlLoadingData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTapReview(ProposalInfo item) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("contractor", item.contractor);
        startActivity(intent);
    }

    @Override
    public void onTapChat(ProposalInfo item) {
        showProgressDialog();
        String endpointIdentity = GlobalUtils.getEndpointIdentity(item.contractor.id);
        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.joinOrCreateChannelWithCompletion(endpointIdentity, new CallbackListener<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                hideProgressDialog();
                goToChatActivity(channel.getSid());
            }

            public void onError(ErrorInfo errorInfo) {
                hideProgressDialog();
                Toast.makeText(AppliedJobActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToChatActivity(String  channelSid){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("sid", channelSid);
        startActivity(intent);
    }

    @Override
    public void onTapAccept(ProposalInfo item) {
        MessageUtils.showPaymentHoldAlertDialog(this, new Notify() {
            @Override
            public void onSuccess(Object object) {
                onAcceptProposal(item);
            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void onTapDecline(ProposalInfo item) {

    }

    @Override
    public void onTapFileView(String url) {
        Intent intent = new Intent(AppliedJobActivity.this, ImagePreviewActivity.class);
        intent.putExtra("file", url);
        intent.putExtra("isLocal", false);
        startActivity(intent);
    }

    void goToPhoneVerifyActivity(){
        Intent intent = new Intent(AppliedJobActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("from", FROM_VERIFY_PHONE);
        startActivityForResult(intent, REQUEST_CODE_APPLIED_JOB);
    }

    void goToPaymentActivity(){
        Intent intent = new Intent(AppliedJobActivity.this, CreditCardActivity.class);
        startActivityForResult(intent, REQUEST_CODE_APPLIED_JOB);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_APPLIED_JOB:
                    break;
            }
        }
    }

    void onAcceptProposal(ProposalInfo item) {
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.acceptBidder(this, item.id, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        MessageUtils.showConfirmAlertDialog(AppliedJobActivity.this, getResources().getString(R.string.you_accepted_successfully), new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                Intent intent = new Intent();
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } else if (data.status == 1) {
                        MessageUtils.showCustomAlertDialog(AppliedJobActivity.this, data.message, new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                // need to verify mobile number
                                goToPhoneVerifyActivity();
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } else if (data.status == 2) {
                        MessageUtils.showCustomAlertDialog(AppliedJobActivity.this, data.message, new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                // need to add payment
                                goToPaymentActivity();
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }else {
                        Toast.makeText(AppliedJobActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AppliedJobActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(AppliedJobActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
