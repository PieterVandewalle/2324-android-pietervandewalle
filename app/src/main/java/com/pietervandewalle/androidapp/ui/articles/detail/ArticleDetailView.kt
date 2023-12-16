package com.pietervandewalle.androidapp.ui.articles.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailView(modifier: Modifier = Modifier, articleDetailViewModel: ArticleDetailViewModel = viewModel(factory = ArticleDetailViewModel.Factory), onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val uiState by articleDetailViewModel.uiState.collectAsState()
    val articleUiState = uiState.article

    val shareIntentTitle = stringResource(R.string.share_article)
    val shareIntentExtraTitle = stringResource(
        R.string.read_this_article_from_city_ghent,
    )

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = articleDetailViewModel::onErrorConsumed)

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
                        contentDescription = "share",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
    ) { innerPadding ->
        when (articleUiState) {
            is ArticleDetailUiState.Loading -> Column(modifier = modifier.padding(innerPadding)) { LoadingIndicator() }
            is ArticleDetailUiState.Error -> Column(modifier = modifier.padding(innerPadding)) {
                Text(
                    stringResource(id = R.string.loading_failed),
                )
            }
            is ArticleDetailUiState.Success ->
                ArticleDetail(article = articleUiState.article, modifier = modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ArticleDetail(modifier: Modifier = Modifier, article: Article) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (article.imageUrl != null) {
            Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f)) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(article.title, style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.city_ghent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    dateFormatter.format(article.date),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(article.content ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
