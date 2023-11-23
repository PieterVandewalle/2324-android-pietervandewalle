package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ArticleSampler {
    private val sampleArticles = mutableListOf(
        Article(title = "Ontwerp klaar voor kinderdagverblijf in Dampoortwijk met 42 plaatsen", date = LocalDate.parse("2023-11-21"), readMoreUrl = "https://stad.gent/id/news/823525"),
        Article(title = "Jaarabonnement De Lijn voor ouders", date = LocalDate.parse("2023-11-20"), readMoreUrl = "https://stad.gent/id/news/823522"),
        Article(title = "Buurtsporthal Ledeberg zoekt uitbater voor de verbruiksruimte/cafetaria", date = LocalDate.parse("2023-11-20"), readMoreUrl = "https://stad.gent/id/news/823240"),

    )

    val getAll: () -> MutableList<Article> = {
        sampleArticles
    }
}
