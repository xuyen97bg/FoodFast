package com.example.foodfast.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.databinding.FragmentFoodDetailBinding;

public class FoodDetailFragment extends Fragment {
    private FragmentFoodDetailBinding binding;
    private int amount = 1;
    HomeViewModel viewModel ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel =
                new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        viewModel.foodDetail.observe(getViewLifecycleOwner(), food -> {
          if(food.getTitle() != null){
              binding.title.setText(food.getTitle());
              binding.describe.setText(food.getDescription());
              binding.price.setText( String.format("$ %s",food.getPrice()*(100- food.getDiscount())/100));
              Glide.with(requireContext()).load(food.getUrlImage()).into(binding.image);
          }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnMinus.setOnClickListener(v -> {
            if(amount >= 2){
                amount --;
            }
            binding.amount.setText(amount+"");
        });

        binding.btnPlus.setOnClickListener(v -> {
            binding.amount.setText(++amount+"");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
