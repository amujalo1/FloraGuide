package ba.unsa.etf.rma.spirala1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

@Entity(
    tableName = "BiljkaBitmap",
    foreignKeys = [ForeignKey(entity = Biljka::class, parentColumns = ["id"], childColumns = ["idBiljke"], onDelete = ForeignKey.CASCADE)]
)
data class BiljkaBitmap(
    @PrimaryKey(autoGenerate = true)  val id: Int = 0,
    val idBiljke: Int,
    val bitmap: String
)

class BitmapConverter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @TypeConverter
    fun toBitmap(encodedString: String): Bitmap {
        val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
