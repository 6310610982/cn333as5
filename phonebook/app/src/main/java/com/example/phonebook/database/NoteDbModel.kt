package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean,
) {
    companion object {
        val DEFAULT_NOTES = listOf(
            NoteDbModel(1, "Margot", "098 765 432", false, false, 1, false),
            NoteDbModel(2, "Samantha", "012 345 678", false, false, 2, false),
            NoteDbModel(3, "Matcha", "034 567 231", false, false, 3, false),
            NoteDbModel(4, "Cate", "0567 891 012", false, false, 4, false),
            NoteDbModel(5, "James", "012 345 567", false, false, 1, false),
            NoteDbModel(6, "Gigi", "091 231 123", false, false, 2, false),
            NoteDbModel(7, "Taylor", "084 564 564", false, false, 3, false),
            NoteDbModel(8, "Timothee", "087 657 765", false, false, 4, false),
            NoteDbModel(9, "Robert", "092 234 342", false, false, 1, false),
            NoteDbModel(10, "Harry", "099 999 999", false, false, 2, false),
            NoteDbModel(11, "Dylan", "088 888 888", true, false, 1, false),
            NoteDbModel(12, "Hero", "098 989 989", true, false, 2, false)
        )
    }
}
