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
import com.example.foodfast.ui.notify.NotificationViewModel;
import com.google.android.material.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {
    NavController navController;
    private ActivityMainBinding binding;
    BadgeDrawable badge;
    BadgeDrawable badgeNotification;
    HomeViewModel viewModel;
    NotificationViewModel notificationViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //badge on top cart- bottom nav
        //Get Cart
        SessionManager sessionManager = new SessionManager(this);
        String id = sessionManager.fetchId();
        if (!id.isEmpty()) {
            viewModel.getCart(this, id);
        }
        badge = binding.navView.getOrCreateBadge(R.id.navigation_cart);
        viewModel.cart.observe(this, cart -> {
            badge.setNumber(cart.getCartItems().size());
            badge.setVisible(!cart.getCartItems().isEmpty());
        });
        //Get Notification
        notificationViewModel.allNotification(this);
        badgeNotification = binding.navView.getOrCreateBadge(R.id.navigation_notifications);
        notificationViewModel.indexUnread.observe(this, integer -> {
            badgeNotification.setNumber(integer);
            badgeNotification.setVisible(integer != 0);
        });
    }

    public void navigateTo(int frangmentRes){
        navController.navigate(frangmentRes);
    }

}
//login