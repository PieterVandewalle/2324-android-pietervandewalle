package com.pietervandewalle.androidapp.network

import com.fleeksoft.ksoup.Ksoup
import com.pietervandewalle.androidapp.model.Article
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class ApiArticle(
    val nieuwsbericht: String,
    val titel: String,
    val inhoud: String,
    val publicatiedatum: String,
)

fun List<ApiArticle>.asDomainObjects(): List<Article> {
    var domainList = map {
        val articleHtmlDoc = Ksoup.parse(it.inhoud)
        Article(
            title = it.titel,
            date = LocalDate.parse(it.publicatiedatum),
            readMoreUrl = it.nieuwsbericht,
            content = articleHtmlDoc.getElementsByClass("paragraph paragraph--type--text paragraph--view-mode--full").map { element -> element.text() }.filter { el -> el.isNotEmpty() }.reduce { acc, next -> acc + "\n\n" + next },
            imageUrl = articleHtmlDoc.getElementsByTag("source").first()?.attr("srcset"),
        )
    }
    return domainList
}
fun List<Article>.asApiObjects(): List<ApiArticle> {
    return this.map { article ->
        ApiArticle(
            nieuwsbericht = article.readMoreUrl,
            titel = article.title,
            inhoud = constructHtmlContent(article),
            publicatiedatum = article.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        )
    }
}

// Will only be needed for testing
private fun constructHtmlContent(article: Article): String {
    // Implement the logic to reconstruct the HTML content.
    val htmlContent = StringBuilder()
    htmlContent.append("<p class='paragraph paragraph--type--text paragraph--view-mode--full'>${article.content}</p>")

    article.imageUrl?.let {
        htmlContent.append("<source srcset=\"$it\"></source>")
    }

    return htmlContent.toString()
}
