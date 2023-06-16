package com.example.foodfast.ui.notify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodfast.data.model.Notify;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.FragmentNotificationsBinding;
import com.example.foodfast.ui.notify.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private final List<Notify> list = new ArrayList<>();
    private NotificationAdapter adapter;
    String id;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUiAndData();
    }
    NotificationViewModel viewModel;
    private void initUiAndData() {
        id = new SessionManager(getContext()).fetchId();
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        //SetUp recycleview
        adapter = new NotificationAdapter(getContext(), list, notify -> {
            viewModel.setReadingNotification(notify,id);
        });
        binding.recyclerView.setAdapter(adapter);
        viewModel.listNotification.observe(getViewLifecycleOwner(), notifies -> {
            list.clear();
            if(id != null){
                list.addAll(notifies);
            }
            if (list.isEmpty()){
                binding.empty.setVisibility(View.VISIBLE);
            }else {
                binding.empty.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.allNotification(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
