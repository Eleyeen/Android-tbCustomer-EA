package com.toolsbox.customer.view.activity.login.forgot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.ValidationHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.profile.PinCodeActivity;
import com.toolsbox.customer.view.customUI.IconEditText;

public class ForgotPhoneActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ForgotPhoneActivity";

    private Toolbar toolbar;
    private CardView cvSubmit;
    private IconEditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_phone);
        initUI();
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        setupToolbar();
        cvSubmit = findViewById(R.id.cv_submit);
        cvSubmit.setOnClickListener(this);
        etPhone = findViewById(R.id.et_phone);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.forgot_password);
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

    boolean checkValidation(){
        etPhone.setError(null);

        boolean valid = true;
        String phone = etPhone.getText().toString().trim();
        if (StringHelper.isEmpty(phone)){
            etPhone.setError(getResources().getString(R.string.empty_field));
            valid = false;
        } else if (!ValidationHelper.isValidPhoneNumber(phone)) {
            etPhone.setError(getResources().getString(R.string.invalid_phone));
            valid = false;
        }

        return valid;
    }

    void goToPinCodeActivity(String phone){
        Intent intent = new Intent(ForgotPhoneActivity.this, PinCodeActivity.class);
        intent.putExtra("from", Constant.FROM_FORGOT_PASSWORD_BY_PHONE);
        intent.putExtra("phoneNumber", phone);
        startActivity(intent);
    }

    void requestForgotByPhone(String phone){
        showProgressDialog();
        ApiUtils.forgotPhoneVerify(this, phone, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null) {
                    if (data.status == 0) {
                        goToPinCodeActivity(phone);
                    } else {
                        Toast.makeText(ForgotPhoneActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPhoneActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(ForgotPhoneActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_submit:
                if (checkValidation()) {
                    requestForgotByPhone("+1" + etPhone.getText().toString());
                }
                break;
        }
    }
}
