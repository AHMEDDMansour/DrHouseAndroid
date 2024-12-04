package com.example.appdrhouseandroid.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellbeingdata")
data class WellBeingData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // Unique identifier for the user
    val steps: Int,
    val water: Float,
    val sleepHours: Int,
    val coffecups: Int,
    val workout :Int,
    )
