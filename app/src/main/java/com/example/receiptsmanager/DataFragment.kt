package com.example.receiptsmanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DataFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptAdapter
    private var receipts = mutableListOf<Receipt>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 🔹 Najpierw tworzymy adapter, zanim wywołamy loadReceipts()
        adapter = ReceiptAdapter(receipts, requireContext())
        recyclerView.adapter = adapter

        // 🔹 Dopiero teraz możemy załadować zapisane paragony
        loadReceipts()

        return view
    }

    private fun loadReceipts() {
        val sharedPreferences = requireContext().getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val receiptsList = sharedPreferences.getStringSet("receipts", emptySet()) ?: emptySet()

        receipts.clear()
        receiptsList.forEach {
            val parts = it.split("|")
            if (parts.size == 2) {
                val file = File(parts[1])
                if (file.exists()) { // Sprawdzamy, czy plik zdjęcia istnieje
                    receipts.add(Receipt(parts[0], parts[1]))
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

}

