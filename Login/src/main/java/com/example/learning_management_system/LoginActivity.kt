package com.example.learning_management_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.learning_management_system.databinding.ActivityLoginBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

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
        }
    }

    private fun checkIdPass(idCode: String, pass: String) {
        loginBinding.progressBar.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("Student")
        database.child(idCode).get().addOnSuccessListener {
            if (it.exists()) {
                val dbPass = it.child("password").value
                if (pass.equals(dbPass)){
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Student not registered!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener{
            loginBinding.progressBar.visibility = View.GONE
        }
    }
}