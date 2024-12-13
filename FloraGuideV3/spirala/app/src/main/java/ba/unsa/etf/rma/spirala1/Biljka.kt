package ba.unsa.etf.rma.spirala1

import java.io.Serializable

data class Biljka(
    val naziv: String,
    var porodica: String,
    var medicinskoUpozorenje: String,
    val medicinskeKoristi: List<MedicinskaKorist>,
    val profilOkusa: ProfilOkusaBiljke,
    var jela: List<String>,
    var klimatskiTipovi: List<KlimatskiTip>,
    var zemljisniTipovi: List<Zemljiste>
)
