package com.example.stafflogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.stafflogin.databinding.ActivityRegisterBinding
import com.google.firebase.database.*

class Register : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        val spinner = registerBinding.spinner
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

        registerBinding.btnRegister.setOnClickListener {
            val name = registerBinding.etname.text.toString()
            val email = registerBinding.etmail.text.toString()
            val branch = registerBinding.spinner.selectedItem.toString()
            val phone = registerBinding.etnum.text.toString()
            val pass = registerBinding.etpass.text.toString()
            val conPass = registerBinding.etConPass.text.toString()

            if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            } else if (pass != conPass) {
                Toast.makeText(
                    this, "Password and Confirm Password didn't match", Toast.LENGTH_SHORT
                ).show()
            } else if (branch == items[0]) {
                Toast.makeText(this, "Please select the department", Toast.LENGTH_SHORT).show()
            } else {
                database = FirebaseDatabase.getInstance().getReference("Departments")
                val Staff = Staff(name,email,branch,phone,pass)
                database.child(branch).child("Staff").child(phone).setValue(Staff).addOnSuccessListener {
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        registerBinding.txtAlready.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}