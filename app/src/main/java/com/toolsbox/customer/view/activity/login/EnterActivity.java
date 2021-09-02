package com.toolsbox.customer.view.activity.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.view.activity.basic.BaseActivity;

public class EnterActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = "EnterActivity";

    private CardView cvLogin, cvSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        cvLogin = findViewById(R.id.cv_login);
        cvSignup = findViewById(R.id.cv_signup);
        cvLogin.setOnClickListener(this);
        cvSignup.setOnClickListener(this);
    }

    void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void goToSignupActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_login:
                goToLoginActivity();
                break;

            case R.id.cv_signup:
                goToSignupActivity();
                break;

        }
    }
}
