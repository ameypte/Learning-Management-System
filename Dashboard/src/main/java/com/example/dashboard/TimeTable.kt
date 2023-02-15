package com.example.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dashboard.databinding.FragmentTimeTableBinding
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimeTable.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeTable : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference
    private var studentRecyclerView: RecyclerView? = null
    private lateinit var studArrayList: ArrayList<Student>


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

        //code for recycler view
        studentRecyclerView = view?.findViewById<RecyclerView>(R.id.studentList)

        studentRecyclerView?.layoutManager = LinearLayoutManager(context)
        studentRecyclerView?.setHasFixedSize(true)

        studArrayList = arrayListOf<Student>()
        getStudData()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false)
    }

    private fun getStudData() {
        database = FirebaseDatabase.getInstance().getReference("Student")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (studSnapshot in snapshot.children) {
                        val stud = studSnapshot.getValue(Student::class.java)
                        studArrayList.add(stud!!)
                    }
                    studentRecyclerView?.adapter = MyAdapter(studArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimeTable.
         */
        // TODO: Rename and change types and number of parameters
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