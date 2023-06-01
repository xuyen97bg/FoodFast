package com.example.foodfast.ui.cart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.CartItem;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.databinding.ItemCartBinding;
import com.example.foodfast.utils.Utils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<VH>{
    Context context;
    private final Cart cart;
    private final List<Food> listFood;

    public IClick clickItem;
    public  interface IClick{
        void edit(Cart cart, int pos);
    }

    public CartAdapter(Context context, Cart cart, List<Food> listFood, IClick iClick) {
        this.clickItem = iClick;
        this.context = context;
        this.cart = cart;
        this.listFood = listFood;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemCartBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if(listFood.isEmpty()){
            return;
        }
        CartItem item = cart.getCartItems().get(position);
        Food food = null;
        for (int i = 0; i < listFood.size(); i++) {
            if(listFood.get(i).getId().equals(item.getIdFood())){
                food = listFood.get(i);
                break;
            }
        }
        if (food != null){
            Glide.with(context).load(food.getUrlImage()).into(holder.binding.image);
            holder.binding.amount.setText(String.format("%s",item.getAmountBuy()));
            holder.binding.name.setText(food.getTitle());
            holder.binding.price.setText(String.format("$ %s", Utils.convertMoney(item.getPrice())));
            holder.binding.btnMinus.setOnClickListener(v -> {
                if (item.getAmountBuy()>1){
                    item.minus();
                    clickItem.edit(cart,position);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
                    builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                        cart.getCartItems().remove(position);
                        clickItem.edit(cart,position);
                        dialog.dismiss();
                    }).setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss()).create().show();
                }
            });
            holder.binding.btnPlus.setOnClickListener(v -> {
                    item.plus();
                    clickItem.edit(cart,position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return cart.getCartItems().size();
    }
}

class VH extends RecyclerView.ViewHolder {
    public final ItemCartBinding binding;

    public VH(@NonNull ItemCartBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

