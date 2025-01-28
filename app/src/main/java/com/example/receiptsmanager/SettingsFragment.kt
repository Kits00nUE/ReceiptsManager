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
        val instructionText = """
        📌 **Instrukcja obsługi aplikacji „Paragony”** 📌
        
        🔹 **Cel aplikacji:** 
        Aplikacja pozwala na zapisywanie, przeglądanie i udostępnianie paragonów.

        ✅ **Jak dodać nowy paragon?**
        1️⃣ Przejdź do zakładki **Home** (🏠).  
        2️⃣ Wpisz nazwę paragonu w polu tekstowym.  
        3️⃣ Kliknij **„Zrób zdjęcie”** – otworzy się aparat.  
        4️⃣ Zrób zdjęcie i zatwierdź – paragon zostanie zapisany w pamięci.

        🔍 **Jak przeglądać paragony?**
        1️⃣ Przejdź do zakładki **Paragony** (📜).  
        2️⃣ Znajdziesz tam listę zapisanych paragonów.  
        3️⃣ Kliknij nazwę paragonu, aby zobaczyć jego pełny podgląd.  

        📤 **Jak udostępnić paragon?**
        1️⃣ W zakładce **Paragony** kliknij przycisk **„📤 Udostępnij”** obok wybranego paragonu.  
        2️⃣ Wybierz aplikację, przez którą chcesz udostępnić zdjęcie.  

        🗑 **Jak usunąć paragon?**
        1️⃣ Kliknij ikonę kosza **„🗑 Usuń”** obok wybranego paragonu.  
        2️⃣ Paragon zostanie trwale usunięty z aplikacji.  

        🔔 **Powiadomienia:**  
        Po dodaniu nowego paragonu pojawi się powiadomienie potwierdzające jego zapis.  

        ℹ️ **Dodatkowe informacje:**  
        - Aplikacja nie przechowuje paragonów w chmurze – wszystkie dane są lokalne.  
        - Po usunięciu paragonu **nie można go odzyskać**.  

        ✅ **Gotowe! Teraz możesz bezpiecznie zarządzać swoimi paragonami.**
    """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📖 Instrukcja obsługi")
            .setMessage(instructionText)
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
