package com.example.chomp.roomdb

import android.app.Application
import androidx.lifecycle.LiveData

class CityRepository(application: Application) {

    private var cityDao: CityDao
    private var userDao: UserDao
    private var allCities: LiveData<List<String>>

    private val database = CityDatabase.getInstance(application)

    init {
        cityDao = database.cityDao()
        userDao = database.userDao()
        allCities = cityDao.getAllCities()
    }

    fun insertUser(uid: String, city: String) {
        userDao.insertUser(uid,city)
    }

    fun updateUser(uid: String, city: String) {
        userDao.updateUser(uid,city)
    }

    fun getUserCity(uid: String): Int {
        return userDao.getUserCity(uid)
    }

    fun getUserCityName(uid: String): String {
        return userDao.getUserCityName(uid)
    }

    fun getCityId(name: String): Int {
        return cityDao.getCityId(name)
    }

    fun getCityCount(name: String): Int {
        return cityDao.getCityCount(name)
    }

    fun getAllCities(): LiveData<List<String>> {
        return allCities
    }
}