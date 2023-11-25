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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import java.time.format.DateTimeFormatter

@Composable
fun ArticleDetailView(modifier: Modifier = Modifier, articleDetailViewModel: ArticleDetailViewModel = viewModel(factory = ArticleDetailViewModel.Factory), onNavigateBack: () -> Unit) {
    val articleDetailState by articleDetailViewModel.uiState.collectAsState()
    val articleApiState = articleDetailViewModel.articlesApiState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.home_title, canNavigateBack = true, onNavigateBack = onNavigateBack) {
                IconButton(onClick = {
                    val shareIntent: Intent = Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, "Bekijk dit artikel van Stad Gent")
                            putExtra(Intent.EXTRA_TEXT, articleDetailState.article.readMoreUrl)
                            type = "text/plain"
                        },
                        "Artikel delen",
                    )
                    context.startActivity(shareIntent)
                }, enabled = articleApiState is ArticleApiState.Success) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "share",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
    ) { innerPadding ->
        when (articleApiState) {
            is ArticleApiState.Loading -> Column(modifier = modifier.padding(innerPadding)) { LoadingIndicator() }
            is ArticleApiState.Error -> Column(modifier = modifier.padding(innerPadding)) { Text("Couldn't load...") }
            is ArticleApiState.Success ->
                ArticleDetail(article = articleDetailState.article, modifier = modifier.padding(innerPadding))
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
                Text("Stad Gent", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    dateFormatter.format(article.date),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(article.content ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
