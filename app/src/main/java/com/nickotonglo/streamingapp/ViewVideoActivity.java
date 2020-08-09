package com.nickotonglo.streamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.nickotonglo.streamingapp.ui.main.PanelTopFragment;

public class ViewVideoActivity extends AppCompatActivity {

    private View bottomHorizontalBar1,bottomHorizontalBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        bottomHorizontalBar1 = (View)findViewById(R.id.view_bottom_horizontal_bar_1);
        bottomHorizontalBar2 = (View)findViewById(R.id.view_bottom_horizontal_bar_2);

        PanelTopFragment topFragment = new PanelTopFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.layout_fragment,topFragment,topFragment.getTag())
                .commit();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            bottomHorizontalBar1.setVisibility(View.VISIBLE);
            bottomHorizontalBar2.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            bottomHorizontalBar1.setVisibility(View.GONE);
            bottomHorizontalBar2.setVisibility(View.GONE);
        }
    }
}