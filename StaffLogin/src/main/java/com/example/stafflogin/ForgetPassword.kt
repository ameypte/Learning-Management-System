package com.example.stafflogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.stafflogin.databinding.ActivityForgetPasswordBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ForgetPassword : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var forgotBinding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(forgotBinding.root)

        val spinner = forgotBinding.spinner
        val items = arrayOf(
            "Select Department",
            "Information Technology",
            "Computer Engineering",
            "Civil Engineering",
            "Mechanical Engineering",
            "ExTc Engineering",
            "Electrical Engineering",
            "Chemical Engineering",
            "Plastic & Polymer Engineering"
        )
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val items1 = spinner.selectedItem.toString()
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        forgotBinding.btnContinue.setOnClickListener {

            val mob = forgotBinding.etMob.text.toString()
            val email = forgotBinding.etEmail.text.toString()
            val branch = forgotBinding.spinner.selectedItem.toString()

            if(mob.isBlank() || email.isBlank()){
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            }else if (branch == items[0]){
                Toast.makeText(this,"Select the Department", Toast.LENGTH_SHORT).show()
            }
            else {
                checkIdEmail(mob, email, branch)
            }
        }
    }

    private fun checkIdEmail(mob: String, email: String, branch: String) {

        database = FirebaseDatabase.getInstance().getReference("Departments")
        database.child(branch).child("Staff").child(mob).get().addOnSuccessListener {
            if (it.exists()) {
                val dbEmail = it.child("email").value
                if (email == dbEmail) {
                    val bundle = Bundle()
                    bundle.putString("mob", mob)
                    bundle.putString("branch", branch)
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect Email!", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Student not registered!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}