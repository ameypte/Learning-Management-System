package com.example.staffdashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.staffdashboard.databinding.FragmentTimeTableBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var timeTableBinding: FragmentTimeTableBinding

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
        timeTableBinding = FragmentTimeTableBinding.inflate(inflater, container, false)


        val spinner = timeTableBinding.spDay

        val items = arrayOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Friday",
            "Thursday"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        timeTableBinding.btnUpload.setOnClickListener {
            replaceFragment(UploadTimeTable())
        }

        return timeTableBinding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimeTable().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}