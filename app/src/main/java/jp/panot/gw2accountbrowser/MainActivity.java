package jp.panot.gw2accountbrowser;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import jp.panot.gw2accountbrowser.fragments.AccountFragment;
import jp.panot.gw2accountbrowser.fragments.BankFragment;
import jp.panot.gw2accountbrowser.fragments.WalletFragment;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    ft.add(R.id.main_container, new AccountFragment());
    ft.commit();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    FragmentManager fm = getSupportFragmentManager();
    Fragment currentFragment = fm.findFragmentById(R.id.main_container);

    if (id == R.id.nav_account) {
      if (!(currentFragment instanceof AccountFragment)) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, new AccountFragment());
        ft.commit();
      }
    } else if (id == R.id.nav_achievement) {
      Toast.makeText(this, "Achievements", Toast.LENGTH_SHORT).show();
    } else if (id == R.id.nav_wallet) {
      if (!(currentFragment instanceof WalletFragment)) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, new WalletFragment());
        ft.commit();
      }
    } else if (id == R.id.nav_bank) {
      Toast.makeText(this, "Bank", Toast.LENGTH_SHORT).show();
      if (!(currentFragment instanceof BankFragment)) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, new BankFragment());
        ft.commit();
      }
    } else if (id == R.id.nav_storage) {
      Toast.makeText(this, "Material Storage", Toast.LENGTH_SHORT).show();
    } else if (id == R.id.nav_manage_accounts) {
      Toast.makeText(this, "Manage Accounts", Toast.LENGTH_SHORT).show();
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
