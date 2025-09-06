package com.alvee.fetchjson.presentation.screens.postfeed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.domain.usecase.GetCachedPostsUseCase
import com.alvee.fetchjson.domain.usecase.GetPostsUseCase
import com.alvee.fetchjson.domain.usecase.ToggleFavoriteUseCase
import com.alvee.fetchjson.utils.DataStoreManager
import com.alvee.fetchjson.utils.NetworkConnectivityObserver
import com.alvee.fetchjson.utils.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SharedFeedViewModel"

@HiltViewModel
class SharedFeedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getCachedPostsUseCase: GetCachedPostsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _state = MutableStateFlow(SharedFeedState())
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
        loadCurrentUserId()
        getCachedPosts(userId = _state.value.currentUserId)
    }
    fun loadCurrentUserId(){
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = dataStoreManager.getCurrentUserId()
            _state.value = _state.value.copy(
                currentUserId = currentUserId ?: 0
            )
        }
    }

    fun getPosts(start: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val newPosts = getPostsUseCase(startIndex = start)
                _state.value = _state.value.copy(
                    postList = (_state.value.postList + newPosts).toMutableList(),
                    favoritePostList = (_state.value.postList + newPosts).filter { it.isFavorite }.toMutableList(),
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
                val cachedPosts = getCachedPostsUseCase(userId)
                Log.d(TAG, "getCachedPosts: called $userId")
                _state.value = _state.value.copy(
                    postList = cachedPosts.toMutableList(),
                    favoritePostList = cachedPosts.filter { it.isFavorite }.toMutableList(),
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

    fun getFavoritePosts(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "getFavoritePosts: $userId")
            _state.value = _state.value.copy(isLoading = true)
            try {
                val favoritePosts = getCachedPostsUseCase(userId).filter { it.isFavorite }
                _state.value = _state.value.copy(
                    favoritePostList = favoritePosts.toMutableList(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message.toString()
                )
            }
        }
    }

    fun toggleFavorite(postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = toggleFavoriteUseCase(postId, _state.value.currentUserId)
                Log.d(TAG, "toggleFavorite: $result")
                getCachedPosts(_state.value.currentUserId)
            }catch (e: Exception){
                Log.e(TAG, "toggleFavorite: ${e.message}", )
            }
        }
    }
}