package com.example.learning_management_system

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btnlog = findViewById<Button>(R.id.btnLogin)
        val btncr = findViewById<Button>(R.id.btnCreate)

        btnlog.setOnClickListener{
            val Idcode = findViewById<EditText>(R.id.etIdCode)
            val icode = Idcode.text.toString()

            val Pass = findViewById<EditText>(R.id.etPassword)
            val pass = Pass.text.toString()

            if (icode.isBlank() || pass.isBlank()){
                Toast.makeText(this, "Please insert all the datail", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
            }
        }

        btncr.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}