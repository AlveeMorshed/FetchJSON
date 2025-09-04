package com.alvee.fetchjson.domain.usecase

import com.alvee.fetchjson.data.model.api_model.toDomain
import com.alvee.fetchjson.domain.model.PostItemResponse
import com.alvee.fetchjson.domain.repository.Repository
import javax.inject.Inject

class GetPostsUsecase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(start: Int = 0, limit: Int = 20): List<PostItemResponse> {
        return repository.getPosts(start = start, limit = limit).map { it.toDomain() }
    }
}