package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.GPSCoordinates
import com.pietervandewalle.androidapp.model.StudyLocation

object StudyLocationSampler {
    private val sampleStudyLocations = mutableListOf(
        StudyLocation(
            id = 42,
            title = "Agora Bio-ingenieurswetenschappen",
            address = "Coupure Links 653, Gent",
            totalCapacity = 40,
            imageUrl = "https://images0.persgroep.net/rcs/GY-6ge4MzXdeDMHwAHQdRXCTcqI/diocontent/203339460/_fill/1363/900/?appId=21791a8992982cd8da851550a453bd7f&quality=0.9",
            location = GPSCoordinates(
                longitude = 3.7071643003323533,
                latitude = 51.05358700001341,
            ),
            reservedAmount = 2,
            label = "Campus Coupure",
            readmoreUrl = "https://bloklocaties.stad.gent/dashboard/42",
        ),
        StudyLocation(
            id = 25,
            title = "Bib Schoonmeersen",
            address = "Valentin Vaerwyckweg 1, 9000 Gent",
            totalCapacity = 444,
            imageUrl = "https://www.hogent.be/sites/hogent/assets/File/Campusbeelden/schoonmeersen.jpg",
            location = GPSCoordinates(
                longitude = 3.702117900137771,
                latitude = 51.035405499772274,
            ),
            reservationTag = "Geen reservatie nodig",
            reservedAmount = 0,
            label = "HOGENT campus Schoonmeersen - Gebouw D",
            readmoreUrl = "https://bloklocaties.stad.gent/dashboard/25",
        ),

        StudyLocation(
            id = 19,
            title = "Bibliotheek Economie en Bedrijfskunde",
            address = "Tweekerkenstraat 2, Gent",
            totalCapacity = 142,
            imageUrl = "https://libadmin.ugent.be/settings/uploads/libraries/images/f5/16/EBIB/original.jpeg",
            location = GPSCoordinates(
                longitude = 3.726882635229126,
                latitude = 51.04281334990709,
            ),
            reservationTag = "Geen reservatie nodig",
            availableTag = "Morgen nog beschikbaar, Week, Weekend",
            reservedAmount = 10,
            label = "Campus Tweekerken, Economie en Bedrijfskunde",
            readmoreUrl = "https://bloklocaties.stad.gent/dashboard/19",
        ),
    )
    val getAll: () -> MutableList<StudyLocation> = {
        sampleStudyLocations
    }
}
