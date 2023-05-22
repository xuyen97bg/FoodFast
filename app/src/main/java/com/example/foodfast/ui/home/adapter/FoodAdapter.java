package com.example.foodfast.ui.home.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodfast.databinding.ItemFoodBinding;
import com.example.foodfast.model.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<VH>{
    Context context;

    private List<Food> listFood;

    private IclickDetail clickItem;

    public FoodAdapter(Context context, List<Food> listFood, IclickDetail clickItem) {
        this.context = context;
        this.clickItem = clickItem;
        this.listFood = listFood;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemFoodBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bindFood(listFood.get(position), clickItem, context);
    }

    @Override
    public int getItemCount() {
        return listFood.size();
    }
}

class VH extends RecyclerView.ViewHolder {
    private ItemFoodBinding binding;

    public VH(@NonNull ItemFoodBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindFood(Food food, IclickDetail clickItem, Context context) {
        if (food == null)
            return;
        binding.tvPriceRoot.setText(food.getPrice() + "$");
        binding.tvTitle.setText(food.getTitle());
        Glide.with(context).load(food.getUrlImage()).into(binding.imgFood);
        if (food.getDiscount() != 0) {
            binding.tvDiscount.setVisibility(View.VISIBLE);
            binding.tvDiscount.setText("Giảm" + " " + food.getDiscount() + "%");
            binding.tvPriceDiscount.setVisibility(View.VISIBLE);
            binding.tvPriceDiscount.setText(food.getPrice() * (100 - food.getDiscount()) / 100 + "");
            binding.tvPriceRoot.setPaintFlags(binding.tvPriceRoot.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvPriceRoot.setTextColor(Color.parseColor("#696969"));
            binding.tvPriceDiscount.setTextSize(16);
            binding.tvPriceRoot.setTextSize(12);
            binding.tvPriceDiscount.setTextColor(Color.parseColor("#FF6347"));
        }

        binding.imgFood.setOnClickListener(view -> {
            clickItem.detailFood(food);
        });
    }
}

