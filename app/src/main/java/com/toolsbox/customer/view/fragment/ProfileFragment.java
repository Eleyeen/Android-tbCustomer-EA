package com.toolsbox.customer.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;
import com.toolsbox.customer.R;
import com.toolsbox.customer.TBApplication;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.BitmapHelper;
import com.toolsbox.customer.common.utils.FileUtils;
import com.toolsbox.customer.common.utils.MessageUtils;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.ValidationHelper;
import com.toolsbox.customer.controller.chat.channels.ChannelManager;
import com.toolsbox.customer.view.activity.basic.BaseFragment;
import com.toolsbox.customer.view.activity.login.EnterActivity;
import com.toolsbox.customer.view.activity.login.LoginActivity;
import com.toolsbox.customer.view.activity.main.profile.CreditCardActivity;
import com.toolsbox.customer.view.activity.main.profile.EditProfileActivity;
import com.toolsbox.customer.view.activity.main.profile.ImageCropActivity;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.toolsbox.customer.common.Constant.FRAG_HOME;
import static com.toolsbox.customer.common.utils.BitmapHelper.fixRotation;


public class ProfileFragment extends BaseFragment implements View.OnClickListener{
    private static String TAG = "ProfileFragment";
    private Button btnLogout;
    private CircleImageView ivPhoto;
    private LinearLayout llPersonalInfo, llAddCreditCard;
    private RelativeLayout rlAboutJobs, rlPrivacyPolicy, rlTerms, rlCallCustomer, rlEmailCustomer, rlSharing;

    public ProfileFragment() { }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        initVariable();
        return view;
    }

    void initUI(View view){
        btnLogout = view.findViewById(R.id.btn_logout);
        ivPhoto = view.findViewById(R.id.iv_profile);
        llPersonalInfo = view.findViewById(R.id.ll_personal_info);
        llAddCreditCard = view.findViewById(R.id.ll_add_credit_card);
        rlAboutJobs = view.findViewById(R.id.rl_about_jobs);
        rlPrivacyPolicy = view.findViewById(R.id.rl_privacy_policy);
        rlTerms = view.findViewById(R.id.rl_terms);
        rlSharing = view.findViewById(R.id.rl_sharing);
        rlCallCustomer = view.findViewById(R.id.rl_call_customer_support);
        rlEmailCustomer = view.findViewById(R.id.rl_email_customer_support);
        btnLogout.setOnClickListener(this);
        llPersonalInfo.setOnClickListener(this);
        llAddCreditCard.setOnClickListener(this);
        rlAboutJobs.setOnClickListener(this);
        rlPrivacyPolicy.setOnClickListener(this);
        rlTerms.setOnClickListener(this);
        rlCallCustomer.setOnClickListener(this);
        rlEmailCustomer.setOnClickListener(this);
        rlSharing.setOnClickListener(this);
    }

    void initVariable(){
        if (!PreferenceHelper.isLoginIn()) {
            MessageUtils.showCustomAlertDialogNoCancel(getActivity(), getResources().getString(R.string.note), getResources().getString(R.string.please_signup_to_view_profile), new Notify() {
                @Override
                public void onSuccess(Object object) {
                    Intent intentBroadcast = new Intent(Constant.ACTION_CHANGED_SECTION);
                    intentBroadcast.putExtra("Section", FRAG_HOME);
                    getActivity().sendBroadcast(intentBroadcast);
                    Intent intent = new Intent(getActivity(), EnterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFail() {

                }
            });
            return;
        }
        String imageURL = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        if (!StringHelper.isEmpty(imageURL))
            Picasso.get().load(imageURL).into(ivPhoto);
    }


    void showLogoutDialog(){
        MessageUtils.showCustomAlertDialog(getActivity(), getResources().getString(R.string.logout), getResources().getString(R.string.are_you_sure_you_want_to_logout), new Notify() {
            @Override
            public void onSuccess(Object object) {
                logoutFromServer();
            }

            @Override
            public void onFail() {

            }
        });
    }

    void logoutFromServer(){
        showProgressDialog();
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.logout(getActivity(), token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData) object;
                if (data != null){
                    if (data.status == 0){
                        Log.e(TAG, "Logout Success!");
                        TBApplication.get().getChatClientManager().unsetFCMToken();
                        goToLoginActivity();
                    } else {
                        Toast.makeText(getActivity(), R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToLoginActivity(){
        PreferenceHelper.removePreference();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent);
    }

    void shareURL(){
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareBody = "Connecting you with your service specialist";
        String shareSub = "http://thejobsapp.com/";
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                showLogoutDialog();
                break;
            case R.id.ll_personal_info:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_add_credit_card:
                Intent intent1 = new Intent(getActivity(), CreditCardActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_sharing:
                shareURL();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
