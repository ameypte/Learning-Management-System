package com.example.learning_management_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import java.util.jar.Attributes.Name

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register"

        val btreg = findViewById<Button>(R.id.btnRegister)
        val btnalready = findViewById<Button>(R.id.btnAlready)

        btreg.setOnClickListener {

            val Name = findViewById<EditText>(R.id.etName)
            val name = Name.text.toString()

            val Icode = findViewById<EditText>(R.id.etId)
            val uid = Icode.text.toString()

//            val regen = findViewById<RadioGroup>(R.id.rgGender)
//            val gen = regen.Button.toString()

            val Mail = findViewById<EditText>(R.id.etEmail)
            val email = Mail.text.toString()

            val Phone = findViewById<EditText>(R.id.etPhone)
            val phone = Phone.text.toString()

            val Pass = findViewById<EditText>(R.id.etPass)
            val pass = Pass.text.toString()

            val Conpass = findViewById<EditText>(R.id.etConPass)
            val conpass = Conpass.text.toString()

            if(name.isBlank() || uid.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()){
                Toast.makeText(this, "Please insert all the datail", Toast.LENGTH_SHORT).show()
            }
            else if (pass != conpass){
                Toast.makeText(this, "Password and Confirm Password didn't match", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show()
            }
        }

        btnalready.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}