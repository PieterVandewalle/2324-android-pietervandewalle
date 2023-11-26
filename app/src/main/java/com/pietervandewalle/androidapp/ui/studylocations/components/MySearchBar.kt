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
import androidx.compose.ui.text.input.ImeAction

@Composable
fun MySearchBar(placeholder: String, searchterm: String, onSearchtermChange: (String) -> Unit, onCancel: () -> Unit, onSearch: () -> Unit) {
    val focusRequester = FocusRequester()
    var textFieldLoaded by remember { mutableStateOf(false) }
    TextField(
        value = searchterm,
        onValueChange = onSearchtermChange,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "search") },
        trailingIcon = {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.Close, contentDescription = "cancel")
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
            if (searchterm.isNotBlank()) {
                onSearch()
            } else {
                onCancel()
            }
        }),
    )
}
