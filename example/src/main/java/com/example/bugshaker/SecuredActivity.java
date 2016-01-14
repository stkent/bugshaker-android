package com.example.bugshaker;

import android.os.Bundle;
import android.view.WindowManager;

public final class SecuredActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secured);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

}
