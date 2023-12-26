package com.pietervandewalle.androidapp.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

// https://github.com/jshvarts/UiStatePlayground/blob/master/app/src/main/java/com/example/uistateplayground/core/Result.kt

/**
 * The time interval (in milliseconds) to wait before retrying a failed network request.
 */
private const val RETRY_TIME_IN_MILLIS = 15_000L

/**
 * A sealed interface representing the result of an operation, which can be [Success], [Error], or [Loading].
 *
 * @param T The type of data associated with the result (for [Success]).
 */
sealed interface Result<out T> {
    /**
     * Represents a successful result with associated data of type [T].
     *
     * @param data The data resulting from the operation.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * Represents an error result with an optional [exception].
     *
     * @param exception The exception that occurred during the operation (if any).
     */
    data class Error(val exception: Throwable? = null) : Result<Nothing>

    /**
     * Represents a loading result, indicating that the operation is in progress.
     */
    object Loading : Result<Nothing>
}

/**
 * Converts a [Flow] of type [T] into a [Flow] of [Result] containing the result of the operation.
 *
 * @param T The type of data in the original flow.
 * @return A [Flow] of [Result] representing the result of the operation.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading) }
        .retryWhen { cause, _ ->
            if (cause is IOException) {
                emit(Result.Error(cause))

                delay(RETRY_TIME_IN_MILLIS)
                true
            } else {
                false
            }
        }
        .catch { emit(Result.Error(it)) }
}
