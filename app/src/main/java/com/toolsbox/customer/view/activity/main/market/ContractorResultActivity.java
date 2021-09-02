package com.toolsbox.customer.view.activity.main.market;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
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
import com.toolsbox.customer.common.model.ContractorInfo;
import com.toolsbox.customer.common.model.api.ContractorSearchData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.home.PostJobActivity;
import com.toolsbox.customer.view.activity.main.messages.ChatActivity;
import com.toolsbox.customer.view.adapter.ContractorAdapter;
import com.toolsbox.customer.view.adapter.listener.PaginationScrollListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import static com.toolsbox.customer.common.Constant.FROM_JOB_HIRE;

public class ContractorResultActivity extends BaseActivity implements View.OnClickListener,  ContractorAdapter.OnItemClickListener {
    private static String TAG = "ContractorResultActivity";
    private ContractorAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RelativeLayout rlNodata, rlLoadingData;
    private TextView tvNoTitle;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int selectedIndustry;
    private double lati, longi;
    private int radius;
    private int CURRENT_PAGE = 1;
    private static final int REQUEST_POST_JOB_ACTIVTY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_result);
        initVariable();
        initUI();
        fetchContractor();
    }

    void initVariable(){
        selectedIndustry = getIntent().getIntExtra("Industry", 1);
        lati = getIntent().getDoubleExtra("Lati", 0);
        longi = getIntent().getDoubleExtra("Longi", 0);
        radius = getIntent().getIntExtra("Radius", 0);
    }


    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        rlNodata = findViewById(R.id.rl_no_data);
        rlLoadingData = findViewById(R.id.rl_loading_data);
        tvNoTitle = findViewById(R.id.tv_no_title);
        tvNoTitle.setText("No " + Constant.gArrSpecialization[selectedIndustry - 1] +  "\n Specialists are available in your area");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_contractors);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ContractorAdapter(this, new ArrayList<>());
        adapter.addListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                Log.e(TAG, "loadMoreItems");
                isLoading = true;
                CURRENT_PAGE ++;
                fetchContractor();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(Constant.gArrSpecialization[selectedIndustry - 1]);
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
    public void onClick(View view) {

    }

    public void onRefresh() {
        CURRENT_PAGE = 1;
        isLastPage = false;
        adapter.clear();
        fetchContractor();
    }

    void fetchContractor(){
        isLoading = true;
        updateBackground();
        ApiUtils.searchContractor(this, CURRENT_PAGE, Constant.PER_PAGE, selectedIndustry, String.valueOf(lati), String.valueOf(longi), radius, new Notify() {
            @Override
            public void onSuccess(Object object) {
                isLoading = false;
                ContractorSearchData data = (ContractorSearchData) object;
                if (data != null){
                    if (data.status == 0){
                        if (CURRENT_PAGE != 1) adapter.removeLoading();
                        List<ContractorInfo> arrayContractor = data.info;
                        int totalContractors = data.total_number;
                        adapter.addAll(arrayContractor);

                        // Check if need more loading
                        int totalPage = (totalContractors + Constant.PER_PAGE -1) / Constant.PER_PAGE;
                        if (CURRENT_PAGE < totalPage)
                            adapter.addLoading();
                        else
                            isLastPage = true;

                    } else {
                        Toast.makeText(ContractorResultActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ContractorResultActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
                updateBackground();
            }

            @Override
            public void onFail() {
                isLoading = false;
                Toast.makeText(ContractorResultActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                updateBackground();
            }
        });
    }

    void updateBackground(){
        if (adapter.getItemCount() == 0) {
            if (isLoading) {
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

    // listener of adapter

    @Override
    public void onTapReview(ContractorInfo item) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("contractor", item);
        startActivity(intent);
    }

    @Override
    public void onTapChat(ContractorInfo item) {
        showProgressDialog();
        String endpointIdentity = GlobalUtils.getEndpointIdentity(item.id);
        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.joinOrCreateChannelWithCompletion(endpointIdentity, new CallbackListener<Channel>() {
            @Override
            public void onSuccess(Channel channel) {
                hideProgressDialog();
                goToChatActivity(channel.getSid());
            }

            public void onError(ErrorInfo errorInfo) {
                hideProgressDialog();
                Toast.makeText(ContractorResultActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToChatActivity(String  channelSid){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("sid", channelSid);
        startActivity(intent);
    }

    @Override
    public void onTapHire(ContractorInfo item) {
        Intent intent = new Intent(ContractorResultActivity.this, PostJobActivity.class);
        intent.putExtra("from", FROM_JOB_HIRE);
        intent.putExtra("contractor", item);
        intent.putExtra("industry", selectedIndustry);
        startActivityForResult(intent, REQUEST_POST_JOB_ACTIVTY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_JOB_ACTIVTY:
                    finish();
                    break;
            }
        }
    }
}
