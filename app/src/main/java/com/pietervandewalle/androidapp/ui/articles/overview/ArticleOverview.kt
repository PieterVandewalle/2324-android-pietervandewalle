package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetail
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun ArticleOverview(modifier: Modifier = Modifier, articleOverviewViewModel: ArticleOverviewViewModel = viewModel(factory = ArticleOverviewViewModel.Factory), onNavigateToDetail: (articleId: Int) -> Unit) {
    val uiState by articleOverviewViewModel.uiState.collectAsState()
    val articlesUiState = uiState.articles

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = articleOverviewViewModel::onErrorConsumed)

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.home_title) {
            }
        },
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = uiState.isRefreshing, // rememberPullRefreshState(refreshing = uiState.isRefreshing),
            onRefresh = articleOverviewViewModel::refresh,
            modifier = modifier.padding(innerPadding),
        ) {
            when (articlesUiState) {
                is ArticlesOverviewUiState.Loading -> LoadingIndicator()
                is ArticlesOverviewUiState.Error -> ErrorLoadingIndicatorWithRetry(onRetry = articleOverviewViewModel::refresh)
                is ArticlesOverviewUiState.Success ->
                    ArticleList(
                        articles = articlesUiState.articles,
                        onViewDetail = { onNavigateToDetail(it.id) },
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
    DefaultOverviewListItemCard(
        modifier = modifier.clickable {
            onViewDetail()
        },
    ) {
        ListItem(
            headlineContent = {
                Text(article.title, style = MaterialTheme.typography.titleMedium)
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (article.imageUrl != null) {
                        AsyncImage(
                            model = article.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Image(painterResource(id = R.drawable.logo_gent), contentDescription = null, contentScale = ContentScale.Crop)
                    }
                }
            },
        )
    }
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
