package com.softmaster.airvpn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.softmaster.airvpn.R;
import com.pixplicity.easyprefs.library.Prefs;
import com.softmaster.airvpn.Config;
import com.softmaster.airvpn.LockModel;
import com.softmaster.airvpn.MainApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import debugger.Helper;

public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {

    private List<Country> regions;
    private List<LockModel> regionsLock;
    private final RegionListAdapterInterface listAdapterInterface;

    private final ArrayList<String> premiumServers = new ArrayList<>();


    public RegionListAdapter(Context context, RegionListAdapterInterface listAdapterInterface) {
        this.listAdapterInterface = listAdapterInterface;

        premiumServers.add("Germany");
        premiumServers.add("Japan");
        premiumServers.add("United Kingdom");
        premiumServers.add("United States");
        premiumServers.add("Australia");
        premiumServers.add("Switzerland");
        premiumServers.add("Hong Kong");
        premiumServers.add("Denmark");
        premiumServers.add("Canada");
        premiumServers.add("England");
        premiumServers.add("Romania");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Country country = regions.get(position);
        Locale locale = new Locale("", regions.get(position).getCountry());

        if (country.getCountry() != null && !country.getCountry().equals("")) {
            holder.regionTitle.setText(locale.getDisplayCountry());
            String str = regions.get(position).getCountry();
            holder.regionImage.setImageResource(MainApplication.getStaticContext().getResources().getIdentifier("drawable/" + str, null, MainApplication.getStaticContext().getPackageName()));

            if (position < regionsLock.size()) {

                if (premiumServers.contains(locale.getDisplayCountry())) {
                    regionsLock.get(position).setLock(true);

                    Helper.printLog(locale.getDisplayCountry());
                }

                if (!Config.servers_subscription && regionsLock.get(position).isLock()) {
                    holder.setEnableLock();
                }
                else {
                    holder.setDisbaleLock();
                }
            }

            holder.itemView.setOnClickListener(view -> {
                listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
                Prefs.putString("sname", regions.get(position).getCountry());
                Prefs.putString("simage", regions.get(position).getCountry());
            });
        } else {
            holder.regionTitle.setText(R.string.Unknown_Server);
            holder.regionImage.setImageResource(R.drawable.select_flag_image);
            holder.setClicks();
        }
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.region_title)
        TextView regionTitle;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.region_image)
        ImageView regionImage;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.parent)
        RelativeLayout cardView;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.region_signal_image)
        ImageView signalImage;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lockLayout)
        RelativeLayout lockLayout;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void setEnableLock() {
            lockLayout.setVisibility(View.VISIBLE);
            lockLayout.setClickable(true);
            lockLayout.setFocusable(true);
            lockLayout.setFocusableInTouchMode(true);
        }

        public void setDisbaleLock() {
            lockLayout.setVisibility(View.GONE);
            lockLayout.setClickable(false);
            lockLayout.setFocusable(false);
            lockLayout.setFocusableInTouchMode(false);
        }

        public void setClicks() {
            regionTitle.setClickable(false);
            regionImage.setClickable(false);
            cardView.setClickable(false);
            signalImage.setClickable(false);
            regionTitle.setFocusable(false);
            regionImage.setFocusable(false);
            cardView.setFocusable(false);
            signalImage.setFocusable(false);
        }
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        regions.add(new Country(""));
        regions.addAll(list);

        regionsLock = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {
            regionsLock.add(i, new LockModel());
        }

        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Country item);
    }
}
