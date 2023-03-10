package com.example.staffdashboard

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        return uploadMaterialBinding.root
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
            val data: Intent = result.data!!
            fileUrl = data.data!!
            val file = DocumentFile.fromSingleUri(requireContext(), fileUrl)
            val fileName = file!!.name
            uploadMaterialBinding.btnChooseCurriculum.text = fileName
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