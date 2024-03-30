package com.example.todolistfirebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val database: DatabaseReference) : ViewModel() {
    private val _listState = MutableLiveData<ListState>(ListState.EmptyList)
    val listState: LiveData<ListState> = _listState

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val id = taskSnapshot.key
                    val title = taskSnapshot.child("title").getValue(String::class.java) ?: ""
                    val description = taskSnapshot.child("description").getValue(String::class.java) ?: ""
                    val completed = taskSnapshot.child("completed").getValue(Boolean::class.java) ?: false
                    val task = Task(id, title, description, completed)
                    taskList.add(task)
                }
                _listState.postValue(ListState.UpdatedList(list = taskList))
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun addTask(task: Task) {
        val taskId = database.push().key
        taskId?.let {
            database.child(it).setValue(task)


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
    val title: String = "",
    val description: String ="",
    var completed: Boolean = false
)