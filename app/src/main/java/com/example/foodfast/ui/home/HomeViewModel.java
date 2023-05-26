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

import com.example.foodfast.model.AsyncState;
import com.example.foodfast.model.Category;
import com.example.foodfast.model.Food;
import com.example.foodfast.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class HomeViewModel extends ViewModel {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    public MutableLiveData<List<Food>> listFoodLive = new MutableLiveData<>();
    private final List<Food> listFood = new ArrayList<>();
    public MutableLiveData<AsyncState> state = new MutableLiveData<>();
    public static final String FOOD_REFERENCE = "Food";
    public static final String FOOD_STORAGE_PATH = "cover_photo/";

    public HomeViewModel() {
        state.setValue(AsyncState.UNINITIALIZED);
    }

    public void add(final Context context, final int discount, final String description, final int price, final String title, final Category category, final Uri coverPhotoURL) {
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
                                Food food = new Food(uniqueKey, title, price, discount, description, category, downloadURi.toString());
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

    // check if there is any food in the database
    public boolean anyFoodExists(DataSnapshot dataSnapshot) {
        return dataSnapshot.getChildrenCount() > 0;
    }

    // get all the books from the database
    public void all() {
        state.setValue(AsyncState.LOADING);
        databaseReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (anyFoodExists(dataSnapshot)) {
                    listFood.clear();
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

    // edit book according to the id
    public void edit(final Context context, final String id, final int discount, final String description, final int price, final String title, final Category category, final Uri coverPhotoURL) {
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
                                Food food = new Food(title, price, discount, description, category, downloadURi.toString());
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

    // remove book from the database
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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}