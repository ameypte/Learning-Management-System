package com.example.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dashboard.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private var loggedUser: String? = ""
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)

        loggedUser = sharedPreferences.getString("loggedInUser",null)

        dashboardBinding.txt.text = loggedUser
    }
}