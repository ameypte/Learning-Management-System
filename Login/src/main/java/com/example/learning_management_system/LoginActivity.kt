package com.example.learning_management_system

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboard.Dashboard
import com.example.learning_management_system.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dept: String
    private lateinit var year: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        sharedPreferences = getSharedPreferences(
            getString(com.example.dashboard.R.string.login_preference_file_name),
            MODE_PRIVATE
        )

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        val btnlog = findViewById<Button>(R.id.btnLogin)
        val btncr = findViewById<Button>(R.id.btnCreate)

        btnlog.setOnClickListener {
            val idCode = loginBinding.etIdCode.text.toString()
            val pass = loginBinding.etPassword.text.toString()
            if (idCode.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the datail", Toast.LENGTH_SHORT).show()
            } else {
                checkIdPass(idCode, pass)
            }
        }

        btncr.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginBinding.btnForgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            clear()
        }
    }

    private fun clear() {
        loginBinding.etIdCode.text.clear()
        loginBinding.etPassword.text.clear()
    }

    private fun checkIdPass(idCode: String, pass: String) {
        loginBinding.progressBar.visibility = View.VISIBLE

        database = FirebaseDatabase.getInstance().getReference("Departments")

        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isStudentFound = false

                for (departmentSnapshot in dataSnapshot.children) {
                    var department = departmentSnapshot.child("First Year")
                    if (department.child("Students").child(idCode).exists()) {
                        val student =
                            department.child("Students").child(idCode).getValue(Student::class.java)
                        if (student?.password == pass) {
                            isStudentFound = true
                            dept = departmentSnapshot.key.toString()
                            year = department.key.toString()
                            break
                        }
                    }
                    department = departmentSnapshot.child("Second Year")
                    if (department.child("Students").child(idCode).exists()) {
                        val student =
                            department.child("Students").child(idCode).getValue(Student::class.java)
                        if (student?.password == pass) {
                            isStudentFound = true
                            dept = departmentSnapshot.key.toString()
                            year = department.key.toString()
                            break
                        }
                    }
                    department = departmentSnapshot.child("Third Year")
                    if (department.child("Students").child(idCode).exists()) {
                        val student =
                            department.child("Students").child(idCode).getValue(Student::class.java)
                        if (student?.password == pass) {
                            isStudentFound = true
                            dept = departmentSnapshot.key.toString()
                            year = department.key.toString()
                            break
                        }
                    }
                }
                if (isStudentFound) {
                    savePreferences(idCode)
                    val intent = Intent(this@LoginActivity, Dashboard::class.java)
                    startActivity(intent)
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Student not found or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loginBinding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun savePreferences(idCode: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("loggedInUser", idCode).apply()
        sharedPreferences.edit().putString("loggedUserDept", dept).apply()
        sharedPreferences.edit().putString("loggedUserYear", year).apply()
    }

}