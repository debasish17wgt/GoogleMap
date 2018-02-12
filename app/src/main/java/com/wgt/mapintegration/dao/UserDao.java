package com.wgt.mapintegration.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wgt.mapintegration.model.UserModel;

import java.util.List;

/**
 * Created by root on 10/2/18.
 */

@Dao
public interface UserDao {
    @Insert
    long addUser(UserModel user);

    @Query("SELECT * from UserModel where email = :email")
    UserModel getUserByEmail(String email);

    @Query("SELECT * FROM UserModel")
    List<UserModel> getAllUsers();
}
