package com.toolsbox.customer.view.activity.login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.HomeActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import static com.toolsbox.customer.common.Constant.PRE_EMAIL;
import static com.toolsbox.customer.common.Constant.PRE_FCM_TOKEN;
import static com.toolsbox.customer.common.Constant.PRE_PASSWORD;
import static com.toolsbox.customer.common.Constant.PRE_PHONE;
import static com.toolsbox.customer.common.Constant.PRE_TOKEN;
import static com.toolsbox.customer.common.Constant.RC_HANDLE_PERMISSION;

public class SplashActivity extends BaseActivity {
    private static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setTranslucent(this, 0);
        initVariable();
        getFBKey();
    }

    void initVariable(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkAllPermission();
            }
        }, 2000);
    }



    void checkAllPermission(){
        int phone_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int write_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int camera_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (write_permission == PackageManager.PERMISSION_GRANTED
                &&camera_permission == PackageManager.PERMISSION_GRANTED
                && phone_permission == PackageManager.PERMISSION_GRANTED
                && read_permission == PackageManager.PERMISSION_GRANTED) {
            splashProcess();
        } else {
            requestPermission();
        }
    }

    void splashProcess() {
        goToStartupActivity();
    }


    void goToStartupActivity(){
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    void requestPermission(){
        final String[] permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_PERMISSION);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean getPermission = false;
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            splashProcess();
            getPermission = true;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        if (!getPermission) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle(getString(R.string.permission_title_alert))
                    .setMessage(getString(R.string.permission_content_alert))
                    .setPositiveButton(getString(R.string.ok), listener)
                    .show();
        }
    }


    private void getFBKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.toolsbox.customer", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    String hash_key = Base64.encodeToString(md.digest(), Base64.DEFAULT).replaceAll("\n", "");
                    Log.e("hash_key", hash_key);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
