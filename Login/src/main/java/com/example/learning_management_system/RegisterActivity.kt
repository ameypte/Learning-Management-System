package com.example.learning_management_system

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.learning_management_system.databinding.ActivityRegisterBinding
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var selectedRadioButton: RadioButton

    // database code
    // creating the binding for activity_register.xml
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference

    private fun isLettersOrDigits(uid: String): Boolean {
        var hasOtherChar = false
        for (c in uid) {
            if (c !in 'A'..'Z' || c !in '0'..'9') {
                hasOtherChar = true
            }
        }
        return hasOtherChar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        title = "Register"

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

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val items1 = spinner.selectedItem.toString()
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val year = registerBinding.year
        val item = arrayOf(
            "Select Academic Year",
            "First Year",
            "Second Year",
            "Third Year"
        )

        val adapter1 =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, item)
        year.adapter = adapter1

        year.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val item1 = year.selectedItem.toString()
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        registerBinding.btnRegister.setOnClickListener {

            // we are retrieving the value from xml page to variables
            val name = registerBinding.etName.text.toString()
            val uid = registerBinding.etId.text.toString()
            val branch = registerBinding.spinner.selectedItem.toString()
            val aYear = registerBinding.year.selectedItem.toString()
            val selectedRadioButtonId: Int = registerBinding.rgGender.checkedRadioButtonId
            val email = registerBinding.etEmail.text.toString()
            val phone = registerBinding.etPhone.text.toString()
            val pass = registerBinding.etPass.text.toString()
            val conpass = registerBinding.etConPass.text.toString()

            // getting the reference of realtime database
            database = FirebaseDatabase.getInstance().getReference("Departments")

            if (name.isBlank() || uid.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            } else if (aYear == item[0]) {
                Toast.makeText(this, "Please select the Academic year", Toast.LENGTH_SHORT).show()
            } else if (branch == items[0]) {
                Toast.makeText(this, "Please select the department", Toast.LENGTH_SHORT).show()
            } else if (pass != conpass) {
                Toast.makeText(
                    this, "Password and Confirm Password didn't match",Toast.LENGTH_SHORT).show()
            } else if (!isLettersOrDigits(uid)) {
                Toast.makeText(this, "Please insert idcode in proper manner", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (selectedRadioButtonId != -1) {
                    val ref = this

                    // show progress bar
                    registerBinding.progressBar.visibility = View.VISIBLE

                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // check if student is already registered
                            if (snapshot.child(uid).exists()) {
                                Toast.makeText(
                                    ref, "Student is already registered!", Toast.LENGTH_SHORT
                                ).show()
                                registerBinding.progressBar.visibility = View.GONE
                            } else {
                                selectedRadioButton = findViewById(selectedRadioButtonId)
                                val gender: String = selectedRadioButton.text.toString()

                                // passing the value in Student dataclass
                                val Student =
                                    Student(name, uid, branch, aYear, gender, email, phone, pass)

                                // adding child in database
                                database.child(branch).child(aYear).child("Students").child(uid)
                                    .setValue(Student).addOnSuccessListener {
                                    registerBinding.etName.text.clear()
                                    registerBinding.etId.text.clear()
                                    registerBinding.rgGender.clearCheck()
                                    registerBinding.etEmail.text.clear()
                                    registerBinding.etPhone.text.clear()
                                    registerBinding.etPass.text.clear()
                                    registerBinding.etConPass.text.clear()
                                    Toast.makeText(ref, "Register successfully", Toast.LENGTH_SHORT)
                                        .show()
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(ref, "Something went wrong!", Toast.LENGTH_SHORT)
                                        .show()
                                }.addOnCompleteListener {
                                    registerBinding.progressBar.visibility = View.GONE
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                } else {
                    Toast.makeText(this, "Please Select The gender!", Toast.LENGTH_SHORT).show()
                }
            }

        }
        registerBinding.btnAlready.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}