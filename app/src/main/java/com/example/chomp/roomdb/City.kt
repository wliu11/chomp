package com.example.chomp.roomdb

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(val name: String?, @PrimaryKey(autoGenerate = false) val id: Int? = null)

@Entity(tableName = "user")
data class User(@NonNull @PrimaryKey(autoGenerate = false) val uid: String, val city: String?)