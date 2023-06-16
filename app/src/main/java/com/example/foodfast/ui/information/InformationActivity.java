package com.example.foodfast.ui.information;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.ActivityInformationBinding;
import com.example.foodfast.ui.history.HistoryViewModel;
import com.example.foodfast.ui.home.HomeFragment;
import com.example.foodfast.ui.home.HomeViewModel;
import com.example.foodfast.ui.notify.NotificationViewModel;
import com.example.foodfast.utils.Utils;

public class InformationActivity extends AppCompatActivity {
    InformationViewModel viewModel;
    HomeViewModel homeViewModel;
    NotificationViewModel notificationViewModel;
    HistoryViewModel historyViewModel;
    boolean isFist = true;
    ActivityInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUiAndData();
    }

    private void initUiAndData() {
        String id = getIntent().getStringExtra(HomeFragment.ID);
        viewModel = new ViewModelProvider(this).get(InformationViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        viewModel.getAccount(this, id);
        viewModel.account.observe(this, account -> {
            if (isFist) {
                Glide.with(this).load(account.getImageUrl()).into(binding.imgAvt);
                binding.name.setText(String.format("%s %s", account.getLastName(), account.getFistName()));
                binding.address.setText(account.getAddress());
                binding.birthday.setText(Utils.convertDateType2(account.getBirthday()));
                binding.phone.setText(account.getPhoneNumber());
                binding.gender.setText(account.getGender() == 0 ? "Nữ" : "Nam");
                isFist = false;
            }
        });
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnLogOut.setOnClickListener(v -> {
            homeViewModel.cart.setValue(null);
            homeViewModel.account.setValue(null);
            notificationViewModel.listNotification.setValue(null);
            notificationViewModel.indexUnread.setValue(0);
            historyViewModel.listHistory.setValue(null);
            new SessionManager(this).clear();
            this.finish();
        });
    }
}