package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.dashboard.databinding.FragmentCoursesBinding
import com.google.firebase.database.DatabaseReference

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

        coursesBinding.button.setOnClickListener {
            replaceFragment(AddCourse())
        }

        return coursesBinding.root
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.dashboard.R.id.dashFrameLayout,fragment)
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