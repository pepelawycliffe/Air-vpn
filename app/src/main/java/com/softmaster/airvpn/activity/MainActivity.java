package com.softmaster.airvpn.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SdkInfo;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.VpnPermissions;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.fireshield.FireshieldCategory;
import com.anchorfree.sdk.fireshield.FireshieldConfig;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.ConnectionStatus;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy;
import com.northghost.caketube.CaketubeTransport;
import com.softmaster.airvpn.R;
import com.pixplicity.easyprefs.library.Prefs;
import com.softmaster.airvpn.dialog.RegionChooserDialog;
import com.softmaster.airvpn.fragments.ServersFragment;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends UIActivity implements TrafficListener, VpnStateListener, ServersFragment.RegionChooserInterface {

    private String selectedCountry = "";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.connect_btn)
    ImageView vpn_connect_btn;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vpn_country_image)
    ImageView selectedServerImage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vpn_country_name)
    TextView selectedServerName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.contains("sname") && Prefs.contains("simage")) {
            String name = Prefs.getString("sname", "");
            Locale locale = new Locale("", name);
            selectedServerName.setText(locale.getDisplayCountry());
            selectedServerImage.setImageResource(MainActivity.this.getResources().getIdentifier("drawable/" + name, null, MainActivity.this.getPackageName()));
        }
        else {
            selectedCountry = "no";
            Locale locale = new Locale("", selectedCountry);
            selectedServerName.setText(locale.getDisplayCountry());
            selectedServerImage.setImageResource(MainActivity.this.getResources().getIdentifier("drawable/" + selectedCountry, null, MainActivity.this.getPackageName()));
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSDK.removeVpnStateListener(this);
        UnifiedSDK.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
    }

    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(@NonNull VpnException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected void isLoggedIn(Callback<Boolean> callback) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(callback);
    }

    @Override
    protected void loginToVpn() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

                updateUI();
            }

            @Override
            public void failure(@NonNull VpnException e) {

                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void logOutFromVnp() {


        UnifiedSDK.getInstance().getBackend().logout(new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
        selectedCountry = "";


        updateUI();
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .withVirtualLocation(selectedCountry)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            Toasty.success(MainActivity.this, "VPN Connected Successfully!", Toast.LENGTH_SHORT, true).show();
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            updateUI();
                            Toasty.error(MainActivity.this, "Error Connecting", Toast.LENGTH_SHORT, true).show();
                            handleError(e);
                        }
                    });
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void disconnectFromVnp() {
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                stopUIUpdateTask();
            }

            @Override
            public void error(@NonNull VpnException e) {
                updateUI();
                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.failure(e);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    public void onRegionSelected(Country item) {
        Locale locale = new Locale("", item.getCountry());
        selectedServerName.setText(locale.getDisplayCountry());
        selectedServerImage.setImageResource(MainActivity.this.getResources().getIdentifier("drawable/" + item.getCountry(), null, MainActivity.this.getPackageName()));
        selectedCountry = item.getCountry();
        updateUI();

        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    //check for naative
    // Example of error handling
    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("User revoked airvpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant airvpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with airvpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            } else {
                showMessage("Error in VPN Service");
            }
        } else if (e instanceof PartnerApiException) {
            switch (((PartnerApiException) e).getContent()) {
                case PartnerApiException.CODE_NOT_AUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case PartnerApiException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check PartnerApiException constants");
                    break;
            }
        }
    }

    //fake method to support migration documentation and list all available methods
    public void sdkMethodsList() {
        final UnifiedSDK instance = UnifiedSDK.getInstance();
        instance.getBackend().deletePurchase(0, CompletableCallback.EMPTY);
        instance.getVPN().getStartTimestamp(new Callback<Long>() {
            @Override
            public void success(@NonNull Long aLong) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getConnectionStatus(new Callback<ConnectionStatus>() {
            @Override
            public void success(@NonNull ConnectionStatus connectionStatus) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        instance.getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        instance.getVPN().restart(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(TrafficRule.Builder.block().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.bypass().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                .exceptApps(new ArrayList<>())
                .forApps(new ArrayList<>())
                .withVirtualLocation("")
                .withPolicy(AppPolicy.newBuilder().build())
                .withFireshieldConfig(new FireshieldConfig.Builder()
                        .addCategory(FireshieldCategory.Builder.block(""))
                        .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                        .addCategory(FireshieldCategory.Builder.bypass(""))
                        .addCategory(FireshieldCategory.Builder.custom("", ""))
                        .addCategory(FireshieldCategory.Builder.proxy(""))
                        .addCategory(FireshieldCategory.Builder.vpn(""))
                        .build())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);

        instance.getBackend().purchase("", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
        instance.getBackend().purchase("", "", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getInfo(new Callback<SdkInfo>() {
            @Override
            public void success(@NonNull SdkInfo sdkInfo) {
                String deviceId = sdkInfo.getDeviceId();
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        instance.getBackend().currentUser(new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getStatus(new Callback<SessionInfo>() {
            @Override
            public void success(@NonNull SessionInfo sessionInfo) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getBackend().getAccessToken();

        VpnPermissions.request(new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });

        UnifiedSDK.addVpnCallListener(parcelable -> {

        });
        UnifiedSDK.removeVpnCallListener(null);

        instance.getVPN().updateConfig(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(TrafficRule.Builder.block().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.bypass().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                .exceptApps(new ArrayList<>())
                .withTransport("")
                .withSessionId("")
                .forApps(new ArrayList<>())
                .withVirtualLocation("")
                .withPolicy(AppPolicy.newBuilder().build())
                .withFireshieldConfig(new FireshieldConfig.Builder()
                        .addCategory(FireshieldCategory.Builder.block(""))
                        .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                        .addCategory(FireshieldCategory.Builder.bypass(""))
                        .addCategory(FireshieldCategory.Builder.custom("", ""))
                        .addCategory(FireshieldCategory.Builder.proxy(""))
                        .addCategory(FireshieldCategory.Builder.vpn(""))
                        .build())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
    }



}
