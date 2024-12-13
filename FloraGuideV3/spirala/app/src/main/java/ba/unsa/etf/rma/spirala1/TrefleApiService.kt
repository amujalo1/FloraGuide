package ba.unsa.etf.rma.spirala1

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface TrefleApiService {
    @GET("plants/{slug}")
    suspend fun searchPlant(
        @Path("slug") latinName: String,
        @Query("token") token: String
    ): PlantResponse

    @GET("plants")
    suspend fun searchPlantsByFlowerColor(
        @Query("filter[flower_color]") flowerColor: String,
        @Query("token") token: String,
        @Query("page") stranica: Int
    ): PlantListResponse
}

