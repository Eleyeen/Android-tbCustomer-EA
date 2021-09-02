package com.toolsbox.customer.view.activity.main.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.toolsbox.customer.R;

public class ImagePreviewActivity extends AppCompatActivity {
    private static String TAG = "ImagePreviewActivity";

    private ImageView ivPhoto;
    private Toolbar toolbar;
    private String strFileUrl;
    private boolean isLocalFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initVariable();
        initUI();
    }

    void initVariable(){
        isLocalFile = getIntent().getBooleanExtra("isLocal", true);
        strFileUrl = getIntent().getStringExtra("file");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        ivPhoto = findViewById(R.id.iv_photo);
        if (isLocalFile) {
            ivPhoto.setImageURI(Uri.parse(strFileUrl));
        } else {
            Picasso.get().load(strFileUrl).into(ivPhoto);
        }

    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
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

}
