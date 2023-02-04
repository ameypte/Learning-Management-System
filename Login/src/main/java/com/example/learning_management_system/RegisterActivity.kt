package com.example.learning_management_system

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.learning_management_system.databinding.ActivityRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


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

        val btnalready = findViewById<Button>(R.id.btnAlready)

        registerBinding.btnRegister.setOnClickListener {

            // we are retrieving the value from xml page to variables
            val name = registerBinding.etName.text.toString()
            val uid = registerBinding.etId.text.toString()
            val selectedRadioButtonId: Int = registerBinding.rgGender.checkedRadioButtonId
            val email = registerBinding.etEmail.text.toString()
            val phone = registerBinding.etPhone.text.toString()
            val pass = registerBinding.etPass.text.toString()
            val conpass = registerBinding.etConPass.text.toString()

            // getting the refrence of realtime databse
            databse = FirebaseDatabase.getInstance().getReference("Student")

            if (name.isBlank() || uid.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            } else if (pass != conpass) {
                Toast.makeText(
                    this, "Password and Confirm Password didn't match", Toast.LENGTH_SHORT
                ).show()
            } else {
                if (selectedRadioButtonId != -1) {
                    selectedRadioButton = findViewById(selectedRadioButtonId)
                    val gender: String = selectedRadioButton.text.toString()

                    // passing the value in Student dataclass
                    val Student = Student(name, uid, gender, email, phone, pass)
                    // adding child in database
                    databse.child(uid).setValue(Student).addOnSuccessListener {
                        registerBinding.etName.text.clear()
                        registerBinding.etId.text.clear()
                        registerBinding.rgGender.clearCheck()
                        registerBinding.etEmail.text.clear()
                        registerBinding.etPhone.text.clear()
                        registerBinding.etPass.text.clear()
                        registerBinding.etConPass.text.clear()
                        Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT)
                            .show()
                    }

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