package com.example.staffdashboard.attendance

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import com.example.staffdashboard.R
import com.example.staffdashboard.courses.Courses
import com.example.staffdashboard.courses.PdfReader
import com.example.staffdashboard.databinding.FragmentAttendanceCourseBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AttendanceCourse : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAttendanceCourseBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase

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
        binding = FragmentAttendanceCourseBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(com.example.staffdashboard.R.string.login_preference_file_name),
            android.content.Context.MODE_PRIVATE
        )
        val dept = sharedPreferences.getString("loggedStaffDepartment", "Dept")
        val phone = sharedPreferences.getString("loggedStaffPhone", "Phone")


        database = FirebaseDatabase.getInstance()
        var ref = database.getReference("Departments").child(dept!!).child("Staff").child(phone!!)
            .child("coursesTeach")

        var courseCodeList = ArrayList<String>()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    courseCodeList.add(i.key.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        var courseList = ArrayList<String>()
        ref = database.getReference("Courses")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (courseCodeList.contains(i.key.toString())) {
                        courseList.add(i.key.toString() + " " + i.child("courseTitle").value.toString())
                    }
                }
                val courseAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    courseList
                )
                binding.lvCourses.adapter = courseAdapter

                binding.svCourses.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        binding.svCourses.clearFocus()
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
                TODO()
            }
        })

        binding.lvCourses.setOnItemClickListener { parent, view, position, id ->
            val course = parent.getItemAtPosition(position).toString()
            val courseCode = course.split(" ")[0]
            val loggedStaffDepartment = sharedPreferences.getString("loggedStaffDepartment", "")
            val loggedStaffPhone = sharedPreferences.getString("loggedStaffPhone", "")
            val text = binding.lvCourses.getItemAtPosition(position).toString()

            // sed this data to the fragment using bundle
            val bundle = Bundle()
            bundle.putString("courseCode", courseCode)
            bundle.putString("text", text)
            val attendanceFragment = Attendance()
            attendanceFragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.dashFrameLayout, attendanceFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AttendanceCourse().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}