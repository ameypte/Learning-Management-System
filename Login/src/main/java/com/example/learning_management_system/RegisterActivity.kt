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
    private lateinit var databse: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // database code
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        title = "Register"


        val spinner = findViewById<Spinner>(R.id.spinner)
        val items = arrayOf(
            "Select Department",
            "Information Technology",
            "Computer Engineering",
            "Civil Engineering",
            "Mechanical Engineering",
            "ExTc Engineering",
            "Electrical Engineering",
            "Chemical Engineering"
        )
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.setAdapter(adapter)

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                // Do what you want
                val items1 = spinner.selectedItem.toString()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }


        val btnalready = findViewById<Button>(R.id.btnAlready)

        registerBinding.btnRegister.setOnClickListener {

            // we are retrieving the value from xml page to variables
            val name = registerBinding.etName.text.toString()
            val uid = registerBinding.etId.text.toString()
            val branch = registerBinding.spinner.selectedItem.toString()
            val selectedRadioButtonId: Int = registerBinding.rgGender.checkedRadioButtonId
            val email = registerBinding.etEmail.text.toString()
            val phone = registerBinding.etPhone.text.toString()
            val pass = registerBinding.etPass.text.toString()
            val conpass = registerBinding.etConPass.text.toString()

            // getting the reference of realtime database
            databse = FirebaseDatabase.getInstance().getReference("Student")

            if (name.isBlank() || uid.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            } else if (branch == items[0]) {
                Toast.makeText(this, "Please select the department", Toast.LENGTH_SHORT).show()
            } else if (pass != conpass) {
                Toast.makeText(
                    this, "Password and Confirm Password didn't match", Toast.LENGTH_SHORT
                ).show()
            } else {
                if (selectedRadioButtonId != -1) {
                    val ref = this

                    // show progress bar
                    registerBinding.progressBar.visibility = View.VISIBLE

                    databse.addListenerForSingleValueEvent(object : ValueEventListener {
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
                                val Student = Student(name, uid, branch, gender, email, phone, pass)

                                // adding child in database
                                databse.child(uid).setValue(Student).addOnSuccessListener {
                                    registerBinding.etName.text.clear()
                                    registerBinding.etId.text.clear()
                                    registerBinding.rgGender.clearCheck()
                                    registerBinding.etEmail.text.clear()
                                    registerBinding.etPhone.text.clear()
                                    registerBinding.etPass.text.clear()
                                    registerBinding.etConPass.text.clear()
                                    Toast.makeText(ref, "Register successfully", Toast.LENGTH_SHORT)
                                        .show()
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
        btnalready.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}