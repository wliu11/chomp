package com.example.chomp.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [City::class,User::class], version = 1)
abstract class CityDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun userDao(): UserDao

    companion object {
        private var instance: CityDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): CityDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(ctx, CityDatabase::class.java, "restaurants.db")
                    .createFromAsset("restaurants.db")
                    .allowMainThreadQueries()
                    .build()

            return instance!!

        }
    }
}