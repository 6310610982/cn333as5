package com.example.phonebook.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.domain.model.NoteModel
import com.example.phonebook.ui.components.NoteColor
import com.example.phonebook.util.fromHex
import com.example.phonebook.viewmodel.MainViewModel
import com.example.phonebook.routing.PhoneNumberRouter
import com.example.phonebook.routing.Screen
import com.example.phonebook.R
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.domain.model.NEW_NOTE_ID


import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SaveNoteScreen(viewModel: MainViewModel) {
    val noteEntry by viewModel.noteEntry.observeAsState(NoteModel())

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveNoteToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhoneNumberRouter.navigateTo(Screen.Notes)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
            SaveNoteTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { PhoneNumberRouter.navigateTo(Screen.Notes) },
                onSaveNoteClick = { viewModel.saveNote(noteEntry) },
                onOpenTagPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeleteNoteClick = {
                    moveNoteToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                TagPicker(
                    tags = tags,
                    onTagSelect = { tag ->
                        viewModel.onNoteEntryChange(noteEntry.copy(tag = tag))
                    }
                )
            }
        ) {
            SaveNoteContent(
                note = noteEntry,
                onNoteChange = { updateNoteEntry ->
                    viewModel.onNoteEntryChange(updateNoteEntry)
                }
            )
        }

        if (moveNoteToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    moveNoteToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move note to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this note to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.moveNoteToTrash(noteEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        moveNoteToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenTagPickerClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Note",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenTagPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeleteNoteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SaveNoteContent(
    note: NoteModel,
    onNoteChange: (NoteModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Name",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone Number",
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))
            }
        )

        val canBeCheckedOff: Boolean = note.isCheckedOff != null

        NoteCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null

                onNoteChange.invoke(note.copy(isCheckedOff = isCheckedOff))
            }
        )

        PickedTag(tag = note.tag)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can contact be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked Tag: " + tag.name,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(tag.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun TagPicker(
    tags: List<TagModel>,
    onTagSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tags.size) { itemIndex ->
                val tag = tags[itemIndex]
                TagItem(
                    tag = tag,
                    onColorSelect = onTagSelect
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tag: TagModel,
    onColorSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(tag)
                }
            )
    ) {
        NoteColor(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(tag.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = tag.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun TagItemPreview() {
    TagItem(TagModel.DEFAULT) {}
}

@Preview
@Composable
fun TagPickerPreview() {
    TagPicker(
        tags = listOf(
            TagModel.DEFAULT,
            TagModel.DEFAULT,
            TagModel.DEFAULT
        )
    ) { }
}

@Preview
@Composable
fun PickedTagPreview() {
    PickedTag(TagModel.DEFAULT)
}