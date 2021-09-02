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
import com.toolsbox.customer.view.activity.login.LoginActivity;
import com.toolsbox.customer.view.activity.main.profile.PinCodeActivity;
import com.toolsbox.customer.view.customUI.IconEditText;

public class ForgotEmailActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ForgotEmailActivity";

    private Toolbar toolbar;
    private CardView cvSubmit;
    private IconEditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email);
        initUI();
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        setupToolbar();
        cvSubmit = findViewById(R.id.cv_submit);
        cvSubmit.setOnClickListener(this);
        etEmail = findViewById(R.id.et_email);
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

    boolean checkValidation(){
        etEmail.setError(null);

        boolean valid = true;
        String email = etEmail.getText().toString().trim();
        if (StringHelper.isEmpty(email)){
            etEmail.setError(getResources().getString(R.string.empty_field));
            valid = false;
        } else if (!ValidationHelper.isValidEmail(email)) {
            etEmail.setError(getResources().getString(R.string.invalid_email));
            valid = false;
        }

        return valid;
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

    void goToPinCodeActivity(String email){
        Intent intent = new Intent(ForgotEmailActivity.this, PinCodeActivity.class);
        intent.putExtra("from", Constant.FROM_FORGOT_PASSWORD_BY_EMAIL);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    void requestForgotByEmail(String email){
        showProgressDialog();
        ApiUtils.forgotEmailVerify(this, email, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null) {
                    if (data.status == 0) {
                        goToPinCodeActivity(email);
                    } else {
                        Toast.makeText(ForgotEmailActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotEmailActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(ForgotEmailActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_submit:
                if (checkValidation()) {
                    requestForgotByEmail(etEmail.getText().toString());
                }
                break;
        }
    }
}
