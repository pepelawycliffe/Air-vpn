// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.fragments;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.google.android.material.tabs.TabLayout;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ServersFragment_ViewBinding implements Unbinder {
  private ServersFragment target;

  private View view7f0900ca;

  @UiThread
  public ServersFragment_ViewBinding(final ServersFragment target, View source) {
    this.target = target;

    View view;
    target.serversTablayout = Utils.findRequiredViewAsType(source, R.id.servers_tablayout, "field 'serversTablayout'", TabLayout.class);
    target.serversViewPager = Utils.findRequiredViewAsType(source, R.id.servers_viewPager, "field 'serversViewPager'", ViewPager.class);
    target.activityName = Utils.findRequiredViewAsType(source, R.id.activity_name, "field 'activityName'", TextView.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.servers_progressbar, "field 'progressBar'", ProgressBar.class);
    view = Utils.findRequiredView(source, R.id.finish_activity, "method 'onViewClicked'");
    view7f0900ca = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ServersFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.serversTablayout = null;
    target.serversViewPager = null;
    target.activityName = null;
    target.progressBar = null;

    view7f0900ca.setOnClickListener(null);
    view7f0900ca = null;
  }
}
