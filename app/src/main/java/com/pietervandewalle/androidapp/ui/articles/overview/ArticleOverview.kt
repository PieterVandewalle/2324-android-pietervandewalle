package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.articles.common.helpers.formatArticleDate
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.common.components.ScrollToTopButton
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@OptIn(ExperimentalMaterial3Api::class)
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
        modifier = modifier,
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = uiState.isRefreshing,
            onRefresh = articleOverviewViewModel::refresh,
            modifier = Modifier.padding(innerPadding),
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
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < 1200.dp) {
            LazyColumn(state = lazyListState) {
                items(articles) { article ->
                    ArticleListItem(article = article, onViewDetail = { onViewDetail(article) })
                }
            }
        } else {
            // 2 articles per row on large screens
            LazyColumn(state = lazyListState) {
                for (index in articles.indices step 2) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.padding_small),
                            ),
                        ) {
                            ArticleListItem(modifier = modifier.fillMaxWidth(0.5f), article = articles[index], onViewDetail = { onViewDetail(articles[index]) })
                            if (index + 1 != articles.size) {
                                ArticleListItem(article = articles[index + 1], onViewDetail = { onViewDetail(articles[index + 1]) })
                            }
                        }
                    }
                }
            }
        }
        ScrollToTopButton(lazyListState = lazyListState)
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
                        .width(dimensionResource(R.dimen.image_container_small))
                        .height(dimensionResource(R.dimen.image_container_small)),
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
            supportingContent = {
                Text(
                    formatArticleDate(article.date),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_extra_small)),
                )
            },
        )
    }
}

@Preview
@Composable
private fun ArticleListPreview() {
    AndroidAppTheme {
        ArticleList(articles = ArticleSampler.getAll(), onViewDetail = {})
    }
}
