package com.example.foodfast.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodfast.data.model.Account;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.model.Category;
import com.example.foodfast.data.model.Food;
import com.example.foodfast.data.network.DatabaseTableName;
import com.example.foodfast.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public MutableLiveData<List<Food>> listFoodLive = new MutableLiveData<>();
    public MutableLiveData<AsyncState> state = new MutableLiveData<>();
    public MutableLiveData<Food> foodDetail = new MutableLiveData<>();
    public MutableLiveData<Account> account = new MutableLiveData<>();

    public HomeViewModel() {
        state.setValue(AsyncState.UNINITIALIZED);
    }

    // Food
    public void all() {
        state.setValue(AsyncState.LOADING);
        List<Food> listFood = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.FOOD_REFERENCE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (anyFoodExists(dataSnapshot)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Food food = snapshot.getValue(Food.class);
                        food.setId(snapshot.getKey());
                        listFood.add(food);
                    }
                    listFoodLive.setValue(listFood);
                    state.setValue(AsyncState.SUCCESS);
                } else {
                    state.setValue(AsyncState.FAIL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void filterFood(Context context, Category category) {
        state.setValue(AsyncState.LOADING);
        List<Food> listFood = new ArrayList<>();
        if (Utils.isNetworkAvailable(context)) {
            FirebaseDatabase.getInstance()
                    .getReference(DatabaseTableName.FOOD_REFERENCE)
                    .orderByChild(DatabaseTableName.FIELD_CATEGORY_ID)
                    .equalTo(category.getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (anyFoodExists(dataSnapshot)) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Food food = snapshot.getValue(Food.class);
                                    food.setId(snapshot.getKey());
                                    listFood.add(food);
                                }
                                listFoodLive.setValue(listFood);
                                state.setValue(AsyncState.SUCCESS);
                            } else {
                                state.setValue(AsyncState.FAIL);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        } else {
            state.setValue(AsyncState.FAIL);
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchFood(Context context, String keyword){
        state.setValue(AsyncState.LOADING);
        List<Food> listFood = new ArrayList<>();
        if (Utils.isNetworkAvailable(context)) {
            FirebaseDatabase.getInstance().getReference(DatabaseTableName.FOOD_REFERENCE)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (anyFoodExists(dataSnapshot)) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Food food = snapshot.getValue(Food.class);
                            if (food.getTitle().contains(keyword)){
                                food.setId(snapshot.getKey());
                                listFood.add(food);
                            }
                        }
                        listFoodLive.setValue(listFood);
                        state.setValue(AsyncState.SUCCESS);
                    } else {
                        state.setValue(AsyncState.FAIL);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            state.setValue(AsyncState.FAIL);
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    public void add(final Context context, final int discount, final String description, final int price, final String title, final String categoryId, final Uri coverPhotoURL, String ingredient) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.FOOD_REFERENCE);
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final String uniqueKey = databaseReference.push().getKey();
                    storageReference = FirebaseStorage.getInstance().getReference().child(uniqueKey).child(DatabaseTableName.FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    storageReference.putFile(coverPhotoURL).continueWithTask(taskSnapshot -> {
                        if (!taskSnapshot.isSuccessful()) {
                            throw taskSnapshot.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadURi = task.getResult();
                            Food food = new Food(uniqueKey, title, price, discount, description, categoryId, downloadURi.toString(), ingredient);
                            databaseReference.child(uniqueKey).setValue(food);
                        }
                        state.setValue(AsyncState.SUCCESS);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            state.setValue(AsyncState.FAIL);
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    // edit book according to the id
    public void edit(final Context context, final String id, final int discount, final String description, final int price, final String title, final String categoryId, final String ingredient, final Uri coverPhotoURL) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.FOOD_REFERENCE).child(id);
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(id).child(DatabaseTableName.FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    storageReference.putFile(coverPhotoURL).continueWithTask(taskSnapshot -> {
                        if (!taskSnapshot.isSuccessful()) {
                            throw taskSnapshot.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadURi = task.getResult();
                            Food food = new Food(id, title, price, discount, description, categoryId, downloadURi.toString(), ingredient);
                            databaseReference.setValue(food);
                        }
                        state.setValue(AsyncState.SUCCESS);
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    // remove
    public boolean remove(final Context context, final Food food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            //state.setValue(AsyncState.LOADING);
            databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.FOOD_REFERENCE).child(food.getId());
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getUrlImage());
            storageReference.delete().addOnSuccessListener(aVoid -> {
                databaseReference.removeValue();
                state.setValue(AsyncState.SUCCESS);
                dialog.dismiss();
            });
        }).setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss()).create().show();
        return true;
    }
    //Account add
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

    public void getAcc(final String id) {
        state.setValue(AsyncState.LOADING);
        FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (anyFoodExists(snapshot)) {
                    state.setValue(AsyncState.SUCCESS);
                    account.setValue(snapshot.getValue(Account.class));
                } else {
                    state.setValue(AsyncState.FAIL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<List<Account>> listAccountLive = new MutableLiveData<>();

    //all account
    public void allAccount() {
        state.setValue(AsyncState.LOADING);
        List<Account> listAccount = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (anyFoodExists(dataSnapshot)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Account account = snapshot.getValue(Account.class);
                        account.setId(snapshot.getKey());
                        listAccount.add(account);
                    }
                    listAccountLive.setValue(listAccount);
                    state.setValue(AsyncState.SUCCESS);
                } else {
                    state.setValue(AsyncState.FAIL);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    // edit acount
    public void edit(final Context context, Account account, final Uri coverPhotoURL) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE).child(account.getId());
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(account.getId()).child(DatabaseTableName.FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    storageReference.putFile(coverPhotoURL).continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) taskSnapshot -> {
                        if (!taskSnapshot.isSuccessful()) {
                            throw taskSnapshot.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadURi = task.getResult();
                            account.setImageUrl(downloadURi.toString());
                            databaseReference.setValue(account);
                        }
                        state.setValue(AsyncState.SUCCESS);
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    // remove account
    public boolean remove(final Context context, final Account account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            //state.setValue(AsyncState.LOADING);
            databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.ACCOUNT_REFERENCE).child(account.getId());
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(account.getImageUrl());
            storageReference.delete().addOnSuccessListener(aVoid -> {
                databaseReference.removeValue();
                state.setValue(AsyncState.SUCCESS);
                all();
                dialog.dismiss();
            });
        }).setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss()).create().show();
        return true;
    }
    //Category
    public MutableLiveData<List<Category>> listCategoryLive = new MutableLiveData<>();
    public void add(final Context context, Category category, Uri uriImage) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CATEGORY_REFERENCE);
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
                            category.setId(uniqueKey);
                            category.setImageUrl(downloadURi.toString());
                            databaseReference.child(uniqueKey).setValue(category);
                        }
                        state.setValue(AsyncState.SUCCESS);
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    public void allCategory() {
        state.setValue(AsyncState.LOADING);
        List<Category> listCategory = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CATEGORY_REFERENCE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (anyFoodExists(dataSnapshot)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Category category = snapshot.getValue(Category.class);
                        category.setId(snapshot.getKey());
                        listCategory.add(category);
                    }
                    listCategoryLive.setValue(listCategory);
                    state.setValue(AsyncState.SUCCESS);
                } else {
                    state.setValue(AsyncState.FAIL);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    // edit
    public void edit(final Context context, Category category, final Uri coverPhotoURL) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CATEGORY_REFERENCE).child(category.getId());
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(category.getId()).child(DatabaseTableName.FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    storageReference.putFile(coverPhotoURL).continueWithTask(taskSnapshot -> {
                        if (!taskSnapshot.isSuccessful()) {
                            throw taskSnapshot.getException();
                        }
                        return storageReference.getDownloadUrl();
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                category.setImageUrl(downloadURi.toString());
                                databaseReference.setValue(category);
                            }
                            state.setValue(AsyncState.SUCCESS);
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    // remove
    public boolean remove(final Context context, final Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            //state.setValue(AsyncState.LOADING);
            databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CATEGORY_REFERENCE).child(category.getId());
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(category.getImageUrl());
            storageReference.delete().addOnSuccessListener(aVoid -> {
                databaseReference.removeValue();
                state.setValue(AsyncState.SUCCESS);
                dialog.dismiss();
            });
        }).setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss()).create().show();
        return true;
    }
    //CART table
    public MutableLiveData<Cart> cart = new MutableLiveData<>();
    public void getCart(Context context, String id){
        state.setValue(AsyncState.LOADING);
        createCart(context, id);
        FirebaseDatabase.getInstance().getReference(DatabaseTableName.CART_REFERENCE)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (anyFoodExists(dataSnapshot)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cart mCart = snapshot.getValue(Cart.class);
                        mCart.setId(snapshot.getKey());
                        cart.setValue(mCart);
                    }
                    state.setValue(AsyncState.SUCCESS);
                } else {
                    state.setValue(AsyncState.FAIL);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void createCart(final Context context, String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CART_REFERENCE).child(id);
        if (Utils.isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0) {
                        long time = Calendar.getInstance().getTimeInMillis();
                        Cart cart = new Cart(id, time, time);
                        databaseReference.setValue(cart);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    public void edit(Cart cart) {
        cart.setUpdateAt(Calendar.getInstance().getTimeInMillis());
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.CART_REFERENCE).child(cart.getId());
        databaseReference.setValue(cart);
    }
    //HISTORY
    public void createOrder(Cart cart) {
        FirebaseDatabase.getInstance().getReference(DatabaseTableName.CART_REFERENCE).child(cart.getId()).removeValue();
        this.cart.setValue(null);
        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.HISTORY_REFERENCE).child(cart.getId());
        String uniqueKey = databaseReference.push().getKey();
        databaseReference.child(uniqueKey).setValue(cart);
    }
    // check if there is any food in the database
    public boolean anyFoodExists(DataSnapshot dataSnapshot) {
        return dataSnapshot.getChildrenCount() > 0;
    }
}