package com.example.learning_management_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.learning_management_system.databinding.ActivityForgotPasswordBinding
import com.example.learning_management_system.databinding.ActivityLoginBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotBinding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotBinding.root)

        forgotBinding.btnContinue.setOnClickListener {
            val intent = Intent(this,ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}