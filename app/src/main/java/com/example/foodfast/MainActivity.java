package com.example.foodfast;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.ActivityMainBinding;
import com.example.foodfast.ui.home.HomeViewModel;
import com.example.foodfast.ui.information.InformationViewModel;
import com.example.foodfast.ui.notify.NotificationViewModel;
import com.google.android.material.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {
    NavController navController;
    private ActivityMainBinding binding;
    BadgeDrawable badge;
    BadgeDrawable badgeNotification;
    HomeViewModel viewModel;
    NotificationViewModel notificationViewModel;
    InformationViewModel informationViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        informationViewModel = new ViewModelProvider(this).get(InformationViewModel.class);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //badge on top cart- bottom nav
        badge = binding.navView.getOrCreateBadge(R.id.navigation_cart);
        badge.setVisible(false);
        viewModel.cart.observe(this, cart -> {
            if (cart == null){
                badge.setVisible(false);
            }else {
                badge.setNumber(cart.getCartItems().size());
                badge.setVisible(!cart.getCartItems().isEmpty());
            }
        });
        badgeNotification = binding.navView.getOrCreateBadge(R.id.navigation_notifications);
        notificationViewModel.indexUnread.observe(this, integer -> {
            badgeNotification.setNumber(integer);
            badgeNotification.setVisible(integer != 0);
        });
    }

    public void navigateTo(int frangmentRes){
        navController.navigate(frangmentRes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager sessionManager = new SessionManager(this);
        String id = sessionManager.fetchId();
        if(id == null){
            badge.setVisible(false);
            badgeNotification.setVisible(false);
            notificationViewModel.listNotification.setValue(null);
            informationViewModel.account.setValue(null);
        }else {
            viewModel.getCart(this,id);
            //Get Notification
            notificationViewModel.allNotification(this);
            informationViewModel.getAccount(this,id);
        }
    }
}