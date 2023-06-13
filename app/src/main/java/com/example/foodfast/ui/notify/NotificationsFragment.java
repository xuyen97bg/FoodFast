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
import com.example.foodfast.databinding.FragmentNotificationsBinding;
import com.example.foodfast.ui.notify.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private final List<Notify> list = new ArrayList<>();
    private NotificationAdapter adapter;

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
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        //SetUp recycleview
        adapter = new NotificationAdapter(getContext(), list, viewModel::setReadingNotification);
        binding.recyclerView.setAdapter(adapter);
        viewModel.allNotification(getContext());
        viewModel.listNotification.observe(getViewLifecycleOwner(), notifies -> {
            list.clear();
            list.addAll(notifies);
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
