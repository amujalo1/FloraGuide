package ba.unsa.etf.rma.spirala1

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    companion object {
        lateinit var db: SupportSQLiteDatabase
        lateinit var context: Context
        lateinit var roomDb: BiljkaDatabase
        lateinit var biljkaDAO: BiljkaDAO

        @BeforeClass
        @JvmStatic
        fun createDB() = runBlocking {
            context = ApplicationProvider.getApplicationContext()
            roomDb = Room.inMemoryDatabaseBuilder(context, BiljkaDatabase::class.java).build()
            biljkaDAO = roomDb.biljkaDao()
            db = roomDb.openHelper.readableDatabase
        }
    }

    @get:Rule
    val intentsTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking {
        biljkaDAO.clearData()
    }

    @Test
    fun test1() = runBlocking {
        val biljka = Biljka(
            naziv = "Bosiljak (Ocimum basilicum)",
            family = "Lamiaceae (usnate)",
            medicinskoUpozorenje = "Može iritirati kožu osjetljivu na sunce. Preporučuje se oprezna upotreba pri korištenju ulja bosiljka.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.SMIRENJE,
                MedicinskaKorist.REGULACIJAPROBAVE
            ),
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Salata od paradajza", "Punjene tikvice"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.ILOVACA),
        )
        ViewMatchers.assertThat(biljkaDAO.saveBiljka(biljka),CoreMatchers.`is`(true))
        val biljkaIzBaze = biljkaDAO.getAllBiljkas().first()
        ViewMatchers.assertThat(biljkaIzBaze.naziv, CoreMatchers.`is`(biljka.naziv))
        ViewMatchers.assertThat(biljkaIzBaze.family, CoreMatchers.`is`(biljka.family))
        ViewMatchers.assertThat(biljkaIzBaze.medicinskoUpozorenje, CoreMatchers.`is`(biljka.medicinskoUpozorenje))
        ViewMatchers.assertThat(biljkaIzBaze.medicinskeKoristi, CoreMatchers.`is`(biljka.medicinskeKoristi))
        ViewMatchers.assertThat(biljkaIzBaze.profilOkusa, CoreMatchers.`is`(biljka.profilOkusa))
        ViewMatchers.assertThat(biljkaIzBaze.jela, CoreMatchers.`is`(biljka.jela))
        ViewMatchers.assertThat(biljkaIzBaze.klimatskiTipovi, CoreMatchers.`is`(biljka.klimatskiTipovi))
        ViewMatchers.assertThat(biljkaIzBaze.zemljisniTipovi, CoreMatchers.`is`(biljka.zemljisniTipovi))
        ViewMatchers.assertThat(biljkaDAO.getAllBiljkas().size, CoreMatchers.`is`(1))
    }

    @Test
    fun test2() = runBlocking {
        biljkaDAO.clearData()
        ViewMatchers.assertThat(biljkaDAO.getAllBiljkas().size, CoreMatchers.`is`(0))
    }

    @Test
    fun test3() = runBlocking {
        biljkaDAO.insertAll(BiljkeStaticData.getBiljke())
        val n = biljkaDAO.fixOfflineBiljka()
        val lista = biljkaDAO.getAllBiljkas()
        ViewMatchers.assertThat(n, CoreMatchers.`is`(10))
    }
}
