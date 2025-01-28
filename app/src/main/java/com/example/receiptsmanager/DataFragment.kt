package com.example.receiptsmanager

import android.annotation.SuppressLint
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

        adapter = ReceiptAdapter(receipts, requireContext())
        recyclerView.adapter = adapter

        loadReceipts()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadReceipts() {
        val sharedPreferences =
            requireContext().getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val receiptsList = sharedPreferences.getStringSet("receipts", emptySet()) ?: emptySet()

        val newReceipts = mutableListOf<Receipt>()
        receiptsList.forEach {
            val parts = it.split("|")
            if (parts.size == 2) {
                val file = File(parts[1])
                if (file.exists()) {
                    newReceipts.add(Receipt(parts[0], parts[1]))
                }
            }
        }


        val oldSize = receipts.size
        val newSize = newReceipts.size

        if (newSize > oldSize) {
            receipts.addAll(newReceipts.subList(oldSize, newSize))
            adapter.notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else if (newSize < oldSize) {
            receipts.clear()
            receipts.addAll(newReceipts)
            adapter.notifyItemRangeRemoved(
                newSize,
                oldSize - newSize
            )
        } else {
            receipts.clear()
            receipts.addAll(newReceipts)
            adapter.notifyDataSetChanged()
        }
    }
}
