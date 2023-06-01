package com.example.foodfast.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.FragmentCartBinding;
import com.example.foodfast.ui.cart.adapter.CartAdapter;
import com.example.foodfast.ui.home.HomeViewModel;
import com.example.foodfast.utils.Utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    Cart cart = null;
    List<Food> foods = null;
    HomeViewModel viewModel;
    String id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        //Get all Foods
        viewModel.all();
        viewModel.listFoodLive.observe(getViewLifecycleOwner(), listFood -> foods = listFood);
        //Get Cart
        SessionManager sessionManager = new SessionManager(requireContext());
        id = sessionManager.fetchId();
        if (id == null) {
            //Yêu cầu đăng nhập
            sessionManager.saveId("-NWYZidgj1Tqe4zs80aE");
            id = "-NWYZidgj1Tqe4zs80aE";
        }
        viewModel.getCart(getContext(), id);
        viewModel.cart.observe(getViewLifecycleOwner(), cart1 -> cart = cart1);
        //Setup recycle view
        viewModel.state.observe(getViewLifecycleOwner(),asyncState -> {
           if(asyncState == AsyncState.SUCCESS){
               if (cart != null && foods != null) {
                  if(cart.getCartItems().isEmpty()){
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.empty.setVisibility(View.VISIBLE);
                        binding.total.setText("0 $");
                        binding.feeShip.setText("0 $");
                        binding.discount.setText("0 $");
                      binding.subTotal.setText("0 $");
                  }else {
                      binding.recyclerView.setVisibility(View.VISIBLE);
                      binding.empty.setVisibility(View.GONE);
                      //Set up recycle view
                      CartAdapter adapter = new CartAdapter(getContext(), cart, foods,
                              (cart, pos) -> viewModel.edit(cart));
                      binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                      binding.recyclerView.setAdapter(adapter);
                      AtomicInteger tong = new AtomicInteger();
                      cart.getCartItems().forEach(e -> tong.addAndGet(e.getPrice() * e.getAmountBuy()));
                      binding.total.setText(String.format("%s $", Utils.convertMoney(tong.get())));
                      binding.feeShip.setText("10.000 $");
                      binding.discount.setText("-10.000 $");
                      binding.subTotal.setText(String.format("%s $", Utils.convertMoney(tong.get())));
                  }
               }
           }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}