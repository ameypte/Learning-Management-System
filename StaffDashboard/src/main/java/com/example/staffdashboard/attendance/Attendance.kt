package com.example.staffdashboard.attendance

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.R
import com.example.staffdashboard.databinding.FragmentAttendanceBinding
import com.example.staffdashboard.home.Home
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Attendance : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var database: DatabaseReference
    private lateinit var database2: DatabaseReference
    private lateinit var date: String
    private lateinit var totalLecture: String
    private lateinit var totalAttendance: String
    private lateinit var percentage: String

    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var studentList: ArrayList<Student>

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
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)

        val args = arguments
        val courseCode = args?.getString("courseCode")
        val text = args?.getString("text")
        binding.subjectCodeTitle.text = text

        binding.next.setOnClickListener {
            val day = binding.datePicker.dayOfMonth
            val month = binding.datePicker.month
            val year = binding.datePicker.year
            date = "$day-$month-$year"
            binding.date.text = date

            database = FirebaseDatabase.getInstance().getReference("Attendance")
                .child(courseCode.toString()).child(date)

            studentRecyclerView = binding.studentList
            studentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            studentRecyclerView.setHasFixedSize(true)
            studentList = arrayListOf()
            getData(courseCode.toString())

            binding.datePicker.visibility = View.GONE
            binding.next.visibility = View.GONE
            binding.studentList.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("Attendance")
                .child(courseCode.toString()).child(date)
            for (i in studentList) {
                database.child(i.idCode.toString()).setValue(i.attendance)
            }
            replaceFragment(Home())
            Toast.makeText(requireContext(), "Attendance Marked", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun getData(courseCode: String) {
        database =
            FirebaseDatabase.getInstance().getReference("Courses").child(courseCode)
                .child("registerStudents")

        database2 = FirebaseDatabase.getInstance().getReference("Attendance")
            .child(courseCode)

        database2.get().addOnSuccessListener {
            if (it.exists()) {
                totalLecture = it.childrenCount.toString()
            }
        }

        database.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (i in dataSnapshot.children) {

                    // This database2 is for attendance checkbox
                    database2.child(date).child(i.key.toString()).get().addOnSuccessListener {
                        if (it.exists()) {
                            getStudentPercentage(courseCode, i.key.toString()) { percentage ->
                                Log.d("percentage", percentage)
                                val attendance = it.value.toString().toBoolean()
                                val student = Student(
                                    i.key.toString(),
                                    i.value.toString(),
                                    attendance,
                                    String.format("%.2f", percentage.toDouble()).toDouble()
                                )
                                Log.d("student", student.toString())
                                studentList.add(student)
                                binding.totalStudentsValue.text = studentList.size.toString()
                                studentRecyclerView.adapter = AttendanceAdapter(studentList)
                            }
                        } else {
                            getStudentPercentage(courseCode, i.key.toString()) { percentage ->
                                Log.d("percentage", percentage)
                                val student = Student(
                                    i.key.toString(),
                                    i.value.toString(),
                                    false,
                                    String.format("%.2f", percentage.toDouble()).toDouble()
                                )
                                Log.d("student", student.toString())
                                studentList.add(student)
                                binding.totalStudentsValue.text = studentList.size.toString()
                                studentRecyclerView.adapter = AttendanceAdapter(studentList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getStudentPercentage(
        courseCode: String,
        studentId: String,
        callback: (percentage: String) -> Unit
    ) {
        var per = "0.0"
        database = FirebaseDatabase.getInstance().getReference("Attendance")
            .child(courseCode)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAttendance = 0
                for (data in snapshot.children) {
                    if (data.hasChild(studentId) && data.child(studentId).value.toString()
                            .toBoolean()
                    ) {
                        totalAttendance++
                    }
                }
                per = (totalAttendance.toDouble() / totalLecture.toDouble() * 100).toString()
                callback(per)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the cancellation error if needed
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Attendance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}