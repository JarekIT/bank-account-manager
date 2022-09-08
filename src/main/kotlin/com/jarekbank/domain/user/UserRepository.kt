package com.jarekbank.domain.user

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
internal interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByPesel(pesel: String): Mono<User>
}