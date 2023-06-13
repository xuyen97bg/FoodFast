package com.example.foodfast.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodfast.data.model.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("select * from `user.tb`")
    List<User> getList();
    @Insert
    void insert(User user);
}
