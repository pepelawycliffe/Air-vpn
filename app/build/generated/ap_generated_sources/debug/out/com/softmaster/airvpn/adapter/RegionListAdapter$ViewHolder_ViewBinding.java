// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegionListAdapter$ViewHolder_ViewBinding implements Unbinder {
  private RegionListAdapter.ViewHolder target;

  @UiThread
  public RegionListAdapter$ViewHolder_ViewBinding(RegionListAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.regionTitle = Utils.findRequiredViewAsType(source, R.id.region_title, "field 'regionTitle'", TextView.class);
    target.regionImage = Utils.findRequiredViewAsType(source, R.id.region_image, "field 'regionImage'", ImageView.class);
    target.cardView = Utils.findRequiredViewAsType(source, R.id.parent, "field 'cardView'", RelativeLayout.class);
    target.signalImage = Utils.findRequiredViewAsType(source, R.id.region_signal_image, "field 'signalImage'", ImageView.class);
    target.lockLayout = Utils.findRequiredViewAsType(source, R.id.lockLayout, "field 'lockLayout'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegionListAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.regionTitle = null;
    target.regionImage = null;
    target.cardView = null;
    target.signalImage = null;
    target.lockLayout = null;
  }
}
