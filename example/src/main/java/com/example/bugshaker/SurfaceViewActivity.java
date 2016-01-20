package com.example.bugshaker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

public class SurfaceViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);

        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.setBackgroundColor(Color.RED);
    }

}
