package com.toolsbox.customer.view.activity.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.utils.BitmapHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import id.zelory.compressor.Compressor;

public class ImageCropActivity extends BaseActivity implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener{
    private static String TAG = "ImageCropActivity";
    private Toolbar toolbar;
    private CropImageView civPhoto;
    private Uri imageUri;
    private Bitmap cropedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        initVariable();
        initUI();
    }

    void initVariable(){
        imageUri = getIntent().getParcelableExtra("uri");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        civPhoto = findViewById(R.id.crop_image_view);
        civPhoto.setImageUriAsync(imageUri);
        civPhoto.setOnCropImageCompleteListener(this);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.crop_image);
        setSupportActionBar(toolbar);
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                civPhoto.getCroppedImageAsync();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void goToFirstScreen(Uri savedUri){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        intent.putExtra("cropUri", savedUri);
        finish();
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            cropedImage = result.getBitmap();
            Log.e(TAG, "Crop Bitmap Size width=" + cropedImage.getWidth() + "  Height=" + cropedImage.getHeight());
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + "temp.jpg";
            File cropFile =  BitmapHelper.savePictureAsFile(this, cropedImage, filePath);

            File compressedImage;
            try {
                compressedImage = new Compressor(this)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(cropFile);
                Uri savedUri = Uri.fromFile(compressedImage);
                Log.e(TAG, "Saved Uri" + savedUri.toString());
                Bitmap test = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
                Log.e(TAG, "final width:" + test.getWidth());
                Log.e(TAG, "final height:" + test.getHeight());
                goToFirstScreen(savedUri);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error =>" + e.getLocalizedMessage());
            }

//            Uri savedUri = BitmapHelper.savePicture(this, cropedImage, Environment.getExternalStorageDirectory().getPath() + "/" + "temp.jpg");
//            Log.e(TAG, "Saved Uri" + savedUri.toString());
//            goToFirstScreen(savedUri);
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    this,
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
