package com.example.foodfast.ui.history;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Cart;
import com.example.foodfast.data.network.DatabaseTableName;
import com.example.foodfast.data.network.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryViewModel extends ViewModel {
    private DatabaseReference databaseReference;
    public MutableLiveData<AsyncState> state = new MutableLiveData<>();
    public MutableLiveData<List<Cart>> listHistory = new MutableLiveData<>();
    public HistoryViewModel() {
        state.setValue(AsyncState.UNINITIALIZED);
    }
    public void all(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String id = sessionManager.fetchId();
        if(id != null){
            state.setValue(AsyncState.LOADING);
            List<Cart> histories = new ArrayList<>();
            databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseTableName.HISTORY_REFERENCE).child(id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (hasData(dataSnapshot)) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Cart history = snapshot.getValue(Cart.class);
                            history.setId(snapshot.getKey());
                            histories.add(history);
                        }
                        Collections.reverse(histories);
                        listHistory.setValue(histories);
                        state.setValue(AsyncState.SUCCESS);
                    } else {
                        state.setValue(AsyncState.FAIL);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            listHistory.setValue(null);
            state.setValue(AsyncState.FAIL);
        }
    }
    public boolean hasData(DataSnapshot dataSnapshot) {
        return dataSnapshot.getChildrenCount() > 0;
    }
}
