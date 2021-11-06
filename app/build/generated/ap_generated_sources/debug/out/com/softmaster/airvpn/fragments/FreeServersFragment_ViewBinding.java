// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.fragments;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FreeServersFragment_ViewBinding implements Unbinder {
  private FreeServersFragment target;

  @UiThread
  public FreeServersFragment_ViewBinding(FreeServersFragment target, View source) {
    this.target = target;

    target.freeServersRecyclerview = Utils.findRequiredViewAsType(source, R.id.freeServersRecyclerview, "field 'freeServersRecyclerview'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FreeServersFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.freeServersRecyclerview = null;
  }
}
