package ba.unsa.etf.rma.spirala1

import com.google.gson.annotations.SerializedName

data class SerializedBiljka(
    @SerializedName("common_name") var common_name: String,
    @SerializedName("scientific_name") var scientific_name: String,
    @SerializedName("slug") var slug: String,
    @SerializedName("image_url") var image_url: String,
    @SerializedName("main_species") var mainSpecies: MainSpecies,
    @SerializedName("family") var family: family
)
data class PlantResponse(
    @SerializedName("data") val plant: SerializedBiljka
)
data class PlantListResponse(
    @SerializedName("data") val plants: List<SerializedBiljkaPoBoji>
)
data class SerializedBiljkaPoBoji(
    @SerializedName("slug") var slug: String,
    @SerializedName("common_name") var common_name: String,
    @SerializedName("scientific_name") var scientific_name: String,
)
data class family(
    @SerializedName("name") val name: String
)
data class Growth(
    @SerializedName("soil_texture") var soilTexture: Int?,
    @SerializedName("light") var light: Int?,
    @SerializedName("atmospheric_humidity") var atmosphericHumidity: Int?
)
data class flower(
    @SerializedName("color") val color: List<String>?
)
data class MainSpecies(
    @SerializedName("edible") var edible: Boolean?,
    @SerializedName("specifications") var specifications: Specifications,
    @SerializedName("growth") var growth: Growth,
    @SerializedName("flower") var flower: flower

)

data class Specifications(
    @SerializedName("toxicity") var toxicity: List<String>?
)

