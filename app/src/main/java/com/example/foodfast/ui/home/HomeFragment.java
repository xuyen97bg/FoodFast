package com.example.foodfast.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodfast.databinding.FragmentHomeBinding;
import com.example.foodfast.model.Food;
import com.example.foodfast.ui.home.adapter.FoodAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    HomeViewModel viewModel;

    List<Food> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        FoodAdapter adapter = new FoodAdapter(getContext(),list, food -> {

        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerView.setAdapter(adapter);
        viewModel.all();
        viewModel.listFoodLive.observe(getViewLifecycleOwner(),foods -> {
            list.clear();
            list.addAll(foods);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}