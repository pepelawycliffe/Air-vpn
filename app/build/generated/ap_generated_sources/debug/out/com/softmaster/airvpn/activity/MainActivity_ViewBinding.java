// Generated code from Butter Knife. Do not modify!
package com.softmaster.airvpn.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.UiThread;
import butterknife.internal.Utils;
import com.softmaster.airvpn.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding extends UIActivity_ViewBinding {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    super(target, source);

    this.target = target;

    target.vpn_connect_btn = Utils.findRequiredViewAsType(source, R.id.connect_btn, "field 'vpn_connect_btn'", ImageView.class);
    target.selectedServerImage = Utils.findRequiredViewAsType(source, R.id.vpn_country_image, "field 'selectedServerImage'", ImageView.class);
    target.selectedServerName = Utils.findRequiredViewAsType(source, R.id.vpn_country_name, "field 'selectedServerName'", TextView.class);
  }

  @Override
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vpn_connect_btn = null;
    target.selectedServerImage = null;
    target.selectedServerName = null;

    super.unbind();
  }
}
