package com.example.phonebook.database

import android.content.Context
import androidx.room.*
import com.example.phonebook.database.NoteDbModel
import com.example.phonebook.database.TagDbModel

@Database(entities = [NoteDbModel::class, TagDbModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "phonebook-maker-database"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
            }

            return instance
        }
    }
}