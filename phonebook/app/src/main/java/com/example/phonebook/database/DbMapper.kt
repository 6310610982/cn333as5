package com.example.phonebook.database


import com.example.phonebook.domain.model.NEW_NOTE_ID
import com.example.phonebook.domain.model.NoteModel
import com.example.phonebook.domain.model.TagModel


class DbMapper {
    // Create list of ContactModels by pairing each note with a tag
    fun mapNotes(
        contactDbModels: List<NoteDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<NoteModel> = contactDbModels.map {
        val tagDbModel = tagDbModels[it.tagId]
            ?: throw RuntimeException("Tag for tagId: ${it.tagId} was not found. Make sure that all tags are passed to this method")

        mapNote(it, tagDbModel)
    }

    // convert NoteDbModel to NoteModel
    fun mapNote(noteDbModel: NoteDbModel, tagDbModel: TagDbModel): NoteModel {
        val tag = mapTag(tagDbModel)
        val isCheckedOff = with(noteDbModel) { if (canBeCheckedOff) isCheckedOff else null }
        return with(noteDbModel) { NoteModel(id, title, content, isCheckedOff, tag) }
    }

    // convert list of TagDdModels to list of TagModels
    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    // convert TagDbModel to TagModel
    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, name, hex) }

    // convert NoteModel back to NoteDbModel
    fun mapDbNote(note: NoteModel): NoteDbModel =
        with(note) {
            val canBeCheckedOff = isCheckedOff != null
            val isCheckedOff = isCheckedOff ?: false
            if (id == NEW_NOTE_ID)
                NoteDbModel(
                    title = title,
                    content = content,
                    canBeCheckedOff = canBeCheckedOff,
                    isCheckedOff = isCheckedOff,
                    tagId = tag.id,
                    isInTrash = false
                )
            else
                NoteDbModel(id, title, content, canBeCheckedOff, isCheckedOff, tag.id, false)
        }
}