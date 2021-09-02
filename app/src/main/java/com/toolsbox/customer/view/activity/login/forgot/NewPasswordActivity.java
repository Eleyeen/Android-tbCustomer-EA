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
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.ValidationHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.login.LoginActivity;
import com.toolsbox.customer.view.activity.main.profile.PinCodeActivity;
import com.toolsbox.customer.view.customUI.IconEditText;

public class NewPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "NewPasswordActivity";

    private Toolbar toolbar;
    private CardView cvSubmit;
    private IconEditText etPassword, etRePassword;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initVariable();
        initUI();
    }

    void initVariable(){
        token = getIntent().getStringExtra("token");
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        setupToolbar();
        cvSubmit = findViewById(R.id.cv_submit);
        cvSubmit.setOnClickListener(this);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_re_password);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.enter_password);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }


    boolean checkValidation(){
        boolean cancel = false;
        View focusView = null;
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        etPassword.setError(null);
        etRePassword.setError(null);


        // Check password
        if (StringHelper.isEmpty(password) || !ValidationHelper.isValidPassword(password)){
            focusView = etPassword;
            etPassword.setError(getString(R.string.invalid_password));
            cancel = true;
        }

        // Check re-password
        if (StringHelper.isEmpty(rePassword) || !ValidationHelper.isValidPassword(rePassword)){
            focusView = etRePassword;
            etRePassword.setError(getString(R.string.invalid_password));
            cancel = true;
        }


        if (cancel){
            focusView.requestFocus();
        }
        return cancel;
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

    void updatePassword(){
        showProgressDialog();
        ApiUtils.updatePassword(this, etPassword.getText().toString(), token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null) {
                    if (data.status == 0) {
                        MessageUtils.showCustomAlertDialogNoCancel(NewPasswordActivity.this, "Updated Password!", "Please login with new password again", new Notify() {
                            @Override
                            public void onSuccess(Object object) {
                                Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    } else {
                        Toast.makeText(NewPasswordActivity.this, data.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewPasswordActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(NewPasswordActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_submit:
                if (!checkValidation()){
                    if (etPassword.getText().toString().equals(etRePassword.getText().toString())) {
                        updatePassword();
                    } else {
                        Toast.makeText(this, R.string.not_match_password, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
