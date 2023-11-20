package com.pietervandewalle.androidapp.ui.articles

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.data.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun ArticleOverview(modifier: Modifier = Modifier, articleOverviewViewModel: ArticleOverviewViewModel = viewModel()) {
    val articleOverviewState by articleOverviewViewModel.uiState.collectAsState()

    ArticleList(articles = articleOverviewState.articles)
}

@Composable
fun ArticleList(modifier: Modifier = Modifier, articles: List<Article>) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState) {
        items(articles) { article ->
            ArticleListItem(article = article)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListItem(modifier: Modifier = Modifier, article: Article) {
    ListItem(
        modifier = modifier.padding(5.dp),
        shadowElevation = 1.dp,
        headlineText = {
            Text(article.title, style = MaterialTheme.typography.titleMedium)
        },
        supportingText = {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(article.author, style = MaterialTheme.typography.titleSmall)

                Text(
                    DateUtils.getRelativeTimeSpanString(article.date.time).toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        trailingContent = {
            AsyncImage(
                modifier = Modifier.clip(
                    shape = RoundedCornerShape(5.dp),
                ).fillMaxHeight().fillMaxWidth(0.25f),
                model = article.imageUrl,
                contentDescription = "Article image",
                contentScale = ContentScale.FillBounds,
            )
        },
    )
}

@Preview
@Composable
fun ArticleListPreview() {
    AndroidAppTheme {
        ArticleList(articles = ArticleSampler.getAll())
    }
}
