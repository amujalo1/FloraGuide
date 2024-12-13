package ba.unsa.etf.rma.spirala1

import android.graphics.Bitmap
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface BiljkaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBiljka(biljka: Biljka) : Boolean {
        return try {
            addBiljka(biljka)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBiljka(biljka: Biljka): Long


    @Transaction
    suspend fun fixOfflineBiljka(): Int {
        val offlineBiljke = getAllBiljkas()
        var updatedCount = 0
        val novaListaBiljaka: MutableList<Biljka> = mutableListOf()
        val trefleDAO = TrefleDAO()
        for (biljka in offlineBiljke) {
            if (!biljka.onlineChecked) {
                try {
                    val kopija = biljka.copy()
                    kopija.onlineChecked = true
                    val original = withContext(Dispatchers.IO) {
                        trefleDAO.fixData(biljka)
                    }
                    if (original != kopija) {
                        updatedCount++
                    }
                    novaListaBiljaka.add(original)
                } catch (e: Exception) {
                    Log.e("fixOfflineBiljka", "Error fixing biljka: ${e.message}")
                    novaListaBiljaka.add(biljka)
                }
            } else {
                // Ako je biljka onlineChecked, dodaj je izravno u novu listu bez ažuriranja
                novaListaBiljaka.add(biljka)
            }
        }

        clearBiljke()
        insertAll(novaListaBiljaka)
        return updatedCount
    }



    @Query("SELECT * FROM Biljka")
    suspend fun getAllBiljkas(): List<Biljka>

    @Query("DELETE FROM Biljka")
    fun clearBiljke()

    @Query("DELETE FROM BiljkaBitmap")
    fun clearBiljkeBitmap()

    @Transaction
    fun clearData() {
        clearBiljke()
        clearBiljkeBitmap()
    }

    @Query("SELECT COUNT(*) FROM Biljka")
    suspend fun getBiljkaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(biljke: List<Biljka>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiljkaBitmap(biljkaBitmap: BiljkaBitmap): Long

    @Transaction
    suspend fun addImage(idBiljke: Int, bitmap: Bitmap): Boolean {
        // Check if the plant exists
        val biljkaExists = existsById(idBiljke) == 1
         Log.d("BiljkaDAO", "Plant exists: $biljkaExists")
        if (!biljkaExists) {
            Log.d("BiljkaDAO", "Plant does not exist with id: $idBiljke")
            return false
        }

        // Check if the image already exists for this plant
        val existingBitmap = getBitmapById(idBiljke)
        Log.d("BiljkaDAO", "Existing bitmap: $existingBitmap")
        if (existingBitmap != null) {
            Log.d("BiljkaDAO", "Image already exists for plant id: $idBiljke")
            return false
        }

        // Add the image
        val bitmapConverter = BitmapConverter()
        val biljkaBitmap = BiljkaBitmap(idBiljke = idBiljke, bitmap = bitmapConverter.fromBitmap(bitmap))
        val result = insertBiljkaBitmap(biljkaBitmap)
        Log.d("BiljkaDAO", "Inserted biljkaBitmap id: $result")
        return true
    }

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Biljka WHERE id = :id) THEN 1 ELSE 0 END")
    suspend fun existsById(id: Int): Int

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM BiljkaBitmap WHERE idBiljke = :id) THEN 1 ELSE 0 END")
    suspend fun existsBitmapByIdBiljka(id: Int): Int

    @Query("SELECT bitmap FROM BiljkaBitmap WHERE idBiljke = :idBiljke")
    suspend fun getBitmapById(idBiljke: Int): Bitmap?

}
