package com.example.todolistfirebase

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Tag

class TaskViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tasks")
    private val _listState = MutableLiveData<ListState>(ListState.EmptyList)
    val listState: LiveData<ListState> = _listState

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it)
                    }
                }
                _listState.postValue(ListState.UpdatedList(list = taskList))
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun addTask(task: Task) {
        val taskId = database.push().key
        taskId?.let {
            database.child(it).setValue(task)
                .addOnSuccessListener {
                    Log.d(TAG, "Task added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding task: $e")

                }
        }
    }

    fun removeTask(task: Task) {
        task.id?.let { database.child(it).removeValue() }
    }

    fun updateTask(task: Task) {
        task.id?.let { database.child(it).setValue(task) }
    }

    sealed class ListState {
        object EmptyList : ListState()
        class UpdatedList(val list: List<Task>) : ListState()
    }
}

data class Task(
    val id: String? = null,
    val title: String,
    val description: String,
    var completed: Boolean = false
){
    constructor(): this("","","", true)
}