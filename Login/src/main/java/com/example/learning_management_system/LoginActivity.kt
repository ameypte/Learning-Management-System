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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
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
        database = FirebaseDatabase.getInstance().getReference("Student")
        database.child(idCode).get().addOnSuccessListener {
            if (it.exists()) {
                val dbPass = it.child("password").value
                if (pass == dbPass) {
                    savePreferences(idCode)
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Student not registered!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            loginBinding.progressBar.visibility = View.GONE
        }
    }

    private fun savePreferences(idCode: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("loggedInUser", idCode).apply()
    }

}