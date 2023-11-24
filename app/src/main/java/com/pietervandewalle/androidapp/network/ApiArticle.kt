package com.pietervandewalle.androidapp.network

import com.fleeksoft.ksoup.Ksoup
import com.pietervandewalle.androidapp.model.Article
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ApiArticle(
    val nieuwsbericht: String,
    val titel: String,
    val inhoud: String,
    val publicatiedatum: String,
)

fun List<ApiArticle>.asDomainObjects(): List<Article> {
    var domainList = this.map {
        val articleHtmlDoc = Ksoup.parse(it.inhoud)
        Article(
            title = it.titel,
            date = LocalDate.parse(it.publicatiedatum),
            readMoreUrl = it.nieuwsbericht,
            content = articleHtmlDoc.getElementsByClass("paragraph paragraph--type--text paragraph--view-mode--full").first()?.text(),
            imageUrl = articleHtmlDoc.getElementsByTag("source").first()?.attr("srcset"),
        )
    }
    return domainList
}
