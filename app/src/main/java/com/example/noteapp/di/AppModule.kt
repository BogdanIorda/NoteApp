package com.example.noteapp.di

import android.content.Context
import androidx.room.Room
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.data.NoteDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    // !! (at Compile Time) !!
    fun provideAppDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context, NoteDatabase::class.java, "notes_db"
        ).fallbackToDestructiveMigration(false).build()
    // It is running the fully functioning NoteDatabaseDao_Impl, but it's not awake
    // This builds the entire Database connection (the House). Inside that house,
    // the NoteDatabaseDao_Impl robot is sitting on the couch, waiting.

    @Singleton
    @Provides
    // !! (at Run Time) !!
    fun provideNotesDao(noteDatabase: NoteDatabase): NoteDatabaseDao = noteDatabase.noteDao()
    // It is returning the fully functioning NoteDatabaseDao_Impl (it is awake now, it's running)
    // This doesn't "run" the robot's commands (like insert or delete). Instead,
    // it simply fetches the robot off the couch and hands it to Hilt.


}