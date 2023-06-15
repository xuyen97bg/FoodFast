package com.example.foodfast.ui.information;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.network.DatabaseTableName;
import com.example.foodfast.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InformationViewModel extends ViewModel {
    public MutableLiveData<Account> account = new  MutableLiveData();
    public void getAccount(Context context, String id) {
        if (Utils.isNetworkAvailable(context)) {
            FirebaseDatabase.getInstance()
                    .getReference(DatabaseTableName.ACCOUNT_REFERENCE)
                    .child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(Utils.hasData(snapshot)){
                                account.setValue(snapshot.getValue(Account.class));
                            }else {
                                Toast.makeText(context, "Không có thông tin tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else {
            Toast.makeText(context, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
        }
    }
}
