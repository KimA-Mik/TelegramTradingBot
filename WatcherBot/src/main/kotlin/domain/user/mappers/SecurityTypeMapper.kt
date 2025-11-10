package domain.user.mappers

import domain.user.model.SecurityType
import ru.kima.cacheserver.api.schema.model.Future
import ru.kima.cacheserver.api.schema.model.Security
import ru.kima.cacheserver.api.schema.model.Share

val Security.type
    get() = when (this) {
        is Future -> SecurityType.FUTURE
        is Share -> SecurityType.SHARE
    }
