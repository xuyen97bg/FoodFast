package com.example.foodfast.ui.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.databinding.FragmentLoginBinding;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    SecurityViewModel viewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(SecurityViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUiAndData();
    }

    private void initUiAndData() {
        binding.btnSignUp.setOnClickListener(v -> {
            ((SecurityActivity) Objects.requireNonNull(getActivity())).addFragmentRegister();
        });
        binding.btnBack.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        binding.btnSignIn.setOnClickListener(v -> {
            String username = Objects.requireNonNull(binding.username.getText()).toString();
            String password = Objects.requireNonNull(binding.password.getText()).toString();
            viewModel.login(getContext(),username,password);
        });

        viewModel.loginState.observe(getViewLifecycleOwner(),asyncState -> {
            switch (asyncState){
                case SUCCESS:
                    getActivity().finish();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
