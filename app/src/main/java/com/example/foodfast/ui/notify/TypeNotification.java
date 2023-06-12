package com.example.foodfast.ui.notify;

import android.content.Context;

import com.example.foodfast.R;

public enum TypeNotification {
    ORDER, COMFIRM, FINISH;

    public int getType() {
        switch (this) {
            case ORDER:
                return 0;
            case FINISH:
                return 1;
            case COMFIRM:
                return 2;
        }
        return -1;
    }
    public String getTitle(Context context){
        switch (this) {
            case ORDER:
                return  context.getString(R.string.order_success);
            case FINISH:
                return context.getString(R.string.order_comfirm);
            case COMFIRM:
                return context.getString(R.string.order_finish);
        }
        return "";
    }
}
