package com.pietervandewalle.androidapp.ui.articles.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.articles.common.helpers.formatArticleDate
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun ArticleDetailView(modifier: Modifier = Modifier, articleDetailViewModel: ArticleDetailViewModel = viewModel(factory = ArticleDetailViewModel.Factory), onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val uiState by articleDetailViewModel.uiState.collectAsState()
    val articleUiState = uiState.article

    val shareIntentTitle = stringResource(R.string.share_article)
    val shareIntentExtraTitle = stringResource(R.string.read_this_article_from_city_ghent)

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.home_title, canNavigateBack = true, onNavigateBack = onNavigateBack) {
                IconButton(onClick = {
                    val shareIntent: Intent = Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TITLE,
                                shareIntentExtraTitle,
                            )
                            putExtra(Intent.EXTRA_TEXT, if (articleUiState is ArticleDetailUiState.Success) articleUiState.article.readMoreUrl else "")
                            type = "text/plain"
                        },
                        shareIntentTitle,
                    )
                    context.startActivity(shareIntent)
                }, enabled = articleUiState is ArticleDetailUiState.Success) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.delen),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (articleUiState) {
                is ArticleDetailUiState.Loading -> LoadingIndicator()
                is ArticleDetailUiState.Error ->
                    Text(
                        stringResource(id = R.string.loading_failed),
                    )
                is ArticleDetailUiState.Success ->
                    ArticleDetail(
                        article = articleUiState.article,
                    )
            }
        }
    }
}

@Composable
fun ArticleDetail(modifier: Modifier = Modifier, article: Article) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (article.imageUrl != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.image_container_large)),
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
            Text(article.title, style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                Text(stringResource(R.string.city_ghent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

                Text(
                    formatArticleDate(article.date),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(article.content ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
private fun ArticleDetailPreview() {
    AndroidAppTheme {
        ArticleDetail(article = ArticleSampler.getAll().first())
    }
}
