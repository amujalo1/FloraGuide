package ba.unsa.etf.rma.spirala1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var biljkeStaticData: BiljkeStaticData
    private lateinit var modSpinner: Spinner
    private lateinit var bojeSpinner: Spinner 
    private lateinit var resetBtn: Button
    private lateinit var brzaPretraga: Button
    private lateinit var novaBiljkaBtn: Button
    private lateinit var biljkeRV: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var pretragaBoja: LinearLayout
    private lateinit var pretragaET: EditText
    private lateinit var trefleDAO: TrefleDAO
    private lateinit var odabranaBoja: String
    private var toggle: Boolean = false
    private var modovi = arrayOf("Medicinski", "Kuharski", "Botanički")
    private var modoviBoja = arrayOf("boje","red", "blue", "yellow", "orange", "purple","brown","green")
    private var trenutneBiljke: List<Biljka> = listOf()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biljkeStaticData = BiljkeStaticData.getInstance()

        trefleDAO = TrefleDAO()
        trefleDAO.setContext(this)

        modSpinner = findViewById(R.id.modSpinner)
        resetBtn = findViewById(R.id.resetBtn)
        pretragaET = findViewById(R.id.pretragaET)
        brzaPretraga = findViewById(R.id.brzaPretraga)
        novaBiljkaBtn = findViewById(R.id.novaBiljkaBtn)
        biljkeRV = findViewById(R.id.biljkeRV)
        pretragaBoja = findViewById(R.id.pretragaBoja)
        bojeSpinner = findViewById(R.id.bojaSPIN)
        // Postavljanje adaptera na RecyclerView
        trenutneBiljke = biljkeStaticData.getBiljke()
        layoutManager = LinearLayoutManager(this)
        biljkeRV.layoutManager = layoutManager
        adapter = MedicinskiAdapter(trenutneBiljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
        biljkeRV.adapter = adapter
        pretragaBoja.visibility = View.GONE
        // Postavljanje spinnera
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modovi)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modSpinner.adapter = spinnerAdapter
        modSpinner.setSelection(0)

        val bojeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modoviBoja)
        bojeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bojeSpinner.adapter = bojeAdapter
        bojeSpinner.setSelection(0)


        // Postavljanje onClickListener-a za dugme za reset
        resetBtn.setOnClickListener {
            toggle = false
            resetMod()
        }
        brzaPretraga.setOnClickListener{
            if(odabranaBoja != "boje" && pretragaET.text.toString() != ""){
                launch{
                    toggle = true
                    val biljke = withContext(Dispatchers.IO) {
                        trefleDAO.getPlantsWithFlowerColor(odabranaBoja, pretragaET.text.toString())
                    }
                    adapter = BotanickiAdapter(biljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
                    biljkeRV.adapter = adapter
                }
            }

        }
        novaBiljkaBtn.setOnClickListener {
            val intent = Intent(this, NovaBiljkaActivity::class.java)
            startActivity(intent)
        }

        // Postavljanje itemSelectedListener-a za spinner
        modSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toggle = false
                adapter = when (position) {
                    0 -> {
                        bojeSpinner.setSelection(0)
                        pretragaET.text = null
                        pretragaBoja.visibility = View.GONE
                        MedicinskiAdapter(trenutneBiljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
                    }
                    1 -> {
                        bojeSpinner.setSelection(0)
                        pretragaET.text = null
                        pretragaBoja.visibility = View.GONE
                        KuharskiAdapter(trenutneBiljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
                    }
                    2 -> {
                        pretragaBoja.visibility = View.VISIBLE
                        BotanickiAdapter(trenutneBiljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
                    }
                    else -> throw IllegalStateException("Nepoznat mod")
                }
                biljkeRV.adapter = adapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Implementacija nije potrebna
            }
        }
        bojeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                odabranaBoja = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Implementacija ukoliko ništa nije odabrano
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetMod()
    }

    //@SuppressLint("NotifyDataSetChanged")
    private fun resetMod() {
        // Resetiranje na defaultni mod (Medicinski)
        modSpinner.setSelection(0)
        trenutneBiljke = biljkeStaticData.getBiljke()
        //adapter.notifyDataSetChanged()
        layoutManager = LinearLayoutManager(this)
        biljkeRV.layoutManager = layoutManager
        adapter = MedicinskiAdapter(trenutneBiljke, { biljka -> showBiljkaDetails(biljka) }, trefleDAO)
        biljkeRV.adapter = adapter
    }

    private fun showBiljkaDetails(biljka: Biljka) {
        if (!toggle){
            when (modSpinner.selectedItemPosition) {
                0 -> { // Medicinski mod
                    val similarBiljke = biljkeStaticData.getBiljke().filter { it.medicinskeKoristi.intersect(biljka.medicinskeKoristi).isNotEmpty() }
                    biljkeRV.adapter = MedicinskiAdapter(similarBiljke, { clickedBiljka -> showBiljkaDetails(clickedBiljka) }, trefleDAO)
                    trenutneBiljke = similarBiljke
                }
                1 -> { // Kuharski mod
                    val similarBiljke = biljkeStaticData.getBiljke().filter { it.profilOkusa == biljka.profilOkusa || it.jela.intersect(biljka.jela).isNotEmpty() }
                    biljkeRV.adapter = KuharskiAdapter(similarBiljke, { clickedBiljka -> showBiljkaDetails(clickedBiljka) }, trefleDAO)
                    trenutneBiljke = similarBiljke
                }
                2 -> { // Botanički mod
                    val similarBiljke = biljkeStaticData.getBiljke().filter { it.porodica == biljka.porodica && it.klimatskiTipovi.intersect(biljka.klimatskiTipovi).isNotEmpty() && it.zemljisniTipovi.intersect(biljka.zemljisniTipovi).isNotEmpty() }
                    biljkeRV.adapter = BotanickiAdapter(similarBiljke, { clickedBiljka -> showBiljkaDetails(clickedBiljka) }, trefleDAO)
                    trenutneBiljke = similarBiljke
                }
            }
        }

    }
}
