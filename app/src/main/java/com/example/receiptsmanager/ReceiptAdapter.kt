package com.example.receiptsmanager

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import androidx.core.content.FileProvider

class ReceiptAdapter(private val receipts: MutableList<Receipt>, private val context: Context) :
    RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.description.text = receipt.description

        val file = File(receipt.imagePath)
        if (file.exists()) {
            holder.imageView.setImageURI(Uri.fromFile(file))
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder) // Jeśli plik nie istnieje
        }

        holder.imageView.contentDescription = "Zdjęcie paragonu: ${receipt.description}"

        // Kliknięcie w nazwę paragonu otwiera podgląd zdjęcia
        holder.description.setOnClickListener {
            if (file.exists()) {
                showImageDialog(receipt.imagePath, holder.itemView.context)
            } else {
                Toast.makeText(holder.itemView.context, "Zdjęcie nie istnieje", Toast.LENGTH_SHORT).show()
            }
        }

        // Udostępnianie paragonu (Intencja NIEJAWNA)
        holder.shareButton.setOnClickListener {
            shareReceipt(receipt.imagePath)
        }

        // Usuwanie paragonu
        holder.deleteButton.setOnClickListener {
            removeReceipt(position)
        }
    }

    override fun getItemCount() = receipts.size

    //  Funkcja do wyświetlania zdjęcia w dialogu
    private fun showImageDialog(imagePath: String, context: Context) {
        val file = File(imagePath)

        if (!file.exists()) {
            Toast.makeText(context, "Zdjęcie nie istnieje", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = AlertDialog.Builder(context)
        val imageView = ImageView(context)
        imageView.setImageURI(Uri.fromFile(file))

        dialog.setView(imageView)
        dialog.setNegativeButton("Zamknij") { d, _ -> d.dismiss() }
        dialog.show()
    }

    // 📤 Udostępnianie paragonu
    private fun shareReceipt(imagePath: String) {
        val file = File(imagePath)

        if (!file.exists()) {
            Toast.makeText(context, "Nie można udostępnić – plik nie istnieje", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Udostępnij paragon"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Błąd podczas udostępniania: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 🗑️ Usuwanie paragonu
    private fun removeReceipt(position: Int) {
        val receipt = receipts[position]
        val file = File(receipt.imagePath)

        if (file.exists()) {
            val deleted = file.delete()
            if (!deleted) {
                Toast.makeText(context, "Błąd podczas usuwania pliku", Toast.LENGTH_SHORT).show()
                return
            }
        }

        receipts.removeAt(position)
        notifyItemRemoved(position)

        val sharedPreferences = context.getSharedPreferences("ReceiptsData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val receiptsList = sharedPreferences.getStringSet("receipts", mutableSetOf())?.toMutableSet()
        receiptsList?.remove("${receipt.description}|${receipt.imagePath}")

        editor.putStringSet("receipts", receiptsList)
        editor.apply()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.tv_receipt_description)
        val imageView: ImageView = view.findViewById(R.id.iv_receipt_image)
        val deleteButton: Button = view.findViewById(R.id.btn_delete)
        val shareButton: Button = view.findViewById(R.id.btn_share) // Dodajemy przycisk udostępniania
    }
}
