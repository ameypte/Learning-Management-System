package com.example.stafflogin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.staffdashboard.StaffDashboard
import com.example.stafflogin.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class Login : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var dept: String
    private lateinit var phone: String
    private lateinit var st: String
    private lateinit var token: String

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
        var departmentName = ""

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isStaffFound = false
                for (departmentSnapshot in snapshot.children) {
                    var staff = departmentSnapshot.child("Staff")
                    if (staff.child(phone).exists()) {
                        val s = staff.child(phone).getValue(Staff::class.java)
                        if (s?.password == pass) {
                            isStaffFound = true
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    val token = task.result
                                    Log.d("FCM", token.toString())
                                    Toast.makeText(this@Login, token, Toast.LENGTH_SHORT).show()

                                    database =
                                        FirebaseDatabase.getInstance().getReference("Departments")
                                            .child(s.branch.toString()).child("Staff").child(phone)
                                    database.child("fcmToken").setValue(token.toString())
                                    setStudentData(departmentSnapshot, staff, s)
                                    departmentName = departmentSnapshot.key.toString()
                                    sharedPreferences.edit()
                                        .putString("loggedStaffDepartment", departmentName).apply()
                                    // by default subscribe to all department topics
                                    subscribeToTopic(departmentName.replace(" ","") + "FirstYear")
                                    subscribeToTopic(departmentName.replace(" ","") + "SecondYear")
                                    subscribeToTopic(departmentName.replace(" ","") + "ThirdYear")

                                    savePreferences(phone, departmentName)
                                    val intent = Intent(this@Login, StaffDashboard::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT)
                                        .show()
                                    loginBinding.prBarLogin.visibility = View.GONE
                                    finish()
                                })
                            break
                        }
                    }
                }
                if (isStaffFound) {
                    // blank
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

    private fun setStudentData(departmentSnapshot: DataSnapshot, staff: DataSnapshot, s: Staff) {
        dept = departmentSnapshot.key.toString()
        st = staff.key.toString()
        name = s.name.toString()
        phone = s.phone.toString()
        email = s.email.toString()
        token = s.fcmToken.toString()
    }

    private fun subscribeToTopic(s: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(s)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to $s"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to $s"
                }
                Log.d("FCM", msg)
            }
    }

    private fun savePreferences(phone: String, departmentName: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("loggedStaffName", name).apply()
        sharedPreferences.edit().putString("loggedStaffPhone", phone).apply()
        sharedPreferences.edit().putString("loggedStaffEmail", email).apply()
        sharedPreferences.edit().putString("loggedStaffDepartment", departmentName).apply()
        sharedPreferences.edit().putString("fcmToken", token).apply()
    }

    private fun clear() {
        loginBinding.etStaffPhone.text.clear()
        loginBinding.etStaffPass.text.clear()
    }
}