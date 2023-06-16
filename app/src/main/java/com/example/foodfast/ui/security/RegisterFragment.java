package com.example.foodfast.ui.security;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.databinding.DialogDatepickerBinding;
import com.example.foodfast.databinding.FragmentRegisterBinding;
import com.example.foodfast.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;

public class RegisterFragment extends Fragment {
    private Uri coverPhotoURL = null;
    private FragmentRegisterBinding binding;
    SecurityViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SecurityViewModel.class);
        initUiAndData();
    }

    private void initUiAndData() {
        binding.btnBack.setOnClickListener(v -> getActivity().onBackPressed());
        binding.btnSubmit.setOnClickListener(v -> submitAccount());
        binding.pickImage.setOnClickListener(v -> pickImage());
        viewModel.loginState.observe(getViewLifecycleOwner(), asyncState -> {
            if (asyncState == AsyncState.SUCCESS) {
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.checkUsernameState.observe(getViewLifecycleOwner(), asyncState -> {
            if (asyncState == AsyncState.SUCCESS) {
                Account account = new Account(username, password, firstname, lastname, birthday,
                        phone, address, gender, 0, coverPhotoURL.toString());
                viewModel.add(getContext(), account, coverPhotoURL);
                getActivity().onBackPressed();
            }
        });
        binding.layoutBirthday.setStartIconOnClickListener(v -> {
            createDialogDatePicker(getContext());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    String username, password, firstname, lastname, phone, address;
    int gender = 0;
    long birthday = 0;

    private void submitAccount() {
        username = Objects.requireNonNull(binding.username.getText()).toString();
        password = Objects.requireNonNull(binding.password.getText()).toString();
        firstname = Objects.requireNonNull(binding.name.getText()).toString();
        lastname = Objects.requireNonNull(binding.lastname.getText()).toString();
        birthday = 0;
        phone = Objects.requireNonNull(binding.phone.getText()).toString();
        address = Objects.requireNonNull(binding.address.getText()).toString();
        gender = binding.male.isChecked() ? 1 : 0;
        if (coverPhotoURL == null) {
            Toast.makeText(getContext(), "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastname.isEmpty()) {
            Toast.makeText(getContext(), "Họ trống\nVui lòng điền họ", Toast.LENGTH_SHORT).show();
            binding.lastname.setFocusable(true);
            return;
        }
        if (firstname.isEmpty()) {
            Toast.makeText(getContext(), "Tên trống\nVui lòng điền tên", Toast.LENGTH_SHORT).show();
            binding.name.setFocusable(true);
            return;
        }
        if (username.isEmpty()) {
            binding.username.setFocusable(true);
            Toast.makeText(getContext(), "Tên tài khoản trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            binding.password.setFocusable(true);
            Toast.makeText(getContext(), "Mật khẩu trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.birthday.getText().toString().isEmpty()) {
            binding.password.setFocusable(true);
            Toast.makeText(getContext(), "Ngày sinh trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty()) {
            binding.address.setFocusable(true);
            Toast.makeText(getContext(), "Địa chỉ trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            binding.phone.setFocusable(true);
            Toast.makeText(getContext(), "Số điện thoại trống", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String date = binding.birthday.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date2 = sdf.parse(date);
            birthday = date2.getTime();
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Ngày sinh không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.checkUsername(getContext(), username);
    }

    private void createDialogDatePicker(Context context) {
        Dialog dialog = new Dialog(context);
        DialogDatepickerBinding bindingDialog = DialogDatepickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(bindingDialog.getRoot());
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        bindingDialog.datePicker.updateDate(2000, 1, 1);
        bindingDialog.datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        bindingDialog.btnAccept.setOnClickListener(v -> {
            binding.birthday.setText(String.format("%02d/%02d/%s",
                    bindingDialog.datePicker.getDayOfMonth(),
                    bindingDialog.datePicker.getMonth() + 1,
                    bindingDialog.datePicker.getYear()));
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void pickImage() {
        Utils.requestPermissions();
        TedImagePicker.with(Objects.requireNonNull(getActivity())).start(uri -> {
            Log.d("URL image", uri.toString());
            Glide.with(this).load(uri).into(binding.avatar);
            coverPhotoURL = uri;
        });
    }
}
