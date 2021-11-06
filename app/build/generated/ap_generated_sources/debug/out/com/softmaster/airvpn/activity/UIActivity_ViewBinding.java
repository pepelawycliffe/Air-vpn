// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UIActivity_ViewBinding implements Unbinder {
  private UIActivity target;

  private View view7f09008c;

  private View view7f0900b3;

  private View view7f09021b;

  @UiThread
  public UIActivity_ViewBinding(UIActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UIActivity_ViewBinding(final UIActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.connect_btn, "field 'vpn_connect_btn' and method 'onConnectBtnClick'");
    target.vpn_connect_btn = Utils.castView(view, R.id.connect_btn, "field 'vpn_connect_btn'", ImageView.class);
    view7f09008c = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onConnectBtnClick(p0);
      }
    });
    target.uploading_speed_textview = Utils.findRequiredViewAsType(source, R.id.uploading_speed, "field 'uploading_speed_textview'", TextView.class);
    target.downloading_speed_textview = Utils.findRequiredViewAsType(source, R.id.downloading_speed, "field 'downloading_speed_textview'", TextView.class);
    target.vpn_connection_time = Utils.findRequiredViewAsType(source, R.id.vpn_connection_time, "field 'vpn_connection_time'", TextView.class);
    target.vpn_connection_time_text = Utils.findRequiredViewAsType(source, R.id.vpn_connection_time_text, "field 'vpn_connection_time_text'", TextView.class);
    view = Utils.findRequiredView(source, R.id.drawer_opener, "field 'Drawer_opener_image' and method 'OpenDrawer'");
    target.Drawer_opener_image = Utils.castView(view, R.id.drawer_opener, "field 'Drawer_opener_image'", ImageView.class);
    view7f0900b3 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.OpenDrawer(p0);
      }
    });
    target.timerTextView = Utils.findRequiredViewAsType(source, R.id.tv_timer, "field 'timerTextView'", TextView.class);
    target.connection_btn_block = Utils.findRequiredViewAsType(source, R.id.connection_btn_block, "field 'connection_btn_block'", RelativeLayout.class);
    target.second_elipse = Utils.findRequiredViewAsType(source, R.id.second_elipse, "field 'second_elipse'", RelativeLayout.class);
    view = Utils.findRequiredView(source, R.id.vpn_select_country, "method 'showRegionDialog'");
    view7f09021b = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.showRegionDialog();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    UIActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vpn_connect_btn = null;
    target.uploading_speed_textview = null;
    target.downloading_speed_textview = null;
    target.vpn_connection_time = null;
    target.vpn_connection_time_text = null;
    target.Drawer_opener_image = null;
    target.timerTextView = null;
    target.connection_btn_block = null;
    target.second_elipse = null;

    view7f09008c.setOnClickListener(null);
    view7f09008c = null;
    view7f0900b3.setOnClickListener(null);
    view7f0900b3 = null;
    view7f09021b.setOnClickListener(null);
    view7f09021b = null;
  }
}
