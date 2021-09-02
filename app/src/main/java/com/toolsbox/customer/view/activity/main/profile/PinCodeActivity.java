package com.toolsbox.customer.view.activity.main.profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.User;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.login.forgot.NewPasswordActivity;

public class PinCodeActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "PinCodeActivity";
    private static final int REQUEST_CODE_PIN_CODE = 1;

    private Toolbar toolbar;
    private Button btnConfirm;
    private TextView tvDetail;
    private PinView pinView;
    private int from;
    private String phoneNumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        initVariable();
        initUI();
    }

    void initVariable(){
        from = getIntent().getIntExtra("from", Constant.FROM_EDIT_PHONE);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        email = getIntent().getStringExtra("email");
    }

    void initUI(){
        tvDetail = findViewById(R.id.tv_detail);
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        pinView = findViewById(R.id.pinView);
        pinView.setItemBackground(getResources().getDrawable(R.drawable.bg_rounded_ping));
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        if (from == Constant.FROM_FORGOT_PASSWORD_BY_EMAIL) {
            tvToolbar.setText(R.string.email_verification);
            tvDetail.setText(R.string.you_should_be_receving_email);
        } else {
            tvToolbar.setText(R.string.phone_verification);
            tvDetail.setText(R.string.you_should_be_receving_sms);
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

    void finishWithResult(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    void checkPinCode(String pinCode){
        if (from == Constant.FROM_VERIFY_PHONE || from == Constant.FROM_EDIT_PHONE) {
            showProgressDialog();
            ApiUtils.customerPhoneVerifyConfirm(this, phoneNumber, pinCode, PreferenceHelper.getToken(), new Notify() {
                @Override
                public void onSuccess(Object object) {
                    hideProgressDialog();
                    LoginData data = (LoginData) object;
                    if (data != null) {
                        if (data.status == 0) {
                            PreferenceHelper.removePreference();
                            PreferenceHelper.savePreference(data.info.id, data.info.sign_type, data.info.email, data.info.phone, "", data.info.name, data.info.image_url, data.info.fcm_token, data.info.token);
                            finishWithResult();
                        } else {
                            Toast.makeText(PinCodeActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PinCodeActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    hideProgressDialog();
                    Toast.makeText(PinCodeActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (from == Constant.FROM_FORGOT_PASSWORD_BY_PHONE) {
            showProgressDialog();
            ApiUtils.forgotPhoneVerifyConfirm(this, email, pinCode, new Notify() {
                @Override
                public void onSuccess(Object object) {
                    hideProgressDialog();
                    LoginData data = (LoginData) object;
                    if (data != null) {
                        if (data.status == 0) {
                            goToNewPasswordActivity(data.info.token);
                        } else {
                            Toast.makeText(PinCodeActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PinCodeActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    hideProgressDialog();
                    Toast.makeText(PinCodeActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (from == Constant.FROM_FORGOT_PASSWORD_BY_EMAIL) {
            showProgressDialog();
            ApiUtils.forgotEmailVerifyConfirm(this, email, pinCode, new Notify() {
                @Override
                public void onSuccess(Object object) {
                    hideProgressDialog();
                    LoginData data = (LoginData) object;
                    if (data != null) {
                        if (data.status == 0) {
                            goToNewPasswordActivity(data.info.token);
                        } else {
                            Toast.makeText(PinCodeActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PinCodeActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    hideProgressDialog();
                    Toast.makeText(PinCodeActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void goToNewPasswordActivity(String token) {
        Intent intent = new Intent(PinCodeActivity.this, NewPasswordActivity.class);
        intent.putExtra("token", token);
        startActivityForResult(intent, REQUEST_CODE_PIN_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                String pinCode = pinView.getText().toString();
                if (!StringHelper.isEmpty(pinCode)) {
                    checkPinCode(pinCode);
                } else {
                    Toast.makeText(this, R.string.please_fill_verification_code, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
