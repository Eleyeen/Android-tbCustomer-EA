package com.toolsbox.customer.view.activity.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.toolsbox.customer.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;

public class OverlayProgressDialog extends DialogFragment {


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_progress_overlay, container, false);
    }
}
