package com.example.foodfast.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodfast.R;
import com.example.foodfast.databinding.ItemCategoryBinding;
import com.example.foodfast.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<VH2> {
    private Context context;
    private List<Category> list;
    private IclickCategory iclick;
    public int posSelected = 0;

    public CategoryAdapter(Context context, List<Category> list, IclickCategory iclick) {
        this.context = context;
        this.list = list;
        this.iclick = iclick;
    }

    @NonNull
    @Override
    public VH2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH2(ItemCategoryBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH2 holder, int position) {
        list.get(position).setSelected(position == posSelected);
        holder.bindData(list.get(position), context);
        holder.itemView.setOnClickListener(v -> {
            posSelected = holder.getAdapterPosition();
            //send event click out handle
            iclick.iClick(list.get(holder.getAdapterPosition()));
            this.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class VH2 extends RecyclerView.ViewHolder {
    private ItemCategoryBinding binding;

    public VH2(@NonNull ItemCategoryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindData(Category category, Context context) {
        Glide.with(context).load(category.getImageUrl()).into(binding.img);
        binding.name.setText(category.getName());
        binding.nameSelected.setText(category.getName());
        if (category.isSelected()) {
            binding.name.setVisibility(View.GONE);
            binding.nameSelected.setVisibility(View.VISIBLE);
            binding.layoutItem.setBackgroundResource(R.drawable.bg_category_selected);
        }else {
            binding.name.setVisibility(View.VISIBLE);
            binding.nameSelected.setVisibility(View.GONE);
            binding.layoutItem.setBackgroundResource(R.drawable.bg_category_no_select);
        }
    }
}