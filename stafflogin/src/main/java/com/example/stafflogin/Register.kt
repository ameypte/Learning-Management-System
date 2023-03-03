package com.example.stafflogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stafflogin.databinding.ActivityLoginBinding
import com.example.stafflogin.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)


    }
}