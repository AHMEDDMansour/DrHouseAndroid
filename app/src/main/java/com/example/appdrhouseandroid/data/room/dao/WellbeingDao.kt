package com.example.appdrhouseandroid.data.room.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appdrhouseandroid.data.room.entities.WellBeingData

@Dao
interface WellBeingDao {
    @Insert()
    suspend fun insert(data: WellBeingData)

    @Query("SELECT * FROM wellbeingdata WHERE userId = :userId")
    suspend fun getDataForUser(userId: String): List<WellBeingData>  // Correct return type


    @Query("DELETE FROM wellbeingdata WHERE userId = :userId")
    suspend fun deleteDataForUser(userId: String)
}
