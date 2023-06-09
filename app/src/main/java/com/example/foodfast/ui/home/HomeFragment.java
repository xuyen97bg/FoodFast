package com.example.foodfast.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.foodfast.MainActivity;
import com.example.foodfast.R;
import com.example.foodfast.databinding.FragmentHomeBinding;
import com.example.foodfast.data.model.Category;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.ui.home.adapter.CategoryAdapter;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        //Slide show
        List<SlideModel> imageList = new ArrayList<>() ;// Create image list
        imageList.add(new SlideModel(R.drawable.banner_1, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.banner_2, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.banner_3, ScaleTypes.CENTER_CROP));
        binding.imageSlider.setImageList(imageList);

        viewModel.allCategory();
        List<Category> listCategory = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(),listCategory,category -> {
            //handle load food
        });
        binding.recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        binding.recyclerCategory.setAdapter(categoryAdapter);
        viewModel.listCategoryLive.observe(getViewLifecycleOwner(),categories -> {
            listCategory.clear();
            listCategory.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
        });

        FoodAdapter adapter = new FoodAdapter(getContext(),list, food -> {
            Log.d("Food HOme", food.getTitle());
            viewModel.foodDetail.setValue(food);
            //Chuyển fragment
            ((MainActivity)getActivity()).navigateTo(R.id.action_navigation_home_to_foodDetailFragment);
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerView.setAdapter(adapter);
        viewModel.all();
        viewModel.listFoodLive.observe(getViewLifecycleOwner(),foods -> {
            list.clear();
            list.addAll(foods);
            adapter.notifyDataSetChanged();
        });

        binding.avatar.setOnClickListener(v ->
                ((MainActivity)getActivity())
                        .navigateTo(R.id.action_navigation_home_to_dashboardFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}