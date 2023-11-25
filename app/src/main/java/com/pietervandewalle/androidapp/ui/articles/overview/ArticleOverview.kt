package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetail
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun ArticleOverview(modifier: Modifier = Modifier, articleOverviewViewModel: ArticleOverviewViewModel = viewModel(factory = ArticleOverviewViewModel.Factory), onNavigateToDetail: (articleName: String) -> Unit) {
    val articleOverviewState by articleOverviewViewModel.uiState.collectAsState()
    val articlesApiState = articleOverviewViewModel.articlesApiState
    val articlesApiRefreshingState = articleOverviewViewModel.articleApiRefreshingState
    val isRefreshing = articlesApiRefreshingState is ArticlesApiState.Loading

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
            when (articlesApiState) {
                is ArticlesApiState.Loading -> LoadingIndicator()
                is ArticlesApiState.Error -> Text("Couldn't load...")
                is ArticlesApiState.Success ->
                    ArticleList(
                        modifier = modifier,
                        articles = articleOverviewState.articles,
                        onViewDetail = { onNavigateToDetail(it.title) },
                    )
            }
        }
    }
}

@Composable
fun ArticleList(modifier: Modifier = Modifier, articles: List<Article>, onViewDetail: (Article) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(articles) { article ->
            ArticleListItem(article = article, onViewDetail = { onViewDetail(article) })
        }
    }
}

@Composable
fun ArticleListItem(modifier: Modifier = Modifier, article: Article, onViewDetail: () -> Unit) {
    ListItem(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                onViewDetail()
            },
        shadowElevation = 1.dp,
        headlineContent = {
            Text(article.title, style = MaterialTheme.typography.titleMedium)
        },
        leadingContent = {
            if (article.imageUrl != null) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp),
                ) {
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
        ArticleList(articles = ArticleSampler.getAll(), onViewDetail = {})
    }
}

@Preview
@Composable
fun ArticleDetailPreview() {
    AndroidAppTheme {
        ArticleDetail(article = ArticleSampler.getAll().first())
    }
}
