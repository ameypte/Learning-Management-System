package com.example.staffdashboard.courses

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.staffdashboard.databinding.FragmentPdfReaderBinding
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PdfReader : Fragment() {

    private lateinit var pdfReaderBinding: FragmentPdfReaderBinding
    private lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pdfReaderBinding = FragmentPdfReaderBinding.inflate(inflater, container, false)
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
                Log.e(ContentValues.TAG, "Failed to load PDF file: ${e.message}")
            }
        }
        return pdfReaderBinding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PdfReader().apply {
                arguments = Bundle().apply {
                }
            }
    }
}