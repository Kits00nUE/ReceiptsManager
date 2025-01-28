package com.example.receiptsmanager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private val CAMERA_REQUEST_CODE = 101
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var imageView: ImageView
    private lateinit var editTextDescription: EditText
    private var photoURI: Uri? = null
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        editTextDescription = view.findViewById(R.id.et_receipt_description)
        val buttonTakePhoto = view.findViewById<Button>(R.id.btn_take_photo)
        imageView = view.findViewById(R.id.iv_receipt_preview)

        buttonTakePhoto.setOnClickListener {
            val description = editTextDescription.text.toString().trim()

            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Najpierw wpisz podpis!", Toast.LENGTH_SHORT).show()
            } else {
                checkCameraPermission()
            }
        }

        return view
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Tworzymy plik na zdjęcie
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

        if (photoFile != null) {
            val authority = "${requireContext().packageName}.fileprovider"
            photoURI = FileProvider.getUriForFile(requireContext(), authority, photoFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Aby zrobić zdjęcie, musisz wyrazić zgodę na dostęp do aparatu!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(photoURI)
            saveReceiptToStorage(editTextDescription.text.toString(), currentPhotoPath)
        }
    }

    private fun saveReceiptToStorage(description: String, imagePath: String) {
        val sharedPreferences = requireContext().getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val receiptsList = sharedPreferences.getStringSet("receipts", mutableSetOf())?.toMutableSet()
        receiptsList?.add("$description|$imagePath") // Format: "Nazwa paragonu|Ścieżka do zdjęcia"

        editor.putStringSet("receipts", receiptsList)
        editor.apply()
    }
}
