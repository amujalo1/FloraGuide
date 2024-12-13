package ba.unsa.etf.rma.spirala1

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class BotanickiAdapter(
    private val biljke: List<Biljka>,
    private val listener: (Biljka) -> Unit,
    private val trefleDAO: TrefleDAO
) : RecyclerView.Adapter<BotanickiAdapter.BiljkaViewHolder>(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_botanicki, parent, false)
        return BiljkaViewHolder(view)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }

    override fun getItemCount(): Int = biljke.size

    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        val biljka = biljke[position]
        holder.bind(biljka)
        holder.itemView.setOnClickListener { listener(biljka) }
    }

    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val naziv: TextView = itemView.findViewById(R.id.nazivItem)
        private val slika: ImageView = itemView.findViewById(R.id.slikaItem)
        private val porodica: TextView = itemView.findViewById(R.id.porodicaItem)
        private val zemljisniTip: TextView = itemView.findViewById(R.id.zemljisniTipItem)
        private val klimatskiTip: TextView = itemView.findViewById(R.id.klimatskiTipItem)

        fun bind(biljka: Biljka) {
            naziv.text = biljka.naziv
            // Postavljanje porodice, zemljišnog i klimatskog tipa
            launch {
                val bitmap = withContext(Dispatchers.IO) {
                    trefleDAO.getImage(biljka)
                }
                slika.setImageBitmap(bitmap)
                /*withContext(Dispatchers.Main) {
                    val requestOptions = RequestOptions()
                        .transform(CenterCrop(), RoundedCorners(14)) // Chain transformations

                    Glide.with(itemView.context)
                        .load(bitmap)
                        .apply(requestOptions)
                        .placeholder(R.drawable.plant)
                        .into(slika)
                }*/
            }
            porodica.text = biljka.family
            zemljisniTip.text = biljka.zemljisniTipovi.getOrNull(0)?.naziv ?: ""
            klimatskiTip.text = biljka.klimatskiTipovi.getOrNull(0)?.opis ?: ""
            if(zemljisniTip.text == "") zemljisniTip.visibility = View.GONE
            if(klimatskiTip.text == "") klimatskiTip.visibility = View.GONE
        }
    }
}
