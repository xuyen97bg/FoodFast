package com.example.foodfast.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.databinding.FragmentHistoryBinding;
import com.example.foodfast.ui.history.adapter.HistoryAdapter;
import com.example.foodfast.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private List<Food> listFood = new ArrayList<>();
    private List<Cart> listCart = new ArrayList<>();
    HistoryAdapter adapter;
    private FragmentHistoryBinding binding;
    HomeViewModel homeViewModel;
    HistoryViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(HistoryViewModel.class);
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        binding.empty.setVisibility(View.VISIBLE);
        adapter = new HistoryAdapter(getContext(), listCart, listFood);
        binding.recyclerView.setAdapter(adapter);
        viewModel.all(getContext());
        homeViewModel.listFoodLive.observe(getViewLifecycleOwner(), foods -> {
            listFood.clear();
            listFood.addAll(foods);
        });
        viewModel.listHistory.observe(getViewLifecycleOwner(), carts -> {
            listCart.clear();
            if(carts != null){
                listCart.addAll(carts);
            }
            if (listCart.isEmpty()){
                binding.empty.setVisibility(View.VISIBLE);
            }else {
                binding.empty.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}