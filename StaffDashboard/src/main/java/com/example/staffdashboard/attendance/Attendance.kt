package com.example.staffdashboard.attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.staffdashboard.R
import com.example.staffdashboard.databinding.FragmentAttendanceBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Attendance : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAttendanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)

        val args = arguments
        val courseCode = args?.getString("courseCode")
        val text = args?.getString("text")
        binding.subjectCodeTitle.text = text

        binding.next.setOnClickListener {
            binding.datePicker.visibility = View.GONE
            binding.next.visibility = View.GONE
        }


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Attendance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}