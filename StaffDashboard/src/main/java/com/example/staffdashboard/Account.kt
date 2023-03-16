package com.example.staffdashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.staffdashboard.databinding.FragmentAccountBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Account : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var accountBinding:FragmentAccountBinding
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
    ): View{
        accountBinding=FragmentAccountBinding.inflate(inflater,container,false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        accountBinding.txtName.text = sharedPreferences.getString("loggedStaffName", "Name")
        accountBinding.tNameValue.text = sharedPreferences.getString("loggedStaffName", "Name")
        accountBinding.tMobValue.text = sharedPreferences.getString("loggedStaffPhone", "Phone")
        accountBinding.tMailValue.text = sharedPreferences.getString("loggedStaffEmail", "Mail")
        accountBinding.tDeptValue.text = sharedPreferences.getString("loggedStaffDepartment", "Dept")

        accountBinding.btnLogout.setOnClickListener {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            Toast.makeText(requireContext(),"Logout Successfully!",Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        accountBinding.imgEdit.setOnClickListener{
            replaceFragment(StaffEdit())
            Toast.makeText(requireContext(),"You can edit now!",Toast.LENGTH_SHORT).show()
        }

        return accountBinding.root
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
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}