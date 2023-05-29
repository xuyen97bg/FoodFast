package com.example.foodfast.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.foodfast.R;

public class SessionManager {
    private final String ID = "id";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }
    public void saveId(String id){
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString(ID,id);
       editor.apply();
    }
    public String fetchId(){
        return prefs.getString(ID,null);
    }
    public void clear(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
