package com.example.chomp.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CityDao {

    @Query("select id from cities where name=:name")
    fun getCityId(name: String): Int

    @Query("select count(*) as count from cities where name=:name")
    fun getCityCount(name: String): Int

    @Query("select name from cities")
    fun getAllCities(): LiveData<List<String>>
}