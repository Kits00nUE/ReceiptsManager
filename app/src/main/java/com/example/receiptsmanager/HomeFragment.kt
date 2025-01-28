package com.example.receiptsmanager

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var editTextDescription: EditText
    private var photoURI: Uri? = null
    private lateinit var currentPhotoPath: String

    // 🔹 Launcher dla nowego systemu uprawnień (Android 11+)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Brak dostępu do aparatu!", Toast.LENGTH_SHORT).show()
            }
        }

    // 🔹 Launcher dla nowego systemu `startActivityForResult()`
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(photoURI)
                saveReceiptToStorage(editTextDescription.text.toString(), currentPhotoPath)
                showNotification(editTextDescription.text.toString()) // 🔔 Powiadomienie
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        editTextDescription = view.findViewById(R.id.et_receipt_description)
        val buttonTakePhoto = view.findViewById<Button>(R.id.btn_take_photo)
        imageView = view.findViewById(R.id.iv_receipt_preview)

        createNotificationChannel() // 🔔 Tworzenie kanału powiadomień

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

    // 🔹 Sprawdzanie i proszenie o uprawnienia do aparatu
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // 🔹 Otwieranie aparatu
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
            cameraLauncher.launch(cameraIntent) // 🔹 Uruchamiamy kamerę przez `ActivityResultLauncher`
        }
    }

    // 🔹 Tworzenie pliku na zdjęcie
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    // 🔹 Zapisywanie danych o paragonie w SharedPreferences
    private fun saveReceiptToStorage(description: String, imagePath: String) {
        val sharedPreferences = requireContext().getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val receiptsList = sharedPreferences.getStringSet("receipts", mutableSetOf())?.toMutableSet()
        receiptsList?.add("$description|$imagePath") // Format: "Nazwa|Ścieżka do pliku"

        editor.putStringSet("receipts", receiptsList)
        editor.apply()
    }

    // 🔹 Tworzenie kanału powiadomień
    private fun createNotificationChannel() {

            val name = "Receipt Notifications"
            val descriptionText = "Kanał powiadomień dla aplikacji Paragony"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("receipt_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

    }

    // 🔹 Wyświetlanie powiadomienia o nowym paragonie
    private fun showNotification(description: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        if (!isNotificationsEnabled) return

        val builder = NotificationCompat.Builder(requireContext(), "receipt_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nowy paragon")
            .setContentText("Dodano: $description")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(requireContext()).notify(1, builder.build())
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationManagerCompat.from(requireContext()).notify(1, builder.build())
        }
    }
}
