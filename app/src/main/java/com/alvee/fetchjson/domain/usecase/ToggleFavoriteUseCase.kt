package com.alvee.fetchjson.domain.usecase

import com.alvee.fetchjson.domain.repository.PostFeedRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val postFeedRepository: PostFeedRepository
) {
    suspend operator fun invoke(postId: Int, userId: Int): Int {
        return postFeedRepository.toggleFavorite(postId, userId)
    }
}