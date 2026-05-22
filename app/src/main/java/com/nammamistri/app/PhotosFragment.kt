package com.nammamistri.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nammamistri.app.databinding.FragmentPhotosBinding
import com.nammamistri.app.databinding.ItemPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class PhotosFragment : Fragment() {
    private var _b: FragmentPhotosBinding? = null
    private val b get() = _b!!
    private val photos = mutableListOf<Photo>()
    private lateinit var adapter: Adapter

    private val pick = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@registerForActivityResult
        try {
            requireContext().contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {}
        val cap = b.etCaption.text.toString().ifBlank { "Site Photo" }
        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        photos.add(0, Photo(UUID.randomUUID().toString(), uri.toString(), cap, date))
        adapter.notifyItemInserted(0); b.rvPhotos.scrollToPosition(0)
        b.etCaption.text.clear(); persist()
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentPhotosBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        photos.addAll(Storage.load<List<Photo>>(requireContext(), "photos", emptyList()))
        adapter = Adapter()
        b.rvPhotos.layoutManager = LinearLayoutManager(requireContext())
        b.rvPhotos.adapter = adapter
        b.btnPick.setOnClickListener { pick.launch(arrayOf("image/*")) }
    }

    private fun persist() = Storage.save(requireContext(), "photos", photos)
    override fun onDestroyView() { super.onDestroyView(); _b = null }

    inner class Adapter : RecyclerView.Adapter<Adapter.VH>() {
        inner class VH(val v: ItemPhotoBinding) : RecyclerView.ViewHolder(v.root)
        override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
            VH(ItemPhotoBinding.inflate(LayoutInflater.from(p.context), p, false))
        override fun getItemCount() = photos.size
        override fun onBindViewHolder(h: VH, pos: Int) {
            val p = photos[pos]
            h.v.img.setImageURI(Uri.parse(p.uri))
            h.v.tvCaption.text = p.caption
            h.v.tvDate.text = p.date
            h.v.btnDel.setOnClickListener {
                val i = photos.indexOf(p); if (i >= 0) {
                    photos.removeAt(i); notifyItemRemoved(i); persist()
                }
            }
        }
    }
}
