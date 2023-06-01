package com.example.foodfast.ui.notify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.databinding.FragmentNotificationsBinding;
import com.example.foodfast.ui.home.HomeViewModel;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    HomeViewModel viewModel ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
