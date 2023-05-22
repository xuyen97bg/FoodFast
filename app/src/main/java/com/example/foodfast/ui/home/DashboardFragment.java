package com.example.foodfast.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.databinding.FragmentDashboardBinding;
import com.example.foodfast.utils.Utils;

import gun0912.tedimagepicker.builder.TedImagePicker;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Uri coverPhotoURL;
    HomeViewModel viewModel ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.urlImage.setOnClickListener(view ->  pickImage());
        binding.btnSubmit.setOnClickListener(view -> submit());
        viewModel.state.observe(getViewLifecycleOwner(), asyncState -> {
            switch (asyncState) {
                case LOADING: {
                    Toast.makeText(getContext(), "Đang thêm", Toast.LENGTH_SHORT).show();
                }
                case SUCCESS: {
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private void submit() {
        String title = binding.title.getText().toString();
        int price = Integer.parseInt(binding.price.getText().toString());
        int discount = Integer.parseInt(binding.discount.getText().toString());
        String description = binding.description.getText().toString();
        int category = Integer.parseInt(binding.category.getText().toString());
        viewModel.add(getContext(),discount,description,price,title,category,coverPhotoURL);
    }

    private void pickImage() {
        Utils.requestPermissions();
        TedImagePicker.with(getActivity()).start(uri -> {
            Glide.with(this).load(uri).into(binding.urlImage);
            coverPhotoURL = uri;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}