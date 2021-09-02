package com.toolsbox.customer.view.activity.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.toolsbox.customer.R;
import com.toolsbox.customer.common.Constant;
import com.toolsbox.customer.common.interFace.Notify;
import com.toolsbox.customer.common.model.api.GeneralData;
import com.toolsbox.customer.common.utils.ApiUtils;
import com.toolsbox.customer.common.utils.AppPreferenceManager;
import com.toolsbox.customer.view.activity.basic.BaseActivity;
import com.toolsbox.customer.view.activity.main.market.AddReviewActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class InputCCActivity extends BaseActivity {
    private static String TAG = "InputCCActivity";
    private Toolbar toolbar;
    private CardInputWidget cardInputWidget;
    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        setContentView(R.layout.activity_input_cc);
        initVariable();
        initUI();
    }

    void initVariable(){

    }

    void initUI() {
        setupToolbar();
        cardInputWidget = findViewById(R.id.card_input_widget);
    }

    void setupToolbar(){
        toolbar =  findViewById(R.id.toolbar);
        TextView tvToolbar = toolbar.findViewById(R.id.toolbar_title);
        tvToolbar.setText(R.string.add_payment);
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

            case R.id.action_done:
                saveCreditCard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_industry, menu);
        return true;
    }

    void saveCreditCard(){
        card = cardInputWidget.getCard();
        if (card != null) {
            showProgressDialog();
            Stripe stripe = new Stripe(this, "pk_test_GXoJOEwGWCMNl96kc56o77j2");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            Log.e("CardTokenTest", "token : " + token.getId());
                            addCreditCard(token.getId());
                        }

                        public void onError(Exception error) {
                            // Show localized error message
                            hideProgressDialog();
                            Toast.makeText(InputCCActivity.this,
                                    error.getLocalizedMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                    }
            );
        } else {
            Toast.makeText(InputCCActivity.this, "Please input valid card!", Toast.LENGTH_LONG).show();
        }
    }

    void addCreditCard(String cardToken){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        ApiUtils.addCreditCard(this, cardToken, token, new Notify() {
            @Override
            public void onSuccess(Object object) {
                hideProgressDialog();
                GeneralData data = (GeneralData)object;
                if (data != null){
                    if (data.status == 0){
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(InputCCActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InputCCActivity.this, R.string.server_not_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                hideProgressDialog();
                Toast.makeText(InputCCActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
