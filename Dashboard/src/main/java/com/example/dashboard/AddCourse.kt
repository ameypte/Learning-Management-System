package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.dashboard.databinding.FragmentAddCourseBinding
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddCourse : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var addCourseBinding: FragmentAddCourseBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

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
        addCourseBinding = FragmentAddCourseBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        database = FirebaseDatabase.getInstance().getReference("Courses")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var courseList = ArrayList<String>()
                for (course in snapshot.children) {
                    courseList.add(course.key.toString()+" "+course.child("courseTitle").value.toString())
                }

                val courseAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    courseList
                )

                addCourseBinding.lvCourses.adapter = courseAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return addCourseBinding.root
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