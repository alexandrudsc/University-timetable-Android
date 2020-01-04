package com.developer.alexandru.orarusv.navigation_drawer;

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import androidx.drawerlayout.widget.DrawerLayout;

/** Created by Alexandru on 9/14/2014. Listener for navigation drawer's elements */
public class NavigationItemClickListener implements AdapterView.OnItemClickListener {

  private DrawerLayout drawer;
  private DrawerToggle drawerToggle;

  public NavigationItemClickListener(DrawerLayout drawer, DrawerToggle drawerToggle) {
    this.drawer = drawer;
    this.drawerToggle = drawerToggle;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (position == drawerToggle.getCurrentPage()) {
      drawer.closeDrawer(Gravity.START);
    } else {
      drawerToggle.setSelectedPage(position);

      drawer.closeDrawer(Gravity.START);
    }
  }
}
