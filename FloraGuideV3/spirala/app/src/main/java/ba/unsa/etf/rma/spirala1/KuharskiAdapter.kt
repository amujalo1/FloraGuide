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

class KuharskiAdapter(
    private var biljke: List<Biljka>,
    private val listener: (Biljka) -> Unit,
    private val trefleDAO: TrefleDAO
) : RecyclerView.Adapter<KuharskiAdapter.BiljkaViewHolder>(), CoroutineScope{

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_kuharski, parent, false)
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
        private val profilOkusa: TextView = itemView.findViewById(R.id.profilOkusaItem)
        private val jelo1: TextView = itemView.findViewById(R.id.jelo1Item)
        private val jelo2: TextView = itemView.findViewById(R.id.jelo2Item)
        private val jelo3: TextView = itemView.findViewById(R.id.jelo3Item)

        fun bind(biljka: Biljka) {
            naziv.text = biljka.naziv
            launch {
                val bitmap = withContext(Dispatchers.IO) {
                    trefleDAO.getImage(biljka)
                }
                withContext(Dispatchers.Main) {
                    val requestOptions = RequestOptions()
                        .transform(CenterCrop(), RoundedCorners(14)) // Chain transformations

                    Glide.with(itemView.context)
                        .load(bitmap)
                        .apply(requestOptions)
                        .placeholder(R.drawable.plant)
                        .into(slika)
                }
            }
            profilOkusa.text = biljka.profilOkusa.opis
            jelo1.text = biljka.jela.getOrNull(0) ?: ""
            jelo2.text = biljka.jela.getOrNull(1) ?: ""
            jelo3.text = biljka.jela.getOrNull(2) ?: ""
            if(jelo1.text == "") jelo1.visibility = View.GONE
            if(jelo2.text == "") jelo2.visibility = View.GONE
            if(jelo3.text == "") jelo3.visibility = View.GONE
        }
    }
}
