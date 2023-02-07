package com.example.learning_management_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.learning_management_system.databinding.ActivityResetPasswordBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var resetBinding: ActivityResetPasswordBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        resetBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(resetBinding.root)

        val itemList = intent.getStringExtra("id")

        // getting the reference of realtime database
        database = FirebaseDatabase.getInstance().getReference("Student")


        resetBinding.btnResetPass.setOnClickListener {
            val newpass = resetBinding.etNewPass.text.toString()
            val conpass = resetBinding.etConPass.text.toString()

            if (newpass.isBlank() || conpass.isBlank()) {
                Toast.makeText(this, "Please insert all the datail", Toast.LENGTH_SHORT).show()
            } else
                if (newpass != conpass) {
                    Toast.makeText(this, "Password and Confirm Password didn't match", Toast.LENGTH_SHORT).show()
                } else {
                    if (intent != null) {
                        Toast.makeText(this, intent.getStringExtra("id"), Toast.LENGTH_SHORT).show()
                        database.child("Student").child(itemList.toString()).child("password").setValue(conpass)
                    }

                }
        }

    }
}