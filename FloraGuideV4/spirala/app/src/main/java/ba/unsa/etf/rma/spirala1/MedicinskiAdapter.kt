package ba.unsa.etf.rma.spirala1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MedicinskiAdapter(
    private val biljke: List<Biljka>,
    private val listener: (Biljka) -> Unit,
    private val trefleDAO: TrefleDAO
) : RecyclerView.Adapter<MedicinskiAdapter.BiljkaViewHolder>(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_medicinski, parent, false)
        return BiljkaViewHolder(view)
    }

    override fun getItemCount(): Int = biljke.size

    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        val biljka = biljke[position]
        holder.bind(biljka)
        holder.itemView.setOnClickListener { listener(biljka) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }

    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val naziv: TextView = itemView.findViewById(R.id.nazivItem)
        private val slika: ImageView = itemView.findViewById(R.id.slikaItem)
        private val korist1: TextView = itemView.findViewById(R.id.korist1Item)
        private val korist2: TextView = itemView.findViewById(R.id.korist2Item)
        private val korist3: TextView = itemView.findViewById(R.id.korist3Item)
        private val upozorenje: TextView = itemView.findViewById(R.id.upozorenjeItem)

        fun bind(biljka: Biljka) {

            naziv.text = biljka.naziv
            // Pokretanje korutine
            launch {
                val bitmap = withContext(Dispatchers.IO) {
                    trefleDAO.getImage(biljka)
                }
                slika.setImageBitmap(bitmap)
                /*val requestOptions = RequestOptions()
                    .transform(CenterCrop(), RoundedCorners(14)) // Chain transformations

                Glide.with(itemView.context)
                        .load(bitmap)
                        .apply(requestOptions)
                        .placeholder(R.drawable.plant)
                        .into(slika)
                */
            }
            korist1.text = biljka.medicinskeKoristi.getOrNull(0)?.opis ?: ""
            korist2.text = biljka.medicinskeKoristi.getOrNull(1)?.opis ?: ""
            korist3.text = biljka.medicinskeKoristi.getOrNull(2)?.opis ?: ""

            if(korist1.text == "") korist1.visibility = View.GONE
            if(korist2.text == "") korist2.visibility = View.GONE
            if(korist3.text == "") korist3.visibility = View.GONE
            upozorenje.text = biljka.medicinskoUpozorenje ?: ""
        }
    }
}
