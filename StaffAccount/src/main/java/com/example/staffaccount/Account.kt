package com.example.staffaccount

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.staffaccount.databinding.FragmentAccountBinding

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

        accountBinding.btnLogout.setOnClickListener {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            Toast.makeText(requireContext(),"Logout Successfully!",Toast.LENGTH_SHORT).show()
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