package com.example.staffdashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.staffdashboard.databinding.FragmentUploadMaterialBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UploadMaterial() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var uploadMaterialBinding: FragmentUploadMaterialBinding

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
    ): View? {
        uploadMaterialBinding = FragmentUploadMaterialBinding.inflate(inflater,container,false)
        val args = arguments
        val courseCode = args?.getString("courseCode")
        val courseTitle = args?.getString("courseTitle")

        uploadMaterialBinding.txt.text = "$courseCode $courseTitle"
        return uploadMaterialBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadMaterial().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}