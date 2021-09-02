package com.toolsbox.customer.view.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.toolsbox.customer.R;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;

import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Alpha;
import su.levenetc.android.textsurface.animations.Delay;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.Slide;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Side;

public class StartupActivity extends BaseActivity {
    private static String TAG = "StartupActivity";

    private TextView tvLabel1, tvLabel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        initUI();
    }


    void initUI(){
        tvLabel1 = findViewById(R.id.tv_label1);
        tvLabel2 = findViewById(R.id.tv_label2);
        tvLabel1.setText(getResources().getString(R.string.jobs));
        tvLabel2.setText(getResources().getString(R.string.connecting_you_with));
        /*
        Animation aniFade1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        Animation aniFade2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in2);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLabel1.setText(getResources().getString(R.string.jobs));
                tvLabel1.startAnimation(aniFade1);
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLabel2.setText(getResources().getString(R.string.connecting_you_with));
                        tvLabel2.startAnimation(aniFade2);
                    }
                });

            }
        }, 1000);

         */

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (PreferenceHelper.isLoginIn()) {
                    Intent intent = new Intent(StartupActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(StartupActivity.this, SliderActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1800);

    }
}
