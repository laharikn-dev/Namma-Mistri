package com.nammamistri.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nammamistri.app.databinding.FragmentLaborBinding
import com.nammamistri.app.databinding.ItemWorkerBinding
import java.text.SimpleDateFormat
import java.util.*

class LaborFragment : Fragment() {
    private var _b: FragmentLaborBinding? = null
    private val b get() = _b!!
    private val workers = mutableListOf<Worker>()
    private lateinit var adapter: WorkerAdapter
    private val today: String get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentLaborBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        workers.addAll(Storage.load<List<Worker>>(requireContext(), "workers", emptyList()))
        adapter = WorkerAdapter(workers, ::persist)
        b.rvWorkers.layoutManager = LinearLayoutManager(requireContext())
        b.rvWorkers.adapter = adapter

        b.btnAdd.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val wage = b.etWage.text.toString().toDoubleOrNull() ?: 0.0
            if (name.isEmpty() || wage <= 0) return@setOnClickListener
            workers.add(Worker(UUID.randomUUID().toString(), name, wage))
            adapter.notifyItemInserted(workers.size - 1)
            b.etName.text.clear(); b.etWage.text.clear()
            persist()
        }
    }

    private fun persist() = Storage.save(requireContext(), "workers", workers)

    override fun onDestroyView() { super.onDestroyView(); _b = null }

    inner class WorkerAdapter(
        private val items: MutableList<Worker>,
        private val onChange: () -> Unit
    ) : RecyclerView.Adapter<WorkerAdapter.VH>() {
        inner class VH(val v: ItemWorkerBinding) : RecyclerView.ViewHolder(v.root)

        override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
            VH(ItemWorkerBinding.inflate(LayoutInflater.from(p.context), p, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(h: VH, pos: Int) {
            val w = items[pos]
            h.v.tvName.text = w.name
            h.v.tvWage.text = "${Storage.inr(w.dailyWage)}/ದಿನ"
            val mark: (String) -> Unit = { s ->
                w.attendance[today] = s
                refresh(h, w); onChange()
            }
            h.v.btnP.setOnClickListener { mark("P") }
            h.v.btnH.setOnClickListener { mark("H") }
            h.v.btnA.setOnClickListener { mark("A") }
            h.v.btnAdv.setOnClickListener {
                val amt = h.v.etAdvance.text.toString().toDoubleOrNull() ?: return@setOnClickListener
                w.advances.add(Advance(today, amt))
                h.v.etAdvance.text.clear()
                refresh(h, w); onChange()
            }
            h.v.btnDelete.setOnClickListener {
                val idx = items.indexOf(w); if (idx >= 0) {
                    items.removeAt(idx); notifyItemRemoved(idx); onChange()
                }
            }
            refresh(h, w)
        }

        private fun refresh(h: VH, w: Worker) {
            val pres = w.attendance.values.count { it == "P" }
            val half = w.attendance.values.count { it == "H" }
            val abs = w.attendance.values.count { it == "A" }
            h.v.tvSummary.text = "ಹಾಜರಿ: P=$pres  H=$half  A=$abs   ಮುಂಗಡ: ${Storage.inr(w.advanceTotal())}"
            h.v.tvBalance.text = "ಬಾಕಿ: ${Storage.inr(w.balance())}"
        }
    }
}
