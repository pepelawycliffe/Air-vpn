package com.softmaster.airvpn.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.android.material.tabs.TabLayout;
import com.softmaster.airvpn.R;
import com.pixplicity.easyprefs.library.Prefs;
import com.softmaster.airvpn.adapter.TabsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import debugger.Helper;

public class ServersFragment extends DialogFragment implements PassServerData {

    public static final String TAG = ServersFragment.class.getSimpleName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.servers_tablayout)
    TabLayout serversTablayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.servers_viewPager)
    ViewPager serversViewPager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.activity_name)
    TextView activityName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.servers_progressbar)
    ProgressBar progressBar;

    private TabsAdapter adapter;

    //lists
    private final List<Country> freeList = new ArrayList<>();
    private final List<Country> paidList = new ArrayList<>();
    private RegionChooserInterface mCallback;


    public static ServersFragment newInstance() {
        ServersFragment frag = new ServersFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_servers, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activityName.setText(R.string.Servers);

        getServers();
    }

    @Override
    public void onAttach(@NonNull Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            mCallback = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {

        if (fragment instanceof FreeServersFragment) {
            FreeServersFragment freeServersFragment = (FreeServersFragment) fragment;
            freeServersFragment.setFreeServersList(freeList);
            freeServersFragment.registerCallback(this);
        } else if (fragment instanceof VipServersFragment) {
            VipServersFragment vipServersFragment = (VipServersFragment) fragment;
            vipServersFragment.setVIPServersList(paidList);
            vipServersFragment.registerCallback(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void getServers() {

        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {

                for (Country country :
                        countries.getCountries()) {

                    Locale locale = new Locale("", country.getCountry());

                    if (locale.getDisplayCountry().equalsIgnoreCase("Unknown server")) {
                    } else if (country.getCountry().equalsIgnoreCase("de") ||
                            country.getCountry().equalsIgnoreCase("hk") ||
                            country.getCountry().equalsIgnoreCase("jp") ||
                            country.getCountry().equalsIgnoreCase("dk") ||
                            country.getCountry().equalsIgnoreCase("gb") ||
                            country.getCountry().equalsIgnoreCase("us") ||
                            country.getCountry().equalsIgnoreCase("ch") ||
                            country.getCountry().equalsIgnoreCase("au")
                    ) {
                        paidList.add(country);
                    } else {
                        freeList.add(country);
                    }
                }

                adapter = new TabsAdapter(getChildFragmentManager());
                adapter.addFragment(FreeServersFragment.createInstance(), "Free Servers");
                adapter.addFragment(VipServersFragment.createInstance(), "VIP Servers");

                serversViewPager.setAdapter(adapter);
                serversTablayout.setupWithViewPager(serversViewPager);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Helper.showToast(getContext(), "Unable to fetch Servers");
                dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.finish_activity)
    public void onViewClicked() {
        dismiss();
    }

    //selected server from free or vip servers
    @Override
    public void getSelectedServer(Country country) {

        Prefs.putString("sname", country.getCountry());
        Prefs.putString("simage", country.getCountry());

        Toast.makeText(getContext(), country.getCountry(), Toast.LENGTH_SHORT).show();

        mCallback.onRegionSelected(country);
        dismiss();
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }
}
