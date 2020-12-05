package com.example.chomp.roomdb

import android.app.Application
import androidx.lifecycle.LiveData

class CityRepository(application: Application) {

    private var cityDao: CityDao
    private var allCities: LiveData<List<String>>

    private val database = CityDatabase.getInstance(application)

    init {
        cityDao = database.cityDao()
        allCities = cityDao.getAllCities()
    }

    fun insert(city: City) {
        cityDao.insert(city)
    }

    fun update(city: City) {
        cityDao.update(city)
    }

    fun delete(city: City) {
        cityDao.delete(city)
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