package com.alvee.fetchjson.presentation.screens.postfeedscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.domain.usecase.GetCachedPostsUsecase
import com.alvee.fetchjson.domain.usecase.GetPostsUsecase
import com.alvee.fetchjson.utils.NetworkConnectivityObserver
import com.alvee.fetchjson.utils.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PostFeedViewModel"

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val getPostsUsecase: GetPostsUsecase,
    private val getCachedPostsUsecase: GetCachedPostsUsecase,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
    private val _state = MutableStateFlow(PostFeedState())
    val state = _state.asStateFlow()

    private val _networkStatus = MutableStateFlow(
        if (networkConnectivityObserver.isOnline()) NetworkStatus.Available else NetworkStatus.Unavailable
    )
    val networkStatus = _networkStatus.asStateFlow()

    init {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collect { status ->
                _networkStatus.value = status
            }
        }
    }

    fun getPosts(start: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val newPosts = getPostsUsecase(userId = 1, startIndex = start)
                //Log.d(TAG, "getPosts: ${newPosts.indices}")
                Log.d(
                    TAG,
                    "getPosts: ${(_state.value.postList + newPosts).toMutableList().indices}"
                )
                _state.value = _state.value.copy(
                    postList = (_state.value.postList + newPosts).toMutableList(),
                    isLoading = false,
                    error = ""
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun getCachedPosts(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val cachedPosts = getCachedPostsUsecase(userId)
                Log.d(TAG, "getCachedPosts: called")
                _state.value = _state.value.copy(
                    postList = cachedPosts.toMutableList(),
                    isLoading = false,
                    error = ""
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
}