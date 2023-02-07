package com.example.learning_management_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dashboard.Dashboard
import com.example.learning_management_system.databinding.ActivityForgotPasswordBinding
import com.google.android.gms.actions.ReserveIntents
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotBinding: ActivityForgotPasswordBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotBinding.root)

        forgotBinding.btnContinue.setOnClickListener {

            val id = forgotBinding.etIdCode.text.toString()
            val email = forgotBinding.etEmail.text.toString()


            if(id.isBlank() || email.isBlank()){
                Toast.makeText(this, "Please insert all the detail", Toast.LENGTH_SHORT).show()
            }
            else {
                checkIdEmail(id, email)
            }
        }
    }

    private fun checkIdEmail(id: String, email: String) {

        database = FirebaseDatabase.getInstance().getReference("Student")
        database.child(id).get().addOnSuccessListener {
            if (it.exists()) {
                val dbEmail = it.child("email").value
                if (email == dbEmail) {
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtra("id", id)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect Email!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Student not registered!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}