package com.pietervandewalle.androidapp.data.sampler

import com.pietervandewalle.androidapp.model.Article
import java.time.LocalDate

object ArticleSampler {
    private val sampleArticles = mutableListOf(
        Article(
            id = 1,
            title = "Ontwerp klaar voor kinderdagverblijf in Dampoortwijk met 42 plaatsen",
            date = LocalDate.parse("2023-11-21"),
            readMoreUrl = "https://stad.gent/id/news/823525",
            imageUrl = "https://stad.gent/sites/default/files/styles/gallery_single/public/media/images/505689-2022-5-KDV%2520Dampoort-images5-496bb0-original-1696323618.png?itok=ycOxLE43",
            content = "Het eerste ontwerp voor een nieuw stedelijk kinderdagverblijf bij basisschool De Vlieger in de Dampoortwijk is voorgesteld. Er komen 42 plaatsen. De bouw begint eind 2025, twee jaar later worden de eerste kindjes er opgevangen.\n" +
                "\n" +
                "De Stad Gent plant een nieuw kinderdagverblijf in de Dampoortwijk, naast basisschool De Vlieger, tussen de Wasstraat en de Dendermondsesteenweg. De nieuwbouw komt op een braakliggend terreintje langs de Wasstraat. Het eerste ontwerp, van de hand van architectenbureaus NWLND Rogiers Vandeputte en Havana architectuur, is nu klaar.\n" +
                "\n" +
                "Het gebouw telt twee verdiepingen voor drie leefgroepen. Die krijgen elk een aparte ruimte, een keukenhoekje en een aansluitende buitenruimte. De ruimtes kunnen echter ook verbonden worden met elkaar om er één open plek van te maken. Er komt ook een grote keuken in het gebouw, een wasruimte, een ruimte voor opslag en een lift.",
        ),
        Article(id = 2, title = "Buurtsporthal Ledeberg zoekt uitbater voor de verbruiksruimte/cafetaria", date = LocalDate.parse("2023-11-20"), readMoreUrl = "https://stad.gent/id/news/823240"),

    )

    val getAll: () -> MutableList<Article> = {
        sampleArticles
    }
}
