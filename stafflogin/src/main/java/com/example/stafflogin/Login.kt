package com.example.stafflogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stafflogin.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
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
            val intent = Intent(this,ForgetPassword::class.java)
            startActivity(intent)
            clear()
        }
    }

    private fun clear() {
        loginBinding.etStaffEmail.text.clear()
        loginBinding.etStaffPass.text.clear()
    }
}