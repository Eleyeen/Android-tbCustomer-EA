package com.toolsbox.customer.view.customUI;

import android.content.Context;
import android.util.AttributeSet;

import static com.toolsbox.customer.common.utils.FONT.Montserrat_light;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context) {
        super(context);
        init();
    }

    public void init() {
        setTypeface(Montserrat_light);

    }
}
