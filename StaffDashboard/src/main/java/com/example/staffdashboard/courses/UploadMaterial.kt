package com.example.staffdashboard.courses

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.example.staffdashboard.databinding.FragmentUploadMaterialBinding
import com.example.staffdashboard.notification.NotificationData
import com.example.staffdashboard.notification.PushNotification
import com.example.staffdashboard.notification.api.ApiUtilities
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadMaterial() : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var uploadMaterialBinding: FragmentUploadMaterialBinding
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fileUrl: Uri
    private lateinit var toast: Toast
    private lateinit var notesUrl: Uri
    private lateinit var selectedCourse: String
    private lateinit var staffName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uploadMaterialBinding = FragmentUploadMaterialBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            getString(com.example.staffdashboard.R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        val args = arguments
        val courseCode = args?.getString("courseCode")
        val courseTitle = args?.getString("courseTitle")
        staffName = sharedPreferences.getString("loggedStaffName", "abc").toString()


        uploadMaterialBinding.selectedCourse.text = "$courseCode $courseTitle"

        uploadMaterialBinding.btnChooseCurriculum.setOnClickListener {
            chooseFile()
        }
        uploadMaterialBinding.btnUploadCurriculum.setOnClickListener {
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
            uploadNotes(courseCode, unit)
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
            ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item, items)
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
        selectedCourse = uploadMaterialBinding.selectedCourse.text.toString()
        if (uploadMaterialBinding.btnChooseNotes.text == "Choose File") {
            if (::toast.isInitialized)
                toast.cancel()
            toast = Toast.makeText(requireContext(), "Please Choose a File", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            if (uploadMaterialBinding.simpleSpinner.selectedItem.toString() == "Select Unit") {
                if (::toast.isInitialized)
                    toast.cancel()
                toast = Toast.makeText(requireContext(), "Please Select a Unit", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                uploadMaterialBinding.pbNotesUpload.visibility = View.VISIBLE
                val msg = uploadMaterialBinding.etOthers.text.toString()
                database =
                    FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
                        .child("notes").child(unit)
                storageReference = FirebaseStorage.getInstance()
                    .getReference("notes/${courseCode}/$unit/${uploadMaterialBinding.btnChooseNotes.text}")

                storageReference.putFile(notesUrl)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            var key = uploadMaterialBinding.btnChooseNotes.text.toString()
                            key = key.substringBeforeLast(".")
                            database.child(key).child("message").setValue(msg)
                            database.child(key).child("notesUrl").setValue(it.toString())
                                .addOnSuccessListener {
                                    sendNotification("Notes", courseCode, selectedCourse, staffName,unit,msg)
                                }
                        }
                    }
                    .addOnFailureListener {
                        if (::toast.isInitialized)
                            toast.cancel()
                        toast = Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                        toast.show()
                    }
            }
        }
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
                    transaction.replace(
                        com.example.staffdashboard.R.id.dashFrameLayout,
                        pdfRenderer
                    )
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


    private fun chooseFile() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        getFile.launch(intent)
    }

    private fun uploadCurriculum(courseCode: String?) {
        selectedCourse = uploadMaterialBinding.selectedCourse.text.toString()
        if (uploadMaterialBinding.btnChooseCurriculum.text == "Choose File") {
            if (::toast.isInitialized)
                toast.cancel()
            toast = Toast.makeText(requireContext(), "Please Choose a File", Toast.LENGTH_SHORT)
            toast.show()
            return
        }



        uploadMaterialBinding.pbCurriculumUpload.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("Courses").child(courseCode!!)
            .child("curriculum")
        storageReference = FirebaseStorage.getInstance()
            .getReference("curriculum/${uploadMaterialBinding.selectedCourse.text}")
        storageReference.putFile(fileUrl)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    database.child("curriculumUrl").setValue(it.toString())
                    database.child("curriculumName").setValue(selectedCourse).addOnSuccessListener {
                        uploadMaterialBinding.btnChooseCurriculum.text = "Choose File"

                        sendNotification("Curriculum", courseCode, selectedCourse, staffName)

                    }
                }
            }
    }

    private fun sendNotification(
        type: String,
        courseCode: String,
        selectedCourse: String,
        staffName: String?,
        unit: String = "",
        massage: String = ""
    ) {
        val topic = "/topics/$courseCode"
        val title = "New $type Uploaded"
        val body = if (unit == "") {
            "$staffName has uploaded $type for $selectedCourse"
        }else{
            "$staffName has uploaded $type for $selectedCourse\nof $unit Message: $massage"
        }
        val notificationData = PushNotification(
            NotificationData(
                title = title,
                body = body,
                type = type,
            ),
            topic
        )
        val apiInterface = ApiUtilities.getInstance()
        val call = apiInterface.sendNotification(notificationData)

        call.enqueue(object : Callback<PushNotification> {
            override fun onResponse(
                call: Call<PushNotification>,
                response: Response<PushNotification>
            ) {
                if (response.isSuccessful) {
                    if (::toast.isInitialized)
                        toast.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        "$type Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    uploadMaterialBinding.pbCurriculumUpload.visibility = View.GONE
                    uploadMaterialBinding.pbNotesUpload.visibility = View.GONE
                    uploadMaterialBinding.etOthers.setText("")
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.errorBody().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val getFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data = result.data
                val fileUri = data?.data
                if (fileUri != null) {
                    val file = DocumentFile.fromSingleUri(requireContext(), fileUri)
                    if (file != null && file.type == "application/pdf") {
                        fileUrl = fileUri
                        uploadMaterialBinding.btnChooseCurriculum.text = file.name
                    } else {
                        // Show error message
                        Toast.makeText(
                            requireContext(),
                            "Please select a PDF file",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }

    private val getNotes =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
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
                }
            }
    }
}