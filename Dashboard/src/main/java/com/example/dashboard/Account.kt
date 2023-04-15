package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.dashboard.databinding.FragmentAccountBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var accountBinding: FragmentAccountBinding
    private lateinit var sharedPreferences: SharedPreferences
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

        accountBinding.btnEdit.setOnClickListener {
            accountBinding.btnEdit.visibility = View.GONE
            accountBinding.tNameValue.visibility = View.GONE
            accountBinding.tMobValue.visibility = View.GONE
            accountBinding.tMailValue.visibility = View.GONE
            accountBinding.btnLogout.visibility = View.GONE

            accountBinding.editName.visibility = View.VISIBLE
            accountBinding.editMobile.visibility = View.VISIBLE
            accountBinding.editMail.visibility = View.VISIBLE
            accountBinding.btnUpdate.visibility = View.VISIBLE

            accountBinding.editName.setText(sharedPreferences.getString("loggedUserName", "Name"))
            accountBinding.editMobile.setText(sharedPreferences.getString("loggedUserPhone", "Phone"))
            accountBinding.editMail.setText(sharedPreferences.getString("loggedUserEmail", "Mail"))

        }

        accountBinding.btnUpdate.setOnClickListener {
            val name = accountBinding.editName.text
            val mob = accountBinding.editMobile.text
            val mail = accountBinding.editMail.text

            accountBinding.tName.text = name
            accountBinding.tMobValue.text = mob
            accountBinding.tMailValue.text = mail

            database = FirebaseDatabase.getInstance().getReference("Departments")
            if (name.isBlank() || mob.isBlank() || mail.isBlank()){
                Toast.makeText(requireContext(),"Please insert all the datails", Toast.LENGTH_SHORT).show()
            }
            else{
                accountBinding.uploadProgressBar.visibility = View.VISIBLE
                accountBinding.btnUpdate.visibility = View.GONE

                sharedPreferences.edit().putString("loggedUserName", name.toString()).apply()
                sharedPreferences.edit().putString("loggedUserPhone", mob.toString()).apply()
                sharedPreferences.edit().putString("loggedUserEmail", mail.toString()).apply()

                val id = sharedPreferences.getString("loggedInUser", "").toString()
                val dept = sharedPreferences.getString("loggedUserDept", "").toString()
                val year = sharedPreferences.getString("loggedUserYear", "").toString()

                database.child(dept).child(year).child("Students").child(id).child("name").setValue(name.toString())
                database.child(dept).child(year).child("Students").child(id).child("phone").setValue(mob.toString())
                database.child(dept).child(year).child("Students").child(id).child("email").setValue(mail.toString())
                    .addOnSuccessListener{
                        accountBinding.tNameValue.visibility = View.VISIBLE
                        accountBinding.tMobValue.visibility = View.VISIBLE
                        accountBinding.tMailValue.visibility = View.VISIBLE
                        accountBinding.btnLogout.visibility = View.VISIBLE
                        accountBinding.editName.visibility = View.GONE
                        accountBinding.editMobile.visibility = View.GONE
                        accountBinding.editMail.visibility = View.GONE
                        accountBinding.uploadProgressBar.visibility = View.GONE
                        accountBinding.btnUpdate.visibility = View.GONE
                        accountBinding.btnEdit.visibility = View.VISIBLE

                        Toast.makeText(requireContext(),"Updated Successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        accountBinding.uploadProgressBar.visibility = View.GONE
                        accountBinding.btnUpdate.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),"Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }
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