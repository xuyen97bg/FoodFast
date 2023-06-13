package com.example.foodfast.ui.notify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodfast.R;
import com.example.foodfast.data.model.Notify;
import com.example.foodfast.databinding.ItemNotifitionBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {
    Context context;
    List<Notify> list;

    IClick iClick;
    public interface IClick{
        void iclick(Notify notify);
    }
    public NotificationAdapter(Context context, List<Notify> list, IClick click) {
        this.context = context;
        this.list = list;
        this.iClick = click;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemNotifitionBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Notify notifition = list.get(position);
        holder.binding.title.setText(notifition.getTitle());
        String date;
        long DeltaTime = System.currentTimeMillis() - notifition.getCreateAt();
        if (DeltaTime < 3600000) {
            date = String.format("%s phút trước", DeltaTime / 60000);
        } else if (DeltaTime < 86400000) {
            date = String.format("%s giờ trước", DeltaTime / 3600000);
        } else if (DeltaTime < 604800000) {
            date = String.format("%s ngày trước", DeltaTime / 86400000);
        } else{
            date = String.format("%s tuần trước", DeltaTime / 604800000);
        }
        // 60000 = 1 minute, 3600000 = 1 hour, 86400000 = 1 day, 604800000 = 7 day
        holder.binding.date.setText(date);
        holder.itemView.setOnClickListener(v -> iClick.iclick(notifition));
        if(notifition.getStatus() ==1){
            holder.binding.getRoot().setBackgroundResource(R.drawable.bg_item_notifition_read);
        }else {
            holder.binding.getRoot().setBackgroundResource(R.drawable.bg_item_notifition_unread);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        public final ItemNotifitionBinding binding;
        public VH(@NonNull ItemNotifitionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
