package com.pietervandewalle.androidapp.ui.articles

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import java.time.format.DateTimeFormatter

@Composable
fun ArticleOverview(modifier: Modifier = Modifier, articleOverviewViewModel: ArticleOverviewViewModel = viewModel(factory = ArticleOverviewViewModel.Factory)) {
    val articleOverviewState by articleOverviewViewModel.uiState.collectAsState()
    val articleApiState = articleOverviewViewModel.articleApiState
    val articleApiRefreshingState = articleOverviewViewModel.articleApiRefreshingState
    val isRefreshing = articleApiRefreshingState is ArticleApiState.Loading

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.home_title) {
            }
        },
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = articleOverviewViewModel::refresh,
            modifier = modifier.padding(innerPadding),
        ) {
            when (articleApiState) {
                is ArticleApiState.Loading -> LoadingIndicator()
                is ArticleApiState.Error -> Text("Couldn't load...")
                is ArticleApiState.Success ->
                    ArticleList(modifier = modifier, articles = articleOverviewState.articles)
            }
        }
    }
}

@Composable
fun ArticleList(modifier: Modifier = Modifier, articles: List<Article>) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(articles) { article ->
            ArticleListItem(article = article)
        }
    }
}

@Composable
fun ArticleListItem(modifier: Modifier = Modifier, article: Article) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val showArticleInBrowserIntent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(article.readMoreUrl)) }
    ListItem(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                context.startActivity(
                    showArticleInBrowserIntent,
                ) // TODO look into webview for displaying article
            },
        shadowElevation = 1.dp,
        headlineContent = {
            Text(article.title, style = MaterialTheme.typography.titleMedium)
        },
        supportingContent = {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    dateFormatter.format(article.date),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        trailingContent = {
            if (article.imageUrl != null) {
                Box(modifier = Modifier.size(55.dp)) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
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
