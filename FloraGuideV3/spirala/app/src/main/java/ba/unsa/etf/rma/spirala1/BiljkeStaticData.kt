package ba.unsa.etf.rma.spirala1

import android.util.Log

object BiljkeStaticData {
    private var instance: BiljkeStaticData? = null

    fun getInstance(): BiljkeStaticData {
        if (instance == null) {
            instance = BiljkeStaticData
        }
        return instance!!
    }
    private val biljke = mutableListOf(
        Biljka(
            naziv = "Bosiljak (Ocimum basilicum)",
            porodica = "Lamiaceae (usnate)",
            medicinskoUpozorenje = "Može iritati kožu osjetljivu na sunce. Preporučuje se oprezna upotreba pri korištenju ulja bosiljka.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.SMIRENJE,
                MedicinskaKorist.REGULACIJAPROBAVE
            ),
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Salata od paradajza", "Punjene tikvice"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.ILOVACA)
        ),
        Biljka(
            naziv = "Nana (Mentha spicata)",
            porodica = "Lamiaceae (metvice)",
            medicinskoUpozorenje = "Nije preporučljivo za trudnice, dojilje i djecu mlađu od 3 godine.",
            medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO, MedicinskaKorist.PROTIVBOLOVA),
            profilOkusa = ProfilOkusaBiljke.MENTA,
            jela = listOf("Jogurt sa voćem", "Gulaš"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.UMJERENA),
            zemljisniTipovi = listOf(Zemljiste.GLINENO, Zemljiste.CRNICA)
        ),
        Biljka(
            naziv = "Kamilica (Matricaria chamomilla)",
            porodica = "Asteraceae (glavočike)",
            medicinskoUpozorenje = "Može uzrokovati alergijske reakcije kod osjetljivih osoba.",
            medicinskeKoristi = listOf(MedicinskaKorist.SMIRENJE, MedicinskaKorist.PROTUUPALNO),
            profilOkusa = ProfilOkusaBiljke.AROMATICNO,
            jela = listOf("Čaj od kamilice"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.KRECNJACKO)
        ),
        Biljka(
            naziv = "Ružmarin (Rosmarinus officinalis)",
            porodica = "Lamiaceae (metvice)",
            medicinskoUpozorenje = "Treba ga koristiti umjereno i konsultovati se sa ljekarom pri dugotrajnoj upotrebi ili upotrebi u većim količinama.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.PROTUUPALNO,
                MedicinskaKorist.REGULACIJAPRITISKA
            ),
            profilOkusa = ProfilOkusaBiljke.AROMATICNO,
            jela = listOf("Pečeno pile", "Grah", "Gulaš"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.SLJUNOVITO, Zemljiste.KRECNJACKO)
        ),
        Biljka(
            naziv = "Lavanda (Lavandula angustifolia)",
            porodica = "Lamiaceae (metvice)",
            medicinskoUpozorenje = "Nije preporučljivo za trudnice, dojilje i djecu mlađu od 3 godine. Također, treba izbjegavati kontakt lavanda ulja sa očima.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.SMIRENJE,
                MedicinskaKorist.PODRSKAIMUNITETU
            ),
            profilOkusa = ProfilOkusaBiljke.AROMATICNO,
            jela = listOf("Jogurt sa voćem"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.KRECNJACKO)
        ),
        Biljka(
            naziv = "Majčina dušica (Thymus vulgaris)",
            porodica = "Lamiaceae (metvice)",
            medicinskoUpozorenje = "Osobe alergične na biljke iz porodice Lamiaceae trebaju izbjegavati korištenje majčine dušice.",
            medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO, MedicinskaKorist.PROTIVBOLOVA),
            profilOkusa = ProfilOkusaBiljke.AROMATICNO,
            jela = listOf("Piletina s majčinom dušicom", "Tjestenina s umakom od rajčice"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.GLINENO, Zemljiste.KRECNJACKO)
        ),

        Biljka(
            naziv = "Kopriva (Urtica dioica)",
            porodica = "Urticaceae (koprive)",
            medicinskoUpozorenje = "Kopriva može izazvati alergijske reakcije kod nekih osoba. Treba izbjegavati dodir s kožom.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.REGULACIJAPRITISKA,
                MedicinskaKorist.PROTUUPALNO
            ),
            profilOkusa = ProfilOkusaBiljke.GORKO,
            jela = listOf("Juha od koprive", "Quiche s koprivama"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.ILOVACA, Zemljiste.SLJUNOVITO)
        ),

        Biljka(
            naziv = "Peršin (Petroselinum crispum)",
            porodica = "Apiaceae (štitarkovke)",
            medicinskoUpozorenje = "Osobe koje uzimaju lijekove za razrjeđivanje krvi trebaju izbjegavati prekomjernu konzumaciju peršina zbog visokog sadržaja vitamina K.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.REGULACIJAPROBAVE,
                MedicinskaKorist.PROTUUPALNO,
                MedicinskaKorist.REGULACIJAPRITISKA
            ),
            profilOkusa = ProfilOkusaBiljke.KORIJENASTO,
            jela = listOf("Piletina s peršinom", "Krompir salata s peršinom"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.ILOVACA, Zemljiste.GLINENO)
        ),

        Biljka(
            naziv = "Limun trava (Cymbopogon citratus)",
            porodica = "Poaceae (trave)",
            medicinskoUpozorenje = "Osobe s povišenim tlakom trebaju koristiti limun travu s oprezom jer može uzrokovati skokove tlaka.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.SMIRENJE,
                MedicinskaKorist.REGULACIJAPROBAVE
            ),
            profilOkusa = ProfilOkusaBiljke.CITRUSNI,
            jela = listOf("Tom yum supa", "Tajlandski zeleni curry"),
            klimatskiTipovi = listOf(KlimatskiTip.TROPSKA, KlimatskiTip.SUHA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO)
        ),

        Biljka(
            naziv = "Majčina djevica (Artemisia vulgaris)",
            porodica = "Asteraceae (glavočike)",
            medicinskoUpozorenje = "Trudnice i dojilje trebaju izbjegavati korištenje majčine djevice zbog mogućih komplikacija.",
            medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO, MedicinskaKorist.SMIRENJE),
            profilOkusa = ProfilOkusaBiljke.GORKO,
            jela = listOf("Čaj od majčine djevice"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.ILOVACA)
        ),
    )
    fun getBiljke(): List<Biljka> {
        Log.d("NovaBiljkaActivity", "Komanda getBiljke() je uspješno izvršena")
        return biljke.toList()
    }

    fun dodajBiljku(biljka: Biljka) {
        biljke.add(biljka)
        Log.d("NovaBiljkaActivity", "Komanda dodajBiljku() je uspješno izvršena")
    }
}
