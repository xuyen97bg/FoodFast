package com.example.foodfast.ui.home.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodfast.R;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.databinding.ItemFoodBinding;
import com.example.foodfast.utils.Utils;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.VH> {
    Context context;
    private final List<Food> listFood;
    public interface IclickDetail {
        void detailFood(Food food);
    }

    private final IclickDetail clickItem;

    public FoodAdapter(Context context, List<Food> listFood, IclickDetail clickItem) {
        this.context = context;
        this.clickItem = clickItem;
        this.listFood = listFood;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemFoodBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bindFood(listFood.get(position), clickItem, context);
        holder.itemView.setOnClickListener(v -> clickItem.detailFood(listFood.get(position)));
    }

    @Override
    public int getItemCount() {
        return listFood.size();
    }

    class VH extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding;
        public VH(@NonNull ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bindFood(Food food, IclickDetail clickItem, Context context) {
            binding.price.setText(Html.fromHtml(String.format("<del>$ %s</del>"
                            , Utils.convertMoney(food.getPrice()))
                    , Html.FROM_HTML_MODE_COMPACT));
            int price = food.getPrice() * (100 - food.getDiscount()) / 100;
            binding.priceDiscount.setText(String.format("$ %s", Utils.convertMoney(price)));
            binding.title.setText(food.getTitle());
            binding.ingredient.setText(food.getIngredient());
            Glide.with(context).load(food.getUrlImage()).into(binding.image);
            if (food.getDiscount() > 0)
                binding.discount.setText("Giảm "+food.getDiscount() + "%");
            else {
                binding.discount.setBackgroundResource(R.color.white);
                binding.price.setText("");
            }
        }
    }
}

