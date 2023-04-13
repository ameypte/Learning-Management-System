package com.example.staffdashboard

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.staffdashboard.databinding.FragmentViewMaterialBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var toast: Toast
private lateinit var database: DatabaseReference


class ViewMaterial : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewMaterialBinding: FragmentViewMaterialBinding

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
        viewMaterialBinding = FragmentViewMaterialBinding.inflate(inflater, container, false)
        val args = arguments
        val courseCode = args?.getString("courseCode")
        val courseTitle = args?.getString("courseTitle")

        val spinner = viewMaterialBinding.simpleSpinner
        val items = arrayOf(
            "UNIT 1",
            "UNIT 2",
            "UNIT 3",
            "UNIT 4",
            "UNIT 5",
            "UNIT 6",
        )
        val adapter =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                updateNotesList(courseCode, spinner.selectedItem.toString())
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        viewMaterialBinding.selectedCourse.text = courseTitle

        viewMaterialBinding.btnViewCurriculum.setOnClickListener {
            getFirestoreData(courseCode)
        }

        viewMaterialBinding.notesList.setOnItemClickListener { parent, view, position, id ->
            val fileToOpen = parent.getItemAtPosition(position).toString();
            val index = fileToOpen.lastIndexOf('.')
            val fileKey = if (index == -1) fileToOpen else fileToOpen.substring(0, index)

            database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
                .child("notes").child(spinner.selectedItem.toString()).child(fileKey).child("notesUrl")

            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("QueryPermissionsNeeded")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val url = snapshot.value.toString()
                    val storageReference: StorageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(url)
                    storageReference.downloadUrl.addOnSuccessListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(it.toString())
                        if (fileExtension == "pdf"){
                            intent.setDataAndType(it, "application/pdf")
                        }
                        else if (fileExtension == "docx"){
                            intent.setDataAndType(it, "application/msword")
                        }
                        else if (fileExtension == "pptx"){
                            intent.setDataAndType(it, "application/vnd.ms-powerpoint")
                        }
                        else if (fileExtension == "xlsx"){
                            intent.setDataAndType(it, "application/vnd.ms-excel")
                        }
                        else if (fileExtension == "zip"){
                            intent.setDataAndType(it, "application/zip")
                        }
                        else if (fileExtension == "rar"){
                            intent.setDataAndType(it, "application/rar")
                        }
                        else if (fileExtension == "txt"){
                            intent.setDataAndType(it, "text/plain")
                        }
                        else if (fileExtension == "jpg" || fileExtension == "jpeg" || fileExtension == "png"){
                            intent.setDataAndType(it, "image/*")
                        }
                        else if (fileExtension == "mp3"){
                            intent.setDataAndType(it, "audio/*")
                        }
                        else if (fileExtension == "mp4"){
                            intent.setDataAndType(it, "video/*")
                        }
                        else{
                            intent.setDataAndType(it, "*/*")
                        }
//                        intent.setDataAndType(it, null )
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            if (::toast.isInitialized)
                                toast.cancel()
                            toast = Toast.makeText(
                                requireContext(),
                                "No Application found to open this file",
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (::toast.isInitialized)
                        toast.cancel()
                    toast = Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                    toast.show()
                }
            })

        }

        return viewMaterialBinding.root
    }

    private fun updateNotesList(courseCode: String?, toString: String) {
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
            .child("notes").child(toString)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notesList = ArrayList<String>()
                for (notes in snapshot.children) {
                    notesList.add(notes.key.toString()+fileExtension(notes.child("notesUrl").value.toString()))
                }
                val notesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    notesList
                )
                viewMaterialBinding.notesList.adapter = notesAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                if (::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }

    private fun fileExtension(toString: String): Any? {
        return "." + MimeTypeMap.getFileExtensionFromUrl(toString)
    }

    private fun getFirestoreData(courseCode: String?) {
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
            .child("curriculum")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val curriculumUrl = snapshot.child("curriculumUrl").value.toString()
                    val curriculumName = snapshot.child("curriculumName").value.toString()
                    val bundle = Bundle()
                    bundle.putString("Url", curriculumUrl)
                    bundle.putString("Name", curriculumName)
                    val pdfRenderer = PdfReader()
                    pdfRenderer.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.dashFrameLayout, pdfRenderer)
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    if (::toast.isInitialized)
                        toast.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        "No Curriculum Uploaded",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                toast.show()
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewMaterial().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}