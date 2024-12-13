package ba.unsa.etf.rma.spirala1

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TestS2 {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun testValidacija() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).perform(ViewActions.scrollTo())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Naziv mora imati između 3 i 19 znakova")))
        onView(withId(R.id.porodicaET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Porodica mora imati između 3 i 19 znakova")))

        onView(withId(R.id.medicinskoUpozorenjeET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Medicinsko upozorenje mora imati između 3 i 19 znakova")))

        onView(withId(R.id.jeloET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Morate dodati barem jedno jelo")))

        onView(withId(R.id.medicinskaKoristTV)).perform(ViewActions.scrollTo())
        onView(withId(R.id.medicinskaKoristTV)).check(matches(withErrorText("Morate zaokruziti barem jednu medicinsku korist")))

        onView(withId(R.id.klimatskiTipTV)).perform(ViewActions.scrollTo())
        onView(withId(R.id.klimatskiTipTV)).check(matches(withErrorText("Morate zaokruziti barem jedan klimatski tip")))

        onView(withId(R.id.zemljisniTipTV)).perform(ViewActions.scrollTo())
        onView(withId(R.id.zemljisniTipTV)).check(matches(withErrorText("Morate zaokruziti barem jedan zemljisni tip")))

        onView(withId(R.id.profilOkusaTV)).perform(ViewActions.scrollTo())
        onView(withId(R.id.profilOkusaTV)).check(matches(withErrorText("Morate zaokruziti barem jedan profil okusa")))
    }
    private fun withErrorText(expectedError: String?): Matcher<in View> {
        return object: TypeSafeMatcher<Any>(){
            override fun matchesSafely(item: Any?): Boolean {
                if (item is TextView){
                    return item.error?.toString() == expectedError
                }
                return false
            }

            override fun describeTo(description: Description?) {
                description?.appendText("with error text: $expectedError")
            }
        }
    }
    @Test
    fun testPrikazSlikeBiljke() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        Intents.init()
        val bitmapSlika = BitmapFactory.decodeResource(
            getInstrumentation().targetContext.resources,
            R.drawable.plant_upload
        )
        val rezData = Intent().apply {
            putExtra("data", bitmapSlika)
        }
        val rezultat = Instrumentation.ActivityResult(Activity.RESULT_OK, rezData)
        Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(rezultat)
        onView(withId(R.id.uslikajBiljkuBtn)).perform(click())
        onView(withId(R.id.slikaIV)).check(matches(withImage(R.drawable.plant_upload)))
        Intents.release()


    }

    private fun withImage(expectedResourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View?): Boolean {
                if (item is ImageView) {
                    val expectedBitmap = BitmapFactory.decodeResource(item.resources, expectedResourceId)
                    val imageViewBitmap = (item as ImageView).drawable.toBitmap()
                    return imageViewBitmap.sameAs(expectedBitmap)
                }
                return false
            }

            override fun describeTo(description: Description?) {
                description?.appendText("with image drawable from resource id: ")
                description?.appendValue(expectedResourceId)
            }
        }
    }
    @Test
    fun dodavanjeNoveBiljke() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        val nazivNoveBiljke = "Nova biljka (Nova species)"
        val porodica = "Porodica Example"
        val medicinskoUpozorenje = "Ovo je medicinsko upozorenje"
        val jela = listOf("Jelo 1", "Jelo 2", "Jelo 3") // Možete dodati više jela po potrebi
        val redoslijedMedicinskaKorist = arrayOf(0, 2) // Prvi i treći element
        val redoslijedKlimatskiTip = arrayOf(0, 1) // Prvi i drugi element
        val redoslijedZemljisniTip = arrayOf(0, 2) // Prvi i treći element
        val redoslijedProfilOkusa = 0 // Prvi element

        onView(withId(R.id.nazivET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.nazivET)).perform(replaceText(nazivNoveBiljke))
        onView(withId(R.id.porodicaET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.porodicaET)).perform(replaceText(porodica))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(ViewActions.scrollTo())
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(replaceText(medicinskoUpozorenje))

        jela.forEach { jelo ->
            onView(withId(R.id.jeloET)).perform(replaceText(jelo))
            onView(withId(R.id.dodajJeloBtn)).perform(click())
        }
        onView(withId(R.id.medicinskaKoristLV)).perform(ViewActions.scrollTo())
        selektujStavke(redoslijedMedicinskaKorist, R.id.medicinskaKoristLV)
        onView(withId(R.id.klimatskiTipLV)).perform(ViewActions.scrollTo())
        selektujStavke(redoslijedKlimatskiTip, R.id.klimatskiTipLV)
        onView(withId(R.id.zemljisniTipLV)).perform(ViewActions.scrollTo())
        selektujStavke(redoslijedZemljisniTip, R.id.zemljisniTipLV)
        onView(withId(R.id.profilOkusaLV)).perform(ViewActions.scrollTo())
        onData(anything())
            .inAdapterView(withId(R.id.profilOkusaLV))
            .atPosition(redoslijedProfilOkusa)
            .perform(click())


        onView(withId(R.id.dodajBiljkuBtn)).perform(ViewActions.scrollTo())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        var biljkeStaticData: BiljkeStaticData
        biljkeStaticData = BiljkeStaticData.getInstance()
        Log.d("Veličina liste biljaka", biljkeStaticData.getBiljke().size.toString())

    }
    fun selektujStavke(redoslijed: Array<Int>, listViewId: Int) {
        redoslijed.forEach { index ->
            onData(anything())
                .inAdapterView(withId(listViewId))
                .atPosition(index)
                .perform(click())
        }
    }

}