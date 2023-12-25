package com.pietervandewalle.androidapp.network

import com.fleeksoft.ksoup.Ksoup
import com.pietervandewalle.androidapp.model.Article
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Represents an API article received from the City of Ghent's API.
 *
 * @property nieuwsbericht The URL of the article.
 * @property titel The title of the article.
 * @property inhoud The content of the article in HTML format.
 * @property publicatiedatum The publication date of the article in ISO date format.
 */
@Serializable
data class ApiArticle(
    val nieuwsbericht: String,
    val titel: String,
    val inhoud: String,
    val publicatiedatum: String,
)

/**
 * Converts a list of [ApiArticle] objects to a list of domain [Article] objects.
 *
 * @receiver The list of [ApiArticle] objects to convert.
 * @return A list of domain [Article] objects.
 */
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

/**
 * Converts a list of domain [Article] objects to a list of [ApiArticle] objects.
 * This function is intended for testing purposes.
 *
 * @receiver The list of domain [Article] objects to convert.
 * @return A list of [ApiArticle] objects.
 */
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

/**
 * Constructs HTML content for an [Article] object.
 * This function is intended for testing purposes.
 *
 * @param article The [Article] object to construct HTML content for.
 * @return The HTML content as a string.
 */
private fun constructHtmlContent(article: Article): String {
    // Implement the logic to reconstruct the HTML content.
    val htmlContent = StringBuilder()
    htmlContent.append("<p class='paragraph paragraph--type--text paragraph--view-mode--full'>${article.content}</p>")

    article.imageUrl?.let {
        htmlContent.append("<source srcset=\"$it\"></source>")
    }

    return htmlContent.toString()
}
