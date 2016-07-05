package com.airplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.airplayer.R;

/**
 * Created by ZiyiTsang on 16/6/24.
 */
public class LaunchActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private CoordinatorLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mLayout = (CoordinatorLayout) findViewById(R.id.layout);
        grantPermission();

        Button grantButton = (Button) findViewById(R.id.grant_button);
        grantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantPermission();
            }
        });
    }

    private void grantPermission() {
        ActivityCompat.requestPermissions(LaunchActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                startActivity(new Intent(LaunchActivity.this, AirMainActivity.class));
                LaunchActivity.this.finish();
            } else {
                Snackbar.make(mLayout, R.string.hint_msg_permission_denied, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.snack_action_finish_app, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LaunchActivity.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
