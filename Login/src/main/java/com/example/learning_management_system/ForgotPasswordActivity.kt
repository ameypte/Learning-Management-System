package com.example.learning_management_system

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learning_management_system.databinding.ActivityForgotPasswordBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotBinding: ActivityForgotPasswordBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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
            ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val items1 = spinner.selectedItem.toString()
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val year = forgotBinding.year
        val item = arrayOf(
            "Select Academic Year",
            "First Year",
            "Second Year",
            "Third Year"
        )

        val adapter1 =
            ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, item)
        year.adapter = adapter1

        year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val item1 = year.selectedItem.toString()
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        forgotBinding.btnContinue.setOnClickListener {

            val id = forgotBinding.etIdCode.text.toString()
            val email = forgotBinding.etEmail.text.toString()
            val branch = forgotBinding.spinner.selectedItem.toString()
            val aYear = forgotBinding.year.selectedItem.toString()

            if(id.isBlank() || email.isBlank()){
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            }else if (branch == items[0]){
                Toast.makeText(this,"Select the Department",Toast.LENGTH_SHORT).show()
            }else if (aYear == items[0]){
                Toast.makeText(this,"Select the Academic Year",Toast.LENGTH_SHORT).show()
            }
            else {
                checkIdEmail(id, email, branch, aYear)
            }
        }
    }

    private fun checkIdEmail(id: String, email: String, branch: String, aYear: String) {

        database = FirebaseDatabase.getInstance().getReference("Departments")
        database.child(branch).child(aYear).child("Students").child(id).get().addOnSuccessListener {
            if (it.exists()) {
                val dbEmail = it.child("email").value
                if (email == dbEmail) {
                    val bundle = Bundle()
                    bundle.putString("id", id)
                    bundle.putString("branch", branch)
                    bundle.putString("year", aYear)
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