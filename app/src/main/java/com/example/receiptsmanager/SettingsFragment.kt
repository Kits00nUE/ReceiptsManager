package com.example.receiptsmanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Pobranie referencji do przycisków
        val buttonInstruction = view.findViewById<Button>(R.id.btn_instruction)
        val buttonFaq = view.findViewById<Button>(R.id.btn_faq)

        // Obsługa kliknięcia przycisku "Instrukcja"
        buttonInstruction.setOnClickListener {
            showInstructionDialog()
        }

        // Obsługa kliknięcia przycisku "FAQ"
        buttonFaq.setOnClickListener {
            showFaqDialog()
        }

        return view
    }

    // Funkcja wyświetlająca instrukcję obsługi
    private fun showInstructionDialog() {
        val message = """
            📌 Jak używać aplikacji?
            
            1️⃣ Otwórz zakładkę "Home".
            2️⃣ Wpisz nazwę lub opis paragonu.
            3️⃣ Kliknij "Zrób zdjęcie" i sfotografuj paragon.
            4️⃣ Zdjęcie zostanie zapisane w aplikacji.
            5️⃣ Przejdź do zakładki "Data", aby zobaczyć wszystkie zapisane paragony.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Instrukcja Obsługi")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Funkcja wyświetlająca żartobliwe FAQ
    private fun showFaqDialog() {
        val message = """
            ❓ FAQ
            
            🔹 Jak działa gwarancja?
            📝 "Gwarancja - do bramy i się nie znamy!"
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("FAQ")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
