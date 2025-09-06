package com.alvee.fetchjson.domain.usecase

import com.alvee.fetchjson.domain.model.PostItem
import com.alvee.fetchjson.domain.repository.PostFeedRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postFeedRepository: PostFeedRepository
) {
    suspend operator fun invoke(startIndex: Int): List<PostItem> {
        return postFeedRepository.getPosts(startIndex)
    }
}