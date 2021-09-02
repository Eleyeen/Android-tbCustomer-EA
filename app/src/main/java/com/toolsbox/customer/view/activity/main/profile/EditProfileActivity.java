package com.toolsbox.customer.view.activity.main.profile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.FileUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.ValidationHelper;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseActivity;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.toolsbox.customer.common.Constant.FROM_EDIT_PHONE;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "EditProfileActivity";
    private static final int REQUEST_CODE_EDIT_PROFILE = 1;
    private static final int REQUEST_CODE_SELECT_PICTURE = 2;
    private static final int REQUEST_CODE_CROP_ACTIVITY = 3;

    private Toolbar toolbar;
    private CircleImageView ivProfile;
    private RelativeLayout rlEditProfile;
    private EditText etName, etEmail, etPhone;
    private Button btnEditPhone, btnSave;
    private File rawImageFile;
    private Uri profileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initVariable();
        initUI();
    }

    void initVariable(){
        // For Crop Image
        File dir = FileUtils.getDiskCacheDir(this, "temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        rawImageFile = new File(dir, "Camera.png");
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        setupToolbar();
        ivProfile = findViewById(R.id.iv_profile);
        rlEditProfile = findViewById(R.id.rl_edit_profile);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnEditPhone = findViewById(R.id.btn_edit_phone);
        btnSave = findViewById(R.id.btn_save);
        etPhone.setEnabled(false);
        rlEditProfile.setOnClickListener(this);
        btnEditPhone.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        updateUI();
    }

    void updateUI(){
        // init value
        etName.setText(PreferenceHelper.getName());
        etEmail.setText(PreferenceHelper.getEmail());
        etPhone.setText(PreferenceHelper.getPhone());

        if (!StringHelper.isEmpty(PreferenceHelper.getImageURL()))
            Picasso.get().load(PreferenceHelper.getImageURL()).into(ivProfile);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.edit_profile);
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

    void goToVerifyPhoneActivity(){
        Intent intent = new Intent(EditProfileActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("from", FROM_EDIT_PHONE);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
    }

    void chooseAction() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(rawImageFile));

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.profile_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {captureImageIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_PICTURE);
    }


    void doUpdateProfile(){
        File profileFile = null;
        if (profileUri != null)
            profileFile = new File(profileUri.getPath());
        showProgressDialog();
        ApiUtils.updateProfile(this, etName.getText().toString(), etEmail.getText().toString(), PreferenceHelper.getPhone(), profileFile,  PreferenceHelper.getToken(),  new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                LoginData data = (LoginData) object;
                if (data != null){
                    if (data.status == 0){
                        PreferenceHelper.removePreference();
                        PreferenceHelper.savePreference(data.info.id, data.info.sign_type, data.info.email, data.info.phone, "", data.info.name, data.info.image_url, data.info.fcm_token, data.info.token);

                        // update Twilio Profile info
                        ChannelManager.getInstance().updateTwilioUserInfo();

                        Toast.makeText(EditProfileActivity.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(EditProfileActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    boolean checkValidation(){
        etEmail.setError(null);
        etName.setError(null);

        boolean valid = true;
        String email = etEmail.getText().toString().trim();
        if (StringHelper.isEmpty(email)){
            etEmail.setError(getResources().getString(R.string.empty_field));
            valid = false;
        } else if (!ValidationHelper.isValidEmail(email)) {
            etEmail.setError(getResources().getString(R.string.invalid_email));
            valid = false;
        }
        String name = etName.getText().toString();
        if (StringHelper.isEmpty(name)){
            etName.setError(getResources().getString(R.string.empty_field));
            valid = false;
        }


        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_edit_profile:
                chooseAction();
                break;

            case R.id.btn_edit_phone:
                goToVerifyPhoneActivity();
                break;

            case R.id.btn_save:
                if (checkValidation()) {
                    doUpdateProfile();
                }
                break;
        }
    }

    Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  Uri.fromFile(rawImageFile) : data.getData();
    }

    void goToCropImageActivity(Uri imageUri){
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("uri", imageUri);
        startActivityForResult(intent, REQUEST_CODE_CROP_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_PROFILE:
                    updateUI();
                    break;

                case REQUEST_CODE_SELECT_PICTURE:
                    Uri imageUri =  getPickImageResultUri(data);
                    goToCropImageActivity(imageUri);
                    break;
                case REQUEST_CODE_CROP_ACTIVITY:
                    if (data != null){
                        profileUri = data.getParcelableExtra("cropUri");
                        ivProfile.setImageDrawable(null);
                        ivProfile.setImageURI(profileUri);
                    }
                    break;
            }
        }
    }
}
