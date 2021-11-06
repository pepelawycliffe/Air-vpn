// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.dialog;

import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegionChooserDialog_ViewBinding implements Unbinder {
  private RegionChooserDialog target;

  @UiThread
  public RegionChooserDialog_ViewBinding(RegionChooserDialog target, View source) {
    this.target = target;

    target.regionsRecyclerView = Utils.findRequiredViewAsType(source, R.id.regions_recycler_view, "field 'regionsRecyclerView'", RecyclerView.class);
    target.regionsProgressBar = Utils.findRequiredViewAsType(source, R.id.regions_progress, "field 'regionsProgressBar'", ProgressBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegionChooserDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.regionsRecyclerView = null;
    target.regionsProgressBar = null;
  }
}
