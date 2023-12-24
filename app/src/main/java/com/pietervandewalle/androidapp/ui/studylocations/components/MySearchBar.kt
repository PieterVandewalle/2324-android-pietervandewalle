package com.pietervandewalle.androidapp.ui.studylocations.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

/**
 * Composable function for displaying a search bar.
 *
 * @param placeholder The placeholder text for the search bar.
 * @param searchTerm The current search term.
 * @param onSearchTermChange Callback to handle changes in the search term.
 * @param onCancel Callback to cancel the search.
 * @param onSearch Callback to perform the search.
 */
@Composable
fun MySearchBar(placeholder: String, searchTerm: String, onSearchTermChange: (String) -> Unit, onCancel: () -> Unit, onSearch: () -> Unit) {
    val focusRequester = FocusRequester()
    var textFieldLoaded by remember { mutableStateOf(false) }
    TextField(
        value = searchTerm,
        onValueChange = onSearchTermChange,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = stringResource(
                    R.string.search,
                ),
            )
        },
        trailingIcon = {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cancel))
            }
        },
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onGloballyPositioned {
                if (!textFieldLoaded) {
                    focusRequester.requestFocus() // IMPORTANT
                    textFieldLoaded = true // stop cyclic recompositions
                }
            },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusRequester.freeFocus()
            if (searchTerm.isNotBlank()) {
                onSearch()
            } else {
                onCancel()
            }
        }),
    )
}

@Composable
@Preview(showBackground = true)
private fun SearchBarPreview() {
    AndroidAppTheme {
        MySearchBar(
            placeholder = "Zoek iets",
            searchTerm = "",
            onSearchTermChange = {},
            onCancel = { },
            onSearch = {},
        )
    }
}
