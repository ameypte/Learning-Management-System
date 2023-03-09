package com.example.stafflogin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.staffdashboard.StaffDashboard
import com.example.stafflogin.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class Login : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        sharedPreferences = getSharedPreferences(
            getString(com.example.staffdashboard.R.string.login_preference_file_name),
            MODE_PRIVATE
        )

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@Login, StaffDashboard::class.java)
            startActivity(intent)
            finish()
        }

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
            val phone = loginBinding.etStaffPhone.text.toString()
            val pass = loginBinding.etStaffPass.text.toString()
            if (phone.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Please insert all the details!", Toast.LENGTH_SHORT).show()
            } else {
                checkPhonePass(phone, pass)
            }
        }
    }

    private fun checkPhonePass(phone: String, pass: String) {
        loginBinding.prBarLogin.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("Departments")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isStaffFound = false
                for (departmentSnapshot in snapshot.children) {
                    var staff = departmentSnapshot.child("Staff")
                    if (staff.child(phone).exists()) {
                        val s = staff.child(phone).getValue(Staff::class.java)
                        if (s?.password == pass) {
                            isStaffFound = true
                            break
                        }
                    }
                }
                if (isStaffFound) {
                    savePreferences(phone)
                    val intent = Intent(this@Login,StaffDashboard::class.java)
                    startActivity(intent)
                    Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT)
                        .show()
                    loginBinding.prBarLogin.visibility = View.GONE
                    finish()
                } else {
                    Toast.makeText(
                        this@Login,
                        "Staff not registered or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginBinding.prBarLogin.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun savePreferences(phone: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("loggedStaffEmail", phone).apply()
    }

    private fun clear() {
        loginBinding.etStaffPhone.text.clear()
        loginBinding.etStaffPass.text.clear()
    }
}