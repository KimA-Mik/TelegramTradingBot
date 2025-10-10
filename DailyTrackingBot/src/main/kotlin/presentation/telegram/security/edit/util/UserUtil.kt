package presentation.telegram.security.edit.util

import domain.user.model.User

fun User.getTickerInEditScreen(): String? =
    pathList.getOrNull(pathList.lastIndex - 1)
