package com.example.staffdashboard

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.example.staffdashboard.databinding.FragmentUploadMaterialBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UploadMaterial() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var uploadMaterialBinding: FragmentUploadMaterialBinding
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var fileUrl : Uri
    private lateinit var toast: Toast
    private lateinit var notesUrl : Uri

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

        uploadMaterialBinding.selectedCourse.text = "$courseCode $courseTitle"

        uploadMaterialBinding.btnChooseCurriculum.setOnClickListener{
            chooseFile()
        }
        uploadMaterialBinding.btnUploadCurriculum.setOnClickListener{
            uploadCurriculum(courseCode)
        }

        uploadMaterialBinding.btnViewCurriculum.setOnClickListener {
            getFirestoreData(courseCode)
        }

        uploadMaterialBinding.btnChooseNotes.setOnClickListener {
            chooseNotes()
        }

        uploadMaterialBinding.btnUploadNotes.setOnClickListener {
            val unit = uploadMaterialBinding.simpleSpinner.selectedItem.toString()
            uploadNotes(courseCode,unit)
        }

        val spinner = uploadMaterialBinding.simpleSpinner
        val items = arrayOf(
            "Select Unit",
            "UNIT 1",
            "UNIT 2",
            "UNIT 3",
            "UNIT 4",
            "UNIT 5",
            "UNIT 6",
            "OTHERS"
        )
        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {

            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }


        return uploadMaterialBinding.root
    }

    private fun chooseNotes() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        getNotes.launch(intent)
    }


    private fun uploadNotes(courseCode: String?, unit: String) {
        if (uploadMaterialBinding.btnChooseNotes.text == "Choose File"){
            if(::toast.isInitialized)
                toast.cancel()
            toast = Toast.makeText(requireContext(),"Please Choose a File",Toast.LENGTH_SHORT)
            toast.show()
        }
        else{
            if (uploadMaterialBinding.simpleSpinner.selectedItem.toString() == "Select Unit") {
                if (::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(), "Please Select a Unit", Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                uploadMaterialBinding.pbNotesUpload.visibility = View.VISIBLE
                database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!).child("notes").child(unit)
                storageReference = FirebaseStorage.getInstance().getReference("notes/${courseCode}/$unit/${uploadMaterialBinding.btnChooseNotes.text}")

                storageReference.putFile(notesUrl)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            var key = uploadMaterialBinding.btnChooseNotes.text.toString()
                            key = key.substringBeforeLast(".")
                            database.child(key).child("notesUrl").setValue(it.toString()).addOnSuccessListener {
                                if(::toast.isInitialized)
                                    toast.cancel()
                                toast = Toast.makeText(requireContext(), "Notes Uploaded Successfully", Toast.LENGTH_SHORT)
                                toast.show()
                                uploadMaterialBinding.btnChooseNotes.text = "Choose File"
                                uploadMaterialBinding.simpleSpinner.setSelection(0)
                                uploadMaterialBinding.pbNotesUpload.visibility = View.GONE
                            }
                        }
                    }
                    .addOnFailureListener {
                        if(::toast.isInitialized)
                            toast.cancel()
                        toast = Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                        toast.show()
                    }
            }
        }
    }

    private fun getFirestoreData(courseCode: String?) {
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!).child("curriculum")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val curriculumUrl = snapshot.child("curriculumUrl").value.toString()
                    val curriculumName = snapshot.child("curriculumName").value.toString()
                    val bundle = Bundle()
                    bundle.putString("Url",curriculumUrl)
                    bundle.putString("Name",curriculumName)
                    val pdfRenderer = PdfReader()
                    pdfRenderer.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.dashFrameLayout,pdfRenderer)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else{
                    if(::toast.isInitialized)
                        toast.cancel()
                    toast = Toast.makeText(requireContext(),"No Curriculum Uploaded",Toast.LENGTH_SHORT)
                    toast.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if(::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }


    private fun chooseFile() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        getFile.launch(intent)
    }

    private fun uploadCurriculum(courseCode: String?) {
        if (uploadMaterialBinding.btnChooseCurriculum.text == "Choose File"){
            if(::toast.isInitialized)
                toast.cancel()
            toast = Toast.makeText(requireContext(), "Please Choose a File", Toast.LENGTH_SHORT)
            toast.show()
            return
        }

        uploadMaterialBinding.pbCurriculumUpload.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!).child("curriculum")
        storageReference = FirebaseStorage.getInstance().getReference("curriculum/${uploadMaterialBinding.selectedCourse.text}")

        storageReference.putFile(fileUrl)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    database.child("curriculumUrl").setValue(it.toString())
                    database.child("curriculumName").setValue(uploadMaterialBinding.selectedCourse.text.toString()).addOnSuccessListener {
                        uploadMaterialBinding.btnChooseCurriculum.text = "Choose File"
                        if(::toast.isInitialized)
                            toast.cancel()
                        toast = Toast.makeText(requireContext(),"Curriculum Uploaded Successfully",Toast.LENGTH_SHORT)
                        toast.show()
                        uploadMaterialBinding.pbCurriculumUpload.visibility = View.GONE
                    }
                }
            }
    }

    private val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data
            val fileUri = data?.data
            if (fileUri != null) {
                val file = DocumentFile.fromSingleUri(requireContext(), fileUri)
                if (file != null && file.type == "application/pdf") {
                    fileUrl = fileUri
                    uploadMaterialBinding.btnChooseCurriculum.text = file.name
                } else {
                    // Show error message
                    Toast.makeText(requireContext(), "Please select a PDF file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val getNotes = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data
            val fileUri = data?.data
            if (fileUri != null) {
                val file = DocumentFile.fromSingleUri(requireContext(), fileUri)
                    notesUrl = fileUri
                if (file != null) {
                    uploadMaterialBinding.btnChooseNotes.text = file.name
                }
            }
        }
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