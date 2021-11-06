package com.softmaster.airvpn.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.android.material.snackbar.Snackbar;
import com.softmaster.airvpn.R;
import com.softmaster.airvpn.Config;
import com.softmaster.airvpn.MainApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity  {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.parent)
    RelativeLayout parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Snackbar snackbar = Snackbar
                .make(parent, "Server Connecting, Please wait...", Snackbar.LENGTH_LONG);
        snackbar.show();

        loginUser();


    }

    private void loginUser() {
        //logging in
        ((MainApplication) getApplication()).setNewHostAndCarrier(Config.baseURL, Config.carrierID);
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();

            }

            @Override
            public void failure(@NonNull VpnException e) {

                Log.w("sdasd", e.getMessage());

                Snackbar snackbar = Snackbar
                        .make(parent, "Authentication Error, Please try again.", Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Try again", v -> loginUser());
                snackbar.show();
            }
        });
    }


}
