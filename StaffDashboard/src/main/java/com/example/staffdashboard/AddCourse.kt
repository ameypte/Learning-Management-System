package com.example.staffdashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.example.staffdashboard.databinding.FragmentAddCourseBinding
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddCourse : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var courseBinding: FragmentAddCourseBinding
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
        courseBinding = FragmentAddCourseBinding.inflate(layoutInflater, container, false)

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

        courseBinding.lvCourses.setOnItemClickListener { parent, view, position, id ->
            val course = parent.getItemAtPosition(position).toString()
            val courseCode = course.split(" ")[0]
            val loggedStaffDepartment = sharedPreferences.getString("loggedStaffDepartment", "")
            val loggedStaffPhone = sharedPreferences.getString("loggedStaffPhone", "")
            database = FirebaseDatabase.getInstance().getReference("Departments")
                .child(loggedStaffDepartment.toString())
                .child("Staff")
                .child(loggedStaffPhone.toString())
                .child("coursesTeach")
            database.child(courseCode).setValue("")
            replaceFragment(Courses())
        }
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
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}