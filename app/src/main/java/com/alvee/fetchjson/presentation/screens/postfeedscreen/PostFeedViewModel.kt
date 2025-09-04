package com.alvee.fetchjson.presentation.screens.postfeedscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvee.fetchjson.domain.usecase.GetPostsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PostFeedViewModel"
@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val getPostsUsecase: GetPostsUsecase
) : ViewModel() {
    private val _state = MutableStateFlow(PostFeedState())
    val state = _state.asStateFlow()

    init {
        getPosts(0)
    }

    fun getPosts(start: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val newPosts = getPostsUsecase(start)
                var newList = _state.value.postList.toMutableList()
                newList.addAll(newPosts)

                Log.d(TAG, "getPosts: ${newList.indices}")
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
}