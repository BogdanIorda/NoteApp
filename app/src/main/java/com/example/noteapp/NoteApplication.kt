package com.example.noteapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NoteApplication : Application() {
}

//The Trigger for Hilt, first thing that it happens when the app Icon is tapped by the user
