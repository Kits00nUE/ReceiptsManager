package com.example.receiptsmanager

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ReceiptAdapter(private val receipts: MutableList<Receipt>, private val context: Context) :
    RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.description.text = receipt.description
        holder.imageView.setImageURI(Uri.fromFile(File(receipt.imagePath)))

        // Kliknięcie w nazwę paragonu otwiera podgląd zdjęcia
        holder.description.setOnClickListener {
            showImageDialog(receipt.imagePath, holder.itemView.context)
        }

        // Usuwanie paragonu po kliknięciu przycisku "Usuń"
        holder.deleteButton.setOnClickListener {
            removeReceipt(position)
        }
    }

    override fun getItemCount() = receipts.size

    private fun showImageDialog(imagePath: String, context: Context) {
        val dialog = AlertDialog.Builder(context)
        val imageView = ImageView(context)
        imageView.setImageURI(Uri.fromFile(File(imagePath)))

        dialog.setView(imageView)
        dialog.setNegativeButton("Zamknij") { d, _ -> d.dismiss() }
        dialog.show()
    }

    private fun removeReceipt(position: Int) {
        val receipt = receipts[position]
        val file = File(receipt.imagePath)

        if (file.exists()) {
            file.delete() // 🔥 Usunięcie zdjęcia z pamięci
        }

        receipts.removeAt(position) // 🔥 Usunięcie z listy w aplikacji
        notifyItemRemoved(position)

        val sharedPreferences = context.getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val receiptsList = sharedPreferences.getStringSet("receipts", mutableSetOf())?.toMutableSet()
        receiptsList?.remove("${receipt.description}|${receipt.imagePath}") // 🔥 Usunięcie z `SharedPreferences`

        editor.putStringSet("receipts", receiptsList)
        editor.apply()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.tv_receipt_description)
        val imageView: ImageView = view.findViewById(R.id.iv_receipt_image)
        val deleteButton: Button = view.findViewById(R.id.btn_delete)
    }
}
