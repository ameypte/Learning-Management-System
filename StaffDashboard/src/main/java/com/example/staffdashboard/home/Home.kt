package com.example.staffdashboard.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.staffdashboard.R
import com.example.staffdashboard.account.Account
import com.example.staffdashboard.attendance.AttendanceCourse
import com.example.staffdashboard.databinding.FragmentHomeBinding
import com.example.staffdashboard.timetable.TimeTable


class Home : Fragment() {
    private lateinit var homeBinding: FragmentHomeBinding
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
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        val name = sharedPreferences.getString("loggedStaffName", "Name")
        homeBinding.txtName.text = ("Welcome $name")

        homeBinding.imgProfile.setOnClickListener {
            replaceFragment(Account())
        }
        homeBinding.reschedule.setOnClickListener {
            replaceFragment(TimeTable())
        }
        homeBinding.attendance.setOnClickListener{
            replaceFragment(AttendanceCourse())
        }
        return homeBinding.root
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}