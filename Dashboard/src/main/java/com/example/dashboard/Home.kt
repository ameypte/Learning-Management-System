package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Home : Fragment() {
    private lateinit var loggedUserDept:String
    private lateinit var loggedUserYear:String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedUserDept = sharedPreferences.getString("loggedUserDept",null).toString()
        loggedUserYear = sharedPreferences.getString("loggedUserYear",null).toString()

        val a = view.findViewById<TextView>(R.id.txtHome)
        a.text = ("$loggedUserDept \n $loggedUserYear")
        return view


    }
}