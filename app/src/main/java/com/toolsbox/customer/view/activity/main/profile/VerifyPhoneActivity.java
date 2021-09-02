package com.toolsbox.customer.view.activity.main.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.market.AddReviewActivity;

public class VerifyPhoneActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "VerifyPhoneActivity";
    private static final int REQUEST_CODE_VERIFY_PHONE = 1;
    private Toolbar toolbar;
    private EditText etPhone;
    private Button btnVerify;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        initVariable();
        initUI();
    }

    void initVariable(){
        from = getIntent().getIntExtra("from", Constant.FROM_EDIT_PHONE);
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        etPhone = findViewById(R.id.et_phone);
        btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(this);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        if (from == Constant.FROM_EDIT_PHONE) {
            tvToolbar.setText(R.string.edit_phone_number);
        } else {
            tvToolbar.setText(R.string.verification_required);
        }

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

    void goToPinCodeActivity(String phone){
        Intent intent = new Intent(VerifyPhoneActivity.this, PinCodeActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("phoneNumber", phone);
        startActivityForResult(intent, REQUEST_CODE_VERIFY_PHONE);
    }

    void requestPhoneVerification(String phoneNumber){
        showProgressDialog();
        ApiUtils.customerPhoneVerify(this, phoneNumber, PreferenceHelper.getToken(), new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        goToPinCodeActivity(phoneNumber);
                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyPhoneActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(VerifyPhoneActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                if (!StringHelper.isEmpty(etPhone.getText().toString())) {
                    requestPhoneVerification("+1" + etPhone.getText().toString());
                } else {
                    Toast.makeText(this,  R.string.please_fill_phone_number, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void finishWithResult(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_VERIFY_PHONE:
                    finishWithResult();
                    break;

            }
        }
    }
}
