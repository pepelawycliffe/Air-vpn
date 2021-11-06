package com.softmaster.airvpn.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.softmaster.airvpn.R;
import com.softmaster.airvpn.Config;
import com.softmaster.airvpn.MainApplication;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipServersFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.freeServersRecyclerview)
    RecyclerView VIPServersRecyclerview;

    private PassServerData mCallback;

    private final VIPServersAdapter adapter = new VIPServersAdapter();
    private List<Country> list = new ArrayList<>();

    public void registerCallback(PassServerData passServerData) {
        mCallback = passServerData;
    }

    public static Fragment createInstance() {
        return new VipServersFragment();
    }

    public void setVIPServersList(List<Country> servers) {
        list = servers;
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_free_servers, container, false);
        ButterKnife.bind(this, view);

        VIPServersRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        VIPServersRecyclerview.setAdapter(adapter);
        return view;
    }

    class VIPServersAdapter extends RecyclerView.Adapter<VIPServersViewHolder> {

        @NonNull
        @Override
        public VIPServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.region_list_item, parent, false);
            return new VIPServersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VIPServersViewHolder holder, int position) {
            holder.populateView(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    /**
     * RecyclerView viewHolder
     */
    class VIPServersViewHolder extends RecyclerView.ViewHolder {

        private final TextView mRegionTitle;
        private final ImageView mRegionImage;
        private final RelativeLayout mLockLayout;
        private final RelativeLayout mItemView;
        private final CustomCheckBox checkBox_server;

        public VIPServersViewHolder(@NonNull View itemView) {
            super(itemView);

            mRegionTitle = itemView.findViewById(R.id.region_title);
            mRegionImage = itemView.findViewById(R.id.region_image);
            mLockLayout = itemView.findViewById(R.id.lockLayout);
            mItemView = itemView.findViewById(R.id.itemView);
            checkBox_server = itemView.findViewById(R.id.server_selection_checkbox);
        }

        public void populateView(Country country) {

            if (!Config.servers_subscription) {
                mLockLayout.setVisibility(View.VISIBLE);
            } else {
                mLockLayout.setVisibility(View.GONE);
            }

            Locale locale = new Locale("", country.getCountry());
            mRegionTitle.setText(locale.getDisplayCountry());
            mRegionImage.setImageResource(MainApplication.getStaticContext().getResources().getIdentifier("drawable/" + country.getCountry(), null, MainApplication.getStaticContext().getPackageName()));

            mItemView.setOnClickListener(v -> mCallback.getSelectedServer(country));
            checkBox_server.setOnClickListener(v -> mCallback.getSelectedServer(country));
        }
    }
}