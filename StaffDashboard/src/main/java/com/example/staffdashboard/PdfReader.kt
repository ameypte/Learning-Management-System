package com.example.staffdashboard

import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.staffdashboard.databinding.FragmentPdfReaderBinding
import com.github.barteksc.pdfviewer.PDFView
import java.net.URL
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PdfReader : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var pdfReaderBinding: FragmentPdfReaderBinding
    private lateinit var pdfView: PDFView

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
        pdfReaderBinding = FragmentPdfReaderBinding.inflate(inflater,container,false)
        pdfReaderBinding.progressBarFileLoading.visibility = View.VISIBLE

        pdfView = pdfReaderBinding.pdfView
        val args = arguments
        val pdfUrl = args?.getString("Url")
        val pdfName = args?.getString("Name")

        pdfReaderBinding.txtFileName.text = pdfName

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val pdfStream = URL(pdfUrl).openStream()
                withContext(Dispatchers.Main) {
                    pdfView.fromStream(pdfStream).load()
                    pdfReaderBinding.progressBarFileLoading.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load PDF file: ${e.message}")
            }
        }
        return pdfReaderBinding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PdfReader().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}