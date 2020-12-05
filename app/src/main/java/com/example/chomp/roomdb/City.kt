package com.example.chomp.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(val name: String?, @PrimaryKey(autoGenerate = false) val id: Int? = null)