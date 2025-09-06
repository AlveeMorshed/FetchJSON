package com.alvee.fetchjson.domain.usecase

import com.alvee.fetchjson.domain.model.PostItem
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import javax.inject.Inject

class GetCachedPostsUseCase @Inject constructor(
    private val postFeedRepository: PostFeedRepository
) {
    suspend operator fun invoke(userId: Int): List<PostItem> {
        return postFeedRepository.getCachedPosts(userId)
    }
}