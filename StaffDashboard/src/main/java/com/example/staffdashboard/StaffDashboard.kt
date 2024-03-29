package com.example.staffdashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.staffdashboard.account.Account
import com.example.staffdashboard.courses.Courses
import com.example.staffdashboard.databinding.ActivityStaffDashboardBinding
import com.example.staffdashboard.home.Home
import com.example.staffdashboard.messages.Messages
import com.example.staffdashboard.timetable.TimeTable


class StaffDashboard : AppCompatActivity() {
    private lateinit var staffDashBinding: ActivityStaffDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        staffDashBinding = ActivityStaffDashboardBinding.inflate(layoutInflater)
        setContentView(staffDashBinding.root)


        replaceFragment(Home())
        staffDashBinding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.courses -> replaceFragment(Courses())
                R.id.timeTable -> replaceFragment(TimeTable())
                R.id.messages -> replaceFragment(Messages())
                R.id.account -> replaceFragment(Account())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}