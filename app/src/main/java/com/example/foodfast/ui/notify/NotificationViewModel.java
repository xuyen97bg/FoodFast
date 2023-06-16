package com.example.foodfast.ui.notify;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodfast.R;
import com.example.foodfast.data.model.AsyncState;
import com.example.foodfast.data.model.Notify;
import com.example.foodfast.data.network.SessionManager;
import com.example.foodfast.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    public static String NOTIFICATION_REFERENCE = "Notification";
    public MutableLiveData<AsyncState> state = new MutableLiveData<>();
    public MutableLiveData<List<Notify>> listNotification = new MutableLiveData<>();

    public NotificationViewModel() {
        state.setValue(AsyncState.UNINITIALIZED);
        indexUnread.setValue(0);
    }

    public MutableLiveData<Integer> indexUnread = new MutableLiveData();


    public void allNotification(Context context) {
        state.setValue(AsyncState.LOADING);
        String id = new SessionManager(context).fetchId();
        if (id != null) {
            FirebaseDatabase.getInstance().getReference(NOTIFICATION_REFERENCE).child(id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Utils.isNetworkAvailable(context))
                                if (hasData(dataSnapshot)) {
                                    List<Notify> notifies = new ArrayList<>();
                                    int countNotificationUnread = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Notify notification = snapshot.getValue(Notify.class);
                                        notification.setId(snapshot.getKey());
                                        notifies.add(notification);
                                        countNotificationUnread += (notification.getStatus() == 0 ? 1 : 0);
                                    }
                                    indexUnread.setValue(countNotificationUnread);
                                    Collections.reverse(notifies);
                                    listNotification.setValue(notifies);
                                    state.setValue(AsyncState.SUCCESS);
                                } else {
                                    state.setValue(AsyncState.FAIL);
                                }
                            else {
                                state.setValue(AsyncState.FAIL);
                                Toast.makeText(context, R.string.lose_internet, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }

    }

    public void setReadingNotification(Notify notification,String id) {
        notification.setStatus(1);
        FirebaseDatabase.getInstance()
                .getReference(NOTIFICATION_REFERENCE).child(id)
                .child(notification.getId())
                .setValue(notification);
    }

    public void addNotification(Context context, Notify notification, String id) {
        if (Utils.isNetworkAvailable(context)) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(NOTIFICATION_REFERENCE).child(id);
            String uniqueKey = database.push().getKey();
            notification.setId(uniqueKey);
            database.child(uniqueKey).setValue(notification);
        } else {
            Toast.makeText(context, R.string.lose_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public void createNotification(Context context, TypeNotification type) {
        String id = new SessionManager(context).fetchId();
        this.addNotification(context,
                new Notify(id, System.currentTimeMillis(),type.getTitle(context), type.getType()),id);
    }
    public boolean hasData(DataSnapshot dataSnapshot) {
        return dataSnapshot.getChildrenCount() > 0;
    }
}
