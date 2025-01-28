package com.example.receiptsmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Pobranie referencji do pól
        val editTextDescription = view.findViewById<EditText>(R.id.et_receipt_description)
        val buttonTakePhoto = view.findViewById<Button>(R.id.btn_take_photo)

        // Obsługa kliknięcia przycisku (chwilowo bez backendu)
        buttonTakePhoto.setOnClickListener {
            val description = editTextDescription.text.toString().trim()

            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Najpierw wpisz podpis!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Przycisk działa! W przyszłości otworzymy aparat.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
