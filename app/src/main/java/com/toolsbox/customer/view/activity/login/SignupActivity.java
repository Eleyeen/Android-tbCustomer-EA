package com.toolsbox.customer.view.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.jaeger.library.StatusBarUtil;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.LoginData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.common.utils.PreferenceHelper;
import com.toolsbox.customer.common.utils.StringHelper;
import com.toolsbox.customer.common.utils.ValidationHelper;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.HomeActivity;
import com.toolsbox.customer.view.customUI.IconEditText;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class SignupActivity extends BaseActivity implements View.OnClickListener{
    private static String TAG = "SignupActivity";
    private ImageButton btnBack;
    private IconEditText etName, etEmail, etPassword;
    private CardView cvRegister;
    private AppCompatCheckBox cbTerms;
    private TextView tvTerms;
    private ImageButton btnFacebook, btnTwitter, btnGoogle;
    private CallbackManager callbackManager;
    // FB login
    private LoginButton btnDefaultFacebook;
    // Google login
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    //twitter login
    private TwitterAuthClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initVariable();
        initUI();
    }

    void initVariable(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                AppPreferenceManager.setString(Constant.PRE_FCM_TOKEN, token);
                Log.e(TAG, "FCM Token = " + token);
            }
        });

        // Social login
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Logout success!");
                    }
                });

        //initialize twitter auth client
        logoutTwitter();
        twitterClient = new TwitterAuthClient();
    }

    void initUI(){
        StatusBarUtil.setTranslucent(this, 0);
        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        cbTerms = findViewById(R.id.cb_terms);
        tvTerms = findViewById(R.id.tv_terms);
        cvRegister = findViewById(R.id.cv_register);
        btnBack.setOnClickListener(this);
        cvRegister.setOnClickListener(this);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnGoogle = findViewById(R.id.btn_google);
        btnTwitter = findViewById(R.id.btn_twitter);
        btnFacebook.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        btnDefaultFacebook = findViewById(R.id.login_button);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnDefaultFacebook = findViewById(R.id.login_button);

        // Callback registration
        btnDefaultFacebook.setReadPermissions("public_profile", "email");
        btnDefaultFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showProgressDialog();
                getFacebookUserProfile(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(SignupActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        initTermsText();
    }

    void initTermsText(){
        // Terms text initialize
        // Initialize a new ClickableSpan to display red background
        String text = "I agree to the Terms of Service and Privacy Policy";
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(getResources().getString(R.string.i_agree_to_the_terms));

        ClickableSpan TermsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Do something
                Toast.makeText(SignupActivity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };

        ClickableSpan PrivacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Do something
                Toast.makeText(SignupActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };


        // Apply the clickable text to the span
        ssBuilder.setSpan(
                TermsClickableSpan, // Span to add
                text.indexOf("Terms of Service"), // Start of the span (inclusive)
                text.indexOf("Terms of Service") + String.valueOf("Terms of Service").length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        // Apply the clickable text to the span
        ssBuilder.setSpan(
                PrivacyClickableSpan,
                text.indexOf("Privacy Policy"),
                text.indexOf("Privacy Policy") + String.valueOf("Privacy Policy").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        // Display the spannable text to TextView
        tvTerms.setText(ssBuilder);
        tvTerms.setHighlightColor(getResources().getColor(R.color.colorWhite));

        // Specify the TextView movement method
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    boolean checkValidation(){
        boolean cancel = false;
        View focusView = null;
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);

        // Check name
        if (StringHelper.isEmpty(name)) {
            focusView = etName;
            etName.setError(getString(R.string.empty_field));
            cancel = true;
        }

        // Check password
        if (StringHelper.isEmpty(password) || !ValidationHelper.isValidPassword(password)){
            focusView = etPassword;
            etPassword.setError(getString(R.string.invalid_password));
            cancel = true;
        }

        // Check Email
        if (StringHelper.isEmpty(email) || !ValidationHelper.isValidEmail(email)){
            focusView = etEmail;
            etEmail.setError(getString(R.string.invalid_email));
            cancel = true;
        }



        if (cancel){
            focusView.requestFocus();
        }
        return cancel;
    }


    void doSignup(){
        showProgressDialog();
        ApiUtils.doSignup(this, Constant.SIGN_TYPE_EMAIL, etEmail.getText().toString(), "",
                etPassword.getText().toString(), etName.getText().toString(), "", new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                LoginData data = (LoginData) object;
                if (data != null){
                    switch (data.status){
                        case 0:
                            PreferenceHelper.removePreference();
                            PreferenceHelper.savePreference(data.info.id, data.info.sign_type, data.info.email, "",
                                    "", data.info.name, "", "", data.info.token);
                            goToMainScreen();
                            break;
                        case 1:
                            Toast.makeText(SignupActivity.this, data.message, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SignupActivity.this, R.string.registration_fail, Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else {
                    Toast.makeText(SignupActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(SignupActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void doSocialSignup(String email, String name, String image_url){
        showProgressDialog();
        ApiUtils.doSignup(this, 3, email, "", "", name, image_url, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                LoginData data = (LoginData) object;
                if (data != null){
                    switch (data.status){
                        case 0:
                            PreferenceHelper.removePreference();
                            PreferenceHelper.savePreference(data.info.id, data.info.sign_type, data.info.email, "", "", data.info.name, data.info.image_url, data.info.fcm_token, data.info.token);
                            goToMainScreen();
                            break;
                        case 1:
                            Toast.makeText(SignupActivity.this, R.string.duplicated_email_address, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SignupActivity.this, R.string.registration_fail, Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else {
                    Toast.makeText(SignupActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(SignupActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void goToMainScreen(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the twitterAuthClient.
        if (twitterClient != null)
            twitterClient.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_GOOGLE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                getGoogleUserProfile(task);
                break;
        }
    }

    private void getFacebookUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        hideProgressDialog();
                        try {
                            Log.e(TAG, object.getString("first_name"));
                            Log.e(TAG, object.getString("last_name"));
                            Log.e(TAG, object.getString("email"));
                            String id = object.getString("id");
                            String photo_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            Log.e(TAG, photo_url);
                            String email = object.getString("email");
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            doSocialSignup(email, first_name + " " + last_name, photo_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id, gender, birthday, address, location");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void getGoogleUserProfile(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.e(TAG, account.getDisplayName());
                Log.e(TAG, account.getEmail());
                Log.e(TAG, account.getId());
                Log.e(TAG, account.getPhotoUrl().toString());
                Log.e(TAG, "Google Sign In Success");
                doSocialSignup(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());
            }

        } catch (ApiException e) {
            Log.e(TAG, e.toString());
        //    Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constant.REQUEST_GOOGLE_SIGN_IN);
    }

    public void twitterSignIn() {
        //check if user is already authenticated or not
        if (getTwitterSession() == null) {

            //if user is not authenticated start authenticating
            twitterClient.authorize(this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    //      Log.e(TAG, "===>" + result.response.message());
                    // Do something with result, which provides a TwitterSession for making API calls
                    TwitterSession twitterSession = result.data;

                    //call fetch email only when permission is granted
                    //    fetchTwitterEmail(twitterSession);
                    fetchTwitterUserInfo();
                }

                @Override
                public void failure(TwitterException e) {
                    Log.e(TAG, "TwitterException:" + e.getLocalizedMessage());
                    // Do something on failure
                    Toast.makeText(SignupActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //if user is already authenticated direct call fetch twitter email api
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            fetchTwitterUserInfo();
        }
    }

    public void fetchTwitterUserInfo(){
        //initialize twitter api client
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

        //Link for Help : https://developer.twitter.com/en/docs/accounts-and-users/manage-account-settings/api-reference/get-account-verify_credentials

        //pass includeEmail : true if you want to fetch Email as well
        Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                Log.e(TAG, "User Id : " + user.id + "\nUser Name : " + user.name + "\nEmail Id : " + user.email + "\nScreen Name : " + user.screenName);

                String imageProfileUrl = user.profileImageUrl;
                Log.e(TAG, "Data : " + imageProfileUrl);
                //NOTE : User profile provided by twitter is very small in size i.e 48*48
                //Link : https://developer.twitter.com/en/docs/accounts-and-users/user-profile-images-and-banners
                //so if you want to get bigger size image then do the following:
                imageProfileUrl = imageProfileUrl.replace("_normal", "");

                doSocialSignup(user.email, user.name, imageProfileUrl);

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(SignupActivity.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.cv_register:
                if(!checkValidation()){
                    if (cbTerms.isChecked())
                        doSignup();
                    else
                        Toast.makeText(this, R.string.you_must_agree, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_facebook:
                btnDefaultFacebook.performClick();
                break;

            case R.id.btn_google:
                googleSignIn();
                break;

            case R.id.btn_twitter:
                twitterSignIn();
                break;
        }
    }


    /**
     * get authenticates user session
     *
     * @return twitter session
     */
    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }

    // Remove Twitter cookie

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            Log.e(TAG, "twitterSession != null");
            ClearTwitterCookies(getApplicationContext());
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        } else {
            Log.e(TAG, "twitterSession == null");
        }
    }

    public static void ClearTwitterCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

}
