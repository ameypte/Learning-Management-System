package com.example.staffdashboard

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.staffdashboard.databinding.FragmentStaffEditBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StaffEdit : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var staffEditBinding: FragmentStaffEditBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mob: String
    private lateinit var dept: String

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
    ): View? {
        staffEditBinding = FragmentStaffEditBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(com.example.staffdashboard.R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )


        staffEditBinding.btnEdit.setOnClickListener {
//          val edmob = staffEditBinding.etMobValue.text.toString()
            val edname = staffEditBinding.etNameValue.text.toString()
            val edmail = staffEditBinding.etMailValue.text.toString()
            savePreferences(edname, edmail)

            database = FirebaseDatabase.getInstance().getReference("Departments")
            if (edname.isBlank() || edmail.isBlank()){
                Toast.makeText(requireContext(), "Please insert all the detail", Toast.LENGTH_SHORT).show()
            }
            else {
                mob = sharedPreferences.getString("loggedStaffPhone","").toString()
                dept = sharedPreferences.getString("loggedStaffDepartment","").toString()
//                database.child(dept).child("Staff").child(mob).child("phone").setValue(edmob)
                database.child(dept).child("Staff").child(mob).child("name").setValue(edname)
                database.child(dept).child("Staff").child(mob).child("email").setValue(edmail)
                    .addOnSuccessListener {
                        requireActivity().supportFragmentManager.popBackStack()
                    }.addOnCompleteListener {
//                registerBinding.prBarRegister.visibility = View.GONE
                }
            }
        }
        return staffEditBinding.root
    }

    private fun savePreferences(edname: Any, edmail: Any) {
        sharedPreferences.edit().putString("loggedStaffName", edname.toString()).apply()
        sharedPreferences.edit().putString("loggedStaffEmail", edmail.toString()).apply()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffEdit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}