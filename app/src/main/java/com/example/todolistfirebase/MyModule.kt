package com.example.todolistfirebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyModule {

    @Singleton
    @Provides
    fun provideDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("tasks")
    }
}