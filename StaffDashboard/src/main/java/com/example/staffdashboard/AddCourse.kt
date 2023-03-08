package com.example.staffdashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.staffdashboard.databinding.FragmentAddCourseBinding
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddCourse : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var courseBinding: FragmentAddCourseBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        courseBinding = FragmentAddCourseBinding.inflate(layoutInflater, container, false)

        database = FirebaseDatabase.getInstance().getReference("Courses")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseList = ArrayList<String>()
                for (course in snapshot.children) {
                    courseList.add(course.key.toString()+" "+course.child("courseTitle").value.toString())
                }

                val courseAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    courseList
                )
                courseBinding.lvCourses.adapter = courseAdapter

                courseBinding.svCourses.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        courseBinding.svCourses.clearFocus()
                        if (courseList.contains(query)) {
                            courseAdapter.filter.filter(query)
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        courseAdapter.filter.filter(newText)
                        return false
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return courseBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCourse().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}