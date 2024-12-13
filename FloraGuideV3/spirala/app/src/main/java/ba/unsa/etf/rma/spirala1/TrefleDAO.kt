package ba.unsa.etf.rma.spirala1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class TrefleDAO {
    private val apiKey: String = "MDw8MnKusNOtbpX9JqTti0hN6tD8UMWSWNhzE_xlovE"
    private var defaultBitmap: Bitmap? = null
    private val apiService: TrefleApiService = RetrofitInstance.apiService

    fun setContext(context: Context) {
        defaultBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.plant)
    }


    private fun extractLatinName(naziv: String): String {
        val regex = "\\(([^)]+)\\)".toRegex()
        val matchResult = regex.find(naziv)
        return matchResult?.groupValues?.get(1)?.replace(" ", "-")?.lowercase() ?: ""
    }

    private fun determineClimateTypes(light: Int, humidity: Int): List<KlimatskiTip> {
        val climateTypes = mutableListOf<KlimatskiTip>()
        if (light in 6..9 && humidity in 1..5) climateTypes.add(KlimatskiTip.SREDOZEMNA)
        if (light in 8..10 && humidity in 7..10) climateTypes.add(KlimatskiTip.TROPSKA)
        if (light in 6..9 && humidity in 5..8) climateTypes.add(KlimatskiTip.SUBTROPSKA)
        if (light in 4..7 && humidity in 3..7) climateTypes.add(KlimatskiTip.UMJERENA)
        if (light in 7..9 && humidity in 1..2) climateTypes.add(KlimatskiTip.SUHA)
        if (light in 0..5 && humidity in 3..7) climateTypes.add(KlimatskiTip.PLANINSKA)
        return climateTypes
    }

    private fun determineSoilTypes(textura: Int): List<Zemljiste> {
        val soilTypes = mutableListOf<Zemljiste>()
        if (textura == 9) soilTypes.add(Zemljiste.SLJUNOVITO)
        if (textura == 10) soilTypes.add(Zemljiste.KRECNJACKO)
        if (textura in 1..2) soilTypes.add(Zemljiste.GLINENO)
        if (textura in 3..4) soilTypes.add(Zemljiste.PJESKOVITO)
        if (textura in 5..6) soilTypes.add(Zemljiste.ILOVACA)
        if (textura in 7..8) soilTypes.add(Zemljiste.CRNICA)
        return soilTypes
    }

    suspend fun getImage(biljka: Biljka): Bitmap {
        return try {
            val latinName = extractLatinName(biljka.naziv)
            val plantResponse = apiService.searchPlant(latinName, apiKey)
            if (plantResponse.plant.image_url.isNotEmpty()) {
                val imageUrl = URL(plantResponse.plant.image_url)
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                BitmapFactory.decodeStream(connection.inputStream)
            } else {
                defaultBitmap ?: throw IllegalStateException("Default bitmap not set")
            }
        } catch (e: Exception) {
            Log.e("TrefleDAO", "Exception occurred: ${e.message}")
            defaultBitmap ?: throw IllegalStateException("Default bitmap not set")
        }
    }

    suspend fun fixData(biljka: Biljka): Biljka {
        return try {
            val latinName = extractLatinName(biljka.naziv)
            val plantResponse = apiService.searchPlant(latinName, apiKey)
            if (plantResponse.plant.image_url.isNotEmpty()) {
                val plantDetails = plantResponse.plant
                biljka.porodica = plantDetails.family.name
                if (!plantDetails.mainSpecies.edible!!) {
                    biljka.jela = emptyList()
                    biljka.medicinskoUpozorenje = biljka.medicinskoUpozorenje?.let {
                        if (!it.contains("NIJE JESTIVO")) "$it NIJE JESTIVO" else it
                    } ?: "NIJE JESTIVO"
                }
                if (plantDetails.mainSpecies.specifications.toxicity?.firstOrNull() != "none") {
                    biljka.medicinskoUpozorenje = biljka.medicinskoUpozorenje?.let {
                        if (!it.contains("TOKSIČNO")) "$it TOKSIČNO" else it
                    } ?: "TOKSIČNO"
                }
                biljka.zemljisniTipovi = determineSoilTypes(plantDetails.mainSpecies.growth.soilTexture ?: 0).toMutableList()
                biljka.klimatskiTipovi = determineClimateTypes(
                    plantDetails.mainSpecies.growth.light ?: 0,
                    plantDetails.mainSpecies.growth.atmosphericHumidity ?: 0
                ).toMutableList()
            }
            biljka
        } catch (e: Exception) {
            Log.e("TrefleDAO", "Izuzetak: ${e.message}")
            biljka
        }
    }

    suspend fun getPlantsWithFlowerColor(flower_color: String, substr: String): List<Biljka> {
        return try {
            val biljke = mutableListOf<Biljka>()
            var page = 1
            try{
                while (page < 70) {
                    val plantResponse =
                        apiService.searchPlantsByFlowerColor(flower_color, apiKey, page)
                    page += 1
                    for (plant in plantResponse.plants) {
                        var naziv: String = plant.common_name + "(${plant.scientific_name})"
                        if (naziv.contains(substr, ignoreCase = true)) {
                            val plantDetail = apiService.searchPlant(plant.slug, apiKey)
                            if (plantDetail.plant.mainSpecies.flower.color != null){
                                biljke.add(
                                    Biljka(
                                        naziv = "${plantDetail.plant.common_name} (${plantDetail.plant.scientific_name})",
                                        porodica = plantDetail.plant.family.name,
                                        medicinskoUpozorenje = "",
                                        medicinskeKoristi = emptyList(),
                                        profilOkusa = ProfilOkusaBiljke.AROMATICNO,
                                        jela = emptyList(),
                                        klimatskiTipovi = determineClimateTypes(
                                            plantDetail.plant.mainSpecies.growth.light ?: 0,
                                            plantDetail.plant.mainSpecies.growth.atmosphericHumidity
                                                ?: 0
                                        ),
                                        zemljisniTipovi = determineSoilTypes(
                                            plantDetail.plant.mainSpecies.growth.soilTexture ?: 0
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }catch (e: Exception){
                //Log.d("TrefleDAO", "Ukupno ${page-1} stranica")
            }
            biljke
        } catch (e: Exception) {
            Log.e("TrefleDAO", "Izuzetak: ${e.message}")
            emptyList()
        }
    }
}
