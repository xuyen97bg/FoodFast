package com.example.foodfast.ui.history.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodfast.R;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.databinding.ItemOrderBinding;
import com.example.foodfast.ui.cart.adapter.CartAdapter;
import com.example.foodfast.utils.Utils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
    Context context;
    private final List<Cart> list;
    private final List<Food> listFood;

    public HistoryAdapter(Context context, List<Cart> carts, List<Food> foods) {
        this.context = context;
        this.list = carts;
        this.listFood = foods;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemOrderBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Cart history = list.get(position);
        ItemOrderAdapter adapter = new ItemOrderAdapter(context, history, listFood);
        holder.binding.address.setText(history.getAddress());
        holder.binding.createAt.setText(Utils.convertDateType1(history.getCreateAt()));
        holder.binding.recyclerView.setAdapter(adapter);
        holder.binding.updateAt.setText(Utils.convertDateType1(history.getUpdateAt()));
        holder.binding.total.setText(String.format("Tổng tiền: $ %s", Utils.convertMoney(history.getTotal())));
        switch (history.getStatus()) {
            //Chờ xác nhận
            case 0: {
                holder.binding.layoutTime.setVisibility(View.GONE);
                holder.binding.status.setText(R.string.waiting_confirm);
                holder.binding.status.setBackgroundResource(R.drawable.bg_waiting_confirm);
                break;
            }
            //Đã xác nhận
            case 1: {
                holder.binding.updateAtTitle.setText(R.string.time_comfirm);
                holder.binding.layoutTime.setVisibility(View.VISIBLE);
                holder.binding.status.setText(R.string.confirmed);
                holder.binding.status.setBackgroundResource(R.drawable.bg_confirmed);
                break;
            }
            //Đã giao đơn
            case 2: {
                holder.binding.updateAtTitle.setText(R.string.time_deliver);
                holder.binding.layoutTime.setVisibility(View.VISIBLE);
                holder.binding.status.setText(R.string.delivered);
                holder.binding.status.setBackgroundResource(R.drawable.bg_delivered);
                break;
            }
        }
        holder.binding.hide.setVisibility(View.GONE);
        holder.binding.recyclerView.setVisibility(View.GONE);

        holder.binding.hide.setOnClickListener(v -> {
            holder.binding.hide.setVisibility(View.GONE);
            holder.binding.recyclerView.setVisibility(View.GONE);
            holder.binding.show.setVisibility(View.VISIBLE);
        });
        holder.binding.show.setOnClickListener(v -> {
            holder.binding.hide.setVisibility(View.VISIBLE);
            holder.binding.recyclerView.setVisibility(View.VISIBLE);
            holder.binding.show.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        public final ItemOrderBinding binding;

        public VH(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


