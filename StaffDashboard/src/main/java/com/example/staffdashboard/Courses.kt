package com.example.staffdashboard

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.databinding.FragmentCoursesBinding
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Courses : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var coursesBinding: FragmentCoursesBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loggedStaffDepartment: String
    private lateinit var loggedStaffPhone: String
    private lateinit var database: DatabaseReference

    private lateinit var teachingCourses: ArrayList<String>
    private lateinit var courseList: ArrayList<ModelCourse>
    private lateinit var coursesRecyclerView: RecyclerView

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
    ): View{
        coursesBinding = FragmentCoursesBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedStaffDepartment = sharedPreferences.getString("loggedStaffDepartment", "").toString()
        loggedStaffPhone = sharedPreferences.getString("loggedStaffPhone", "").toString()

        coursesRecyclerView = coursesBinding.rvTeachingCourses
        coursesRecyclerView.layoutManager = LinearLayoutManager(context)
        coursesRecyclerView.setHasFixedSize(true)
        teachingCourses = getTeachingCourses()
        courseList = arrayListOf()
        getData()

        coursesBinding.btnAddCourse.setOnClickListener {
            replaceFragment(AddCourse())
        }

        return coursesBinding.root
    }

    private fun getTeachingCourses(): ArrayList<String> {
        val teachingCourses = arrayListOf<String>()
        database =
            FirebaseDatabase.getInstance().getReference("Departments").child(loggedStaffDepartment)
                .child("Staff").child(loggedStaffPhone).child("coursesTeach")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (course in snapshot.children) {
                        teachingCourses.add(course.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return teachingCourses
    }

    private fun getData() {
        coursesBinding.progressBarTeachingCourses.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("Courses")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (course in snapshot.children) {
                        val courseCode = course.key.toString()
                        val courseTitle = course.child("courseTitle").value.toString()
                        val courseCredits = course.child("courseCredits").value.toString()
                        val modelCourse = ModelCourse(courseCode, courseTitle, courseCredits)
                        if (teachingCourses.contains(courseCode)) {
                            courseList.add(modelCourse)
                        }
                    }
                    val adapter = MyCourseAdapter(courseList)
                    coursesRecyclerView.adapter = adapter
                    coursesBinding.progressBarTeachingCourses.visibility = View.GONE

                    adapter.setOnCourseClickListener(object :
                        MyCourseAdapter.OnCourseListener {
                        override fun onCourseViewClick(position: Int) {
                            val courseCode = courseList[position].courseCode
                            val courseTitle = courseList[position].courseTitle
                            val viewMaterialFragment = ViewMaterial()

                            val args = Bundle()
                            args.putString("courseCode", courseCode)
                            args.putString("courseTitle", courseTitle)
                            viewMaterialFragment.arguments = args

                            replaceFragment(viewMaterialFragment)
                        }

                        override fun onCourseUpdateClick(position: Int) {
                            val courseCode = courseList[position].courseCode
                            val courseTitle = courseList[position].courseTitle
                            val uploadMaterialFragment = UploadMaterial()


                            val args = Bundle()
                            args.putString("courseCode", courseCode)
                            args.putString("courseTitle", courseTitle)
                            uploadMaterialFragment.arguments = args

                            replaceFragment(uploadMaterialFragment)
                        }

                        override fun onCourseRemoveClick(position: Int) {
                            val courseCode = courseList[position].courseCode
                            database =
                                FirebaseDatabase.getInstance().getReference("Departments")
                                    .child(loggedStaffDepartment).child("Staff")
                                    .child(loggedStaffPhone).child("coursesTeach")
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Delete Course")
                            builder.setMessage("Are you sure you want to delete this course?")
                            builder.setPositiveButton("Yes") { dialog, which ->
                                if (courseCode != null) {
                                    database.child(courseCode).removeValue()
                                }
                                courseList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                            builder.setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Courses().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}