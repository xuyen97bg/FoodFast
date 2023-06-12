package com.example.foodfast.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodfast.MainActivity;
import com.example.foodfast.R;
import com.example.foodfast.databinding.FragmentFinishOrderBinding;

public class FinishOrderFragment extends Fragment{
    private FragmentFinishOrderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinishOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v ->
            ((MainActivity)getActivity())
                    .navigateTo(R.id.action_finishOrderFragment_to_navigation_home)
        );
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}