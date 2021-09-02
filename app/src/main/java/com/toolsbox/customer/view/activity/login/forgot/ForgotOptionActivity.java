package com.toolsbox.customer.view.activity.login.forgot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.view.activity.basic.BaseActivity;

public class ForgotOptionActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "ForgotOptionActivity";

    private Toolbar toolbar;
    private RadioGroup radioGroup;
    private RadioButton rbMobile, rbEmail;
    private CardView cvSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_option);
        initUI();
    }

    void initUI(){
        StatusBarUtil.setTransparent(this);
        setupToolbar();
        radioGroup = findViewById(R.id.radio_group);
        rbMobile = findViewById(R.id.rb_mobile);
        rbEmail = findViewById(R.id.rb_email);
        cvSubmit = findViewById(R.id.cv_submit);
        cvSubmit.setOnClickListener(this);
        rbMobile.setChecked(true);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.account_verification);
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

    void goToForgotInputActivity(){
        if (rbMobile.isChecked()) {
            Intent intent = new Intent(ForgotOptionActivity.this, ForgotPhoneActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ForgotOptionActivity.this, ForgotEmailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_submit:
                goToForgotInputActivity();
                break;
        }
    }
}
