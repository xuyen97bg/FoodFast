package com.example.foodfast.ui.security;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.network.DatabaseTableName;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SecurityViewModel extends ViewModel {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public MutableLiveData<AsyncState> state = new MutableLiveData<>();
    public MutableLiveData<AsyncState> checkUsernameState = new MutableLiveData<>();
    public MutableLiveData<AsyncState> loginState = new MutableLiveData<>();

    public void login(Context context, String username, String password) {
        loginState.setValue(AsyncState.LOADING);
        if (Utils.isNetworkAvailable(context)) {
            FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE).orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (Utils.hasData(dataSnapshot)) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Account account = snapshot.getValue(Account.class);
                            if (account.getPassword().equals(password)) {
                                loginState.setValue(AsyncState.SUCCESS);
                                new SessionManager(context).saveId(snapshot.getKey());
                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                loginState.setValue(AsyncState.FAIL);
                                Toast.makeText(context, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    } else {
                        loginState.setValue(AsyncState.FAIL);
                        Toast.makeText(context, "Tên đăng nhập không chính xác", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            loginState.setValue(AsyncState.FAIL);
            Toast.makeText(context, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void add(final Context context, Account account, Uri uriImage) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE);
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final String uniqueKey = databaseReference.push().getKey();
                    storageReference = FirebaseStorage.getInstance().getReference().child(uniqueKey).child(DatabaseTableName.FOOD_STORAGE_PATH + uriImage.getLastPathSegment());
                    storageReference.putFile(uriImage).continueWithTask(taskSnapshot -> {
                        if (!taskSnapshot.isSuccessful()) {
                            throw taskSnapshot.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadURi = task.getResult();
                            account.setId(uniqueKey);
                            account.setImageUrl(downloadURi.toString());
                            databaseReference.child(uniqueKey).setValue(account);
                        }
                        state.setValue(AsyncState.SUCCESS);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkUsername(Context context,String username){
        checkUsernameState.setValue(AsyncState.LOADING);
        if (Utils.isNetworkAvailable(context)) {
            FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE)
                    .orderByChild("username").equalTo(username)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (Utils.hasData(dataSnapshot)) {
                        Toast.makeText(context, "Tên đăng nhập trùng lặp", Toast.LENGTH_SHORT).show();
                        checkUsernameState.setValue(AsyncState.FAIL);
                    } else {
                        checkUsernameState.setValue(AsyncState.SUCCESS);
                        Toast.makeText(context, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(context, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
            checkUsernameState.setValue(AsyncState.FAIL);
        }
    }
}
