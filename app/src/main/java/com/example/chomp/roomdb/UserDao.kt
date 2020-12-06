package com.example.chomp.roomdb

import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.PrimaryKey
import androidx.room.Query

@Dao
interface UserDao {
    @Query("insert into user(uid,city) values(:uid,:city)")
    fun insertUser(uid: String, city: String)

    @Query("update user set city=:city where uid=:uid")
    fun updateUser(uid: String, city: String)

    @Query("select count(*) as count from user where uid=:uid")
    fun getUserCity(uid: String): Int

    @Query("select city from user where uid=:uid")
    fun getUserCityName(uid: String): String
}