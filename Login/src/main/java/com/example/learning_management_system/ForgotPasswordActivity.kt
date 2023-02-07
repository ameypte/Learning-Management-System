package com.example.learning_management_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.learning_management_system.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotBinding: ActivityForgotPasswordBinding
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
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        }
    }
}