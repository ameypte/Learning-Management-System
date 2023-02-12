package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.dashboard.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private var loggedUser: String? = ""
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting which user is logged in
        sharedPreferences = getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedUser = sharedPreferences.getString("loggedInUser",null)

        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)
        replaceFragment(Home())

        dashboardBinding.bottomNavigationView.setOnItemSelectedListener {
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

    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout,fragment)
        fragmentTransaction.commit()
    }
}