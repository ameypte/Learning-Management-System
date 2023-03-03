package com.example.stafflogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stafflogin.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class Login : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.btnRegisterNow.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            clear()
        }
        loginBinding.txtForgot.setOnClickListener {
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
            clear()
        }
        loginBinding.btnLogin.setOnClickListener {
            val email = loginBinding.etStaffEmail.text.toString()
            val pass = loginBinding.etStaffPass.text.toString()
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the details!", Toast.LENGTH_SHORT).show()
            } else {
                checkEmailPass(email, pass)
            }
        }
    }

    private fun checkEmailPass(email: String, pass: String) {
        database = FirebaseDatabase.getInstance().getReference("Departments")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isStaffFound = false
                for (departmentSnapshot in snapshot.children) {
                    var staff = departmentSnapshot.child("Staff")
                    if (staff.child(email).exists()) {
                        val s = staff.child(email).getValue(Staff::class.java)
                        if (s?.password == pass) {
                            isStaffFound = true
                            break
                        }
                    }
                }
                if (isStaffFound) {
                    Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@Login,
                        "Staff not registered or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun clear() {
        loginBinding.etStaffEmail.text.clear()
        loginBinding.etStaffPass.text.clear()
    }
}