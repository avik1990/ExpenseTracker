package com.app.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.app.expensetracker.utility.BaseActivity;

public class SplashActivity extends BaseActivity {

    Context mContext;
    private static final int SPLASH_TIME = 3 * 1000;
    boolean otp_flag = false;

    @Override
    protected void InitListner() {
        if (otp_flag) {
            goToHomeActivity();
        } else {
            goSliderActivity();
        }
    }

    private void goToHomeActivity() {
        Thread background = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                    Intent i = new Intent(getBaseContext(), SLiderActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

    private void goSliderActivity() {
        Thread background = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                    Intent i = new Intent(getBaseContext(), SLiderActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

    @Override
    protected void InitResources() {
        mContext = this;
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }
}
