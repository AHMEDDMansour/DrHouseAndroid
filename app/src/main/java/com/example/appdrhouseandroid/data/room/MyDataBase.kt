package com.example.appdrhouseandroid.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appdrhouseandroid.data.room.dao.WellBeingDao
import com.example.appdrhouseandroid.data.room.entities.WellBeingData

@Database(entities = [WellBeingData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wellBeingDao(): WellBeingDao

    companion object{

        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext,
                    AppDatabase::class.java,"house_db")
                    .allowMainThreadQueries()
                    .build()

            return instance!!
        }

    }
}
