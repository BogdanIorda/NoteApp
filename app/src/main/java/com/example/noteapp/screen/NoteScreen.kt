package com.example.noteapp.screen

import android.R.attr.onClick
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.noteapp.R
import com.example.noteapp.components.NoteButton
import com.example.noteapp.components.NoteInputText
import com.example.noteapp.model.Note
import com.example.noteapp.util.formatDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    notes: List<Note>,
    onAddNote: (Note) -> Unit,
    onRemoveNote: (Note) -> Unit,
    onDeleteAllNotes: () -> Unit
) {
// rememberSaveable instead of remember so the state will not change back to default when the screen rotates
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var popupShowing by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(6.dp)
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(
                    onClick = {
                        popupShowing = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Trash Can Icon",
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.onPrimary)
        )
        //Content
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (notes.isNotEmpty()) {
                NoteInputText(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp),
                    text = searchQuery,
                    label = "Search Title",
                    onTextChange = {
                        searchQuery = it
                    }
                )
            }
            NoteInputText(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 8.dp
                    ),
                text = title,
                label = "Title",
                onTextChange = {
                    if (it.all { char ->
                            char.isLetter() || char.isWhitespace() || char.isDigit()
                        })
                        title = it
                },
            )
            NoteInputText(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 8.dp
                    ),
                text = description,
                label = "Add a note",
                onTextChange = {
                    if (it.all { char ->
                            char.isLetter() || char.isWhitespace() || char.isDigit() || char == '-'
                        })
                        description = it
                },
                maxLine = 3
            )
            if (title.isNotEmpty() || description.isNotEmpty()) {
                NoteButton(
                    modifier = Modifier
                        .padding(10.dp),
                    text = "Save",
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            onAddNote(Note(title = title, description = description))
                            title = ""
                            description = ""
                            Toast.makeText(
                                context,
                                "Note Added",
                                Toast.LENGTH_SHORT
                            ).show()
                            keyboardController?.hide()
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(),
                    shape = CircleShape
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(10.dp)
        )
        if (popupShowing) {
            AlertDialog(
                onDismissRequest = { popupShowing = false },
                title = { Text(text = "Are you sure?") },
                text = { Text(text = "All of your notes will be deleted!") },
                confirmButton = {
                    Button(onClick = {
                        onDeleteAllNotes()
                        Toast.makeText(
                            context,
                            "All Notes Deleted",
                            Toast.LENGTH_LONG
                        ).show()
                        popupShowing = false
                    }) { Text("Yes") }
                },
                dismissButton = {
                    Button(onClick = { popupShowing = false }) { Text("No") }
                }
            )
        }

        val searchList = notes.filter { it.title.startsWith(searchQuery, ignoreCase = true) }

        LazyColumn {
            items(searchList) { note ->
                NoteRow(
                    note = note,
                    onDeleteClicked = {
                        onRemoveNote(note)
                    }
                )
            }
        }
    }
}

@Composable
fun NoteRow(
    modifier: Modifier = Modifier,
    note: Note,
    onDeleteClicked: (Note) -> Unit,
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(topEnd = 35.dp, bottomStart = 35.dp))
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = note.description,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween

            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    text = formatDate(note.entryDate.time),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Left
                )

                Button(
                    modifier = Modifier
                        .padding(4.dp),
                    onClick = {
                        onDeleteClicked(note)
                        Toast.makeText(
                            context,
                            "Note Deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(Color(0xFFF65858)),
                    contentPadding = PaddingValues(0.dp),
                )
                {
                    Text(
                        text = "Delete",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
