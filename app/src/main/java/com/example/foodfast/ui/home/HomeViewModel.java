package com.example.foodfast.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.foodfast.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
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
    public static final String FOOD_REFERENCE = "Food";
    public static final String ACCOUNT_REFERENCE = "Account";
    public static final String CATEGORY_REFERENCE = "Category";
    public static final String CART_REFERENCE = "Cart";

    public static final String CART_ITEM_REFERENCE = "Cart";
    public static final String FOOD_STORAGE_PATH = "cover_photo/";

    public HomeViewModel() {
        state.setValue(AsyncState.UNINITIALIZED);
    }
    // get all the books from the database
    public void all() {
        state.setValue(AsyncState.LOADING);
        List<Food> listFood = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE);
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
    public void add(final Context context, final int discount, final String description, final int price, final String title, final Category category, final Uri coverPhotoURL, String ingredient) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE);
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final String uniqueKey = databaseReference.push().getKey();
                    storageReference = FirebaseStorage.getInstance().getReference().child(uniqueKey).child(FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(coverPhotoURL);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                Food food = new Food(uniqueKey, title, price, discount, description, category, downloadURi.toString(),ingredient);
                                databaseReference.child(uniqueKey).setValue(food);
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
    // edit book according to the id
    public void edit(final Context context, final String id, final int discount, final String description, final int price, final String title, final Category category, final String ingredient, final Uri coverPhotoURL) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE).child(id);
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(id).child(FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(coverPhotoURL);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                Food food = new Food(id,title, price, discount, description, category, downloadURi.toString(),ingredient);
                                databaseReference.setValue(food);
                            }
                            state.setValue(AsyncState.SUCCESS);
                            all();
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
    public boolean remove(final Context context, final Food food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //state.setValue(AsyncState.LOADING);
                databaseReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE).child(food.getId());
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getUrlImage());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.removeValue();
                        state.setValue(AsyncState.SUCCESS);
                        all();
                        dialog.dismiss();
                    }
                });
            }
        }).setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
        return true;
    }
    //Account add
    public void add(final Context context, Account account, Uri uriImage) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(ACCOUNT_REFERENCE);
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final String uniqueKey = databaseReference.push().getKey();
                    storageReference = FirebaseStorage.getInstance().getReference().child(uniqueKey).child(FOOD_STORAGE_PATH + uriImage.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(uriImage);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                account.setId(uniqueKey);
                                account.setImageUrl(downloadURi.toString());
                                databaseReference.child(uniqueKey).setValue(account);
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
    public MutableLiveData<List<Account>> listAccountLive = new MutableLiveData<>();
    //all account
    public void allAccount() {
        state.setValue(AsyncState.LOADING);
        List<Account> listAccount = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(ACCOUNT_REFERENCE);
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
        databaseReference = FirebaseDatabase.getInstance().getReference(ACCOUNT_REFERENCE).child(account.getId());
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(account.getId()).child(FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(coverPhotoURL);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                account.setImageUrl(downloadURi.toString());
                                databaseReference.setValue(account);
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
    // remove account
    public boolean remove(final Context context, final Account account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có chắc chắn muốn xóa món ăn?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //state.setValue(AsyncState.LOADING);
                databaseReference = FirebaseDatabase.getInstance().getReference(ACCOUNT_REFERENCE).child(account.getId());
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(account.getImageUrl());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.removeValue();
                        state.setValue(AsyncState.SUCCESS);
                        all();
                        dialog.dismiss();
                    }
                });
            }
        }).setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
        return true;
    }
    //Category
    public MutableLiveData<List<Category>> listCategoryLive = new MutableLiveData<>();
    public void add(final Context context, Category category, Uri uriImage) {
        state.setValue(AsyncState.LOADING);
        Utils.requestPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE);
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final String uniqueKey = databaseReference.push().getKey();
                    storageReference = FirebaseStorage.getInstance().getReference().child(uniqueKey).child(FOOD_STORAGE_PATH + uriImage.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(uriImage);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURi = task.getResult();
                                category.setId(uniqueKey);
                                category.setImageUrl(downloadURi.toString());
                                databaseReference.child(uniqueKey).setValue(category);
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
    public void allCategory() {
        state.setValue(AsyncState.LOADING);
        List<Category> listCategory = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE);
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
        databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE).child(category.getId());
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    storageReference = FirebaseStorage.getInstance().getReference().child(category.getId()).child(FOOD_STORAGE_PATH + coverPhotoURL.getLastPathSegment());
                    StorageTask storageTask = storageReference.putFile(coverPhotoURL);
                    Task<Uri> uriTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> taskSnapshot) throws Exception {
                            if (!taskSnapshot.isSuccessful()) {
                                throw taskSnapshot.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
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
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //state.setValue(AsyncState.LOADING);
                databaseReference = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE).child(category.getId());
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(category.getImageUrl());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.removeValue();
                        state.setValue(AsyncState.SUCCESS);
                        all();
                        dialog.dismiss();
                    }
                });
            }
        }).setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
        return true;
    }

    //CART table
    public void createCart(final Context context, String id) {
        state.setValue(AsyncState.LOADING);
        databaseReference = FirebaseDatabase.getInstance().getReference(CART_REFERENCE).child(id);
        if (isNetworkAvailable(context)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(anyFoodExists(snapshot)){
                        if(snapshot.getChildrenCount() == 0){
                            long time = Calendar.getInstance().getTimeInMillis();
                            Cart cart = new Cart(id,time,time);
                            databaseReference.setValue(cart);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(context, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }
    public MutableLiveData<Cart> cart = new MutableLiveData<>();
    public void getCart(Context context, String id){
        createCart(context, id);
        databaseReference = FirebaseDatabase.getInstance().getReference(CART_REFERENCE);
        databaseReference.addValueEventListener(new ValueEventListener() {
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
    public void edit(Cart cart){
        databaseReference = FirebaseDatabase.getInstance().getReference(CART_REFERENCE).child(cart.getId());
        databaseReference.setValue(cart);
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // check if there is any food in the database
    public boolean anyFoodExists(DataSnapshot dataSnapshot) {
        return dataSnapshot.getChildrenCount() > 0;
    }
}