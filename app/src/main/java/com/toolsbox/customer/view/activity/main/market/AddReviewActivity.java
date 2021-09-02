package com.toolsbox.customer.view.activity.main.market;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.ContractorInfo;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.GlobalUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.login.LoginActivity;
import com.toolsbox.customer.view.activity.main.jobs.AppliedJobActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class AddReviewActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "AddReviewActivity";
    private Toolbar toolbar;
    private MaterialRatingBar overallRating;
    private EditText etComment;
    private CardView cvSubmit;
    private ContractorInfo currentContractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        initVariable();
        initUI();
    }

    void initVariable(){
        currentContractor = (ContractorInfo) getIntent().getSerializableExtra("contractor");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        cvSubmit = findViewById(R.id.cv_submit);
        etComment = findViewById(R.id.et_comment);
        overallRating = findViewById(R.id.overall_rating);
        cvSubmit.setOnClickListener(this);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.add_review);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }


    boolean requiredFieldNotEmpty(){
        return !(StringHelper.isEmpty(etComment.getText().toString()) || overallRating.getRating() == 0);
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



    void addReview(){
        showProgressDialog();
        int myId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        ApiUtils.addReview(this, currentContractor.id, 0, myId, overallRating.getRating(), 0, 0,
                etComment.getText().toString(), new Notify() {
                    @Override
                    public void onSuccess(Object object) {
                        hideProgressDialog();
                        GeneralData data = (GeneralData)object;
                        if (data != null){
                            if (data.status == 0){
                                MessageUtils.showConfirmAlertDialog(AddReviewActivity.this, getResources().getString(R.string.you_has_provided_feedback_successfully), new Notify() {
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
                            } else {
                                Toast.makeText(AddReviewActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddReviewActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail() {
                        hideProgressDialog();
                        Toast.makeText(AddReviewActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_submit:
                if (requiredFieldNotEmpty()){
                    addReview();
                } else {
                    Toast.makeText(AddReviewActivity.this, R.string.please_fill_all_the_required_info, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

