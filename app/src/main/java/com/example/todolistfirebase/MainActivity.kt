package com.example.todolistfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("tasks")

        adapter = TaskAdapter(viewModel)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        adapter.attachSwipeToDelete(binding.recyclerView)

        viewModel.listState.observe(this) { state ->
            when (state) {
                is TaskViewModel.ListState.EmptyList -> {

                }

                is TaskViewModel.ListState.UpdatedList -> {
                    adapter.submitList(state.list)
                }
            }
        }

        binding.btnAddTask.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            if (title.isNotBlank()) {
                val task = Task(title = title, description = description)
                viewModel.addTask(task)
                binding.etTitle.text.clear()
                binding.etDescription.text.clear()
            }
        }
    }
}