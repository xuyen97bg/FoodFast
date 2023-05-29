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
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.CartItem;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.FragmentFoodDetailBinding;

import java.util.List;
import java.util.Objects;

public class FoodDetailFragment extends Fragment {
    private FragmentFoodDetailBinding binding;
    private int amount = 1;
    private Food food;

    private Cart cart;

    private CartItem cartItem;

    HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel =
                new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String id = sessionManager.fetchId();
        if(id == null){
            //Yêu cầu đăng nhập
            sessionManager.saveId("-NWYZidgj1Tqe4zs80aE");
            id = "-NWYZidgj1Tqe4zs80aE";
        }

        viewModel.cart.observe(getViewLifecycleOwner(),cart1 -> cart = cart1);

        viewModel.getCart(getContext(),id);

        viewModel.foodDetail.observe(getViewLifecycleOwner(), food -> {
            if (food.getTitle() != null) {
                this.food = food;
                binding.title.setText(food.getTitle());
                binding.describe.setText(food.getDescription());
                binding.price.setText(String.format("$ %s", food.getPrice() * (100 - food.getDiscount()) / 100));
                Glide.with(requireContext()).load(food.getUrlImage()).into(binding.image);
                //Create cart_item
                cartItem = new CartItem(food.getId(),0, food.getPrice()*(100- food.getDiscount())/100 );
            }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnMinus.setOnClickListener(v -> {
            if (amount >= 2) {
                amount--;
            }
            binding.amount.setText(amount + "");

        });

        binding.btnPlus.setOnClickListener(v -> binding.amount.setText(++amount + ""));
        binding.btnAdd.setOnClickListener(v -> {
            int index = asyncCartItem();
            if(index != -1){
                cart.getCartItems().get(index).setAmountBuy(amount + cart.getCartItems().get(index).getAmountBuy());
            }else {
                cartItem.setAmountBuy(amount);
                cart.getCartItems().add(cartItem);
            }
            viewModel.edit(cart);
            getActivity().onBackPressed();
        });
    }

    private int asyncCartItem() {
        for (int i = 0; i < cart.getCartItems().size(); i++) {
            if(cart.getCartItems().get(i).getIdFood().equals(cartItem.getIdFood())){
                this.cartItem = cart.getCartItems().get(i);
                return i;
            }
        }
        return -1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
