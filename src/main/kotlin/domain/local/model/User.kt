package domain.local.model

import java.time.LocalDateTime

data class User(
    val id: Long,
    val registered: LocalDateTime,
    val path: String
)
