package mz.org.csaude.mentoring.view.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityMainBinding;
import mz.org.csaude.mentoring.databinding.ActivityNotificationsBinding;
import mz.org.csaude.mentoring.databinding.NavHeaderMainBinding;
import mz.org.csaude.mentoring.view.home.ui.notifications.NotificationsActivity;
import mz.org.csaude.mentoring.view.home.ui.notifications.NotificationsVM;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private ActivityNotificationsBinding activityNotificationsBinding;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar.toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        progressDialog = new ProgressDialog(this);


        NavHeaderMainBinding _bind = DataBindingUtil.inflate(getLayoutInflater(), R.layout.nav_header_main, binding.navView, false);
        binding.navView.addHeaderView(_bind.getRoot());
        _bind.setUser(getCurrentUser());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        //navigationView.getHeaderView(0).get
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_personal_info, R.id.nav_credentials, R.id.nav_log_out)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(NotificationsVM.class);
    }

    @Override
    public NotificationsVM getRelatedViewModel() {
        return (NotificationsVM) super.getRelatedViewModel();
    }

    public NotificationsVM getRelatedNotificationsView(){
        return (NotificationsVM) super.getRelatedViewModel();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() != null) {
            switch (item.getTitle().toString()) {
                case "Notificações":
                    getRelatedNotificationsView().getRelatedActivity().nextActivity(NotificationsActivity.class);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else{
            return super.onOptionsItemSelected(item);
    }
    }
}
