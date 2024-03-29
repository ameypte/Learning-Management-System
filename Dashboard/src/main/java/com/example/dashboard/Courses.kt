package com.example.dashboard

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dashboard.databinding.FragmentCoursesBinding
import com.example.staffdashboard.courses.PdfReader
import com.example.staffdashboard.courses.ViewMaterial
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Courses : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var coursesBinding: FragmentCoursesBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loggedStudentDepartment: String
    private lateinit var loggedInStudent: String
    private lateinit var loggedStudentYear: String
    private lateinit var database: DatabaseReference
    private lateinit var database2: DatabaseReference
    private lateinit var toast: Toast

    private lateinit var registeredCourses: ArrayList<String>
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
    ): View {
        coursesBinding = FragmentCoursesBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedStudentDepartment = sharedPreferences.getString("loggedUserDept", "").toString()
        loggedInStudent = sharedPreferences.getString("loggedInUser", "").toString()
        loggedStudentYear = sharedPreferences.getString("loggedUserYear", "").toString()

        coursesRecyclerView = coursesBinding.rvCourses
        coursesRecyclerView.layoutManager = LinearLayoutManager(context)
        coursesRecyclerView.setHasFixedSize(true)
        registeredCourses = getRegisteredCourses()
        courseList = arrayListOf()
        getData()

        coursesBinding.button.setOnClickListener {
            replaceFragment(AddCourse())
        }

        return coursesBinding.root
    }

    private fun getData() {
        database2 = FirebaseDatabase.getInstance().getReference("Courses")
        database2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (course in snapshot.children) {
                        val courseCode = course.key.toString()
                        val courseTitle = course.child("courseTitle").value.toString()
                        val courseCredits = course.child("courseCredits").value.toString()
                        val modelCourse = ModelCourse(courseCode, courseTitle, courseCredits)
                        if (registeredCourses.contains(courseCode)) {
                            courseList.add(modelCourse)
                        }
                    }
                    val adapter = MyCourseAdapter(courseList)
                    coursesRecyclerView.adapter = adapter

                    adapter.setOnCourseClickListener(object :
                        MyCourseAdapter.OnCourseListener {

                        override fun onCourseRemoveClick(position: Int) {
                            val courseCode = courseList[position].courseCode
                            database2 = FirebaseDatabase.getInstance().getReference("Courses")
                                .child(courseCode.toString()).child("registerStudents")
                            database =
                                FirebaseDatabase.getInstance().getReference("Departments")
                                    .child(loggedStudentDepartment).child(loggedStudentYear)
                                    .child("Students").child(loggedInStudent)
                                    .child("registeredCourses")
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Delete Course")
                            builder.setMessage("Are you sure you want to remove this course?")
                            builder.setPositiveButton("Yes") { dialog, which ->
                                if (courseCode != null) {
                                    database2.child(loggedInStudent).removeValue()
                                    database.child(courseCode).removeValue()
                                    replaceFragment(Courses())
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

                        override fun onCourseViewClick(position: Int) {
                            getFirebaseData(courseList[position].courseCode)
                        }

                        override fun onNotesViewClick(position: Int) {
                            val courseCode = courseList[position].courseCode
                            val courseTitle = courseList[position].courseTitle
                            val viewMaterialFragment = ViewMaterial()
                            val args = Bundle()
                            args.putString("courseCode", courseCode)
                            args.putString("courseTitle", courseTitle)
                            viewMaterialFragment.arguments = args
                            replaceFragment(viewMaterialFragment)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }




    private fun getFirebaseData(courseCode: String?) {
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
            .child("curriculum")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val curriculumUrl = snapshot.child("curriculumUrl").value.toString()
                    val curriculumName = snapshot.child("curriculumName").value.toString()
                    val bundle = Bundle()
                    bundle.putString("Url", curriculumUrl)
                    bundle.putString("Name", curriculumName)
                    val pdfRenderer = PdfReader()
                    pdfRenderer.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.dashFrameLayout, pdfRenderer)
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    if (::toast.isInitialized)
                        toast.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        "No Curriculum Uploaded",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }

    private fun getRegisteredCourses(): ArrayList<String> {
        val registeredCourses = arrayListOf<String>()
        database = FirebaseDatabase.getInstance().getReference("Departments")
            .child(loggedStudentDepartment).child(loggedStudentYear).child("Students")
            .child(loggedInStudent)
            .child("registeredCourses")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (course in snapshot.children) {
                        registeredCourses.add(course.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return registeredCourses
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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
}