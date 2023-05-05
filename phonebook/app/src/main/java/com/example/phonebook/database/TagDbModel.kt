package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hex") val hex: String,
    @ColumnInfo(name = "name") val name: String
) {
    companion object {
        val DEFAULT_TAGS = listOf(
            TagDbModel(1, "#FFFFFF", "Mobile"),
            TagDbModel(2, "#E57373", "Emergency"),
            TagDbModel(3, "#F06292", "Home"),
            TagDbModel(4, "#CE93D8", "Work"),
        )
        val DEFAULT_TAG = DEFAULT_TAGS[0]
    }
}