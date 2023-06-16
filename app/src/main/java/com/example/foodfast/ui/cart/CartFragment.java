package com.example.foodfast.ui.cart;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodfast.MainActivity;
import com.example.foodfast.R;
import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.DialogChooseAddressShipBinding;
import com.example.foodfast.databinding.FragmentCartBinding;
import com.example.foodfast.ui.cart.adapter.CartAdapter;
import com.example.foodfast.ui.home.HomeViewModel;
import com.example.foodfast.ui.notify.NotificationViewModel;
import com.example.foodfast.ui.notify.TypeNotification;
import com.example.foodfast.utils.Utils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    Cart cart = null;
    List<Food> foods = null;
    Account account = null;
    HomeViewModel viewModel;
    NotificationViewModel notificationViewModel;
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
        notificationViewModel = new ViewModelProvider(getActivity()).get(NotificationViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        //Get Cart
        SessionManager sessionManager = new SessionManager(requireContext());
        id = sessionManager.fetchId();
        if (id != null) {
            //Get all Foods
            viewModel.all();
            viewModel.listFoodLive.observe(getViewLifecycleOwner(), listFood -> foods = listFood);
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
                            cart.setTotal(tong.get());
                            binding.subTotal.setText(String.format("%s $", Utils.convertMoney(tong.get())));
                            binding.submit.setOnClickListener(v -> {
                                createDialogInputAddress();
                            });
                        }
                    }
                }
            });
        }else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
            binding.total.setText("0 $");
            binding.feeShip.setText("0 $");
            binding.discount.setText("0 $");
            binding.subTotal.setText("0 $");
        }

    }
    private void createDialogInputAddress() {
        //Get account
        viewModel.getAcc(id);
        Dialog dialog = new Dialog(getContext());
        DialogChooseAddressShipBinding bindingDialog = DialogChooseAddressShipBinding.inflate(getLayoutInflater());
        viewModel.account.observe(getViewLifecycleOwner(),account -> {
            if(account!= null){
                this.account = account;
                if(bindingDialog.your.isChecked()){
                    bindingDialog.address.setText(account.getAddress());
                }
            }
        });
        dialog.setContentView(bindingDialog.getRoot());
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
        bindingDialog.your.setOnClickListener(v1 -> {
            if(account != null){
                bindingDialog.address.setText(account.getAddress());
            }
            bindingDialog.address.setEnabled(false);
        });
        bindingDialog.orther.setOnClickListener(v1 -> {
            bindingDialog.address.setText("");
            bindingDialog.address.setEnabled(true);
            bindingDialog.address.setFocusable(true);
        });
        bindingDialog.cancel.setOnClickListener(v -> dialog.dismiss());
        bindingDialog.submit.setOnClickListener(v1 -> {
            String address = bindingDialog.address.getText().toString();
            if(address.isEmpty()){
                Toast.makeText(getContext(), "Bạn vui lòng nhập địa chỉ nhận hàng.", Toast.LENGTH_SHORT).show();
            }else {
                cart.setAddress(address);
                cart.setStatus(0);
                cart.setCreateAt(Calendar.getInstance().getTimeInMillis());
                cart.setUpdateAt(Calendar.getInstance().getTimeInMillis());
                viewModel.createOrder(cart);
                notificationViewModel.createNotification(getContext(), TypeNotification.ORDER);
                dialog.dismiss();
                ((MainActivity)getActivity()).navigateTo(R.id.action_navigation_cart_to_finishOrderFragment);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}