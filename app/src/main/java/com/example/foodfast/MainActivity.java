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
import com.google.android.material.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {
    NavController navController;
    private ActivityMainBinding binding;
    BadgeDrawable badge;
    HomeViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //badge on top cart- bottom nav
        //Get Cart
        SessionManager sessionManager = new SessionManager(this);
        String id = sessionManager.fetchId();
        if(id!=null){
            viewModel.getCart(this, id);
        }
        badge = binding.navView.getOrCreateBadge(R.id.navigation_cart);
        viewModel.cart.observe(this,cart -> {
            if(cart.getCartItems().isEmpty()){
                badge.setVisible(false);
            }else {
                badge.setVisible(true);
                badge.setNumber(cart.getCartItems().size());
            }
        });
    }

    public void navigateTo(int frangmentRes){
        navController.navigate(frangmentRes);
    }

}
//login