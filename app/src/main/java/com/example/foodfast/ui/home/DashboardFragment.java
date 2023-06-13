package com.example.foodfast.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodfast.databinding.DialogDatepickerBinding;
import com.example.foodfast.databinding.FragmentDashboardBinding;
import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Category;
import com.example.foodfast.utils.Utils;

import java.util.Calendar;

import gun0912.tedimagepicker.builder.TedImagePicker;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Uri coverPhotoURL;
    HomeViewModel viewModel ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.urlImage.setOnClickListener(view ->  pickImage());
        binding.btnSubmit.setOnClickListener(view -> submit());
        viewModel.state.observe(getViewLifecycleOwner(), asyncState -> {
            switch (asyncState) {
                case LOADING: {
                    Toast.makeText(getContext(), "Đang thêm", Toast.LENGTH_SHORT).show();
                }
                case SUCCESS: {
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    viewModel.state.setValue(AsyncState.UNINITIALIZED);
                }
            }
        });

        binding.btnSubmitCate.setOnClickListener(v -> {
            String name = binding.nameCate.getText().toString();
            Category category = new Category(name);
            viewModel.add(getContext(), category, coverPhotoURL);
        });

        binding.birthday.setOnClickListener(v -> createDialogDatePicker(getContext()));
        binding.btnSubmitAcc.setOnClickListener(v -> sumitAccount());
        return root;
    }

    private void submit() {
        String title = binding.title.getText().toString();
        int price = Integer.parseInt(binding.price.getText().toString());
        int discount = Integer.parseInt(binding.discount.getText().toString());
        String description = binding.description.getText().toString();
        viewModel.add(getContext(),discount,description,price,title,"-NWWb5is-8kkMVFmkvqH",coverPhotoURL,"100 gr meat + onion + tomato + Lettuce cheese");
    }

    private void pickImage() {
        Utils.requestPermissions();
        TedImagePicker.with(getActivity()).start(uri -> {
            Log.d("URL image", uri.toString());
            Glide.with(this).load(uri).into(binding.urlImage);
            coverPhotoURL = uri;
        });
    }

    Calendar cal = Calendar.getInstance();

    private void createDialogDatePicker(Context context) {
        Dialog dialog = new Dialog(context);
        DialogDatepickerBinding bindingDialog = DialogDatepickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(bindingDialog.getRoot());
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        bindingDialog.datePicker.updateDate(2000, 1, 1);
        bindingDialog.btnAccept.setOnClickListener(v -> {

            cal.set(bindingDialog.datePicker.getYear(),
                    bindingDialog.datePicker.getMonth(),
                    bindingDialog.datePicker.getDayOfMonth());
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void sumitAccount() {
        String username = binding.username.getText().toString();
        String password = binding.password.getText().toString();
        String firstname = binding.fistname.getText().toString();
        String lastname = binding.lastname.getText().toString();
        long birthday = cal.getTimeInMillis();
        String phone = binding.phoneNumber.getText().toString();
        String aadress = binding.address.getText().toString();
        int role = binding.male.isChecked() ? 1 : 0;
        Account account = new Account(username,password,firstname,lastname,birthday,phone,aadress,role);
        viewModel.add(getContext(),account,coverPhotoURL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}