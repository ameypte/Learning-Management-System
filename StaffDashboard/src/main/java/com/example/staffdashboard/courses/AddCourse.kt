package com.example.staffdashboard.courses

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.staffdashboard.R
import com.example.staffdashboard.databinding.FragmentAddCourseBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class AddCourse : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var courseBinding: FragmentAddCourseBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

                courseBinding.svCourses.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
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
            // subscribe to course notifications
            subscribeToTopic(courseCode)
            replaceFragment(Courses())
        }
        return courseBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCourse().apply {
                arguments = Bundle().apply {
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

    private fun subscribeToTopic(s: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(s)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to $s"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to $s"
                }
                Log.d("FCM", msg)
            }
    }
}