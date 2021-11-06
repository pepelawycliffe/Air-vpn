package com.softmaster.airvpn.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.softmaster.airvpn.R;
import com.pepperonas.materialdialog.MaterialDialog;
import com.softmaster.airvpn.Config;
import com.softmaster.airvpn.MainApplication;
import com.softmaster.airvpn.adapter.PrefManager;
import com.softmaster.airvpn.fragments.ServersFragment;
import com.softmaster.airvpn.utils.Converter;
import java.util.Locale;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import debugger.Helper;
import es.dmoral.toasty.Toasty;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static com.softmaster.airvpn.Config.servers_subscription;

public abstract class UIActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    protected static final String HELPER_TAG = "Helper";

    NativeAd nativeAdd;

    private PrefManager prefManager;
    private Dialog RateDialog;
    private ImageView cursor;
    int count = 0;
    private Runnable runnable;

    private PulsatorLayout pulsator;
    private final Handler customHandler = new Handler();
    private int seconds;
    private boolean running;
    private  boolean wasRunning;

    VPNState state;

   private final Handler handler = new Handler();

    protected Toolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.connect_btn)
    ImageView vpn_connect_btn;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vpn_connection_time)
    TextView vpn_connection_time;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vpn_connection_time_text)
    TextView vpn_connection_time_text;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawer_opener)
    ImageView Drawer_opener_image;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_timer)
    TextView timerTextView;




    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.connection_btn_block)
    RelativeLayout connection_btn_block;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.second_elipse)
    RelativeLayout second_elipse;

    /*google ads*/
    private NativeAd nativeAd;
    private InterstitialAd mInterstitialAd;


    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };





    private RewardedAd mRewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        AdvanceDrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);

        setupDrawer();



        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

                if (vpnState == VPNState.CONNECTED) {
                    Config.isVPNConnected = true;
                } else if (vpnState == VPNState.IDLE) {
                    //enable ads
                    if (!Config.ads_subscription && getResources().getBoolean(R.bool.ads_flag)) {
                        handleAds();
                    }

                    Config.isVPNConnected = false;
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        pulsator = findViewById(R.id.pulsator);
        ImageView rate_win = findViewById(R.id.rate_win);

        InitiaterateDialog();
        rate_win.setOnClickListener(view -> {
            if(cursor!=null){
                cursor.startAnimation(AnimationUtils.loadAnimation(UIActivity.this, R.anim.zoom_in_out));
            }
            RateDialog.show();
        });

        VideoAdclass();

        if (savedInstanceState != null){
            savedInstanceState.getInt("seconds");
            savedInstanceState.getBoolean("running");
            savedInstanceState.getBoolean("wasRunning");

        }
        runTimer();
        admobInterAd();

    }



    protected void VideoAdclass(){
        MobileAds.initialize(this, initializationStatus -> loadRewardedAd());
        LinearLayout watch_btn = findViewById(R.id.go_pro);
        watch_btn.setOnClickListener(v -> showRewardedAd());
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                loadRewardedAd();
                            }
                        });
                    }
                });
    }

    private void showRewardedAd(){
        if (mRewardedAd != null) {
            mRewardedAd.show(this, rewardItem -> {
                Toasty.success(UIActivity.this, "VIP Servers are Unlocked", Toast.LENGTH_SHORT, true).show();
                servers_subscription = true;
            });
        } else {
            Toasty.error(this, "The ad wasn't ready yet", Toast.LENGTH_SHORT, true).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void logOutFromVnp();



    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.vpn_select_country)
    public void showRegionDialog() {

        if (!Config.isVPNConnected) {
            ServersFragment.newInstance().show(getSupportFragmentManager(), ServersFragment.TAG);
        } else {
           Toasty.error(this, "Please disconnect the VPN first", Toast.LENGTH_SHORT,true).show();
        }

    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {

            isConnected(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean aBoolean) {

                    if (aBoolean) {

                        new MaterialDialog.Builder(UIActivity.this)
                                .title("Confirmation")
                                .message("Are You Sure to Disconnect The AirVPN")
                                .positiveText("Disconnect")
                                .negativeText("CANCEL")
                                .positiveColor(R.color.red)
                                .negativeColor(R.color.color_btn)
                                .buttonCallback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        admobInterAd();
                                        if (mInterstitialAd != null) {
                                                 mInterstitialAd.show(UIActivity.this);
                                                 mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                            public void onAdDismissedFullScreenContent() {
                                            super.onAdDismissedFullScreenContent();
                                            disconnectFromVnp();
                                            mInterstitialAd = null;
                                            admobInterAd();
                                            }});
                                          } else {
                                              disconnectFromVnp();
                                                 }
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                    }
                                })
                                .show();

                    } else {
                        connectToVpn();
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {

                }
            });

    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

                switch (vpnState) {
                    case IDLE: {
                        uploading_speed_textview.setText(R.string._0_mbps);
                        downloading_speed_textview.setText(R.string._0_mbps);
                        vpn_connection_time.setText(R.string.disconnected);
                        vpn_connection_time.setTextColor(getResources().getColor(R.color.yellow_color));

                        Config.isVPNConnected = false;
                        wasRunning = running;
                        running = false;
                        vpn_connect_btn.setImageResource(R.drawable.main_icon);
                        connection_btn_block.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_1));
                        second_elipse.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_2));

                        break;
                    }
                    case CONNECTED: {
                        pulsator.stop();
                        running = true;
                        vpn_connection_time.setText(R.string.connected);
                        vpn_connection_time.setTextColor(getResources().getColor(R.color.gnt_green));

                        connection_btn_block.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_yes1));
                        second_elipse.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_yes2));

                        vpn_connect_btn.setImageResource(R.drawable.main_icon);
                        HideTextAnimation();
                        Config.isVPNConnected = true;
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        connection_btn_block.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_con1));
                        second_elipse.setBackground(ContextCompat.getDrawable(UIActivity.this,R.drawable.ellipse_con2));
                        pulsator.start();
                        ViewTextAnimation();
                        running = false;
                        seconds = 0;

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(UIActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                        break;
                    }
                    case PAUSED: {

                        Helper.printLog("paused");
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {


            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(() -> {
                    Locale locale = new Locale("", currentServer);
               });
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

        }
    }





    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void handleUserLogin() {
        ((MainApplication) getApplication()).setNewHostAndCarrier(Config.baseURL, Config.carrierID);
        loginToVpn();
    }

    private void setupDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                UIActivity.this, drawer, null, 0, 0);//R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(UIActivity.this);

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.drawer_opener)
    public void OpenDrawer(View v) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    @SuppressLint({"NonConstantResourceId", "ShowToast"})
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        // Handle navigation view item clicks here.
        switch (menuitem.getItemId()) {
            case R.id.nav_upgrade:
//            upgrade application is available...
              if (!Config.isVPNConnected) {
                    ServersFragment.newInstance().show(getSupportFragmentManager(), ServersFragment.TAG);
                }
                else {
                    Toasty.error(this, "Please disconnect the VPN first", Toast.LENGTH_SHORT,true).show();
                }
                break;
            case R.id.nav_unlock:
                showRewardedAd();
                break;
            case R.id.nav_helpus:
//            find help about the application
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_to_improve_us_email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_to_improve_us_body));

                try {
                    startActivity(Intent.createChooser(intent, "send mail"));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT);
                } catch (Exception ex) {
                    Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.nav_rate:
//            rate application...
                if(cursor!=null){
                    cursor.startAnimation(AnimationUtils.loadAnimation(UIActivity.this, R.anim.zoom_in_out));
                }
                RateDialog.show();

                break;

            case R.id.nav_share:
//            share the application...
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using this Free VPN App, it's provide all servers free https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    Toasty.success(this, "Error..", Toast.LENGTH_SHORT, true).show();
                }
                break;
                case R.id.nav_faq:
                startActivity(new Intent(this, Faq.class));
                break;

            case R.id.nav_about:
                showAboutDialog();
                break;

            case R.id.nav_policy:
                try {
                    Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
                    Intent intent_policy = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent_policy);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toasty.error(this, "Please give a valid privacy policy URL.", Toast.LENGTH_SHORT,true).show();
                }
                break;
        }
     return true;
    }

    private void showAboutDialog() {

        Dialog about_dialog = new Dialog(this);
        about_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        about_dialog.setContentView(R.layout.dialog_about);
        about_dialog.setCancelable(true);
        about_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(about_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        about_dialog.findViewById(R.id.bt_close).setOnClickListener(v -> about_dialog.dismiss());

        about_dialog.show();
        about_dialog.getWindow().setAttributes(lp);
    }


    public void InitiaterateDialog(){
        RateDialog = new Dialog(this);
        RateDialog.setContentView(R.layout.rating_window);
        Button rateButton = RateDialog.findViewById(R.id.btn_rt);
        cursor = RateDialog.findViewById(R.id.ic_rate_us);
        TextView laterButton = RateDialog.findViewById(R.id.btn_later);
        rateButton.setOnClickListener(view -> {
            prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA, PrefManager.MODE_WRITE);
            prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,420);
            RateDialog.dismiss();
            if(cursor!=null)
                cursor.clearAnimation();
            Rate();
        });



        laterButton.setOnClickListener(view -> RateDialog.dismiss());

        RateDialog.setOnCancelListener(dialog -> {
            if(cursor!=null)
                cursor.clearAnimation();
        });
        RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void Rate(){
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    class running_thread implements Runnable {
        running_thread() {
        }

        public void run() {
            if (UIActivity.this.count == 0) {
                UIActivity.this.count = 1;
                UIActivity.this.vpn_connection_time.setText(UIActivity.this.getResources().getString(R.string.loading));
            } else if (UIActivity.this.count == 1) {
                UIActivity.this.count = 2;
                UIActivity.this.vpn_connection_time.setText(UIActivity.this.getResources().getString(R.string.loading) + ".");
            }  else if (UIActivity.this.count == 2) {
                UIActivity.this.count = 3;
                UIActivity.this.vpn_connection_time.setText(UIActivity.this.getResources().getString(R.string.loading) + "..");
            } else if (UIActivity.this.count == 3) {
                UIActivity.this.count = 0;
                UIActivity.this.vpn_connection_time.setText(UIActivity.this.getResources().getString(R.string.loading) + "...");
            }
            UIActivity.this.handler.postDelayed(UIActivity.this.runnable, 400);
        }
    }

    private void ViewTextAnimation() {
        vpn_connection_time.setTextColor(getResources().getColor(R.color.connection_text));
        Handler handler = this.handler;
        Runnable running_thread = new running_thread();
        this.runnable = running_thread;
        handler.postDelayed(running_thread, 400);
    }

    private void HideTextAnimation() {
        this.handler.removeCallbacks(this.runnable);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    protected  void runTimer(){
        customHandler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(),
                "%d:%02d:%02d",
                hours,minutes,secs);

                timerTextView.setText(time);

                if (running){
                    seconds++;
                }
                customHandler.postDelayed(this,1000);
            }
        });
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, com.google.android.gms.ads.nativead.NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));


        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);

        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);

        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
        adView.getAdvertiserView().setVisibility(View.VISIBLE);


        adView.setNativeAd(nativeAd);


    }

    private void refreshAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_advance));
        builder.forNativeAd(unifiedNativeAd -> {

            if (nativeAdd != null) {
                nativeAdd.destroy();
            }
            nativeAdd = unifiedNativeAd;

            FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
            @SuppressLint("InflateParams") com.google.android.gms.ads.nativead.NativeAdView adView =
                    (com.google.android.gms.ads.nativead.NativeAdView)
                            getLayoutInflater().inflate(R.layout.admob_native_new, null);

            populateUnifiedNativeAdView(unifiedNativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        }).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void handleAds() {
        if (getResources().getBoolean(R.bool.ads_flag)) {

            //loading native ad
            refreshAd();
            //interstitial
            admobInterAd();
            if (mInterstitialAd != null) {
                mInterstitialAd.show(UIActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }


            }
    }

    private void admobInterAd(){

        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(UIActivity.this, getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });

    }


}
