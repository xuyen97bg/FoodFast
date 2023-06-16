package com.example.foodfast.ui.security;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodfast.R;
import com.example.foodfast.databinding.ActivitySecurityBinding;

public class SecurityActivity extends AppCompatActivity {
    ActivitySecurityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUiAndData();
    }

    private void initUiAndData() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment()).commit();
    }

    public void addFragmentRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new RegisterFragment())
                .addToBackStack("Register").commit();
    }

}