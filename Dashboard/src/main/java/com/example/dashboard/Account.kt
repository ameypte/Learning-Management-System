package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dashboard.databinding.FragmentAccountBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var accountBinding: FragmentAccountBinding
    private lateinit var sharedPreferences: SharedPreferences

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
        accountBinding = FragmentAccountBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        accountBinding.tIdValue.text = sharedPreferences.getString("loggedInUser", "ID")
        accountBinding.txtName.text = sharedPreferences.getString("loggedUserName", "Name")
        accountBinding.tNameValue.text = sharedPreferences.getString("loggedUserName", "Name")
        accountBinding.tMobValue.text = sharedPreferences.getString("loggedUserPhone", "Phone")
        accountBinding.tDeptValue.text = sharedPreferences.getString("loggedUserDept", "Dept")
        accountBinding.tYearValue.text = sharedPreferences.getString("loggedUserYear", "Year")
        accountBinding.tMailValue.text = sharedPreferences.getString("loggedUserEmail", "Mail")

        accountBinding.btnLogout.setOnClickListener {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            requireActivity().finish()
        }
        return accountBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}