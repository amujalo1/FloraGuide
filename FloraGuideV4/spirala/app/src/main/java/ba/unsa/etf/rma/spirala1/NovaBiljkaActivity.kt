package ba.unsa.etf.rma.spirala1

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

class NovaBiljkaActivity : AppCompatActivity(), CoroutineScope{
    private lateinit var biljkeStaticData: BiljkeStaticData
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var trefleDAO: TrefleDAO
    private lateinit var nazivET: EditText
    private lateinit var porodicaET: EditText
    private lateinit var medicinskoUpozorenjeET: EditText
    private lateinit var jeloET: EditText
    private lateinit var medicinskaKoristLV: ListView
    private lateinit var klimatskiTipLV: ListView
    private lateinit var zemljisniTipLV: ListView
    private lateinit var profilOkusaLV: ListView
    private lateinit var jelaLV: ListView
    private lateinit var dodajJeloBtn: Button
    private lateinit var dodajBiljkuBtn: Button
    private lateinit var uslikajBiljkuBtn: Button
    private lateinit var slikaIV: ImageView
    private lateinit var database: BiljkaDatabase
    private lateinit var biljkaDao:BiljkaDAO
    private lateinit var jelaList: ArrayList<String>
    private lateinit var jelaAdapter: ArrayAdapter<String>
    private lateinit var slikaBiljke:Bitmap
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_biljka)
        biljkeStaticData = BiljkeStaticData.getInstance()
        // Inicijalizacija UI elemenata
        database = BiljkaDatabase.getDatabase(this)
        biljkaDao = database.biljkaDao()
        trefleDAO = TrefleDAO()
        trefleDAO.setContext(this)
        slikaIV = findViewById(R.id.slikaIV)
        nazivET = findViewById(R.id.nazivET)
        porodicaET = findViewById(R.id.porodicaET)
        medicinskoUpozorenjeET = findViewById(R.id.medicinskoUpozorenjeET)
        jeloET = findViewById(R.id.jeloET)
        medicinskaKoristLV = findViewById(R.id.medicinskaKoristLV)
        klimatskiTipLV = findViewById(R.id.klimatskiTipLV)
        zemljisniTipLV = findViewById(R.id.zemljisniTipLV)
        profilOkusaLV = findViewById(R.id.profilOkusaLV)
        jelaLV = findViewById(R.id.jelaLV)
        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuBtn = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuBtn = findViewById(R.id.uslikajBiljkuBtn)
        // Inicijalizacija ListView-ova s enum vrijednostima
        inicijalizujListViewMultiple_choice(medicinskaKoristLV, MedicinskaKorist.entries.toTypedArray())
        inicijalizujListViewMultiple_choice(klimatskiTipLV, KlimatskiTip.entries.toTypedArray())
        inicijalizujListViewMultiple_choice(zemljisniTipLV, Zemljiste.entries.toTypedArray())
        inicijalizujListViewSingle_choice(profilOkusaLV, ProfilOkusaBiljke.entries.toTypedArray())

        // Inicijalizacija liste jela i adaptera
        jelaList = ArrayList()
        jelaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jelaList)
        jelaLV.adapter = jelaAdapter

        // Postavljanje onClickListener-a za dugmad
        dodajJeloBtn.setOnClickListener { dodajJelo() }
        dodajBiljkuBtn.setOnClickListener { dodajBiljku() }
        uslikajBiljkuBtn.setOnClickListener { uslikajBiljku() }

        // Postavljanje onItemClick listener-a za ListView jela
        jelaLV.setOnItemClickListener { _, _, position, _ ->
            // Postavljanje teksta u EditText na odabrano jelo
            val odabranoJelo = jelaList[position]
            jeloET.setText(odabranoJelo)

            // Promjena teksta dugmeta
            dodajJeloBtn.text = "Izmijeni jelo"

            // Brisanje odabranog jela iz liste
            jelaList.removeAt(position)

            // Obnovi adapter ListView-a
            jelaAdapter.notifyDataSetChanged()
        }
    }

    private fun inicijalizujListViewMultiple_choice(listView: ListView, values: Array<*>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, values.map { it.toString() })
        listView.adapter = adapter
    }
    private fun inicijalizujListViewSingle_choice(listView: ListView, values: Array<*>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, values.map { it.toString() })
        listView.adapter = adapter
    }

    private fun dodajJelo() {
        val jelo = jeloET.text.toString()

        // Provjeri da li je tekst prazan
        if (jelo.isEmpty()) {
            // Ako je prazan, postavi grešku
            jeloET.error = "Unesite naziv jela"
            return
        }

        // Provjeri da li je jelo već dodano u listu
        if (jelaList.contains(jelo)) {
            // Ako je dodano, pronađi indeks i izmijeni ga
            jeloET.error = "uneseno Jelo vec postoji"
            val indeks = jelaList.indexOf(jelo)
            jelaList[indeks] = jelo
        } else {
            // Ako nije dodano, dodaj ga u listu
            jelaList.add(jelo)
        }

        // Obnovi adapter ListView-a
        jelaAdapter.notifyDataSetChanged()

        // Resetiraj polje za unos jela i tekst dugmeta
        jeloET.setText("")
        dodajJeloBtn.text = "Dodaj jelo"
    }

    private fun dodajBiljku() {
        val naziv = nazivET.text.toString()
        val porodica = porodicaET.text.toString()
        val medicinskoUpozorenje = medicinskoUpozorenjeET.text.toString()
        val medicinskaKorist = getSelektiraneVrijednostiMedicinskaKorist(medicinskaKoristLV)
        val klimatskiTip = getSelektiraneVrijednostiKlimatskiTip(klimatskiTipLV)
        val zemljisniTip = getSelektiraneVrijednostiZemljiste(zemljisniTipLV)
        val selektiraniProfiliOkusa = getSelektiranuVrijednost(profilOkusaLV)
        val jela = getVrijednosti<String>(jelaLV)

        // Validacija polja
        if (!validirajPolja(naziv, porodica, medicinskoUpozorenje, jela)) {
            return
        }

        // Kreiranje instance biljke
        val novaBiljka = Biljka(
            naziv = naziv,
            family = porodica,
            medicinskoUpozorenje = medicinskoUpozorenje,
            medicinskeKoristi = medicinskaKorist,
            profilOkusa = selektiraniProfiliOkusa,
            jela = jela,
            klimatskiTipovi = klimatskiTip,
            zemljisniTipovi = zemljisniTip,
            onlineChecked = false,
        )


        CoroutineScope(Dispatchers.IO).launch {
            val fixedBiljka = withContext(Dispatchers.IO) {
                trefleDAO.fixData(novaBiljka)
            }
            biljkaDao.saveBiljka(fixedBiljka)
            if(::slikaBiljke.isInitialized){
                Log.d("NovaBiljkaActivity", "${biljkaDao.addImage(biljkaDao.getAllBiljkas().last().id, slikaBiljke)}")
            }
            finish()
        }
    }

    private fun getSelektiraneVrijednostiMedicinskaKorist(listView: ListView): List<MedicinskaKorist> {
        val selektiraneVrijednosti = ArrayList<MedicinskaKorist>()
        val adapter = listView.adapter

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (listView.isItemChecked(i)) {
                    var item: MedicinskaKorist
                    item = when (i) {
                        0 -> MedicinskaKorist.SMIRENJE
                        1 -> MedicinskaKorist.PROTUUPALNO
                        2 -> MedicinskaKorist.PROTIVBOLOVA
                        3 -> MedicinskaKorist.REGULACIJAPRITISKA
                        4 -> MedicinskaKorist.REGULACIJAPROBAVE
                        5 -> MedicinskaKorist.PODRSKAIMUNITETU
                        else -> MedicinskaKorist.SMIRENJE // Ukoliko nema odgovarajuće vrijednosti, vraćamo defaultnu
                    }
                    selektiraneVrijednosti.add(item)
                }
            }
        }
        return selektiraneVrijednosti
    }
    private fun getSelektiraneVrijednostiKlimatskiTip(listView: ListView): List<KlimatskiTip> {
        val selektiraneVrijednosti = ArrayList<KlimatskiTip>()
        val adapter = listView.adapter

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (listView.isItemChecked(i)) {
                    var item: KlimatskiTip
                    item = when (i) {
                        0 -> KlimatskiTip.SREDOZEMNA
                        1 -> KlimatskiTip.TROPSKA
                        2 -> KlimatskiTip.SUBTROPSKA
                        3 -> KlimatskiTip.UMJERENA
                        4 -> KlimatskiTip.SUHA
                        5 -> KlimatskiTip.PLANINSKA
                        else -> KlimatskiTip.SREDOZEMNA // Ukoliko nema odgovarajuće vrijednosti, vraćamo defaultnu
                    }
                    selektiraneVrijednosti.add(item)
                }
            }
        }
        return selektiraneVrijednosti
    }
    private fun getSelektiraneVrijednostiZemljiste(listView: ListView): List<Zemljiste> {
        val selektiraneVrijednosti = ArrayList<Zemljiste>()
        val adapter = listView.adapter

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (listView.isItemChecked(i)) {
                    var item: Zemljiste
                    item = when (i) {
                        0 -> Zemljiste.PJESKOVITO
                        1 -> Zemljiste.GLINENO
                        2 -> Zemljiste.ILOVACA
                        3 -> Zemljiste.CRNICA
                        4 -> Zemljiste.SLJUNOVITO
                        5 -> Zemljiste.KRECNJACKO
                        else -> Zemljiste.PJESKOVITO // Ukoliko nema odgovarajuće vrijednosti, vraćamo defaultnu
                    }
                    selektiraneVrijednosti.add(item)
                }
            }
        }
        return selektiraneVrijednosti
    }
    private fun getSelektiranuVrijednost(listView: ListView): ProfilOkusaBiljke {
        val adapter = listView.adapter

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (listView.isItemChecked(i)) {
                    // Na osnovu indeksa vraćamo odgovarajuću vrijednost
                    return when (i) {
                        0 -> ProfilOkusaBiljke.MENTA
                        1 -> ProfilOkusaBiljke.CITRUSNI
                        2 -> ProfilOkusaBiljke.SLATKI
                        3 -> ProfilOkusaBiljke.BEZUKUSNO
                        4 -> ProfilOkusaBiljke.LJUTO
                        5 -> ProfilOkusaBiljke.KORIJENASTO
                        6 -> ProfilOkusaBiljke.AROMATICNO
                        7 -> ProfilOkusaBiljke.GORKO
                        else -> ProfilOkusaBiljke.BEZUKUSNO // Ukoliko nema odgovarajuće vrijednosti, vraćamo defaultnu
                    }
                }
            }
        }

        return ProfilOkusaBiljke.BEZUKUSNO // Ukoliko nema selektirane vrijednosti, vraćamo defaultnu
    }

    private fun <T> getVrijednosti(listView: ListView): List<T> {
        val Vrijednosti = ArrayList<T>()
        val adapter = listView.adapter

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                    @Suppress("UNCHECKED_CAST")
                    val item = adapter.getItem(i) as T
                    Vrijednosti.add(item)
            }
        }

        return Vrijednosti
    }


    private fun validirajPolja(naziv: String, porodica: String, medicinskoUpozorenje: String, jela: List<String>): Boolean {
        // Resetovanje svih grešaka
        nazivET.error = null
        porodicaET.error = null
        medicinskoUpozorenjeET.error = null
        jeloET.error = null
        findViewById<TextView>(R.id.medicinskaKoristTV).error = null
        findViewById<TextView>(R.id.klimatskiTipTV).error = null
        findViewById<TextView>(R.id.zemljisniTipTV).error = null
        findViewById<TextView>(R.id.profilOkusaTV).error = null

        var T = true
        if (naziv.length !in 3..39) {
            nazivET.error = "Naziv mora imati između 3 i 39 znakova"
            T = false
        }

        if (porodica.length !in 3..19) {
            porodicaET.error = "Porodica mora imati između 3 i 19 znakova"
            T =  false
        }

        if (medicinskoUpozorenje.length !in 3..19) {
            medicinskoUpozorenjeET.error = "Medicinsko upozorenje mora imati između 3 i 19 znakova"
            T = false
        }

        if (jela.isEmpty()) {
            jeloET.error = "Morate dodati barem jedno jelo"
            T = false
        }

        var adapter = medicinskaKoristLV.adapter
        var T1 = false
        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (medicinskaKoristLV.isItemChecked(i)) {
                    T1 = true
                }
            }
        }
        if (!T1) {
            findViewById<TextView>(R.id.medicinskaKoristTV).error = "Morate zaokruziti barem jednu medicinsku korist"
            T = false
        }

        adapter = klimatskiTipLV.adapter
        var T2 = false
        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (klimatskiTipLV.isItemChecked(i)) {
                    T2 = true
                }
            }
        }
        if (!T2) {
            findViewById<TextView>(R.id.klimatskiTipTV).error = "Morate zaokruziti barem jedan klimatski tip"
            T = false
        }

        adapter = zemljisniTipLV.adapter
        var T3 = false
        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (zemljisniTipLV.isItemChecked(i)) {
                    T3 = true
                }
            }
        }
        if (!T3) {
            findViewById<TextView>(R.id.zemljisniTipTV).error = "Morate zaokruziti barem jedan zemljisni tip"
            T = false
        }

        adapter = profilOkusaLV.adapter
        var T4 = false
        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                if (profilOkusaLV.isItemChecked(i)) {
                    T4 = true
                }
            }
        }
        if (!T4) {
            findViewById<TextView>(R.id.profilOkusaTV).error = "Morate zaokruziti barem jedan profil okusa"
            T = false
        }
        return T
    }

    private fun uslikajBiljku() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            slikaBiljke=data?.extras?.get("data") as Bitmap
            slikaIV.setImageBitmap(slikaBiljke)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
